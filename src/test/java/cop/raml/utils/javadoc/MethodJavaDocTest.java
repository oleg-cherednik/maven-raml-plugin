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
package cop.raml.utils.javadoc;

import cop.raml.TestUtils;
import cop.raml.processor.Config;
import cop.raml.processor.exceptions.RamlProcessingException;
import cop.raml.mocks.MessagerMock;
import cop.raml.utils.ThreadLocalContext;
import cop.raml.utils.javadoc.tags.TagLink;
import cop.raml.utils.javadoc.tags.TagParam;
import cop.raml.utils.javadoc.tags.TagReturn;
import cop.raml.mocks.MockUtils;
import org.springframework.http.HttpStatus;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Oleg Cherednik
 * @since 12.12.2016
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public class MethodJavaDocTest {
    @BeforeMethod
    private void initContext() {
        MockUtils.initThreadLocalContext();
    }

    @BeforeMethod
    private void clearContext() {
        ThreadLocalContext.remove();
    }

    @Test
    public void checkNullObject() {
        assertThat(MethodJavaDoc.NULL.getText()).isNull();
        assertThat(MethodJavaDoc.NULL.getParams()).isSameAs(Collections.emptyMap());
        assertThat(MethodJavaDoc.NULL.getReturn()).isSameAs(TagReturn.NULL);
    }

    @Test
    public void testBlankDoc() {
        assertThat(MethodJavaDoc.create(null)).isSameAs(MethodJavaDoc.NULL);
        assertThat(MethodJavaDoc.create(Collections.emptyList())).isSameAs(MethodJavaDoc.NULL);
    }

    @Test
    public void testOneLineDoc() {
        MethodJavaDoc javaDoc = MethodJavaDoc.create(Collections.singletonList("Retrieve project with given projectId"));

        assertThat(javaDoc).isNotNull();
        assertThat(javaDoc.getText()).isEqualTo("Retrieve project with given projectId");
        assertThat(javaDoc.getParams()).isSameAs(Collections.emptyMap());
        assertThat(javaDoc.getReturn()).isSameAs(TagReturn.NULL);
    }

    @Test
    public void shouldReadCompleteDoc() {
        List<String> doc = Arrays.asList(
                "Retrieve project with given projectId",
                "",
                "@param projectId project id",
                "@return Project object",
                "@throws GeneralException in case of any error");
        MethodJavaDoc javaDoc = MethodJavaDoc.create(doc);

        assertThat(javaDoc).isNotNull();
        assertThat(javaDoc.getText()).isEqualTo("Retrieve project with given projectId");
        assertThat(javaDoc.getParams()).hasSize(1);

        TagParam param = javaDoc.getParam("projectId");
        assertThat(param).isNotNull();
        assertThat(param.getText()).isEqualTo("project id");

        TagReturn ret = javaDoc.getReturn();
        assertThat(ret).isNotNull();
        assertThat(ret.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(ret.getText()).isEqualTo("Project object");
        assertThat(ret.getLink()).isSameAs(TagLink.NULL);
        assertThat(ret.isArray()).isFalse();
    }

    @Test
    public void testGetParam() {
        List<String> doc = Arrays.asList(
                "Retrieve project with given projectId",
                "",
                "@param projectId project id",
                "@param modelId model id");
        MethodJavaDoc javaDoc = MethodJavaDoc.create(doc);

        assertThat(javaDoc.getParam(null)).isSameAs(TagParam.NULL);
        assertThat(javaDoc.getParam("")).isSameAs(TagParam.NULL);
        assertThat(javaDoc.getParam("   ")).isSameAs(TagParam.NULL);
        assertThat(javaDoc.getParam("paramId")).isSameAs(TagParam.NULL);
        assertThat(javaDoc.getParam("projectId").getText()).isEqualTo("project id");
        assertThat(javaDoc.getParam("modelId").getText()).isEqualTo("model id");
    }

    @Test
    public void shouldReadReturnDoc() {
        TagReturn obj = MethodJavaDoc.create(Collections.singletonList("@return this is a text")).getReturn();

        assertThat(obj).isNotSameAs(TagReturn.NULL);
        assertThat(obj.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(obj.getText()).isEqualTo("this is a text");
        assertThat(obj.getLink()).isSameAs(TagLink.NULL);
        assertThat(obj.isArray()).isFalse();
    }

    @Test
    public void shouldReadEmptyTextReturnDocAsNullObject() {
        assertThat(MethodJavaDoc.create(Collections.singletonList("@return")).getReturn()).isSameAs(TagReturn.NULL);
    }

    @Test
    public void shouldReadMultiLineReturnDoc() {
        List<String> doc = Arrays.asList(
                "@return one",
                "        two",
                "        three");
        TagReturn obj = MethodJavaDoc.create(doc).getReturn();

        assertThat(obj).isNotSameAs(TagReturn.NULL);
        assertThat(obj.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(obj.getText()).isEqualTo("one\ntwo\nthree");
        assertThat(obj.getLink()).isSameAs(TagLink.NULL);
        assertThat(obj.isArray()).isFalse();
    }

    @Test
    public void shouldReadFirstEmptyLineReturnDoc() {
        List<String> doc = Arrays.asList(
                "@return",
                "         one",
                "         <br>",
                "         two",
                "         <p>",
                "         three");
        TagReturn obj = MethodJavaDoc.create(doc).getReturn();

        assertThat(obj).isNotSameAs(TagReturn.NULL);
        assertThat(obj.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(obj.getText()).isEqualTo("one\n\ntwo\n\n\nthree");
        assertThat(obj.getLink()).isSameAs(TagLink.NULL);
        assertThat(obj.isArray()).isFalse();
    }

    @Test(groups = "getParams")
    public void shouldReadParamDoc() {
        Map<String, TagParam> params = MethodJavaDoc.create(Collections.singletonList("@param paramId this is a text")).getParams();

        assertThat(params).hasSize(1);

        TagParam param = params.get("paramId");
        assertThat(param.getText()).isEqualTo("this is a text");
        assertThat(param.getName()).isNull();
        assertThat(param.getPattern()).isNull();
        assertThat(param.getEnum()).isNull();
        assertThat(param.getDefault()).isNull();
        assertThat(param.getExample()).isNull();
        assertThat(param.getLink()).isSameAs(TagLink.NULL);
        assertThat(param.isRequired()).isTrue();
    }

    @Test
    public void shouldRemoveMacroFromParamText() {
        throw new SkipException("Skipping the test case");
    }

    @Test(groups = "getParams")
    public void shouldReadParamWithoutTextAsParamNullObject() {
        Map<String, TagParam> params = MethodJavaDoc.create(Collections.singletonList("@param paramId")).getParams();

        assertThat(params).hasSize(1);
        assertThat(params.get("paramId")).isSameAs(TagParam.NULL);
    }

    @Test(groups = "getParams")
    public void shouldNodAddEmptyParam() {
        Map<String, TagParam> params = MethodJavaDoc.create(Collections.singletonList("@param")).getParams();
        assertThat(params).isSameAs(Collections.emptyMap());
    }

    @Test(groups = "getParams")
    public void shouldReadMacroName() {
        Map<String, TagParam> params = MethodJavaDoc.create(Collections.singletonList("@param paramId aaa {@name bbb}")).getParams();

        assertThat(params).hasSize(1);

        TagParam param = params.get("paramId");
        assertThat(param.getText()).isEqualTo("aaa");
        assertThat(param.getName()).isEqualTo("bbb");
        assertThat(param.getPattern()).isNull();
        assertThat(param.getEnum()).isNull();
        assertThat(param.getDefault()).isNull();
        assertThat(param.getExample()).isNull();
        assertThat(param.getLink()).isSameAs(TagLink.NULL);
        assertThat(param.isRequired()).isTrue();
    }

    @Test(groups = "getParams")
    public void shouldReadMacroPattern() {
        Map<String, TagParam> params = MethodJavaDoc.create(Collections.singletonList("@param paramId aaa {@pattern bbb}")).getParams();

        assertThat(params).hasSize(1);

        TagParam param = params.get("paramId");
        assertThat(param.getText()).isEqualTo("aaa");
        assertThat(param.getName()).isNull();
        assertThat(param.getPattern()).isEqualTo("bbb");
        assertThat(param.getEnum()).isNull();
        assertThat(param.getDefault()).isNull();
        assertThat(param.getExample()).isNull();
        assertThat(param.getLink()).isSameAs(TagLink.NULL);
        assertThat(param.isRequired()).isTrue();
    }

    @Test(groups = "getParams")
    public void shouldReadMacroEnumUsingDifferentSeparator() {
        Map<String, TagParam> params = MethodJavaDoc.create(Collections.singletonList("@param paramId aaa {@enum [bbb;ccc;ddd]}")).getParams();

        assertThat(params).hasSize(1);

        TagParam param = params.get("paramId");
        assertThat(param.getText()).isEqualTo("aaa");
        assertThat(param.getName()).isNull();
        assertThat(param.getPattern()).isNull();
        assertThat(param.getEnum()).isEqualTo("[bbb,ccc,ddd]");
        assertThat(param.getDefault()).isNull();
        assertThat(param.getExample()).isNull();
        assertThat(param.getLink()).isSameAs(TagLink.NULL);
        assertThat(param.isRequired()).isTrue();
    }

    @Test(groups = "getParams")
    public void shouldReadMacroDefault() {
        Map<String, TagParam> params = MethodJavaDoc.create(Collections.singletonList("@param paramId aaa {@default bbb}")).getParams();

        assertThat(params).hasSize(1);

        TagParam param = params.get("paramId");
        assertThat(param.getText()).isEqualTo("aaa");
        assertThat(param.getName()).isNull();
        assertThat(param.getPattern()).isNull();
        assertThat(param.getEnum()).isNull();
        assertThat(param.getDefault()).isEqualTo("bbb");
        assertThat(param.getExample()).isNull();
        assertThat(param.getLink()).isSameAs(TagLink.NULL);
        assertThat(param.isRequired()).isTrue();
    }

    @Test(groups = "getParams")
    public void shouldReadMacroExample() {
        Map<String, TagParam> params = MethodJavaDoc.create(Collections.singletonList("@param paramId aaa {@example bbb}")).getParams();

        assertThat(params).hasSize(1);

        TagParam param = params.get("paramId");
        assertThat(param.getText()).isEqualTo("aaa");
        assertThat(param.getName()).isNull();
        assertThat(param.getPattern()).isNull();
        assertThat(param.getEnum()).isNull();
        assertThat(param.getDefault()).isNull();
        assertThat(param.getExample()).isEqualTo("bbb");
        assertThat(param.getLink()).isSameAs(TagLink.NULL);
        assertThat(param.isRequired()).isTrue();
    }

    @Test
    public void shouldReadMultiLineMacroExample() {
        throw new SkipException("Skipping the test case");
    }

    @Test(groups = "getParams")
    public void shouldReadMacroLink() {
        Map<String, TagParam> params = MethodJavaDoc.create(Collections.singletonList("@param paramId aaa {@link class#param}")).getParams();

        assertThat(params).hasSize(1);

        TagParam param = params.get("paramId");
        assertThat(param.getText()).isEqualTo("aaa");
        assertThat(param.getName()).isNull();
        assertThat(param.getPattern()).isNull();
        assertThat(param.getEnum()).isNull();
        assertThat(param.getDefault()).isNull();
        assertThat(param.getExample()).isNull();
        assertThat(param.getLink()).isEqualTo(TagLink.create("class", "param"));
        assertThat(param.isRequired()).isTrue();
    }

    @Test(groups = "getParams")
    public void shouldReadMacroOptional() {
        Map<String, TagParam> params = MethodJavaDoc.create(Collections.singletonList("@param paramId aaa {@optional}")).getParams();

        assertThat(params).hasSize(1);

        TagParam param = params.get("paramId");
        assertThat(param.getText()).isEqualTo("aaa");
        assertThat(param.getName()).isNull();
        assertThat(param.getPattern()).isNull();
        assertThat(param.getEnum()).isNull();
        assertThat(param.getDefault()).isNull();
        assertThat(param.getExample()).isNull();
        assertThat(param.getLink()).isSameAs(TagLink.NULL);
        assertThat(param.isRequired()).isFalse();
    }

    @Test(groups = "getParams")
    public void shouldReadFewParametersWithOrdering() {
        List<String> doc = Arrays.asList(
                "@param paramA aaa",
                "@param paramB bbb",
                "@param paramC ccc");
        Map<String, TagParam> params = MethodJavaDoc.create(doc).getParams();

        assertThat(params).hasSize(3);
        assertThat(params.keySet()).containsExactly("paramA", "paramB", "paramC");
    }

    @Test(groups = "getParams")
    public void shouldReadMultiLineParamDoc() {
        List<String> doc = Arrays.asList(
                "@param paramId",
                "                aaa",
                "                one,two",
                "                three,four",
                "",
                "                five,six");

        Map<String, TagParam> params = MethodJavaDoc.create(doc).getParams();
        assertThat(params).hasSize(1);

        TagParam param = params.get("paramId");
        assertThat(param.getText()).isEqualTo("aaa\none,two\nthree,four\n\nfive,six");
        assertThat(param.getName()).isNull();
        assertThat(param.getPattern()).isNull();
        assertThat(param.getEnum()).isNull();
        assertThat(param.getDefault()).isNull();
        assertThat(param.getExample()).isNull();
        assertThat(param.getLink()).isSameAs(TagLink.NULL);
        assertThat(param.isRequired()).isTrue();
    }

    @Test(groups = "getParams")
    public void shouldThrowExceptionWhenParameterDuplication() {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        List<String> doc = Arrays.asList(
                "@param paramId aaa",
                "@param paramId bbb");

        assertThatThrownBy(() -> MethodJavaDoc.create(doc).getParams()).isExactlyInstanceOf(RamlProcessingException.class);
    }

    @Test
    public void shouldThrowExceptionWhenParameterLinksToItself() {
        String str = "aaa {@link class#param} bbb";
        TagLink link = TagLink.create("class", "param");

        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        assertThatThrownBy(() -> MethodJavaDoc.createTagParam(str, link)).isInstanceOf(RamlProcessingException.class);
    }

    @Test
    public void shouldWaringWhenParameterDuplication() {
        String str = TestUtils.joinStrings("aaa {@link class#param} bbb");
        TagLink link = TagLink.create("class", "param");
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(false).build());

        TagParam param = MethodJavaDoc.createTagParam(str, link);
        assertThat(param).isNotSameAs(TagParam.NULL);
        assertThat(param.getText()).isEqualTo("aaa bbb");
        assertThat(param.getLink()).isEqualTo(TagLink.create("class", "param"));
        assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNotEmpty();
    }

    //    @Test
    // TODO check this test, remove if not needed
    public void testGetText() {
        String doc = TestUtils.joinStrings(
                "Retrieve project with given {@code projectId}",
                "<p>",
                "Main purpose of this text is @param to check * multi * line comment<br>",
                "with different @return new line symbols.",
                "",
                "@param projectId project id",
                "@param modelId model id",
                "   this is some additional comment to the param modelId",
                "@return {@link Project} object",
                "@throws GeneralException in case of any error");

        Pattern pattern = Pattern.compile("^(?s)(.*)@param|@return|@throws");
        Matcher matcher = pattern.matcher(doc);

        while (matcher.find()) {
            System.out.println("---");
            System.out.println(matcher.group(1));
        }

//        assertThat(JavaDocUtils.getText(doc)).isEqualTo(TestUtils.joinStrings(
//                "Retrieve project with given {@code projectId}",
//                "",
//                "Main purpose of this text is @param to check * multi * line comment",
//                "with different @return new line symbols."));
    }
}
