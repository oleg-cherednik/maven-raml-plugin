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
package cop.raml.processor;

import cop.raml.RamlVersion;
import cop.raml.TestUtils;
import cop.raml.mocks.AnnotationMirrorMock;
import cop.raml.mocks.AnnotationValueMock;
import cop.raml.mocks.DeclaredTypeMock;
import cop.raml.mocks.ElementMock;
import cop.raml.mocks.ExecutableElementMock;
import cop.raml.mocks.FilerMock;
import cop.raml.mocks.ImportScannerMock;
import cop.raml.mocks.MessagerMock;
import cop.raml.mocks.MockUtils;
import cop.raml.mocks.ProcessingEnvironmentMock;
import cop.raml.mocks.RoundEnvironmentMock;
import cop.raml.mocks.TypeElementMock;
import cop.raml.mocks.TypeMirrorMock;
import cop.raml.mocks.VariableElementMock;
import cop.raml.mocks.annotations.PathVariableMock;
import cop.raml.mocks.annotations.RequestBodyMock;
import cop.raml.mocks.annotations.RequestMappingMock;
import cop.raml.mocks.annotations.RequestParamMock;
import cop.raml.mocks.annotations.RestControllerMock;
import cop.raml.processor.exceptions.RamlProcessingException;
import cop.raml.processor.rest.RestImpl;
import cop.raml.processor.rest.RestImplMock;
import cop.raml.utils.ImportScanner;
import cop.raml.utils.ReflectionUtils;
import cop.raml.utils.ThreadLocalContext;
import cop.raml.utils.Utils;
import cop.raml.utils.javadoc.MethodJavaDoc;
import cop.raml.utils.javadoc.tags.TagLink;
import cop.raml.utils.javadoc.tags.TagParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.testng.SkipException;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Pattern;

