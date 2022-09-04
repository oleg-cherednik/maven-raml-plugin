/*
 * Copyright © 2016 Oleg Cherednik
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package cop.raml.utils.example;

import cop.raml.TestUtils;
import cop.raml.processor.Config;
import cop.raml.utils.ElementUtils;
import cop.raml.utils.ReflectionUtils;
import cop.raml.utils.ThreadLocalContext;
import cop.raml.utils.Utils;
import cop.raml.utils.javadoc.Macro;
import cop.raml.mocks.ElementMock;
import cop.raml.mocks.ElementsMock;
import cop.raml.mocks.MockUtils;
import cop.raml.mocks.ProblemVariableElement;
import cop.raml.mocks.TypeElementMock;
import cop.raml.mocks.TypeMirrorMock;
import cop.raml.mocks.VariableElementMock;
import cop.raml.mocks.annotations.JsonIgnoreMock;
import cop.raml.mocks.annotations.JsonIgnorePropertiesMock;
import cop.raml.mocks.annotations.OverrideMock;
import cop.utils.json.JsonUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 01.01.17
 */
@SuppressWarnings({ "serial", "InstanceMethodNamingConvention" })
public class JsonExampleTest {
    @BeforeMethod
    private void initContext() {
        MockUtils.initThreadLocalContext();
    }

    @AfterMethod
    private void clearContext() {
        ThreadLocalContext.remove();
    }

    @Test
    public void testGetEnumExample() throws Exception {
        ElementMock element;
        TypeMirrorMock type = new TypeMirrorMock(null);
        assertThat(getEnumExample(type)).isNull();

        type.setElement(element = new ElementMock(Macro.class.getSimpleName(), ElementKind.ENUM));
        assertThat(getEnumExample(type)).isEqualTo(Macro.class.getSimpleName());

        element.addEnclosedElement(new ElementMock(Macro.EXAMPLE.toString(), ElementKind.METHOD));
        assertThat(getEnumExample(type)).isEqualTo(Macro.class.getSimpleName());

        element.addEnclosedElement(new ElementMock(Macro.DEFAULT.toString(), ElementKind.ENUM_CONSTANT));
        element.addEnclosedElement(new ElementMock(Macro.ENUM.toString(), ElementKind.ENUM_CONSTANT));
        assertThat(getEnumExample(type)).isEqualTo(Macro.DEFAULT.toString());

        type.setElement(new ElementMock("EmptyEnum", ElementKind.ENUM));
        assertThat(getEnumExample(type)).isEqualTo("EmptyEnum");

        type.setElement(new ProblemVariableElement(ElementKind.ENUM));
        assertThat(getEnumExample(type)).isNull();
    }

    @Test
    public void testGetIgnoreFields() throws Exception {
        assertThat(getIgnoredFields(null)).isSameAs(Collections.emptySet());

        TypeElementMock typeElement = new TypeElementMock("count", ElementKind.CLASS);
        assertThat(getIgnoredFields(typeElement)).isEmpty();

        typeElement.addEnclosedElement(new ElementMock("prop1", ElementKind.FIELD));
        assertThat(getIgnoredFields(typeElement)).isEmpty();

        typeElement.addEnclosedElement(new ElementMock("prop2", new OverrideMock()));
        assertThat(getIgnoredFields(typeElement)).isEmpty();

        typeElement.addEnclosedElement(new ElementMock("prop3", new JsonIgnoreMock(false)));
        assertThat(getIgnoredFields(typeElement)).isEmpty();

        typeElement.addEnclosedElement(new ElementMock("prop4", new JsonIgnoreMock(true)));
        assertThat(getIgnoredFields(typeElement)).containsExactly("prop4");

        typeElement.addAnnotation(new JsonIgnorePropertiesMock());
        assertThat(getIgnoredFields(typeElement)).containsExactly("prop4");

        typeElement.addAnnotation(new JsonIgnorePropertiesMock("prop5"));
        assertThat(getIgnoredFields(typeElement)).containsExactly("prop5", "prop4");
    }

    @Test(groups = "toJson")
    public void testToJson() throws Exception {
        assertThat(toJson(null)).isNull();
        assertThat(toJson("")).isEqualTo("");
        assertThat(toJson("   ")).isEqualTo("   ");
        assertThat(toJson("aaa")).isEqualTo("aaa");
        assertThat(Utils.splitLine(toJson(new LinkedHashMap<String, Object>() {{
            put("key1", "val1");
            put("key2", Collections.singletonMap("key3", "val3"));
        }}))).containsExactly(
                "{",
                "  \"key1\" : \"val1\",",
                "  \"key2\" : {",
                "    \"key3\" : \"val3\"",
                "  }",
                "}");
    }

