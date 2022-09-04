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
package cop.raml.utils;

import cop.raml.mocks.ElementsMock;
import cop.raml.mocks.ExecutableElementMock;
import cop.raml.mocks.MessagerMock;
import cop.raml.mocks.MockUtils;
import cop.raml.mocks.NameMock;
import cop.raml.mocks.PackageElementMock;
import cop.raml.mocks.RoundEnvironmentMock;
import cop.raml.mocks.TypeElementMock;
import cop.raml.mocks.TypeMirrorMock;
import cop.raml.mocks.TypeParameterElementMock;
import cop.raml.mocks.VariableElementMock;
import cop.raml.processor.Config;
import cop.raml.processor.exceptions.RamlProcessingException;
import cop.raml.utils.javadoc.tags.TagLink;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.DirectoryStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Oleg Cherednik
 * @since 08.01.2017
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public class ImportScannerTest {
    private static final DirectoryStream.Filter<String> FILTER_ACCEPT_ALL = str -> true;
    private static final DirectoryStream.Filter<String> FILTER_ACCEPT_NONE = str -> false;

    private ImportScanner importScanner;

    @BeforeMethod
    private void initContext() {
        importScanner = new ImportScanner();
        MockUtils.initThreadLocalContext();
    }

    @AfterMethod
    private void clearContext() {
        ThreadLocalContext.remove();
    }

    @Test(groups = "scanRootElements")
    public void shouldImportTypeElementOnlyWhenScanRootElement() throws Exception {
        assertThat(getRootElements()).isEmpty();

        RoundEnvironmentMock roundEnv = (RoundEnvironmentMock)ThreadLocalContext.getRoundEnv();
        roundEnv.addRootElement(MockUtils.createElement(One.class));
        roundEnv.addRootElement(MockUtils.createElement(Two.class));
        roundEnv.addRootElement(MockUtils.createElement(Three.class));

        roundEnv.addRootElement(MockUtils.createVariable("one", One.class));
        roundEnv.addRootElement(MockUtils.createVariable("two", Two.class));
        roundEnv.addRootElement(MockUtils.createVariable("three", Three.class));

        importScanner.scanRootElements();
        assertThat(getRootElements()).hasSize(3);
    }

    @Test(groups = "scanRootElements")
    public void shouldSkipElementsWhenConfigHasSkipList() throws Exception {
        assertThat(getRootElements()).isEmpty();

        RoundEnvironmentMock roundEnv = (RoundEnvironmentMock)ThreadLocalContext.getRoundEnv();
        roundEnv.addRootElement(MockUtils.createElement(String.class));
        roundEnv.addRootElement(MockUtils.createElement(GetMapping.class));
        roundEnv.addRootElement(MockUtils.createElement(One.class));

        importScanner.scanRootElements();
        assertThat(getRootElements()).hasSize(1);
    }

    @Test(groups = "scanRootElements")
    public void shouldClearExistedRootElementsWhenScanRootElements() throws Exception {
        assertThat(getRootElements()).isEmpty();

        Map<String, TypeElement> rootElements;
        RoundEnvironmentMock roundEnv = (RoundEnvironmentMock)ThreadLocalContext.getRoundEnv();
        roundEnv.addRootElement(MockUtils.createElement(One.class));

        importScanner.scanRootElements();
        assertThat(rootElements = getRootElements()).hasSize(1);
        assertThat(rootElements.keySet()).containsExactly(One.class.getName());

        roundEnv.clearRootElements();
        roundEnv.addRootElement(MockUtils.createElement(Two.class));
        roundEnv.addRootElement(MockUtils.createElement(Three.class));
        importScanner.scanRootElements();
        assertThat(rootElements = getRootElements()).hasSize(2);
        assertThat(rootElements.keySet()).containsOnly(Two.class.getName(), Three.class.getName());
    }

    @Test(groups = "getClassRootElement")
    public void shouldIgnoreNullWhenGetClassRootElement() throws Exception {
        assertThat(getClassRootElement(null)).isNull();
    }

    @Test(groups = "getClassRootElement")
    public void shouldReturnFirstPackageKindElementWhenGetClassRootElement() throws Exception {
        Element element = createElementWithClassRoot(One.class, "getId", null);
        assertThat(getClassRootElement(element)).isSameAs(element.getEnclosingElement());
    }

    @Test(groups = "getClassRootElement")
    public void shouldReturnNullWhenNoPackageRootElement() throws Exception {
        assertThat(getClassRootElement(createElementWithClassRoot(One.class, "getId", ElementKind.CLASS))).isNull();
    }

    @Test(groups = "filter")
    public void shouldAcceptDirectoryWhenFilterIsNull() throws Exception {
        assertThat(filter("cop.raml.Controller", null)).isTrue();
    }

    @Test(groups = "filter")
    public void shouldAcceptDirectoryWhenFilterIsPass() throws Exception {
        assertThat(filter("cop.raml.Controller", "cop.raml.Controller"::equals)).isTrue();
    }

    @Test(groups = "filter")
    public void shouldNotAcceptDirectoryWhenFilterIsNotPass() throws Exception {
        assertThat(filter("cop.raml.Controller", "cop.raml.Service"::equals)).isFalse();
    }

    @Test(groups = "filter")
    public void shouldNotAcceptDirectoryWhenFilterThrowsException() throws Exception {
        assertThat(filter("cop.raml.Controller", entry -> {
            throw new IOException("some problem");
        })).isFalse();
    }

    @Test
    public void testAddAcceptedType() throws Exception {
        assertThat(getTypes()).isEmpty();
        addAcceptedType("cop.raml.Controller", FILTER_ACCEPT_ALL);
        assertThat(getTypes()).hasSize(1);
        addAcceptedType("cop.raml.Service", FILTER_ACCEPT_NONE);
        assertThat(getTypes()).hasSize(1);
    }

    @Test
    public void testVisitExecutable() throws Exception {
        assertThat(getTypes()).isEmpty();
        importScanner.visitExecutable(createExecutableElement("aaa", TypeKind.DECLARED), FILTER_ACCEPT_ALL);
        assertThat(getTypes()).hasSize(1);
        importScanner.visitExecutable(createExecutableElement("aaa", TypeKind.ARRAY), FILTER_ACCEPT_ALL);
        assertThat(getTypes()).hasSize(1);
        importScanner.visitExecutable(createExecutableElement("aaa", TypeKind.DECLARED), FILTER_ACCEPT_NONE);
        assertThat(getTypes()).hasSize(1);
    }

    @Test
    public void testVisitTypeParameter() throws Exception {
        assertThat(getTypes()).isEmpty();
        importScanner.visitTypeParameter(createTypeParameterElement("aaa", TypeKind.DECLARED), FILTER_ACCEPT_ALL);
        assertThat(getTypes()).hasSize(1);
        importScanner.visitTypeParameter(createTypeParameterElement("aaa", TypeKind.ARRAY), FILTER_ACCEPT_ALL);
        assertThat(getTypes()).hasSize(1);
        importScanner.visitTypeParameter(createTypeParameterElement("aaa", TypeKind.DECLARED), FILTER_ACCEPT_NONE);
        assertThat(getTypes()).hasSize(1);
    }

    @Test
    public void testVisitVariable() throws Exception {
        assertThat(getTypes()).isEmpty();
        importScanner.visitVariable(createVariable("aaa", TypeKind.DECLARED), FILTER_ACCEPT_ALL);
        assertThat(getTypes()).hasSize(1);
        importScanner.visitVariable(createVariable("aaa", TypeKind.ARRAY), FILTER_ACCEPT_ALL);
        assertThat(getTypes()).hasSize(1);
        importScanner.visitVariable(createVariable("aaa", TypeKind.DECLARED), FILTER_ACCEPT_NONE);
        assertThat(getTypes()).hasSize(1);
    }

    @Test
    public void testClear() throws Exception {
        Set<String> types = getTypes();
        Map<String, Map<String, TypeElement>> imports = getImports();
        Map<String, TypeElement> rootElements = getRootElements();

        types.add("aaa");
        imports.put("aaa", Collections.emptyMap());
        rootElements.put("aaa", null);

        importScanner.clear();
        assertThat(getTypes()).isEmpty();
        assertThat(getImports()).isEmpty();
        assertThat(getRootElements()).isEmpty();
    }

    @Test(groups = "getOneRootElementWithName")
    public void shouldRetrieveNullWhenNoCandidatesFound() throws Exception {
        importScanner.setCurrentElement(createClassRootElement(Three.class));
        assertThat(getRootElements()).isEmpty();
        assertThat(getOneRootElementWithName("foo")).isNull();
    }

    @Test(groups = "getOneRootElementWithName")
    public void shouldReturnElementWhenOneCandidateFound() throws Exception {
        importScanner.setCurrentElement(createClassRootElement(Three.class));
        TypeElement expected;
        Map<String, TypeElement> rootElements = getRootElements();
        rootElements.put(One.class.getName(), expected = MockUtils.createElement(One.class));
        rootElements.put(Two.class.getName(), MockUtils.createElement(Two.class));
        assertThat(getOneRootElementWithName(One.class.getSimpleName())).isSameAs(expected);
    }

    @Test(groups = "getOneRootElementWithName")
    public void shouldThrowExceptionWhenMultipleCandidatesAndStopOnError() throws Exception {
        importScanner.setCurrentElement(createClassRootElement(Three.class));

        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        Map<String, TypeElement> rootElements = getRootElements();
        rootElements.put("xxx.yyy." + One.class.getSimpleName(), MockUtils.createElement(One.class));
        rootElements.put("xxx.zzz." + One.class.getSimpleName(), MockUtils.createElement(Two.class));
        assertThatThrownBy(() -> getOneRootElementWithName(One.class.getSimpleName())).isInstanceOf(RamlProcessingException.class);
    }

    @Test(groups = "getOneRootElementWithName")
    public void shouldReturnFirstCandidateWhenMultipleCandidatesAndNoStopOnError() throws Exception {
        importScanner.setCurrentElement(createClassRootElement(Three.class));

        TypeElement expected;
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(false).build());
        Map<String, TypeElement> rootElements = getRootElements();
        rootElements.put("xxx.yyy." + One.class.getSimpleName(), expected = MockUtils.createElement(One.class));
        rootElements.put("xxx.zzz." + One.class.getSimpleName(), MockUtils.createElement(Two.class));
        assertThat(getOneRootElementWithName(One.class.getSimpleName())).isSameAs(expected);
        assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNotEmpty();
    }

    @Test(groups = "getElementByString")
    public void shouldReturnNullWhenClassNameIsNotSet() {
        assertThat(importScanner.getElement((String)null)).isNull();
        assertThat(importScanner.getElement("")).isNull();
        assertThat(importScanner.getElement("   ")).isNull();
    }

    @Test(groups = "getElementByString")
    public void shouldReturnNullWhenCollectionItemIsAny() throws ClassNotFoundException {
        ElementsMock elements = (ElementsMock)ThreadLocalContext.getElementUtils();
        elements.addTypeElement(MockUtils.createElement(String.class));
        elements.addTypeElement(MockUtils.createElement(int.class));
        elements.addTypeElement(MockUtils.createElement(MockUtils.Count.class));

        assertThat(importScanner.getElement("long,java.lang.String)java.util.List<any>")).isNull();
    }

    @Test(groups = "getElementByString")
    public void shouldReturnExpectedElement() throws ClassNotFoundException {
        ElementsMock elements = (ElementsMock)ThreadLocalContext.getElementUtils();
        elements.addTypeElement(MockUtils.createElement(String.class));
        elements.addTypeElement(MockUtils.createElement(int.class));
        elements.addTypeElement(MockUtils.createElement(MockUtils.Count.class));

        assertThat(importScanner.getElement(String.class.getName()).toString()).isEqualTo(String.class.getName());
        assertThat(importScanner.getElement(String.class.getSimpleName()).toString()).isEqualTo(String.class.getName());
        assertThat(importScanner.getElement(int.class.getName()).toString()).isEqualTo(int.class.getName());
        assertThat(importScanner.getElement(int.class.getSimpleName()).toString()).isEqualTo(int.class.getName());
        assertThat(importScanner.getElement(MockUtils.Count.class.getName()).toString()).isEqualTo(MockUtils.Count.class.getName());
        assertThat(importScanner.getElement("(long,java.lang.String)java.util.List<cop.raml.mocks.MockUtils$Count>").toString())
                .isEqualTo(MockUtils.Count.class.getName());
    }

    @Test(groups = "getElementByString")
    public void shouldReturnCachedElementWhenImportContainsElement() throws Exception {
        Map<String, TypeElement> rootElements = getRootElements();
        Map<String, Map<String, TypeElement>> imports = getImports();
        TypeElement classElement = (TypeElement)createElementWithClassRoot(One.class, "getId", null).getEnclosingElement();
        TypeElementMock importClassElement = MockUtils.createElement(Two.class);

        rootElements.put(importClassElement.toString(), importClassElement);
        assertThat(importScanner.getElement(importClassElement.toString())).isSameAs(importClassElement);

        importScanner.setCurrentElement(classElement);
        assertThat(importScanner.getElement(importClassElement.toString())).isSameAs(importClassElement);

        imports.put(classElement.toString(), new HashMap<>());
        assertThat(importScanner.getElement(importClassElement.toString())).isSameAs(importClassElement);

        imports.get(classElement.toString()).put(importClassElement.toString(), classElement);
        assertThat(rootElements.get(classElement.toString())).isNotSameAs(imports.get(classElement.toString()).get(importClassElement.toString()));
        assertThat(importScanner.getElement(importClassElement.toString())).isSameAs(classElement);
    }

    @SuppressWarnings("serial")
    @Test(groups = "getElementByString")
    public void shouldReturnRootElementWhenNoImportsAndRootElementExist() throws Exception {
        Map<String, TypeElement> rootElements = getRootElements();
        TypeElementMock element = MockUtils.createElement(Two.class);

        rootElements.put(element.toString(), element);
        assertThat(importScanner.getElement(element.toString())).isSameAs(element);
    }

    @Test(groups = "getElementByString")
    public void shouldReturnFoundRootElementWhenSmallClassName() throws Exception {
        TypeElement expected = MockUtils.createElement(One.class);
        getRootElements().put(expected.toString(), expected);
        assertThat(importScanner.getElement(One.class.getSimpleName())).isSameAs(expected);
    }

    @Test(groups = "getElementByString")
    public void shouldReturnElementWhenFromLangPackage() throws Exception {
        assertThat(getRootElements()).isEmpty();

        TypeElement expected = MockUtils.createElement(String.class);
        MockUtils.setImportScannerElement(expected);
        assertThat(importScanner.getElement(String.class.getSimpleName())).isSameAs(expected);
        assertThat(getRootElements()).hasSize(1);
    }

    @Test(groups = "getElementByString")
    public void shouldReturnNullWhenNoImportFoundForString() {
        assertThat(importScanner.getElement(One.class.getSimpleName())).isNull();
    }

    @Test(groups = "getElementByTagLink")
    public void shouldReturnNullWhenTagLinkIsNull() {
        assertThat(importScanner.getElement(TagLink.NULL)).isNull();
    }

    @Test(groups = "getElementByTagLink")
    public void shouldReturnClassElementWhenClassTagLink() throws Exception {
        TypeElement expected = MockUtils.createElement(One.class);
        getRootElements().put(expected.toString(), expected);
        assertThat(importScanner.getElement(TagLink.create(One.class.getSimpleName(), null))).isSameAs(expected);
    }

    @Test(groups = "getElementByTagLink")
    public void shouldReturnVariableElementWhenVariableTagLink() throws Exception {
        TypeElement element = MockUtils.createElement(Three.class);
        getRootElements().put(element.toString(), element);
        Element actual = importScanner.getElement(TagLink.create(Three.class.getSimpleName(), "aaa"));
        assertThat(actual).isInstanceOf(VariableElement.class);
        assertThat(actual.getSimpleName().toString()).isEqualTo("aaa");
    }

    @Test(groups = "getElementByTagLink")
    public void shouldReturnNullWhenNoImportFoundForTagLink() throws Exception {
        TypeElement element = MockUtils.createElement(Three.class);
        getRootElements().put(element.toString(), element);
        assertThat(importScanner.getElement(TagLink.create(Three.class.getSimpleName(), "ccc"))).isNull();
    }

    @Test(groups = "getElementByTypeMirror")
    public void shouldIgnoreWhenTypeMirrorIsNull() {
        assertThat(importScanner.getElement((TypeMirror)null)).isNull();
    }

    @Test(groups = "getElementByTypeMirror")
    public void shouldReturnElementWhenTypeMirrorIsSet() throws Exception {
        TypeElement expected = MockUtils.createElement(One.class);
        getRootElements().put(expected.toString(), expected);
        assertThat(importScanner.getElement(new TypeMirrorMock(expected))).isSameAs(expected);
    }

    @Test
    public void testAddImport() throws Exception {
        assertThat(getImports()).isEmpty();

        TypeElement one = MockUtils.createElement(One.class);
        TypeElement two = MockUtils.createElement(Two.class);
        TypeElement three = MockUtils.createElement(Three.class);

        addImport("controller", one.toString(), one);
        addImport("controller", two.toString(), two);
        addImport("controller", three.toString(), three);

        addImport("service", one.toString(), one);
        addImport("service", two.toString(), two);
        addImport("service", three.toString(), three);

        Map<String, Map<String, TypeElement>> imports = getImports();
        assertThat(imports).hasSize(2);
        assertThat(imports.keySet()).containsOnly("controller", "service");

        for (String className : Arrays.asList("controller", "service")) {
            Map<String, TypeElement> map = imports.get(className);
            assertThat(map).hasSize(3);
            assertThat(map.keySet()).containsOnly(one.toString(), two.toString(), three.toString());
            assertThat(map.get(one.toString())).isSameAs(one);
            assertThat(map.get(two.toString())).isSameAs(two);
            assertThat(map.get(three.toString())).isSameAs(three);
        }
    }

    @Test(groups = "setCurrentElement")
    public void shouldIgnoreElementWhenTypeNotDeclaredAndError() throws Exception {
        TypeElementMock element = createClassRootElement(One.class).setQualifiedName(NameMock.ERROR_MARKER);
        assertThatThrownBy(() -> importScanner.setCurrentElement(element)).isInstanceOf(Exception.class);
        assertThat(getImports()).isEmpty();

        ((TypeMirrorMock)element.asType()).setKind(TypeKind.ERROR);
        assertThatThrownBy(() -> importScanner.setCurrentElement(element)).isInstanceOf(Exception.class);
        assertThat(getImports()).isEmpty();

        ((TypeMirrorMock)element.asType()).setKind(TypeKind.EXECUTABLE);
        importScanner.setCurrentElement(element);
        assertThat(getImports()).isEmpty();
    }

    @SuppressWarnings("serial")
    @Test(groups = "setCurrentElement")
    public void shouldIgnoreProcessWhenImportsContainsElement() throws Exception {
        TypeElement element = createClassRootElement(One.class);
        getImports().put(One.class.getName(), new HashMap<String, TypeElement>() {{
            put(element.toString(), element);
        }});

        assertThat(getClassName()).isNull();
        importScanner.setCurrentElement(element);
        assertThat(getImports()).hasSize(1);
    }

    @Test(groups = "setCurrentElement")
    public void shouldAddAllRootElementsWhenUseStarInImport() throws Exception {
        Map<String, Map<String, TypeElement>> imports = getImports();
        Map<String, TypeElement> rootElements = getRootElements();

        assertThat(imports).isEmpty();
        TypeElementMock classElement = createClassRootElement(One.class);

        importScanner.setCurrentElement(classElement);
        assertThat(imports).isEmpty();
        assertThat(getClassName()).isEqualTo(classElement.toString());

        clearClassName();
        assertThat(getClassName()).isNull();

        TypeElementMock getMappingElement = MockUtils.createElement(GetMapping.class);
        TypeElementMock postMappingElement = MockUtils.createElement(PostMapping.class);
        TypeElementMock putMappingElement = MockUtils.createElement(PutMapping.class);

        rootElements.put(getMappingElement.toString(), getMappingElement);
        rootElements.put(postMappingElement.toString(), postMappingElement);
        rootElements.put(putMappingElement.toString(), putMappingElement);

        assertThat(SorcererJavacUtils.getImports(classElement)).isEmpty();
        ((ElementsMock)ThreadLocalContext.getElementUtils()).addImport("org.springframework.web.bind.annotation.*");
        importScanner.setCurrentElement(classElement);
        assertThat(imports).hasSize(1);
        assertThat(imports.get(One.class.getName())).hasSize(3);
        assertThat(getClassName()).isEqualTo(classElement.toString());
    }

    @Test(groups = "setCurrentElement")
    public void shouldAddRootElementWhenImportRootElement() throws Exception {
        Map<String, Map<String, TypeElement>> imports = getImports();
        Map<String, TypeElement> rootElements = getRootElements();
        TypeElementMock expected = createClassRootElement(GetMapping.class);

        rootElements.put(expected.toString(), expected);
        ((ElementsMock)ThreadLocalContext.getElementUtils()).addImport(GetMapping.class.getName());

        assertThat(imports).isEmpty();
        assertThat(getClassName()).isNull();

        importScanner.setCurrentElement(expected);
        assertThat(imports).hasSize(1);
        assertThat(imports.get(GetMapping.class.getName())).hasSize(1);
        assertThat(imports.get(GetMapping.class.getName()).get(GetMapping.class.getName())).isSameAs(expected);
    }

    @Test(groups = "setCurrentElement")
    public void shouldNotAddNullElementWhenElementIsNotFound() throws Exception {
        Map<String, Map<String, TypeElement>> imports = getImports();
        TypeElementMock element = createClassRootElement(GetMapping.class);

        ((ElementsMock)ThreadLocalContext.getElementUtils()).addImport(element.toString());
        assertThat(imports).isEmpty();
        assertThat(getClassName()).isNull();

        importScanner.setCurrentElement(element);
        assertThat(imports).isEmpty();
        assertThat(getClassName()).isEqualTo(element.toString());
    }

    @Test(groups = "setCurrentElement")
    public void shouldSkipElementsWhenConfigHasSkipListAndImportExists() throws Exception {
        Map<String, Map<String, TypeElement>> imports = getImports();
        ElementsMock elements = (ElementsMock)ThreadLocalContext.getElementUtils();
        TypeElementMock element = createClassRootElement(GetMapping.class);

        elements.addImport(element.toString());
        assertThat(imports).isEmpty();
        assertThat(getClassName()).isNull();

        ((RoundEnvironmentMock)ThreadLocalContext.getRoundEnv()).addRootElement(element);
        elements.addTypeElement(element);

        importScanner.setCurrentElement(element);
        assertThat(imports).isEmpty();
        assertThat(getClassName()).isEqualTo(element.toString());
    }

    @Test(groups = "setCurrentElement")
    public void shouldAddTpImportAndRootElementsWhenSetCurrentElement() throws Exception {
        Map<String, Map<String, TypeElement>> imports = getImports();
        Map<String, TypeElement> rootElements = getRootElements();
        ElementsMock elements = (ElementsMock)ThreadLocalContext.getElementUtils();
        TypeElementMock element = createClassRootElement(One.class);

        elements.addImport(element.toString());
        assertThat(imports).isEmpty();
        assertThat(rootElements).isEmpty();
        assertThat(getClassName()).isNull();

        ((RoundEnvironmentMock)ThreadLocalContext.getRoundEnv()).addRootElement(element);
        elements.addTypeElement(element);

        importScanner.setCurrentElement(element);
        assertThat(imports).hasSize(1);
        assertThat(imports.get(One.class.getName())).hasSize(1);
        assertThat(rootElements).hasSize(1);
        assertThat(rootElements.get(One.class.getName())).isSameAs(element);
        assertThat(getClassName()).isEqualTo(element.toString());
    }

    private Map<String, TypeElement> getRootElements() throws Exception {
        Field field = ImportScanner.class.getDeclaredField("rootElements");
        field.setAccessible(true);
        return (Map<String, TypeElement>)field.get(importScanner);
    }

    private Set<String> getTypes() throws Exception {
        Field field = ImportScanner.class.getDeclaredField("types");
        field.setAccessible(true);
        return (Set<String>)field.get(importScanner);
    }

    private String getClassName() throws Exception {
        Field field = ImportScanner.class.getDeclaredField("className");
        field.setAccessible(true);
        return (String)field.get(importScanner);
    }

    private void clearClassName() throws Exception {
        Field field = ImportScanner.class.getDeclaredField("className");
        field.setAccessible(true);
        field.set(importScanner, null);
    }

    private Map<String, Map<String, TypeElement>> getImports() throws Exception {
        Field field = ImportScanner.class.getDeclaredField("imports");
        field.setAccessible(true);
        return (Map<String, Map<String, TypeElement>>)field.get(importScanner);
    }

    private void addAcceptedType(String str, DirectoryStream.Filter<String> filter) throws Exception {
        Method method = ImportScanner.class.getDeclaredMethod("addAcceptedType", String.class, DirectoryStream.Filter.class);
        method.setAccessible(true);
        method.invoke(importScanner, str, filter);
    }

    private TypeElement getOneRootElementWithName(String className) throws Exception {
        try {
            Method method = ImportScanner.class.getDeclaredMethod("getOneRootElementWithName", String.class);
            method.setAccessible(true);
            return (TypeElement)method.invoke(importScanner, className);
        } catch(InvocationTargetException e) {
            throw (Exception)e.getTargetException();
        }
    }

    private void addImport(String className, String importClassName, TypeElement importClassType) throws Exception {
        Method method = ImportScanner.class.getDeclaredMethod("addImport", String.class, String.class, TypeElement.class);
        method.setAccessible(true);
        method.invoke(importScanner, className, importClassName, importClassType);
    }

    // ========== data ==========

    @SuppressWarnings("EmptyClass")
    private static class One {
    }

    @SuppressWarnings("EmptyClass")
    private static class Two {
    }

    @SuppressWarnings({ "EmptyClass", "unused" })
    private static class Three {
        int aaa;
        int bbb;
    }

    // ========== static ==========

    private static Element getClassRootElement(Element element) throws Exception {
        return ReflectionUtils.invokeStaticMethod(ImportScanner.class, "getClassRootElement", Element.class, element);
    }

    private static boolean filter(String str, DirectoryStream.Filter<String> filter) throws Exception {
        return ReflectionUtils.invokeStaticMethod(ImportScanner.class, "filter", String.class, DirectoryStream.Filter.class, str, filter);
    }

    private static ExecutableElementMock createExecutableElement(String name, TypeKind returnKind) throws ClassNotFoundException {
        return new ExecutableElementMock(name, MockUtils.createElement(One.class).asType())
                .setReturnType(((TypeMirrorMock)MockUtils.createElement(Two.class).asType()).setKind(returnKind).setName(name));
    }

    private static ExecutableElementMock createElementWithClassRoot(Class<?> cls, String methodName, ElementKind kind)
            throws ClassNotFoundException {
        PackageElementMock packageElement = new PackageElementMock();

        if (kind != null)
            packageElement.setKind(kind);

        return new ExecutableElementMock(methodName + "()", MockUtils.createMethodElement(methodName).asType())
                .setEnclosingElement(MockUtils.createElement(cls).setEnclosingElement(packageElement));
    }

    private static TypeElementMock createClassRootElement(Class<?> cls) throws ClassNotFoundException {
        return MockUtils.createElement(cls).setKind(ElementKind.PACKAGE);
    }

    private static TypeParameterElementMock createTypeParameterElement(String name, TypeKind kind) throws ClassNotFoundException {
        return new TypeParameterElementMock(name).setType(((TypeMirrorMock)MockUtils.createElement(One.class).asType()).setKind(kind));
    }

    private static VariableElementMock createVariable(String name, TypeKind kind) throws ClassNotFoundException {
        return new VariableElementMock(name, ((TypeMirrorMock)MockUtils.createElement(Three.class).asType()).setKind(kind));
    }
}
