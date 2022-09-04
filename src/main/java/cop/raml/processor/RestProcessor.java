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
import cop.raml.processor.exceptions.RamlProcessingException;
import cop.raml.processor.rest.RestImpl;
import cop.raml.processor.rest.SpringRestImpl;
import cop.raml.utils.ImportScanner;
import cop.raml.utils.ProblemResolver;
import cop.raml.utils.ThreadLocalContext;
import cop.raml.utils.Utils;
import cop.raml.utils.example.ExampleProvider;
import cop.raml.utils.javadoc.JavaDocUtils;
import cop.raml.utils.javadoc.Macro;
import cop.raml.utils.javadoc.MethodJavaDoc;
import cop.raml.utils.javadoc.tags.TagLink;
import cop.raml.utils.javadoc.tags.TagParam;
import cop.raml.utils.javadoc.tags.TagReturn;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationTypeMismatchException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static cop.raml.processor.RestProcessor.KEY_FILE_NAME;
import static cop.raml.processor.RestProcessor.KEY_FILTER_REGEX;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;

// TODO: @CookieValue
// TODO: @RequestHeader
// TODO: @ResponseStatus
// TODO: combine class-level and method-level annotations properly
// TODO: MethodNameResolver: plural RequestMapping value support (i.e., two paths bound to one method)
// TODO: support for methods not marked with @RequestMapping whose class does have a @RequestMapping annotation
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({ KEY_FILE_NAME, KEY_FILTER_REGEX })
public final class RestProcessor extends AbstractProcessor {
    public static final String KEY_FILTER_REGEX = "filterRegex";
    public static final String KEY_FILE_NAME = "fileName";
    public static final String DEF_FILE_NAME = "api";

    private final RestImpl restImpl = SpringRestImpl.getInstance();
    private ExampleProvider exampleProvider;
    private boolean done;

    private Pattern filterRegex;
    private String fileName;

    /**
     * Retrieves all classes related to the current {@link RestImpl} from the given {@code roundEnv} and starts each class sequential processing
     *
     * @param api rest api holder where all results will be stored
     */
    private void proceedClasses(@NotNull RestApi api) {
        for (Element classElement : ThreadLocalContext.getRoundEnv().getElementsAnnotatedWith(restImpl.getRequestController()))
            proceedClass(api, classElement);
    }

    /**
     * Retrieves all actual methods from the given {@code classElement} and starts each method sequential processing.
     *
     * @param api          rest api holder where all results will be stored
     * @param classElement current class
     */
    private void proceedClass(@NotNull RestApi api, @NotNull Element classElement) {
        Class<? extends Annotation> annotation = restImpl.getRequestMapping();

        for (Element methodElement : classElement.getEnclosedElements()) {
            if (!(methodElement instanceof ExecutableElement))
                continue;
            if (methodElement.getAnnotation(annotation) == null)
                continue;
            if (filterRegex != null && !filterRegex.matcher(methodElement.getEnclosingElement().toString()).matches())
                continue;

            ThreadLocalContext.setClassName(((QualifiedNameable)classElement).getQualifiedName().toString());
            ThreadLocalContext.setMethodName(methodElement.getSimpleName().toString());
            ThreadLocalContext.getImportScanner().setCurrentElement(methodElement);

            processMethodWithCatchException(api, (ExecutableElement)methodElement);
        }
    }

    /**
     * This method is responsible for calling {@link #processMethod(RestApi, ExecutableElement)} and catch all exceptions.
     *
     * @param api           rest api
     * @param methodElement method element
     */
    private void processMethodWithCatchException(@NotNull RestApi api, @NotNull ExecutableElement methodElement) {
        try {
            processMethod(api, methodElement);
        } catch(AnnotationTypeMismatchException e) {
            String className = methodElement.getEnclosingElement().getSimpleName().toString();
            String message = String.format("Unable to read annotation parameter '%s' for %s.%s(). Try to use simple inline String.",
                    e.element().getName(), className, methodElement.getSimpleName());
            AnnotationMirror annotationMirror = getAnnotationMirror(methodElement, e.element().getDeclaringClass().getName());
            AnnotationValue annotationValue = getAnnotationValue(annotationMirror, e.element().getName() + "()");
            ThreadLocalContext.getMessager().printMessage(WARNING, message, methodElement, annotationMirror, annotationValue);
        } catch(Exception e) {
            String message = String.format("%s: %s", e.getClass().getName(), e.getStackTrace()[0]);
            ThreadLocalContext.getMessager().printMessage(WARNING, message, methodElement);
        }
    }

