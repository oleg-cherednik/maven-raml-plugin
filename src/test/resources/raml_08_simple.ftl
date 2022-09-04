<#-- @ftlvariable name="api" type="cop.raml.processor.RestApi" -->
#%RAML 0.8
---
title: ${api.title}
baseUri: ${api.baseUri}
version: ${api.version}
mediaType: ${api.mediaType}
documentation:
  - title: ${api.docTitle}
    content: |
${api.getDocContent(6)}
