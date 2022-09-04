<#-- @ftlvariable name="api" type="cop.raml.processor.RestApi" -->
<#-- @ftlvariable name="method" type="cop.raml.processor.RestMethod" -->
#%RAML 0.8
---
<#if api.title??>
title: ${api.title}
</#if>
<#if api.baseUri??>
baseUri: ${api.baseUri}
</#if>
<#if api.version??>
version: ${api.version}
</#if>
<#if api.mediaType??>
mediaType: ${api.mediaType}
</#if>
<#if api.docTitle?? && api.docContent??>
documentation:
  - title: ${api.docTitle}
    content: |
${api.getDocContent(6)}
</#if>
<#--securitySchemes:-->
<#--- oauth_2_0:-->
<#--type: OAuth 2.0-->
<#--settings:-->
<#--accessTokenUri: TBD-->
<#--authorizationUri: TBD-->
<#--<#if api.getTraits()?size gt 0>-->
<#--traits:-->
<#--${api.indentedApiTraits(4)}-->
<#--</#if>-->

<#--
  -- resources
  -->
<#list api.resources as resource>
<@_resource_ resource=resource offs=0/>
</#list>

<#--
  -- Show Resource
  -->
<#macro _resource_ resource offs>
<#list 1..*offs as i> </#list>${resource.path}:
<#if resource.displayName??>
<#list 1..*offs+2 as i> </#list>displayName: ${resource.displayName}
</#if>
<#if resource.description??>
<#list 1..*offs+2 as i> </#list>description: |
${resource.getDescription(offs + 4)}
</#if>
<#if resource.uriParameters?has_content>
<@_uri_parameters_ resource=resource offs=offs+2 />
</#if>
<@_resource_parts_ resource=resource offs=offs+2/>
</#macro>

<#--
  -- write out methods of the RAML resource and then the sub-resources
  -->
<#macro _resource_parts_ resource offs>
<#list resource.methods as method>
<@_method_ method=method offs=offs/>
</#list>
<#list resource.children as child>
<@_resource_ resource=child offs=offs/>
</#list>
</#macro>

<#--
  -- write out the method name, description, parameters, body, and response for the given method
  -->
<#macro _method_ method offs>
<#list 1..*offs as i> </#list>${method.type?lower_case}:
<#if method.description??>
<#list 1..*offs+2 as i> </#list>description: |
${method.getDescription(offs+4)}
</#if>
<#if method.queryParameters?has_content>
<@_query_parameters_ method=method offs=offs+2/>
</#if>
<#if method.bodies?has_content>
<@_bodies_ method=method offs=offs+2/>
</#if>
<#if method.responses?has_content>
<@_responses_ method=method offs=offs+2/>
</#if>
    <#--<#if method.authScopesAsString??>-->
        <#--<@write_auth_scopes methodDoc=method offs=offs+4/>-->
    <#--</#if>-->

    <#--<@write_traits methodDoc=method offs=offs+4/>-->

    <#--<@write_parameters methodDoc=method offs=offs+4/>-->

    <#--<#if method.requestSchema??>-->
        <#--<@write_body schema=method.requestSchema example=method.requestExample offs=offs+4/>-->

    <#--</#if>-->
    <#--<@write_response methodDoc=method offs=offs+4/>-->
</#macro>

<#--
  -- query parameters
  -->
<#macro _query_parameters_ method offs>
<#list 1..*offs as i> </#list>queryParameters:
<#list method.queryParameters as queryParameter>
<@_parameter_ parameter=queryParameter offs=offs/>
</#list>
<#--<#assign fields=resource.resourceUrlSubstitutions.fields>-->
<#--<#list fields?keys as key>-->
<#--<@write_parameter fields=fields key=key offs=offs+4/>-->
<#--</#list>-->
</#macro>

<#--
  -- bodies
  -->
<#macro _bodies_ method offs>
<#list 1..*offs as i> </#list>body:
<#list method.bodies as body>
<@_body_ body=body total=method.bodies?size offs=offs/>
</#list>
</#macro>

<#--
  -- body
  -->
<#macro _body_ body total offs>
<#--<#if !body.isEmpty()>-->
<#if body.def && total == 1>
<#if body.example??>
<@_example_ parent=body offs=offs/>
</#if>
<#else>
<#list 1..*offs+2 as i> </#list>${body.mediaType}:
<#if body.example??>
<@_example_ parent=body offs=offs+2/>
</#if>
</#if>
<#--</#if>-->
</#macro>

<#--
  -- responses
  -->
<#macro _responses_ method offs>
<#list 1..*offs as i> </#list>responses:
<#list method.responses as response>
<@_response_ response=response offs=offs/>
</#list>
</#macro>

<#--
  -- response
  -->
<#macro _response_ response offs>
<#list 1..*offs+2 as i> </#list>${response.status}:
<#if response.description??>
<#list 1..*offs+4 as i> </#list>description: |
${response.getDescription(offs+6)}
</#if>
<#if response.bodies?has_content>
<@_bodies_ method=response offs=offs+4/>
</#if>
</#macro>

<#--
  -- example
  -->
<#macro _example_ parent offs>
<#if parent.singleLine>
<#list 1..*offs+2 as i> </#list>example: ${parent.example}
<#else>
<#list 1..*offs+2 as i> </#list>example: |
${parent.getExample(offs + 4)}
</#if>
</#macro>

