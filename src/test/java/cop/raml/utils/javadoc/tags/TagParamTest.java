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
package cop.raml.utils.javadoc.tags;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 22.12.2016
 */
public class TagParamTest {
    @Test
    public void checkNullObject() {
        assertThat(TagParam.NULL.getText()).isNull();
        assertThat(TagParam.NULL.getName()).isNull();
        assertThat(TagParam.NULL.getPattern()).isNull();
        assertThat(TagParam.NULL.getEnum()).isNull();
        assertThat(TagParam.NULL.getDefault()).isNull();
        assertThat(TagParam.NULL.getExample()).isNull();
        assertThat(TagParam.NULL.getLink()).isSameAs(TagLink.NULL);
        assertThat(TagParam.NULL.isRequired()).isFalse();
    }

    @Test
    public void shouldCreateNullObjectForEmptyBuilder() {
        assertThat(TagParam.builder().build()).isSameAs(TagParam.NULL);
        assertThat(TagParam.builder().link(null).build()).isSameAs(TagParam.NULL);
    }

    @Test
    public void shouldCreateValidTagParam() {
        TagParam obj = TagParam.builder()
                               .text("text")
                               .name("name")
                               .pattern("pattern")
                               .anEnum("enum")
                               .def("def")
                               .example("example")
                               .link(TagLink.create("class", "param"))
                               .required(true).build();

        assertThat(obj).isNotSameAs(TagReturn.NULL);
        assertThat(obj.getText()).isEqualTo("text");
        assertThat(obj.getName()).isEqualTo("name");
        assertThat(obj.getPattern()).isEqualTo("pattern");
        assertThat(obj.getEnum()).isEqualTo("enum");
        assertThat(obj.getDefault()).isEqualTo("def");
        assertThat(obj.getExample()).isEqualTo("example");
        assertThat(obj.getLink().getClassName()).isEqualTo("class");
        assertThat(obj.getLink().getParam()).isEqualTo("param");
        assertThat(obj.isRequired()).isTrue();
    }

    @Test
    public void shouldReturnToStringForGoodObject() {
        TagParam obj = TagParam.builder()
                               .text("text")
                               .name("name")
                               .pattern("pattern")
                               .anEnum("enum")
                               .def("def")
                               .example("example")
                               .link(TagLink.create("class", "param"))
                               .required(true).build();
        assertThat(obj.toString()).isEqualTo("text=text, name=name, pattern=pattern, enum=enum, default=def, example=example," +
                " {@link class#param}, (required)");
    }

    @Test
    public void checkBuilderCreateCornerCases() {
        TagParam obj = TagParam.builder().build();
        assertThat(obj).isSameAs(TagParam.NULL);
        assertThat(obj.toString()).isEqualTo("<null>");

        obj = TagParam.builder().text("text").build();
        assertThat(obj).isNotSameAs(TagParam.NULL);
        assertThat(obj.toString()).isEqualTo("text=text, (optional)");

        obj = TagParam.builder().name("name").build();
        assertThat(obj).isNotSameAs(TagParam.NULL);
        assertThat(obj.toString()).isEqualTo("name=name, (optional)");

        obj = TagParam.builder().pattern("pattern").build();
        assertThat(obj).isNotSameAs(TagParam.NULL);
        assertThat(obj.toString()).isEqualTo("pattern=pattern, (optional)");

        obj = TagParam.builder().anEnum("enum").build();
        assertThat(obj).isNotSameAs(TagParam.NULL);
        assertThat(obj.toString()).isEqualTo("enum=enum, (optional)");

        obj = TagParam.builder().def("def").build();
        assertThat(obj).isNotSameAs(TagParam.NULL);
        assertThat(obj.toString()).isEqualTo("default=def, (optional)");

        obj = TagParam.builder().example("example").build();
        assertThat(obj).isNotSameAs(TagParam.NULL);
        assertThat(obj.toString()).isEqualTo("example=example, (optional)");

        obj = TagParam.builder().link(TagLink.create("class", "param")).build();
        assertThat(obj).isNotSameAs(TagReturn.NULL);
        assertThat(obj.toString()).isEqualTo("{@link class#param}, (optional)");

        obj = TagParam.builder().text("text").required(true).build();
        assertThat(obj).isNotSameAs(TagParam.NULL);
        assertThat(obj.toString()).isEqualTo("text=text, (required)");
    }
}