    @Test(groups = "toJson")
    public void shouldReturnNullWhenJsonProblem() throws Exception {
        Data data = new Data();
        data.setData(data);
        assertThat(toJson(data)).isNull();
    }

    @Test(groups = "getPrimitiveExample")
    public void shouldReturnNullWhenKindIsNotSet() throws Exception {
        assertThat(getPrimitiveExample(null)).isNull();
    }

    @Test(groups = "getPrimitiveExample")
    public void shouldReturnRandomBooleanWhenBooleanElement() throws Exception {
        assertThat(getPrimitiveExample(TypeKind.BOOLEAN)).isInstanceOf(Boolean.class);
    }

    @Test(groups = "getPrimitiveExample")
    public void shouldReturnRandomByteWhenByteElement() throws Exception {
        assertThat(getPrimitiveExample(TypeKind.BYTE)).isInstanceOf(Byte.class);
    }

    @Test(groups = "getPrimitiveExample")
    public void shouldReturnRandomShortWhenShortElement() throws Exception {
        assertThat(getPrimitiveExample(TypeKind.SHORT)).isInstanceOf(Short.class);
    }

    @Test(groups = "getPrimitiveExample")
    public void shouldReturnRandomIntegerWhenIntegerElement() throws Exception {
        assertThat(getPrimitiveExample(TypeKind.INT)).isInstanceOf(Integer.class);
    }

    @Test(groups = "getPrimitiveExample")
    public void shouldReturnRandomLongWhenLongElement() throws Exception {
        assertThat(getPrimitiveExample(TypeKind.LONG)).isInstanceOf(Long.class);
    }

    @Test(groups = "getPrimitiveExample")
    public void shouldReturnRandomCharWhenCharElement() throws Exception {
        assertThat(getPrimitiveExample(TypeKind.CHAR)).isInstanceOf(Character.class);
    }

    @Test(groups = "getPrimitiveExample")
    public void shouldReturnRandomFloatWhenFloatElement() throws Exception {
        assertThat(getPrimitiveExample(TypeKind.FLOAT)).isInstanceOf(Float.class);
    }

    @Test(groups = "getPrimitiveExample")
    public void shouldReturnRandomDoubleWhenDoubleElement() throws Exception {
        assertThat(getPrimitiveExample(TypeKind.DOUBLE)).isInstanceOf(Double.class);
    }

    @Test(groups = "getDeclaredExample")
    public void shouldReturnRandomUUIDWhenUUIDElement() throws Exception {
        assertThat(getDeclaredExample(UUID.class.getName(), "id")).isInstanceOf(UUID.class);
    }

    @Test(groups = "getDeclaredExample")
    public void shouldReturnRandomUUIDStringWhenStringElementAndUUIDName() throws Exception {
        assertThat(getDeclaredExample(String.class.getName(), null)).isNull();
        assertThat(getDeclaredExample(String.class.getName(), "id")).isNull();
        assertThat(getDeclaredExample(String.class.getName(), "uuid"))
                .matches(actual -> actual instanceof String && TestUtils.UUID.matcher((CharSequence)actual).matches());
    }

    @Test(groups = "getDeclaredExample")
    public void shouldReturnCurrentMillisecondWhenDateElement() throws Exception {
        long start = System.currentTimeMillis();
        Object actual = getDeclaredExample(Date.class.getName(), "created");
        long end = System.currentTimeMillis();

        assertThat(actual).isInstanceOf(Long.class);
        assertThat((Long)actual).isBetween(start, end);
    }

    @Test(groups = "getAutoGeneratedElementExample")
    public void shouldReturnExampleMacroWhenMacroExists() throws Exception {
        VariableElementMock var = MockUtils.createVariable("param", int.class);
        var.setDocComment("{@example 666}");
        assertThat(getAutoGeneratedElementExample(var)).isEqualTo("666");
    }

    @Test(groups = "getAutoGeneratedElementExample")
    public void shouldReturnLinkedElementExampleMacroWhenLinkedElementWithMacroExists() throws Exception {
        VariableElementMock var = MockUtils.createVariable("projectId", int.class);
        var.setDocComment("{@link Project#id}");
        MockUtils.setImportScannerElement(MockUtils.createProject());
        assertThat(getAutoGeneratedElementExample(var)).isEqualTo("666");
    }

    @Test(groups = "getAutoGeneratedElementExample")
    public void shouldReturnNullWhenElementIsNotSimple() throws Exception {
        assertThat(getAutoGeneratedElementExample(MockUtils.createVariable("projectId", Object.class))).isNull();
        assertThat(getAutoGeneratedElementExample(MockUtils.createElement(MockUtils.Count.class))).isNull();
    }