    private void processMethod(@NotNull RestApi api, @NotNull ExecutableElement methodElement) {
        TypeElement classElement = (TypeElement)methodElement.getEnclosingElement();
        String path = restImpl.getRequestPath(classElement, methodElement);

        if (StringUtils.isBlank(path))
            throw new RamlProcessingException("endpoint url is empty");

        List<String> doc = ThreadLocalContext.getDocCommentAsList(methodElement);
        MethodJavaDoc methodJavaDoc = MethodJavaDoc.create(doc);

        readClassLevelDoc(api, classElement);

        Resource resource = api.createResource(path);
        readUriParameters(resource, methodElement, methodJavaDoc);
        readMethod(resource, methodElement, methodJavaDoc);
    }

    private void readClassLevelDoc(@NotNull RestApi api, @NotNull TypeElement classElement) {
        Resource resource = api.createResource(getClassLevelPath(restImpl, classElement));

        if (resource == null || resource.isDone())
            return;

        String doc = ThreadLocalContext.getDocComment(classElement);
        resource.setDescription(JavaDocUtils.getText(Utils.toLineList(doc)));
        resource.setDisplayName(Macro.NAME.get(doc));
        resource.setDone(true);
    }

    /**
     * First read given {@link Resource#path}, select all variables in it and put it into uri parameters part of the {@code resource}. Then scan given
     * {@code methodElement} and {@code methodJavaDoc} got values information about all selected uri parameters.
     * It's possible, if uri parameters are used in several method. In this case description of the first method only will be written to results.
     *
     * @param resource      current api result object
     * @param methodElement method element
     * @param methodJavaDoc method javadoc
     */
    private void readUriParameters(@NotNull Resource resource, @NotNull ExecutableElement methodElement, @NotNull MethodJavaDoc methodJavaDoc) {
        Utils.getParams(resource.getPath()).forEach(resource::addUriParameter);

        for (VariableElement var : methodElement.getParameters()) {
            if (!restImpl.isUriParam(var))
                continue;

            String paramName = restImpl.getUriParamName(var);
            ThreadLocalContext.setParamName(paramName);
            Parameter parameter = resource.getUriParameter(paramName);

            if (parameter == null || StringUtils.isNotBlank(parameter.getDescription()))
                continue;

            TagParam param = methodJavaDoc.getParam(paramName);
            TagParam linkParam = getLinkParamJavaDoc(param);

            parameter.setDisplayName(Utils.defOnBlank(param.getName(), linkParam.getName()));
            parameter.setExample(Utils.defOnBlank(param.getExample(), linkParam.getExample()));
            parameter.setDescription(Utils.defOnBlank(param.getText(), linkParam.getText()));

            parameter.setType(var.asType());
            parameter.setRequired(restImpl.isUriParamRequired(var));
            parameter.setDefault(param.getDefault());
            parameter.setEnum(Utils.defOnBlank(param.getEnum(), linkParam.getEnum()));
            // TODO min
            // TODO max

            parameter.setPattern(Utils.defOnBlank(param.getPattern(), linkParam.getPattern()));
            // TODO repeat
        }
    }

    /**
     * Rest method contains several parts: query parameters, body and response data. This method creates new {@link RestMethod} in the given {@link
     * Resource} and fill it with existed data from given {@code methodElement}.
     *
     * @param resource      current api result object
     * @param methodElement method element
     * @param methodJavaDoc method javadoc
     */
    private void readMethod(@NotNull Resource resource, @NotNull ExecutableElement methodElement, @NotNull MethodJavaDoc methodJavaDoc) {
        RestMethod res = resource.createMethod(restImpl.getRequestMethod(methodElement));

        res.setDescription(methodJavaDoc.getText());
        // TODO put traits
        readQueryParameters(res, methodElement, methodJavaDoc);
        readBody(res, methodElement);
        readResponse(res, methodElement, methodJavaDoc);
    }