<#--
  -- write out method description
  -->
<#macro write_description methodDoc offs>
<#list 1..offs as i> </#list>description: |
${methodDoc.indentedCommentText(offs+4)}
</#macro>

<#--
  -- write out method auth scopes
  -->
<#macro write_auth_scopes methodDoc offs>
    <#list 1..offs as i> </#list>securedBy: [ oauth_2_0: { scopes: ${methodDoc.authScopesAsString} } ]
</#macro>

<#--
  -- write out method traits
  -->
<#macro write_traits methodDoc offs>
    <#list 1..offs as i> </#list>is: ${methodDoc.traitsAsString}
</#macro>

<#--
  -- uri parameters
  -->
<#macro _uri_parameters_ resource offs>
<#list 1..*offs as i> </#list>uriParameters:
<#list resource.uriParameters as uriParameter>
<@_parameter_ parameter=uriParameter offs=offs/>
</#list>
<#--<#assign fields=resource.resourceUrlSubstitutions.fields>-->
<#--<#list fields?keys as key>-->
<#--<@write_parameter fields=fields key=key offs=offs+4/>-->
<#--</#list>-->
</#macro>

<#--
  -- write out all query parameters for a method
  -->
<#macro _write_parameters methodDoc offs>
    <#list 1..offs as i> </#list>queryParameters:
    <#assign fields=methodDoc.urlParameters.fields>
    <#--<#list fields?keys as key>-->
        <#--<@_parameter_ fields=fields key=key offs=offs+4/>-->
    <#--</#list>-->
</#macro>


<#--
  -- paramter
  -->
<#macro _parameter_ parameter offs>
<#list 1..*offs+2 as i> </#list>${parameter.name}:
<#if parameter.displayName??>
<#list 1..*offs+4 as i> </#list>displayName: ${parameter.displayName}
</#if>
<#if parameter.description??>
<#list 1..*offs+4 as i> </#list>description: |
${parameter.getDescription(offs + 6)}
</#if>
<#if parameter.def??>
<#list 1..*offs+4 as i> </#list>default: ${parameter.def}
</#if>
<#if parameter.type??>
<#list 1..*offs+4 as i> </#list>type: ${parameter.getType()}
</#if>
<#if parameter.pattern??>
<#list 1..*offs+4 as i> </#list>pattern: ${parameter.pattern}
</#if>
<#if parameter.enum??>
<#list 1..*offs+4 as i> </#list>enum: ${parameter.enum}
</#if>
<#list 1..*offs+4 as i> </#list>required: ${parameter.required?c}
<#if parameter.example??>
<@_example_ parent=parameter offs=offs+2/>
</#if>
<#--<@write_parameter_info field=fields[key] offs=offs+4/>-->
</#macro>

<#--
  -- write out a single url parameter type
  -->
<#macro write_parameter_info field offs>
    <#list 1..offs as i> </#list>description: |
    <#list 1..offs as i> </#list>    ${field.fieldDescription!}
    <#if field.fieldType.class.name == "org.versly.rest.wsdoc.impl.JsonPrimitive">
        <#list 1..offs as i> </#list>type: <@write_raml_type field.fieldType.typeName/>
        <#if field.fieldType.restrictions??><#t>
            <#list 1..offs as i> </#list>enum: [ <#list field.fieldType.restrictions as restricton>${restricton}<#if restricton_has_next>, </#if></#list> ]</#if>
    <#else>
        <#list 1..offs as i> </#list>type: string
    </#if>
</#macro>


<#--
  -- write out request or response body
  -->
<#macro write_body schema example offs>
    <#list 1..offs as i> </#list>body:
    <@write_body_media_type schema=schema example=example offs=offs+4/>
</#macro>


<#--
  -- write out media type of request body
  -- (TODO: allow for more interesting media types)
  -->
<#macro write_body_media_type schema example offs>
    <#list 1..offs as i> </#list>application/json:
    <@write_body_schema schema=schema offs=offs+4/>
    <@write_body_example example=example offs=offs+4/>
</#macro>


<#--
  -- write out all url parameters for a method
  -->
<#macro write_body_schema schema offs>
    <#list 1..offs as i> </#list>schema: |
    <#list 1..offs as i> </#list>    ${schema?trim}
</#macro>

<#macro write_body_example example offs>
    <#list 1..offs as i> </#list>example: |
    <#list 1..offs as i> </#list>    ${example?trim}
</#macro>

<#--
  -- write out response for a method
  -->
<#macro write_response methodDoc offs>
    <#list 1..offs as i> </#list>responses:
    <@write_response_code methodDoc=methodDoc offs=offs+4/>
</#macro>


<#--
  -- write out response code for a method
  -->
<#macro write_response_code methodDoc offs>
    <#list 1..offs as i> </#list>200:
    <#if methodDoc.responseSchema??>
        <@write_body schema=methodDoc.responseSchema example=methodDoc.responseExample offs=offs+4/>
    </#if>
</#macro>


<#--
  -- RAML has a limited set of types for URI template parameters
  -->
<#macro write_raml_type type>
    <#if type == "float" || type == "double">number
    <#elseif type == "integer" || type == "long" || type == "short" || type == "byte" >integer
    <#elseif type == "date" || type == "timestamp" || type == "time">date
    <#elseif type == "boolean">boolean
    <#else>string
    </#if>
</#macro>