    @Test(groups = "getAutoGeneratedElementExample")
    public void shouldReturnPrimitiveElementExampleWhenPrimitiveElement() throws Exception {
        assertThat(getAutoGeneratedElementExample(MockUtils.createVariable("id", long.class))).isInstanceOf(Long.class);
    }

    @Test(groups = "getTypeExample")
    public void shouldReturnNullWhenElementIsAlreadyVisited() throws Exception {
        Set<String> visited = new HashSet<>();
        TypeElement obj = MockUtils.createElement(MockUtils.Scenario.class);
        Map<String, Object> map = getTypeExample(obj, visited);

        assertThat(map).hasSize(4);
        assertThat(map.get("id")).isInstanceOf(Integer.class);
        assertThat(map.get("name")).isEqualTo("name");
        assertThat(map.get("ids")).isInstanceOf(Collection.class);
        assertThat((Iterable<Integer>)map.get("ids")).hasSize(1);
        assertThat(((Collection<Integer>)map.get("ids")).iterator().next()).isInstanceOf(Integer.class);
        assertThat(visited).isEmpty();

        visited.add(obj.toString());
        assertThat(getTypeExample(obj, visited)).isNull();
    }

    @Test(groups = "example")
    public void shouldReturnNullWhenElementIsNotSet() {
        assertThat(new JsonExample().example(null, false)).isNull();
    }

    @Test(groups = "example")
    public void shouldReturnVariableExampleWhenVariableElement() throws Exception {
        VariableElementMock var = MockUtils.createVariable("count", MockUtils.Count.class);
        assertThat(new JsonExample().example(var, false)).isEqualTo(toJson(getEnumExample(var.asType())));
    }

    @Test(groups = "example")
    public void shouldReturnArrayWhenArrayOptionIsSet() throws Exception {
        VariableElementMock var = MockUtils.createVariable("count", MockUtils.Count.class);
        assertThat(new JsonExample().example(var, true)).isEqualTo(toJson(Collections.singleton(getEnumExample(var.asType()))));
    }

    @Test(groups = "example")
    public void shouldReturnArrayWhenArrayOptionIsSetAndVariableIsArray() throws Exception {
        VariableElementMock var = MockUtils.createVariable("ids", long[].class);
        List<Object> res = JsonUtils.readList(new JsonExample().example(var, false), Object.class);
        assertThat(res).hasSize(1);
        assertThat(res.iterator().next()).isNotInstanceOf(Collection.class);

        res = JsonUtils.readList(new JsonExample().example(var, true), Object.class);
        assertThat(res).hasSize(1);
        assertThat(res.iterator().next()).isNotInstanceOf(Collection.class);
    }

    @Test(groups = "getElementExample")
    public void shouldCacheResultWhenResultIsNotAutoGenerated() throws Exception {
        JsonExample obj = new JsonExample();
        TypeElement element = MockUtils.createElement(MockUtils.Scenario.class);

        assertThat(getCache(obj)).isEmpty();
        Object expected = getElementExample(obj, element, new HashSet<>());
        Object actual = getElementExample(obj, element, new HashSet<>());
        assertThat(actual).isSameAs(expected);
        assertThat(getCache(obj)).hasSize(2);
    }

    @Test(groups = "getElementExample")
    public void shouldNotCacheResultWhenResultIsAutoGenerated() throws Exception {
        JsonExample obj = new JsonExample();

        assertThat(getCache(obj)).isEmpty();
        assertThat(getElementExample(obj, MockUtils.createVariable("id", int.class), new HashSet<>())).isNotNull();
        assertThat(getCache(obj)).isEmpty();
    }

    @Test(groups = "getElementExample")
    public void shouldNotCacheResultWhenEnumElement() throws Exception {
        JsonExample obj = new JsonExample();

        assertThat(getCache(obj)).isEmpty();
        assertThat(getElementExample(obj, MockUtils.createElement(MockUtils.Count.class), new HashSet<>())).isNotNull();
        assertThat(getCache(obj)).isEmpty();
    }

    @Test(groups = "getElementExample")
    public void shouldNotCacheResultWhenStringElement() throws Exception {
        JsonExample obj = new JsonExample();

        assertThat(getCache(obj)).isEmpty();
        assertThat(getElementExample(obj, MockUtils.createElement(String.class), new HashSet<>())).isNotNull();
        assertThat(getCache(obj)).isEmpty();
    }

    @Test(groups = "getElementExample")
    public void shouldReturnVariableNameWhenStringElement() throws Exception {
        assertThat(getElementExample(new JsonExample(), MockUtils.createVariable("id", String.class), new HashSet<>())).isEqualTo("id");
    }

    @Test(groups = "getElementExample")
    public void shouldReturnNullWhenExceptionOccurred() throws Exception {
        assertThat(getElementExample(new JsonExample(), new ProblemVariableElement(ElementKind.ENUM), Collections.emptySet())).isNull();
    }