    /**
     * Read all query parameters (that comes after {@literal '?'} symbol) from given {@code methodElement} and put it to the {@code res}. Some
     * parameter are read from annotation, some - from javadoc. But id both sources contains parameter description, then java doc has more priority.
     * (e.g. if according to annotation parameter <i>required</i>, but according to javadoc - <i>optional</i>, then this parameter will be marked as
     * <t>optional</t> in the results)
     *
     * @param res           rest method result
     * @param methodElement current method
     * @param methodJavaDoc javadoc
     */
    private void readQueryParameters(@NotNull RestMethod res, @NotNull ExecutableElement methodElement, @NotNull MethodJavaDoc methodJavaDoc) {
        for (VariableElement var : methodElement.getParameters()) {
            if (!restImpl.isQueryParam(var))
                continue;

            String paramName = restImpl.getQueryParamName(var);
            ThreadLocalContext.setParamName(paramName);
            Parameter parameter = res.addQueryParameters(paramName);
            TagParam param = methodJavaDoc.getParam(paramName);

            parameter.setDisplayName(param.getName());
            parameter.setExample(exampleProvider.getExample(var, Utils.APPLICATION_JSON));
            parameter.setDescription(param.getText());

            parameter.setType(var.asType());
            parameter.setRequired(restImpl.isQueryParamRequired(var));
            parameter.setDefault(restImpl.getQueryParamDefault(var));
            parameter.setEnum(Utils.toEnumStr(param.getEnum(), var));
            // TODO min
            // TODO max

            parameter.setPattern(param.getPattern());
            // TODO repeat
        }
    }

    /**
     * Read body parameter for current {@code methodElement} and put it to the {@code res}.
     *
     * @param res           rest method result
     * @param methodElement current method
     */
    private void readBody(@NotNull RestMethod res, @NotNull ExecutableElement methodElement) {
        for (VariableElement var : methodElement.getParameters()) {
            if (!restImpl.isBody(var))
                continue;

            boolean required = restImpl.isBodyRequired(var);
            String[] mediaTypes = checkMediaTypes(isMultipart(var) ? Utils.arrMultipartFormData() : restImpl.getRequestMediaTypes(methodElement));
            ImportScanner importScanner = ThreadLocalContext.getImportScanner();

            for (String mediaType : mediaTypes) {
                Body body = res.addBody(mediaType);
                body.setRequired(required);
                body.setExample(exampleProvider.getExample(importScanner.getElement(var.asType()), mediaType));
            }
        }
    }

    /**
     * Read response data from given {@code methodElement} and put it into {@code res}. In general case it's impossible to correctly detect type of
     * return value automatically
     *
     * @param res           rest method result
     * @param methodElement current method
     * @param methodJavaDoc javadoc
     */
    private void readResponse(@NotNull RestMethod res, @NotNull ExecutableElement methodElement, @NotNull MethodJavaDoc methodJavaDoc) {
        String[] mediaTypes = checkMediaTypes(restImpl.getResponseMediaTypes(methodElement));
        TagReturn ret = methodJavaDoc.getReturn();
        TypeElement element = ThreadLocalContext.getImportedElement(ret.getLink().getClassName());

        if (element == null)
            element = ThreadLocalContext.getImportedElement(methodElement.asType().toString());

        Response response = res.addResponse(ret != TagReturn.NULL ? ret.getStatus() : HttpStatus.OK.value());
        response.setDescription(ret.getText());

        for (String mediaType : mediaTypes) {
            Body body = response.addBody(mediaType);
            body.setExample(exampleProvider.getExample(element, mediaType, ret.isArray()));
        }
    }

    private void saveRaml(@NotNull RestApi api) throws Exception {
        if (Config.get().ramlDev())
            saveRamlToTest(api);
        else
            saveRamlToFile(api, fileName);
    }

