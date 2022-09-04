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
 * @since 16.12.2016
 */
public class TagReturnTest {
    @Test
    public void checkNullObject() {
        assertThat(TagReturn.NULL.getStatus()).isEqualTo(-1);
        assertThat(TagReturn.NULL.getText()).isNull();
        assertThat(TagReturn.NULL.getLink()).isSameAs(TagLink.NULL);
        assertThat(TagReturn.NULL.isArray()).isFalse();
    }

    @Test
    public void shouldCreateNullObjectForEmptyBuilder() {
        assertThat(TagReturn.createBuilder().build()).isSameAs(TagReturn.NULL);
        assertThat(TagReturn.createBuilder().link(null).build()).isSameAs(TagReturn.NULL);
    }

    @Test
    public void shouldCreateValidTagReturn() {
        TagReturn obj = TagReturn.createBuilder()
                                 .status("200")
                                 .text("this is a text")
                                 .link(TagLink.create("class", "param"))
                                 .array(true).build();

        assertThat(obj).isNotSameAs(TagReturn.NULL);
        assertThat(obj.getStatus()).isEqualTo(200);
        assertThat(obj.getText()).isEqualTo("this is a text");
        assertThat(obj.getLink().getClassName()).isEqualTo("class");
        assertThat(obj.getLink().getParam()).isEqualTo("param");
        assertThat(obj.isArray()).isTrue();
    }

    @Test
    public void shouldReturnToStringForGoodObject() {
        TagReturn obj = TagReturn.createBuilder()
                                 .status("200")
                                 .text("this is a text")
                                 .link(TagLink.create("class", "param"))
                                 .array(true).build();
        assertThat(obj.toString()).isEqualTo("status=200, text='this is a text', {@link class#param}, (array)");
    }

    @Test
    public void checkBuilderCreateCornerCases() {
        TagReturn obj = TagReturn.createBuilder().build();
        assertThat(obj).isSameAs(TagReturn.NULL);
        assertThat(obj.toString()).isEqualTo("<null>");

        obj = TagReturn.createBuilder().status("200").build();
        assertThat(obj).isNotSameAs(TagReturn.NULL);
        assertThat(obj.toString()).isEqualTo("status=200");

        obj = TagReturn.createBuilder().text("this is a text").build();
        assertThat(obj).isNotSameAs(TagReturn.NULL);
        assertThat(obj.toString()).isEqualTo("text='this is a text'");

        obj = TagReturn.createBuilder().link(TagLink.create("class", "param")).build();
        assertThat(obj).isNotSameAs(TagReturn.NULL);
        assertThat(obj.toString()).isEqualTo("{@link class#param}");

        obj = TagReturn.createBuilder().array(true).build();
        assertThat(obj).isNotSameAs(TagReturn.NULL);
        assertThat(obj.toString()).isEqualTo("(array)");
    }
}