    @SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
    @Test(groups = "getElementExample")
    public void shouldIgnoreClassWhenInSkipList() throws Exception {
        TypeElement element = MockUtils.createElement(MockUtils.Scenario.class);
        Map<String, Object> map = (Map<String, Object>)getElementExample(new JsonExample(), element, new HashSet<>());
        assertThat(map).hasSize(4);
        assertThat(map.keySet()).containsExactly("id", "name", "ids", "job");

        ThreadLocalContext.setConfig(Config.builder()
                                           .ramlSkipPattern(MockUtils.Job.class.getName().replaceAll("\\.", "\\\\\\.").replaceAll("\\$", "\\\\\\$"))
                                           .build());

        map = (Map<String, Object>)getElementExample(new JsonExample(), element, new HashSet<>());
        assertThat(map).hasSize(3);
        assertThat(map.keySet()).containsExactly("id", "name", "ids");
    }

    @Test(groups = "getElementExample")
    public void shouldIgnoreCircleReferenceWhenElementRefersToItself() throws Exception {
        TypeElementMock element = new TypeElementMock(Node.class.getName(), ElementKind.CLASS);
        element.setType(new TypeMirrorMock(element, TypeMirrorMock.getTypeKind(Node.class)));

        element.addEnclosedElement(MockUtils.createVariable("val", String.class));
        element.addEnclosedElement(new VariableElementMock("", element.asType()));

        Map<String, Object> map = (Map<String, Object>)getElementExample(new JsonExample(), element, new HashSet<>());
        assertThat(map).hasSize(1);
        assertThat(map.keySet()).containsOnly("val");
    }

    @Test(groups = "getElementExample")
    public void shouldReturnCollectionWhenArrayElement() throws Exception {
        TypeElement element = MockUtils.createElement(MockUtils.Count[].class);
        assertThat(ElementUtils.isArray(element)).isTrue();
        assertThat((Iterable<String>)getElementExample(new JsonExample(), element, new HashSet<>())).hasSize(1);
    }

    @Test(groups = "getElementExample")
    public void shouldReturnCollectionWhenCollectionElement() throws Exception {
        ElementMock element = MockUtils.createElement(List.class).setDocComment("{@type String}");
        ((ElementsMock)ThreadLocalContext.getElementUtils()).addTypeElement(MockUtils.createElement(String.class));
        assertThat(ElementUtils.isCollection(element)).isTrue();
        assertThat((Iterable<String>)getElementExample(new JsonExample(), element, new HashSet<>())).hasSize(1);
    }

    // ========== data ==========

    @SuppressWarnings("unused")
    private static class Data {
        private Data data;

        public void setData(Data data) {
            this.data = data;
        }
    }

    @SuppressWarnings({ "unused", "MethodMayBeStatic" })
    private static class Node {
        private String val;
        private Node child;
    }

    // ========== static ==========

    private static String toJson(Object example) throws Exception {
        return ReflectionUtils.invokeStaticMethod(JsonExample.class, "toJson", Object.class, example);
    }

    private static Map<String, Object> getTypeExample(TypeElement obj, Set<String> visited) throws Exception {
        return ReflectionUtils.invokeMethod(new JsonExample(), "getTypeExample", TypeElement.class, Set.class, obj, visited);
    }

    private static Object getPrimitiveExample(TypeKind kind) throws Exception {
        return ReflectionUtils.invokeStaticMethod(JsonExample.class, "getPrimitiveExample", TypeKind.class, Random.class, kind, new Random());
    }

    private static Object getDeclaredExample(String className, String varName) throws Exception {
        return ReflectionUtils.invokeStaticMethod(JsonExample.class, "getDeclaredExample", String.class, String.class, className, varName);
    }

    private static Object getAutoGeneratedElementExample(Element element) throws Exception {
        return ReflectionUtils.invokeStaticMethod(JsonExample.class, "getAutoGeneratedElementExample",
                Element.class, Random.class, element, new Random());
    }

    private static String getEnumExample(TypeMirror type) throws Exception {
        return ReflectionUtils.invokeStaticMethod(JsonExample.class, "getEnumExample", TypeMirror.class, type);
    }

    private static Set<String> getIgnoredFields(TypeElement typeElement) throws Exception {
        return ReflectionUtils.invokeStaticMethod(JsonExample.class, "getIgnoredFields", TypeElement.class, typeElement);
    }

    private static Object getElementExample(JsonExample obj, Element element, Set<String> visited) throws Exception {
        return ReflectionUtils.invokeMethod(obj, "getElementExample", Element.class, Set.class, element, visited);
    }

    private static Map<String, Object> getCache(JsonExample obj) throws Exception {
        return ReflectionUtils.getFieldValue(obj, "cache");
    }
}