import static cop.raml.RamlVersion.RAML_0_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Oleg Cherednik
 * @since 11.12.2016
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public class RestProcessorTest extends AbstractProcessorTest {
    private static final String SIMPLE_RAML = TestUtils.joinStrings(
            "#%RAML 0.8",
            "---",
            "title: title",
            "baseUri: www.com",
            "version: v1",
            "mediaType: application/json",
            "documentation:",
            "  - title: docTitle",
            "    content: |",
            "      docContent");
    private static final String SIMPLE_CONFIG = TestUtils.joinStrings(
            "api:",
            "  title: This is title",
            "  baseUri: www.com/{version}/services",
            "  version: v1",
            "  doc:",
            "    title: This is doc title",
            "    content: |-",
            "      This is doc content",
            "raml:",
            "  version: '0.8'");

    private RestProcessor processor;

    @BeforeMethod(groups = "context")
    private void initContext() {
        MockUtils.initThreadLocalContext();
        (processor = new RestProcessor()).init(ThreadLocalContext.getProcessingEnv());
    }

    @AfterMethod(groups = "context")
    private void clearContext() {
        ThreadLocalContext.remove();
        processor = null;
    }

    @Test
    public void shouldWorkCorrectlyForNoCommentedSources_v08() throws IOException, URISyntaxException {
        File dir = getResourceAsFile("spring/empty");
        ThreadLocalContext.setConfig(Config.builder()
                                           .apiMediaType("application/json")
                                           .ramlDev(true)
                                           .ramlShowExample(false)
                                           .ramlVersion(RAML_0_8)
                                           .build());

        runAnnotationProcessor(dir);

//        System.out.println("----------");
//        System.out.println(DevUtils.getRaml());
//        System.out.println("----------");

        String actual = DevUtils.getRaml();
        String expected = StringUtils.trim(TestUtils.getResourceAsString("v0.8/empty.raml"));
        TestUtils.assertThatEquals(actual, expected);
    }

    @Test
    public void shouldWorkCorrectlyForCommentedSources_v08() throws IOException, URISyntaxException {
        File dir = getResourceAsFile("spring/general");
        ThreadLocalContext.setConfig(Config.builder()
                                           .apiMediaType("application/json")
                                           .ramlDev(true)
                                           .ramlShowExample(false)
                                           .ramlVersion(RAML_0_8)
                                           .build());

        runAnnotationProcessor(dir);

//        System.out.println("----------");
//        System.out.println(DevUtils.getRaml());
//        System.out.println("----------");
//        FileUtils.write(new File("wsdoc.raml"), DevUtils.getRaml(), StandardCharsets.UTF_8);

        String actual = DevUtils.getRaml();
        String expected = StringUtils.trim(TestUtils.getResourceAsString("v0.8/general.raml"));
        TestUtils.assertThatEquals(actual, expected);
    }

    @Test
    public void testConstant() {
        assertThat(RestProcessor.KEY_FILTER_REGEX).isEqualTo("filterRegex");
        assertThat(RestProcessor.KEY_FILE_NAME).isEqualTo("fileName");
        assertThat(RestProcessor.DEF_FILE_NAME).isEqualTo("api");
    }

    @Test(groups = { "context", "readQueryParameters" })
    public void shouldReadAnnotationWhenReadQueryParameter() throws Exception {
        RestMethod restMethod = new RestMethod("GET");
        assertThat(restMethod.getQueryParameters()).isEmpty();

        ExecutableElementMock methodElement = MockUtils.createExecutable("foo");
        methodElement.addParameter(createQueryParameter("par1", int.class, null, "666", true));
        methodElement.addParameter(createQueryParameter("par2", long.class, "par22", "777", false));
        methodElement.addParameter(createQueryParameter("par3", String.class, null, "aaa", true));
        methodElement.addParameter(MockUtils.createVariable("par4", String.class));
        methodElement.addParameter(MockUtils.createVariable("par5", String.class));

        readQueryParameters(processor, restMethod, methodElement, MethodJavaDoc.NULL);
        assertThat(restMethod.getQueryParameters()).hasSize(3);

        Iterator<Parameter> it = restMethod.getQueryParameters().iterator();
        Parameter parameter = it.next();
        assertThat(parameter.getName()).isEqualTo("par1");
        assertThat(parameter.getDef()).isEqualTo("666");
        assertThat(parameter.isRequired()).isTrue();
        assertThat(parameter.getType()).isEqualTo(Parameter.Type.INTEGER.getId());

        parameter = it.next();
        assertThat(parameter.getName()).isEqualTo("par22");
        assertThat(parameter.getDef()).isEqualTo("777");
        assertThat(parameter.isRequired()).isFalse();
        assertThat(parameter.getType()).isEqualTo(Parameter.Type.NUMBER.getId());

        parameter = it.next();
        assertThat(parameter.getName()).isEqualTo("par3");
        assertThat(parameter.getDef()).isEqualTo("aaa");
        assertThat(parameter.isRequired()).isTrue();
        assertThat(parameter.getType()).isEqualTo(Parameter.Type.STRING.getId());
    }

    @Test(groups = { "context", "readQueryParameters" })
    public void shouldOverrideAnnotationWhenReadQueryParameterWithJavaDoc() {
        throw new SkipException("Skipping the test case");
    }

    @Test(groups = { "context", "getLinkParamJavaDoc" })
    public void shouldReturnNullWhenParameterIsNotSet() throws Exception {
        assertThat(getLinkParamJavaDoc(TagParam.NULL)).isSameAs(TagParam.NULL);
    }

    @Test(groups = { "context", "getLinkParamJavaDoc" })
    public void shouldReturnNullAndWarningWhenParameterLinkedWithClass() throws Exception {
        TagParam param = TagParam.builder().link(TagLink.create("foo", null)).build();
        assertThat(getLinkParamJavaDoc(param)).isSameAs(TagParam.NULL);
        assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNotNull();
    }

    @Test(groups = { "context", "getLinkParamJavaDoc" })
    public void shouldReturnTagParamWhenParameterLinkedWithVariable() throws Exception {
        String name = MockUtils.Count.ONE.name();
        TagParam param = TagParam.builder().link(TagLink.create(MockUtils.Count.class.getSimpleName(), name)).build();
        TypeElementMock element = MockUtils.createElement(MockUtils.Count.class);

        for (ElementMock mock : (Iterable<ElementMock>)element.getEnclosedElements())
            if (name.equals(mock.getSimpleName().toString()))
                mock.setDocComment("{@example aaa}");

        ImportScannerMock importScanner = (ImportScannerMock)ThreadLocalContext.getImportScanner();
        importScanner.addElement(MockUtils.Count.class.getSimpleName(), element);

        TagParam link = getLinkParamJavaDoc(param);
        assertThat(link).isNotSameAs(TagParam.NULL);
        assertThat(link.getExample()).isEqualTo("aaa");
    }

    @Test(groups = "context")
    public void shouldReadAnnotationWhenReadUriParameter() throws Exception {
        Resource resource = new Resource("/{par1}/{par22}");
        assertThat(resource.getUriParameters()).isEmpty();

        ExecutableElementMock methodElement = MockUtils.createExecutable("foo");
        methodElement.addParameter(createUriParameter("par1", int.class, null, true));
        methodElement.addParameter(createUriParameter("par2", long.class, "par22", false));
        methodElement.addParameter(createUriParameter("par3", String.class, null, true));
        methodElement.addParameter(MockUtils.createVariable("par4", String.class));
        methodElement.addParameter(MockUtils.createVariable("par5", String.class));

        readUriParameters(processor, resource, methodElement, MethodJavaDoc.NULL);
        assertThat(resource.getUriParameters()).hasSize(2);

        Iterator<Parameter> it = resource.getUriParameters().iterator();
        Parameter parameter = it.next();
        assertThat(parameter.getName()).isEqualTo("par1");
        assertThat(parameter.isRequired()).isTrue();
        assertThat(parameter.getType()).isEqualTo(Parameter.Type.INTEGER.getId());
        assertThat(parameter.getExample()).isNull();
        assertThat(parameter.getDescription()).isNull();
        parameter.setDescription("descr");

        parameter = it.next();
        assertThat(parameter.getName()).isEqualTo("par22");
        assertThat(parameter.isRequired()).isFalse();
        assertThat(parameter.getType()).isEqualTo(Parameter.Type.NUMBER.getId());
        assertThat(parameter.getExample()).isNull();
        assertThat(parameter.getDescription()).isNull();

        readUriParameters(processor, resource, methodElement, MethodJavaDoc.NULL);
        assertThat(resource.getUriParameters()).hasSize(2);

        parameter = resource.getUriParameters().iterator().next();
        assertThat(parameter.getName()).isEqualTo("par1");
        assertThat(parameter.isRequired()).isTrue();
        assertThat(parameter.getType()).isEqualTo(Parameter.Type.INTEGER.getId());
        assertThat(parameter.getExample()).isNull();
        assertThat(parameter.getDescription()).isEqualTo("descr");

    }

    @Test(groups = { "context", "readUriParameters" })
    public void shouldOverrideAnnotationWhenReadUriParameterWithJavaDoc() {
        throw new SkipException("Skipping the test case");
    }

    @Test(groups = "context")
    public void shouldReadAnnotationWhenReadBody() throws Exception {
        RestMethod restMethod = new RestMethod("GET");
        assertThat(restMethod.getBodies()).isEmpty();

        ExecutableElementMock methodElement = MockUtils.createExecutable("foo");
        methodElement.addAnnotation(new RequestMappingMock().addConsumes(Utils.APPLICATION_XML));
        methodElement.addParameter(createBody("par1", true));
        methodElement.addParameter(createUriParameter("par2", long.class, "par22", false));
        methodElement.addParameter(MockUtils.createVariable("par3", String.class));
        readBody(processor, restMethod, methodElement);
        assertThat(restMethod.getBodies()).hasSize(1);

        methodElement = MockUtils.createExecutable("foo");
        methodElement.addParameter(createBody("par1", false));
        readBody(processor, restMethod, methodElement);
        assertThat(restMethod.getBodies()).hasSize(2);

        methodElement = MockUtils.createExecutable("foo");
        VariableElementMock var = createBody("par1", false);
        ((TypeMirrorMock)var.asType()).setName("multipart");
        methodElement.addParameter(var);
        readBody(processor, restMethod, methodElement);
        assertThat(restMethod.getBodies()).hasSize(3);

        ThreadLocalContext.setConfig(Config.builder().apiMediaType(Utils.TEXT_PLAIN).build());
        methodElement = MockUtils.createExecutable("foo");
        methodElement.addParameter(createBody("par1", true));
        readBody(processor, restMethod, methodElement);
        assertThat(restMethod.getBodies()).hasSize(3);

        Iterator<Body> it = restMethod.getBodies().iterator();
        Body body = it.next();
        assertThat(body.getMediaType()).isEqualTo(Utils.APPLICATION_XML);
        assertThat(body.isDef()).isFalse();
        assertThat(body.isRequired()).isTrue();

        body = it.next();
        assertThat(body.getMediaType()).isEqualTo(Utils.APPLICATION_JSON);
        assertThat(body.isDef()).isFalse();
        assertThat(body.isRequired()).isFalse();

        body = it.next();
        assertThat(body.getMediaType()).isEqualTo(Utils.MULTIPART_FORM_DATA);
        assertThat(body.isDef()).isFalse();
        assertThat(body.isRequired()).isFalse();
    }

    @Test(groups = "checkMediaTypes")
    public void shouldReturnSameArrayWhenCheckNotNullMediaTypes() throws Exception {
        assertThat(checkMediaTypes(Utils.arrApplicationJson())).isEqualTo(Utils.arrApplicationJson());
    }

    @Test(groups = { "context", "checkMediaTypes" })
    public void shouldThrowExceptionWhenArrayIsNullAndStopOnError() throws Exception {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(true).build());
        assertThatThrownBy(RestProcessorTest::checkMediaTypes).isExactlyInstanceOf(RamlProcessingException.class);
        assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNotBlank();
    }

    @Test(groups = { "context", "checkMediaTypes" })
    public void shouldReturnDefaultMediaTypeWhenArrayIsNullAndNoStopOnError() throws Exception {
        ThreadLocalContext.setConfig(Config.builder().ramlStopOnError(false).build());
        assertThat(checkMediaTypes()).isEqualTo(Utils.arrApplicationJson());
    }

    @Test(groups = { "context", "readResponse" })
    public void shouldReadMethodReturnTypeWhenReadResponse() throws Exception {
        RestMethod res = new RestMethod("GET");
        assertThat(res.getResponses()).isEmpty();

        ExecutableElementMock methodElement = MockUtils.createExecutable("foo");
        ((TypeMirrorMock)methodElement.asType()).setName(MockUtils.Count.class.getName());
        methodElement.addAnnotation(new RequestMappingMock().addProduces(Utils.APPLICATION_XML));
        MockUtils.setImportScannerElement(MockUtils.createElement(MockUtils.Count.class));

        readResponse(processor, res, methodElement, MethodJavaDoc.NULL);
        assertThat(res.getResponses()).hasSize(1);

        Iterator<Response> it = res.getResponses().iterator();
        Response response = it.next();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getBodies()).hasSize(1);
        assertThat(response.getBodies().iterator().next().getMediaType()).isEqualTo(Utils.APPLICATION_XML);
        // TODO to complete test do check example of type Count
    }

    @Test(groups = { "context", "readResponse" })
    public void shouldUseJavaDocTypeWHenReadResponse() throws Exception {
        RestMethod res = new RestMethod("GET");
        assertThat(res.getResponses()).isEmpty();

        ExecutableElementMock methodElement = MockUtils.createExecutable("foo");
        methodElement.addAnnotation(new RequestMappingMock().addProduces(Utils.APPLICATION_XML));
        MethodJavaDoc methodJavaDoc = MethodJavaDoc.create(Collections.singletonList("@return {@link String}"));
        MockUtils.setImportScannerElement(MockUtils.createElement(String.class));

        readResponse(processor, res, methodElement, methodJavaDoc);
        assertThat(res.getResponses()).hasSize(1);

        Iterator<Response> it = res.getResponses().iterator();
        Response response = it.next();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getBodies()).hasSize(1);
        assertThat(response.getBodies().iterator().next().getMediaType()).isEqualTo(Utils.APPLICATION_XML);
        // TODO to complete test do check example of type String
    }

    @Test
    public void testSetGeneralProperties() throws Exception {
        ThreadLocalContext.setConfig(Config.builder()
                                           .apiTitle("apiTitle")
                                           .doc("docTitle", "docContent")
                                           .apiBaseUri("www.com")
                                           .apiVersion("v666")
                                           .apiMediaType(Utils.APPLICATION_JSON).build());
        RestApi api = new RestApi();
        setGeneralProperties(api);

        assertThat(api.getTitle()).isEqualTo("apiTitle");
        assertThat(api.getDocTitle()).isEqualTo("docTitle");
        assertThat(api.getDocContent()).isEqualTo("docContent");
        assertThat(api.getBaseUri()).isEqualTo("www.com");
        assertThat(api.getVersion()).isEqualTo("v666");
        assertThat(api.getMediaType()).isEqualTo(Utils.APPLICATION_JSON);
    }

    @Test
    public void testAssemble() throws Exception {
        RestApi api = new RestApi();
        api.setTitle("title");
        api.setBaseUri("www.com");
        api.setMediaType(Utils.APPLICATION_JSON);
        api.setDocumentation("docTitle", "docContent");
        api.setVersion("v1");

        Writer out = new StringWriter();

        assemble(api, "raml_08_simple.ftl", out);
        TestUtils.assertThatEquals(out.toString(), SIMPLE_RAML);
    }

    @Test
    public void shouldReturnFilterRegexWhenOptionSet() throws Exception {
        ThreadLocalContext.setProcessingEnv(new ProcessingEnvironmentMock().addOptions(RestProcessor.KEY_FILTER_REGEX, "pattern"));
        assertThat(getFilterRegex().pattern()).isEqualTo("pattern");
    }

    @Test
    public void shouldReturnNullWhenFilterRegexOptionIsNotSet() throws Exception {
        ThreadLocalContext.setProcessingEnv(new ProcessingEnvironmentMock());
        assertThat(getFilterRegex()).isNull();
    }

    @Test
    public void shouldReturnFileNameWhenOptionSet() throws Exception {
        ThreadLocalContext.setProcessingEnv(new ProcessingEnvironmentMock().addOptions(RestProcessor.KEY_FILE_NAME, "file_name"));
        assertThat(getFileName()).isEqualTo("file_name.raml");
    }

    @Test
    public void shouldReturnDefaultFileNameWhenOptionIsNotSet() throws Exception {
        ThreadLocalContext.setProcessingEnv(new ProcessingEnvironmentMock());
        assertThat(getFileName()).isEqualTo(RestProcessor.DEF_FILE_NAME + ".raml");
    }

    @Test
    public void shouldReturnNullWhenConfigIsNotInEnvironment() throws Exception {
        ThreadLocalContext.setProcessingEnv(new ProcessingEnvironmentMock());
        assertThat(getConfig()).isSameAs(Config.NULL);
    }

    @Test
    public void shouldReturnConfigWhenYamlExists() throws Exception {
        FilerMock filer = new FilerMock();
        filer.getResource().setData(SIMPLE_CONFIG);

        ThreadLocalContext.setProcessingEnv(new ProcessingEnvironmentMock().setFiler(filer));

        Config config = getConfig();

        assertThat(config).isNotSameAs(Config.NULL);
        assertThat(config.apiTitle()).isEqualTo("This is title");
        assertThat(config.apiBaseUri()).isEqualTo("www.com/{version}/services");
        assertThat(config.apiVersion()).isEqualTo("v1");
        assertThat(config.docTitle()).isEqualTo("This is doc title");
        assertThat(config.docContent()).isEqualTo("This is doc content");
        assertThat(config.ramlVersion()).isSameAs(RAML_0_8);
    }

    @Test
    public void shouldReturnNullAndErrorMessageWhenYamlIsNotValid() throws Exception {
        FilerMock filer = new FilerMock();
        filer.getResource().setData(TestUtils.joinStrings("ERROR"));

        ThreadLocalContext.setProcessingEnv(new ProcessingEnvironmentMock().setFiler(filer));
        assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNull();
        assertThat(getConfig()).isSameAs(Config.NULL);
        assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNotNull();
    }

    @Test(groups = "context")
    public void testReadMethod() throws Exception {
        Resource resource = new Resource("/{par1}");
        assertThat(resource.getMethods()).isEmpty();

        ExecutableElementMock methodElement = MockUtils.createExecutable("foo");
        methodElement.addAnnotation(new RequestMappingMock().method(RequestMethod.GET).addProduces(Utils.APPLICATION_JSON));
        methodElement.addParameter(createBody("par1", true));
        methodElement.addParameter(createQueryParameter("par2", String.class, null, "aaa", true));

        MethodJavaDoc methodJavaDoc = MethodJavaDoc.create(Collections.singletonList("aaa"));

        readMethod(processor, resource, methodElement, methodJavaDoc);
        assertThat(resource.getMethods()).hasSize(1);

        RestMethod restMethod = resource.getMethods().iterator().next();
        assertThat(restMethod.getRequestMethod()).isEqualTo(RequestMethod.GET.name());
        assertThat(restMethod.getDescription()).isEqualTo("aaa");
        assertThat(restMethod.getQueryParameters()).hasSize(1);
        assertThat(restMethod.getBodies()).hasSize(1);
        assertThat(restMethod.getResponses()).hasSize(1);
    }

    @BeforeGroups("ramlVersion")
    public void addRamlVersionConstant() throws Exception {
        ReflectionUtils.addEnumConstant(RamlVersion.class, "RAML_SIMPLE", "<666>", "raml_08_simple.ftl", RamlVersion[]::new);
        ReflectionUtils.addEnumConstant(RamlVersion.class, "RAML_ERROR", "<777>", "raml_xx_simple.ftl", RamlVersion[]::new);
    }

    @AfterGroups("ramlVersion")
    public void removeRamlVersionConstant() throws Exception {
        ReflectionUtils.removeEnumConstant(RamlVersion.parseId("<666>"), RamlVersion[]::new);
        ReflectionUtils.removeEnumConstant(RamlVersion.parseId("<777>"), RamlVersion[]::new);
    }

    @Test(groups = "ramlVersion")
    public void shouldSaveToThreadLocalWhenSaveRamlToTest() throws Exception {
        assertThat(DevUtils.getRestApi()).isNull();
        assertThat(DevUtils.getRaml()).isNull();

        ThreadLocalContext.setConfig(Config.builder().ramlVersion(RamlVersion.parseId("<666>")).build());
        RestApi api = createRestApi();

        saveRamlToTest(api);
        assertThat(DevUtils.getRestApi()).isSameAs(api);
        assertThat(DevUtils.getRaml()).isNotEmpty();
    }

    @Test(groups = "ramlVersion")
    public void shouldThrowExceptionWhenSaveRamlToTestProblem() {
        ThreadLocalContext.setConfig(Config.builder().ramlVersion(RamlVersion.parseId("<777>")).build());
        assertThatThrownBy(() -> saveRamlToTest(new RestApi())).isInstanceOf(Exception.class);
    }

    @Test(groups = "ramlVersion")
    public void shouldSaveRamlToFileWhenSaveRamlToFile() throws Exception {
        FilerMock filer = new FilerMock();
        ThreadLocalContext.setProcessingEnv(new ProcessingEnvironmentMock().setFiler(filer));
        ThreadLocalContext.setConfig(Config.builder().ramlVersion(RamlVersion.parseId("<666>")).build());
        RestApi api = createRestApi();

        saveRamlToFile(api, "api.raml");
        TestUtils.assertThatEquals(filer.getResource().getOutputData(), SIMPLE_RAML);
    }

    @Test(groups = "ramlVersion")
    public void shouldSaveToThreadLocalWhenTestMode() throws Exception {
        DevUtils.remove();

        FilerMock filer = new FilerMock();
        ThreadLocalContext.setProcessingEnv(new ProcessingEnvironmentMock().setFiler(filer));
        ThreadLocalContext.setConfig(Config.builder().ramlVersion(RamlVersion.parseId("<666>")).ramlDev(true).build());
        RestApi api = createRestApi();

        saveRaml(processor, api);
        assertThat(DevUtils.getRestApi()).isSameAs(api);
        assertThat(DevUtils.getRaml()).isNotEmpty();
        assertThat(filer.getResource().getOutputData()).isNull();
    }

    @Test(groups = "ramlVersion")
    public void shouldSaveToFileWhenTestModeIsOff() throws Exception {
        assertThat(DevUtils.getRestApi()).isNull();
        assertThat(DevUtils.getRaml()).isNull();

        FilerMock filer = new FilerMock();
        ThreadLocalContext.setProcessingEnv(new ProcessingEnvironmentMock().setFiler(filer));
        ThreadLocalContext.setConfig(Config.builder().ramlVersion(RamlVersion.parseId("<666>")).ramlDev(false).build());
        RestApi api = createRestApi();

        saveRaml(processor, api);
        assertThat(DevUtils.getRestApi()).isNull();
        assertThat(DevUtils.getRaml()).isNull();
        TestUtils.assertThatEquals(filer.getResource().getOutputData(), SIMPLE_RAML);
    }

    @Test(groups = "ramlVersion")
    public void shouldThrowExceptionWhenSaveRamlToFileProblem() {
        ThreadLocalContext.setConfig(Config.builder().ramlVersion(RamlVersion.parseId("<777>")).build());
        assertThatThrownBy(() -> saveRamlToFile(new RestApi(), "api.raml")).isInstanceOf(Exception.class);
    }

    @Test(groups = "getAnnotationValue")
    public void shouldReturnNullWhenAnnotationValueIsNotSet() throws Exception {
        assertThat(getAnnotationValue(null, "aaa")).isNull();
        assertThat(getAnnotationValue(new AnnotationMirrorMock(), null)).isNull();
        assertThat(getAnnotationValue(new AnnotationMirrorMock(), "")).isNull();
        assertThat(getAnnotationValue(new AnnotationMirrorMock(), "  ")).isNull();
    }

    @Test(groups = "getAnnotationValue")
    public void shouldReturnNullWhenAnnotationValueIsNotFound() throws Exception {
        assertThat(getAnnotationValue(new AnnotationMirrorMock(), "aaa")).isNull();
    }

    @Test(groups = "getAnnotationValue")
    public void shouldReturnAnnotationValueWhenGetAnnotationValue() throws Exception {
        AnnotationMirrorMock mirror = new AnnotationMirrorMock();
        AnnotationValueMock value = new AnnotationValueMock();

        mirror.addValue(MockUtils.createExecutable("bbb"), new AnnotationValueMock());
        mirror.addValue(MockUtils.createExecutable("aaa"), value);

        assertThat(getAnnotationValue(mirror, "aaa()")).isSameAs(value);
    }

    @Test(groups = "getAnnotationMirror")
    public void shouldReturnNullWhenAnnotationMirrorNotDefined() throws Exception {
        ElementMock element = MockUtils.createElement(int.class);
        assertThat(getAnnotationMirror(element, "aaa"));
        assertThat(getAnnotationMirror(element, ""));
        assertThat(getAnnotationMirror(element, "  "));
        assertThat(getAnnotationMirror(element, null));
    }

    @Test(groups = "getAnnotationMirror")
    public void shouldReturnAnnotationMirrorWhenGetAnnotationMirror() throws Exception {
        ElementMock element = MockUtils.createElement(int.class);
        AnnotationMirrorMock mirror = new AnnotationMirrorMock(new DeclaredTypeMock(MockUtils.createElement(long.class)));

        element.addAnnotationMirror(new AnnotationMirrorMock(new DeclaredTypeMock(MockUtils.createElement(int.class))));
        element.addAnnotationMirror(mirror);

        assertThat(getAnnotationMirror(element, long.class.getName())).isSameAs(mirror);
    }

    @Test(groups = "getAnnotationMirror")
    public void shouldReturnNullWhenAnnotationMirrorNotFound() throws Exception {
        ElementMock element = MockUtils.createElement(int.class);

        assertThat(getAnnotationMirror(element, MockUtils.Count.class.getName())).isNull();

        element.addAnnotationMirror(new AnnotationMirrorMock(new DeclaredTypeMock(MockUtils.createElement(int.class))));
        element.addAnnotationMirror(new AnnotationMirrorMock(new DeclaredTypeMock(MockUtils.createElement(long.class))));

        assertThat(getAnnotationMirror(element, null)).isNull();
        assertThat(getAnnotationMirror(element, MockUtils.Count.class.getName())).isNull();
    }

    @Test(groups = "getClassLevelPath")
    public void shouldReturnPathWhenClassAnnotationPathExists() throws Exception {
        RestImplMock restImpl = new RestImplMock().setRequestPath("/project");
        assertThat(getClassLevelPath(restImpl, null)).isEqualTo("/project");
    }

    @Test(groups = { "context", "getClassLevelPath" })
    public void shouldReturnUrlFromJavadocWhenNoClassAnnotation() throws Exception {
        TypeElementMock typeElement = new TypeElementMock("aaa", ElementKind.LOCAL_VARIABLE).setDocComment("{@url /project}");
        assertThat(getClassLevelPath(new RestImplMock(), typeElement)).isEqualTo("/project");
    }

    @Test(groups = { "context", "getClassLevelPath" })
    public void shouldReturnNullWhenUrlLinkedToVariable() throws Exception {
        TypeElementMock typeElement = new TypeElementMock("aaa", ElementKind.LOCAL_VARIABLE).setDocComment("{@url {@link Project}}");
        assertThat(getClassLevelPath(new RestImplMock(), typeElement)).isNull();
    }

    @Test(groups = { "context", "getClassLevelPath" })
    public void shouldReturnLinkedValueWhenUrlLinkToStaticVariable() throws Exception {
        TypeElementMock typeElement = MockUtils.createElement(MockUtils.Count.class);
        typeElement.getEnclosedElements().stream()
                   .filter(element -> MockUtils.Count.ONE.name().equals(element.getSimpleName().toString()))
                   .map(element -> (VariableElementMock)element)
                   .forEach(element -> element.setConstantValue("/project"));
        ((ImportScannerMock)ThreadLocalContext.getImportScanner()).addElement("MockUtils.Count", typeElement);
        typeElement = new TypeElementMock("aaa", ElementKind.LOCAL_VARIABLE).setDocComment("{@url {@link MockUtils.Count#ONE}}");
        assertThat(getClassLevelPath(new RestImplMock(), typeElement)).isEqualTo("/project");
    }

    @Test(groups = { "context", "getClassLevelPath" })
    public void shouldReturnNullWhenLinkedParameterNotFound() throws Exception {
        TypeElementMock typeElement = new TypeElementMock("aaa", ElementKind.LOCAL_VARIABLE).setDocComment("{@url {@link MockUtils.Count#ONE}}");
        ((ImportScannerMock)ThreadLocalContext.getImportScanner()).addElement("MockUtils.Count", MockUtils.createElement(int.class));
        assertThat(getClassLevelPath(new RestImplMock(), typeElement)).isNull();
    }

    @Test(groups = { "context", "readClassLevelDoc" })
    public void shouldIgnoreWhenPathIsFound() throws Exception {
        RestApi api = new RestApi();

        readClassLevelDoc(processor, api, null);
        assertThat(api.getResources()).isEmpty();
    }

    @Test(groups = { "context", "readClassLevelDoc" })
    public void shouldIgnoreWhenRestApiIsDone() throws Exception {
        TypeElementMock classElement = new TypeElementMock("aaa", ElementKind.LOCAL_VARIABLE).setDocComment("{@name bbb}");
        setRestImpl(processor, new RestImplMock().setRequestPath("/project"));

        RestApi api = new RestApi();
        Resource resource = api.createResource("/project");
        resource.setDisplayName("aaa");

        readClassLevelDoc(processor, api, classElement);
        assertThat(resource.getDisplayName()).isEqualTo("bbb");
        assertThat(resource.isDone()).isTrue();

        classElement.setDocComment("{@name ccc}");
        readClassLevelDoc(processor, api, classElement);
        assertThat(resource.getDisplayName()).isEqualTo("bbb");
        assertThat(resource.isDone()).isTrue();
    }

    @Test(groups = "init")
    public void shouldUseExistedInstancesWhenInit() {
        ThreadLocalContext.remove();
        processor = new RestProcessor();

        assertThat(ThreadLocalContext.getProcessingEnv()).isNull();
        assertThat(ThreadLocalContext.getImportScanner()).isNull();
        assertThat(ThreadLocalContext.getConfig()).isSameAs(Config.NULL);

        ProcessingEnvironment processingEnv = new ProcessingEnvironmentMock();
        ImportScanner importScanner = new ImportScannerMock();
        Config config = Config.builder().ramlShowExample(true).build();

        ThreadLocalContext.setImportScanner(importScanner);
        ThreadLocalContext.setConfig(config);

        processor.init(processingEnv);

        assertThat(ThreadLocalContext.getProcessingEnv()).isSameAs(processingEnv);
        assertThat(ThreadLocalContext.getImportScanner()).isSameAs(importScanner);
        assertThat(ThreadLocalContext.getConfig()).isSameAs(config);
    }

    @Test(groups = "init")
    public void shouldCreateNewInstancesWhenInit() {
        ThreadLocalContext.remove();
        processor = new RestProcessor();

        assertThat(ThreadLocalContext.getProcessingEnv()).isNull();
        assertThat(ThreadLocalContext.getImportScanner()).isNull();
        assertThat(ThreadLocalContext.getConfig()).isSameAs(Config.NULL);

        FilerMock filer = new FilerMock();
        filer.getResource().setData(SIMPLE_CONFIG);
        ProcessingEnvironment processingEnv = new ProcessingEnvironmentMock().setFiler(filer);

        processor.init(processingEnv);

        assertThat(ThreadLocalContext.getProcessingEnv()).isSameAs(processingEnv);
        assertThat(ThreadLocalContext.getImportScanner()).isNotNull();
        assertThat(ThreadLocalContext.getConfig()).isNotSameAs(Config.NULL);
    }

    @Test(groups = { "context", "processMethod" })
    public void shouldThrowExceptionWhenPathIsBlank() throws Exception {
        ExecutableElement methodElement = new ExecutableElementMock("foo", MockUtils.createElement(String.class).asType());
        assertThatThrownBy(() -> processMethod(processor, new RestApi(), methodElement)).isInstanceOf(RamlProcessingException.class);
    }

    @Test(groups = { "context", "processMethod" })
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void testProcessMethod() throws Exception {
        setRestImpl(processor, new RestImplMock().setRequestPath("/analysis").setRequestMethod("GET"));

        TypeElementMock typeElement = MockUtils.createElement(MockUtils.Scenario.class);
        ExecutableElementMock methodElement = typeElement.getEnclosedElements().stream()
                                                         .filter(element -> "getAnalysesByProjectId".equals(element.getSimpleName().toString()))
                                                         .map(element -> (ExecutableElementMock)element)
                                                         .findFirst().get();
        methodElement.addAnnotation(new RequestMappingMock());
        methodElement.setDocComment(TestUtils.joinStrings(
                "Retrieve all analyses related to the given {@code projectId}.",
                "",
                "@param projectId {@link Project#id}",
                "@return {@status 201} list of {@type arr}{@link Analysis} not {@code null} list of all analyses",
                "@throws GeneralException in case of any error",
                "@localRoot"));

        RestApi api = new RestApi();

        processMethod(processor, api, methodElement);
        assertThat(api.getResources()).hasSize(1);

        Resource resource = api.getResources().iterator().next();
        assertThat(resource.getPath()).isEqualTo("/analysis");
        assertThat(resource.getUriParameters()).isEmpty();
        assertThat(resource.getChildren()).isEmpty();
        assertThat(resource.getMethods()).hasSize(1);
        assertThat(resource.isDone()).isTrue();
    }

    @Test(groups = { "context", "process", "ramlVersion" })
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void shouldNotRunProcessWhenIsDone() throws Exception {
        DevUtils.remove();
        ThreadLocalContext.setConfig(Config.builder()
                                           .apiBaseUri("www.com")
                                           .apiMediaType(Utils.APPLICATION_JSON)
                                           .apiVersion("v1")
                                           .doc("docTitle", "docContent")
                                           .ramlVersion(RamlVersion.parseId("<666>"))
                                           .ramlDev(true).build());
        setRestImpl(processor, new RestImplMock().setRequestPath("/analysis").setRequestMapping(RequestMapping.class));
        RoundEnvironmentMock roundEnv = new RoundEnvironmentMock();

        TypeElementMock typeElement = MockUtils.createElement(MockUtils.Scenario.class);
        ExecutableElementMock methodElement = typeElement.getEnclosedElements().stream()
                                                         .filter(element -> "getAnalysesByProjectId".equals(element.getSimpleName().toString()))
                                                         .map(element -> (ExecutableElementMock)element)
                                                         .findFirst().get();
        methodElement.addAnnotation(new RequestMappingMock());
        methodElement.setDocComment(TestUtils.joinStrings(
                "Retrieve all analyses related to the given {@code projectId}.",
                "",
                "@param projectId {@link Project#id}",
                "@return {@status 201} list of {@type arr}{@link Analysis} not {@code null} list of all analyses",
                "@throws GeneralException in case of any error",
                "@localRoot"));

        roundEnv.addElementAnnotatedWith(typeElement);
        assertThat(processor.process(Collections.emptySet(), roundEnv)).isTrue();
        assertThat(DevUtils.getRestApi()).isNotNull();

        DevUtils.remove();
        assertThat(processor.process(Collections.emptySet(), roundEnv)).isTrue();
        assertThat(DevUtils.getRestApi()).isNull();
    }

    @Test(groups = { "context", "process" })
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void shouldThrowExceptionWhenProcessingProblemsInTestMode() throws Exception {
        DevUtils.remove();
        ThreadLocalContext.setConfig(Config.builder().ramlDev(true).build());
        setRestImpl(processor, new RestImplMock().setRequestPath("/analysis").setError(true));
        RoundEnvironmentMock roundEnv = new RoundEnvironmentMock();

        TypeElementMock typeElement = MockUtils.createElement(MockUtils.Scenario.class);
        ExecutableElementMock methodElement = typeElement.getEnclosedElements().stream()
                                                         .filter(element -> "getAnalysesByProjectId".equals(element.getSimpleName().toString()))
                                                         .map(element -> (ExecutableElementMock)element)
                                                         .findFirst().get();
        methodElement.addAnnotation(new RequestMappingMock());
        roundEnv.addElementAnnotatedWith(typeElement);
        MessagerMock messager = (MessagerMock)ThreadLocalContext.getMessager();

        assertThatThrownBy(() -> processor.process(Collections.emptySet(), roundEnv)).isExactlyInstanceOf(RamlProcessingException.class);
        assertThat(messager.getMessage()).isNull();
    }

    @Test(groups = { "context", "process" })
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void shouldWriteMessageWhenProcessingProblems() throws Exception {
        DevUtils.remove();
        ThreadLocalContext.setConfig(Config.builder().ramlDev(false).build());
        setRestImpl(processor, new RestImplMock().setRequestPath("/analysis").setError(true));
        RoundEnvironmentMock roundEnv = new RoundEnvironmentMock();

        TypeElementMock typeElement = MockUtils.createElement(MockUtils.Scenario.class);
        ExecutableElementMock methodElement = typeElement.getEnclosedElements().stream()
                                                         .filter(element -> "getAnalysesByProjectId".equals(element.getSimpleName().toString()))
                                                         .map(element -> (ExecutableElementMock)element)
                                                         .findFirst().get();
        methodElement.addAnnotation(new RequestMappingMock());
        roundEnv.addElementAnnotatedWith(typeElement);

        MessagerMock messager = (MessagerMock)ThreadLocalContext.getMessager();
        ((ProcessingEnvironmentMock)ThreadLocalContext.getProcessingEnv()).setFiler(new FilerMock());

        assertThat(processor.process(Collections.emptySet(), roundEnv)).isTrue();
        assertThat(messager.getMessage()).isNotEmpty();
    }

    @Test(groups = { "context", "processMethodWithCatchException" })
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void shouldProcessMethodWhenNoException() throws Exception {
        setRestImpl(processor, new RestImplMock().setRequestPath("/analysis"));

        TypeElementMock typeElement = MockUtils.createElement(MockUtils.Scenario.class);
        ExecutableElementMock methodElement = typeElement.getEnclosedElements().stream()
                                                         .filter(element -> "getAnalysesByProjectId".equals(element.getSimpleName().toString()))
                                                         .map(element -> (ExecutableElementMock)element)
                                                         .findFirst().get();
        RestApi api = new RestApi();
        processMethodWithCatchException(processor, api, methodElement);
        assertThat(api.getResources()).hasSize(1);
    }

    @Test(groups = { "context", "processMethodWithCatchException" })
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void shouldPrintMessageWhenAnnotationValueReadProblem() throws Exception {
        TypeElementMock typeElement = MockUtils.createElement(MockUtils.Scenario.class);
        ExecutableElementMock methodElement = typeElement.getEnclosedElements().stream()
                                                         .filter(element -> "getAnalysesByProjectId".equals(element.getSimpleName().toString()))
                                                         .map(element -> (ExecutableElementMock)element)
                                                         .findFirst().get();

        typeElement.addAnnotation(new RestControllerMock().setError(true));

        RestApi api = new RestApi();
        processMethodWithCatchException(processor, api, methodElement);
        assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNotEmpty();
    }

    @Test(groups = { "context", "processMethodWithCatchException" })
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void shouldPrintMessageWhenAnyProblem() throws Exception {
        setRestImpl(processor, null);
        RestApi api = new RestApi();
        processMethodWithCatchException(processor, api, MockUtils.createExecutable("foo()"));
        assertThat(((MessagerMock)ThreadLocalContext.getMessager()).getMessage()).isNotEmpty();
    }

    @Test(groups = "context")
    public void shouldProccedOnlySpecificMethodsWhenProceedClass() throws Exception {
        Element classElement = MockUtils.createElement(MockUtils.AnalysisController.class);

        RestApi api = new RestApi();

        proceedClass(processor, api, classElement);
        assertThat(api.getResources()).hasSize(2);
    }

    @Test(groups = "context")
    public void shouldUseFilterWhenProceedClass() throws Exception {
        setFilterRegex(processor, Pattern.compile("cop\\.raml\\.utils\\.mocks\\.MockUtils\\$Count"));
        Element classElement = MockUtils.createElement(MockUtils.AnalysisController.class);

        RestApi api = new RestApi();

        proceedClass(processor, api, classElement);
        assertThat(api.getResources()).isEmpty();

        setFilterRegex(processor, Pattern.compile("cop\\.raml\\.mocks\\.MockUtils\\$AnalysisController"));
        proceedClass(processor, api, classElement);
        assertThat(api.getResources()).hasSize(2);
    }

    // ========== static ==========

    private static RestApi createRestApi() {
        RestApi api = new RestApi();

        api.setTitle("title");
        api.setBaseUri("www.com");
        api.setMediaType(Utils.APPLICATION_JSON);
        api.setDocumentation("docTitle", "docContent");
        api.setVersion("v1");

        return api;
    }

    private static VariableElementMock createBody(String name, boolean required) throws ClassNotFoundException {
        return MockUtils.createVariable(name, String.class).addAnnotation(new RequestBodyMock(required));
    }

    private static VariableElementMock createQueryParameter(String name, Class<?> cls, String value, String def, boolean required)
            throws ClassNotFoundException {
        return MockUtils.createVariable(name, cls).addAnnotation(new RequestParamMock().setRequired(required).setDefaultValue(def).setValue(value));
    }

    private static VariableElementMock createUriParameter(String name, Class<?> cls, String value, boolean required)
            throws ClassNotFoundException {
        return MockUtils.createVariable(name, cls).addAnnotation(new PathVariableMock().setRequired(required).setValue(value));
    }

    private static void readQueryParameters(RestProcessor obj, RestMethod restMethod, ExecutableElement methodElement,
            MethodJavaDoc methodJavaDoc) throws Exception {
        ReflectionUtils.invokeMethod(obj, "readQueryParameters", RestMethod.class, ExecutableElement.class, MethodJavaDoc.class,
                restMethod, methodElement, methodJavaDoc);
    }

    private static TagParam getLinkParamJavaDoc(TagParam param) throws Exception {
        return ReflectionUtils.invokeStaticMethod(RestProcessor.class, "getLinkParamJavaDoc", TagParam.class, param);
    }

    private static void readUriParameters(RestProcessor obj, Resource resource, ExecutableElement methodElement, MethodJavaDoc methodJavaDoc)
            throws Exception {
        ReflectionUtils.invokeMethod(obj, "readUriParameters", Resource.class, ExecutableElement.class, MethodJavaDoc.class,
                resource, methodElement, methodJavaDoc);
    }

    private static void readBody(RestProcessor obj, RestMethod restMethod, ExecutableElement methodElement) throws Exception {
        ReflectionUtils.invokeMethod(obj, "readBody", RestMethod.class, ExecutableElement.class, restMethod, methodElement);
    }

    private static String[] checkMediaTypes(String... mediaTypes) throws Exception {
        return ReflectionUtils.invokeStaticMethod(RestProcessor.class, "checkMediaTypes", String[].class, mediaTypes);
    }

    private static void readResponse(RestProcessor obj, RestMethod res, ExecutableElement methodElement, MethodJavaDoc methodJavaDoc)
            throws Exception {
        ReflectionUtils.invokeMethod(obj, "readResponse", RestMethod.class, ExecutableElement.class, MethodJavaDoc.class,
                res, methodElement, methodJavaDoc);
    }

    private static void setGeneralProperties(RestApi api) throws Exception {
        ReflectionUtils.invokeStaticMethod(RestProcessor.class, "setGeneralProperties", RestApi.class, api);
    }

    private static void assemble(RestApi api, String template, Writer out) throws Exception {
        ReflectionUtils.invokeStaticMethod(RestProcessor.class, "assemble", Writer.class, RestApi.class, String.class, out, api, template);
    }

    private static Pattern getFilterRegex() throws Exception {
        return ReflectionUtils.invokeStaticMethod(RestProcessor.class, "getFilterRegex");
    }

    private static String getFileName() throws Exception {
        return ReflectionUtils.invokeStaticMethod(RestProcessor.class, "getFileName");
    }

    private static Config getConfig() throws Exception {
        return ReflectionUtils.invokeStaticMethod(RestProcessor.class, "getConfig");
    }

    private static void readMethod(RestProcessor obj, Resource resource, ExecutableElement methodElement, MethodJavaDoc methodJavaDoc)
            throws Exception {
        ReflectionUtils.invokeMethod(obj, "readMethod", Resource.class, ExecutableElement.class, MethodJavaDoc.class,
                resource, methodElement, methodJavaDoc);
    }

    private static void saveRamlToTest(RestApi api) throws Exception {
        ReflectionUtils.invokeStaticMethod(RestProcessor.class, "saveRamlToTest", RestApi.class, api);
    }

    private static void saveRamlToFile(RestApi api, String fileName) throws Exception {
        ReflectionUtils.invokeStaticMethod(RestProcessor.class, "saveRamlToFile", RestApi.class, String.class, api, fileName);
    }

    private static void saveRaml(RestProcessor processor, RestApi api) throws Exception {
        ReflectionUtils.invokeMethod(processor, "saveRaml", RestApi.class, api);
    }

    private static AnnotationValue getAnnotationValue(AnnotationMirror mirror, String valueName) throws Exception {
        return ReflectionUtils.invokeStaticMethod(RestProcessor.class, "getAnnotationValue", AnnotationMirror.class, String.class, mirror, valueName);
    }

    private static AnnotationMirror getAnnotationMirror(Element element, String className) throws Exception {
        return ReflectionUtils.invokeStaticMethod(RestProcessor.class, "getAnnotationMirror", Element.class, String.class, element, className);
    }

    private static String getClassLevelPath(RestImpl restImpl, TypeElement typeElement) throws Exception {
        return ReflectionUtils.invokeStaticMethod(RestProcessor.class, "getClassLevelPath", RestImpl.class, TypeElement.class, restImpl, typeElement);
    }

    private static void readClassLevelDoc(RestProcessor obj, RestApi api, TypeElement classElement) throws Exception {
        ReflectionUtils.invokeMethod(obj, "readClassLevelDoc", RestApi.class, TypeElement.class, api, classElement);
    }

    private static void setRestImpl(RestProcessor obj, RestImpl restImpl) throws Exception {
        ReflectionUtils.setFieldValue(obj, "restImpl", restImpl);
    }

    private static void processMethod(RestProcessor obj, RestApi api, ExecutableElement methodElement) throws Exception {
        ReflectionUtils.invokeMethod(obj, "processMethod", RestApi.class, ExecutableElement.class, api, methodElement);
    }

    private static void processMethodWithCatchException(RestProcessor obj, RestApi api, ExecutableElement methodElement) throws Exception {
        ReflectionUtils.invokeMethod(obj, "processMethodWithCatchException", RestApi.class, ExecutableElement.class, api, methodElement);
    }

    private static void proceedClass(RestProcessor obj, RestApi api, Element classElement) throws Exception {
        ReflectionUtils.invokeMethod(obj, "proceedClass", RestApi.class, Element.class, api, classElement);
    }

    private static void setFilterRegex(RestProcessor obj, Pattern filterRegex) throws Exception {
        ReflectionUtils.setFieldValue(obj, "filterRegex", filterRegex);
    }
}
