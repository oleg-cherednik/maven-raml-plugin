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

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oleg Cherednik
 * @since 12.12.2016
 */
public class TagLinkTest {
    @Test
    public void checkNullObject() {
        assertThat(TagLink.NULL.getClassName()).isNull();
        assertThat(TagLink.NULL.getParam()).isNull();
        assertThat(TagLink.NULL.isClass()).isFalse();
    }

    @Test
    public void shouldCreateNullObject() {
        assertThat(TagLink.create(null, null)).isSameAs(TagLink.NULL);
        assertThat(TagLink.create("", "")).isSameAs(TagLink.NULL);
        assertThat(TagLink.create("  ", "  ")).isSameAs(TagLink.NULL);
        assertThat(TagLink.NULL.isClass()).isFalse();
        assertThat(TagLink.NULL.toString()).isEqualTo("<null>");

        assertThat(TagLink.create(null)).isSameAs(TagLink.NULL);
        assertThat(TagLink.create("")).isSameAs(TagLink.NULL);
        assertThat(TagLink.create("  ")).isSameAs(TagLink.NULL);
        assertThat(TagLink.create("This is a text")).isSameAs(TagLink.NULL);
    }

    @Test
    public void shouldCreateClassLink() {
        TagLink link = TagLink.create("className", null);
        assertThat(link.getClassName()).isEqualTo("className");
        assertThat(link.getParam()).isNull();
        assertThat(link.isClass()).isTrue();
        assertThat(link.toString()).isEqualTo("{@link className}");

        link = TagLink.create("className", "  ");
        assertThat(link.getClassName()).isEqualTo("className");
        assertThat(link.getParam()).isNull();
        assertThat(link.toString()).isEqualTo("{@link className}");
    }

    @Test
    public void shouldCreateParamLink() {
        TagLink link = TagLink.create(null, "param");
        assertThat(link.getClassName()).isNull();
        assertThat(link.getParam()).isEqualTo("param");
        assertThat(link.isClass()).isFalse();
        assertThat(link.toString()).isEqualTo("{@link #param}");

        link = TagLink.create("  ", "param");
        assertThat(link.getClassName()).isNull();
        assertThat(link.getParam()).isEqualTo("param");
        assertThat(link.isClass()).isFalse();
        assertThat(link.toString()).isEqualTo("{@link #param}");
    }

    @Test
    public void shouldCreateClassParamLink() {
        TagLink link = TagLink.create("className", "param");
        assertThat(link.getClassName()).isEqualTo("className");
        assertThat(link.getParam()).isEqualTo("param");
        assertThat(link.isClass()).isFalse();
        assertThat(link.toString()).isEqualTo("{@link className#param}");

        link = TagLink.create("{@link className#param}");
        assertThat(link.getClassName()).isEqualTo("className");
        assertThat(link.getParam()).isEqualTo("param");
        assertThat(link.isClass()).isFalse();
        assertThat(link.toString()).isEqualTo("{@link className#param}");

        link = TagLink.create("{@link aaa.bbb.ccc#ddd}");
        assertThat(link.getClassName()).isEqualTo("aaa.bbb.ccc");
        assertThat(link.getParam()).isEqualTo("ddd");
        assertThat(link.isClass()).isFalse();
        assertThat(link.toString()).isEqualTo("{@link aaa.bbb.ccc#ddd}");

        link = TagLink.create("{@link #ccc}");
        assertThat(link.getClassName()).isNull();
        assertThat(link.getParam()).isEqualTo("ccc");
        assertThat(link.isClass()).isFalse();
        assertThat(link.toString()).isEqualTo("{@link #ccc}");
    }

    @Test
    public void shouldReturnCorrectHashCode() {
        TagLink link = TagLink.create("className", "param");
        assertThat(link.hashCode()).isEqualTo(Objects.hash(link.getClassName(), link.getParam()));
    }

    @Test
    public void shouldUseEqualsCorrectly() {
        TagLink link1 = TagLink.create("className1", "param1");
        TagLink link2 = TagLink.create("className1", "param2");
        TagLink link3 = TagLink.create("className1", "param1");
        TagLink link4 = TagLink.create("className2", "param1");

        //noinspection EqualsWithItself
        assertThat(link1.equals(link1)).isTrue();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(link1.equals(Boolean.TRUE)).isFalse();
        assertThat(link1.equals(link3)).isTrue();
        assertThat(link1.equals(link2)).isFalse();
        assertThat(link1.equals(link4)).isFalse();
    }
}