    // ========== Processor ==========

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        ThreadLocalContext.setProcessingEnv(processingEnv);

        if (ThreadLocalContext.getImportScanner() == null)
            ThreadLocalContext.setImportScanner(new ImportScanner());

        exampleProvider = new ExampleProvider();
        filterRegex = getFilterRegex();
        fileName = getFileName();

        if (ThreadLocalContext.getConfig() == Config.NULL)
            ThreadLocalContext.setConfig(getConfig());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (done)
            return true;

        ThreadLocalContext.setProcessingEnv(processingEnv);
        ThreadLocalContext.setRoundEnv(roundEnv);
        ThreadLocalContext.getImportScanner().scanRootElements();

        try {
            RestApi api = new RestApi();
            setGeneralProperties(api);
            proceedClasses(api);
            saveRaml(api);
        } catch(Exception e) {
            if (Config.get().ramlDev())
                throw new RamlProcessingException(e);
            ThreadLocalContext.getMessager().printMessage(ERROR, "Cannot create raml documentation: " + e);
        } finally {
            done = true;
            ThreadLocalContext.remove();
        }

        return true;
    }

    // ========== static ==========

    private static void assemble(@NotNull Writer out, @NotNull RestApi api, @NotNull String template) throws IOException, TemplateException {
        Configuration conf = new Configuration(Configuration.VERSION_2_3_23);
        conf.setClassForTemplateLoading(RamlVersion.class, "/");
        conf.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_23).build());
        conf.getTemplate(template).process(Collections.singletonMap("api", api), out);
    }

    /**
     * Reads {@link Config} object from context and set general properties to the given {@code api}: {@link RestApi#title}, {@link RestApi#docTitle},
     * {@link RestApi#docContent}, {@link RestApi#baseUri}, {@link RestApi#version} and {@link RestApi#mediaType}.
     *
     * @param api rest api object
     */
    private static void setGeneralProperties(@NotNull RestApi api) {
        Config config = Config.get();

        api.setTitle(config.apiTitle());
        api.setDocumentation(config.docTitle(), config.docContent());
        api.setBaseUri(config.apiBaseUri());
        api.setVersion(config.apiVersion());
        api.setMediaType(config.apiMediaType());
    }

    /**
     * Return {@link TagParam} to the liked variable. If parameter is linked with class, then warning message will be printed and {@link
     * TagParam#NULL} will be returned. If linked parameter found, then javadoc for this parameter will be returned.
     *
     * @param param uri parameter
     * @return linked parameter {@link TagParam} object, or {@link TagParam#NULL} id it was not found or point to class instead of variable
     */
    @NotNull
    private static TagParam getLinkParamJavaDoc(@NotNull TagParam param) {
        if (param == TagParam.NULL)
            return TagParam.NULL;

        if (param.getLink().isClass()) {
            ThreadLocalContext.getMessager().printMessage(WARNING, "warning, problem in doc, link points class, expected to variable");
            return TagParam.NULL;
        }

        return MethodJavaDoc.createTagParam(ThreadLocalContext.getDocComment(param.getLink()), param.getLink());
    }

    private static boolean isMultipart(@NotNull VariableElement var) {
        return var.asType().toString().toLowerCase().contains("multipart");
    }

    private static String[] checkMediaTypes(String... mediaTypes) {
        if (ArrayUtils.isNotEmpty(mediaTypes))
            return mediaTypes;

        ProblemResolver.mediaTypeNotDefined();
        return Utils.arrApplicationJson();
    }

    /**
     * Checks given path for class level root. Basically mapped classes are marked with {@link RestImpl#getRequestController()} annotation with some
     * path in it. But it's possible to select full path in the method-level with {@link RestImpl#getRequestMapping()} annotation. In this case we
     * have to set class root path to write javadoc from current class to some place in raml.
     * To do so, do use {@link Macro#URL} in the class-level javadoc (e.g. {{@literal @}url /project/{projectId}/analysis}). Additionally, this url
     * can be read from some variable level javadoc. To select this variable, do use {@literal @}link tag inside {@link Macro#URL}
     * (e.g. {{@literal @}url {{@literal @}link PathUtils#ANALYSIS}}}).
     *
     * @param restImpl     rest implementation
     * @param classElement class level type element
     * @return current class-level path or {@code null} if this path cannot be read
     */
    private static String getClassLevelPath(@NotNull RestImpl restImpl, @NotNull TypeElement classElement) {
        String path = restImpl.getRequestPath(classElement, null);

        if (StringUtils.isNotBlank(path))
            return path;

        path = Macro.URL.get(ThreadLocalContext.getDocComment(classElement));
        TagLink link = TagLink.create(path);

        if (link == TagLink.NULL)
            return path;
        if (link.isClass())
            return null;

        Element varElement = ThreadLocalContext.getImportedElement(link);
        Object val = varElement != null ? ((VariableElement)varElement).getConstantValue() : null;

        return val != null ? String.valueOf(val) : null;
    }

    /**
     * Return {@code filterRegex} option pattern from processing environment {@link ProcessingEnvironment}. This pattern is used to filter processed
     * methods.
     *
     * @return found {@code filterRegex} option or {@code null}
     */
    private static Pattern getFilterRegex() {
        String regex = ThreadLocalContext.getProcessingEnv().getOptions().get(KEY_FILTER_REGEX);
        return StringUtils.isBlank(regex) ? null : Pattern.compile(regex);
    }

    /**
     * Return {@link #KEY_FILE_NAME} option from processing environment {@link ProcessingEnvironment}. This is the name of the result raml file name.
     * If name is not set in the environment, then {@link #DEF_FILE_NAME} will be returned.
     *
     * @return not {@code null} raml file name
     */
    @NotNull
    private static String getFileName() {
        String fileName = ThreadLocalContext.getProcessingEnv().getOptions().get(KEY_FILE_NAME);
        fileName = StringUtils.isNotBlank(fileName) ? fileName : DEF_FILE_NAME;
        return String.format("%s.raml", fileName);
    }

    @NotNull
    private static Config getConfig() {
        Filer filter = ThreadLocalContext.getProcessingEnv().getFiler();

        if (filter == null)
            return Config.NULL;

        try (InputStream in = filter.getResource(StandardLocation.CLASS_OUTPUT, "", Config.YAML).openInputStream()) {
            return Config.builder().parse(IOUtils.toString(in, StandardCharsets.UTF_8)).build();
        } catch(Exception e) {
            ThreadLocalContext.getMessager().printMessage(ERROR, String.format("Cannot read YAML config: '%s'", e.getMessage()));
            return Config.NULL;
        }
    }

    private static AnnotationMirror getAnnotationMirror(@NotNull Element element, String className) {
        List<? extends AnnotationMirror> mirrors = element.getAnnotationMirrors();

        if (CollectionUtils.isEmpty(mirrors) || StringUtils.isBlank(className))
            return null;

        for (AnnotationMirror mirror : mirrors)
            if (className.equals(mirror.getAnnotationType().toString()))
                return mirror;

        return null;
    }

    private static AnnotationValue getAnnotationValue(AnnotationMirror mirror, String valueName) {
        if (mirror == null || StringUtils.isBlank(valueName))
            return null;

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet())
            if (valueName.equals(entry.getKey().toString()))
                return entry.getValue();

        return null;
    }

    private static void saveRamlToTest(@NotNull RestApi api) throws IOException, TemplateException {
        DevUtils.setRestApi(api);

        Writer out = new StringWriter();
        String template = Config.get().ramlVersion().getTemplate();

        assemble(out, api, template);

        DevUtils.setRaml(out.toString().trim());
    }

    private static void saveRamlToFile(@NotNull RestApi api, @NotNull String fileName) throws Exception {
        FileObject file = ThreadLocalContext.getProcessingEnv().getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", fileName);
        String template = Config.get().ramlVersion().getTemplate();

        try (OutputStreamWriter out = new OutputStreamWriter(file.openOutputStream(), StandardCharsets.UTF_8)) {
            assemble(out, api, template);
        }
    }
}
