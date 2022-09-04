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

import edu.emory.mathcs.backport.java.util.Collections;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * @author Oleg Cherednik
 * @since 29.01.2017
 */
@SuppressWarnings("InstanceMethodNamingConvention")
public class AutoCreateMapTest {
    private AutoCreateMap<String, String> map;

    @BeforeMethod
    private void initContext() {
        map = new AutoCreateMap<>();
    }

    @Test
    public void shouldThrowExceptionWhenKeyOrCreatorNotSet() {
        assertThatThrownBy(() -> map.put(null, new CreatorImpl("val"))).isExactlyInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> map.put("key", (AutoCreateMap.Creator<String, String>)null)).isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldUserCreatorWhenAddNewKey() {
        assertThat(map.put("one", new CreatorImpl("val_one"))).isEqualTo("val_one");
        assertThat(map.put("one", new CreatorImpl("val_two"))).isEqualTo("val_one");
    }

    @Test
    public void shouldRewriteKeyWhenPutKeyValue() {
        assertThat(map.put("one", "val_one")).isNull();
        assertThat(map.put("one", "val_two")).isEqualTo("val_one");
        assertThat(map.get("one")).isEqualTo("val_two");
        assertThat(map.values()).hasSize(1);
    }

    @Test
    public void shouldReturnEmptySetWhenKeySetAndEmpty() {
        assertThat(map.keySet()).isSameAs(Collections.emptySet());
    }

    @Test
    public void shouldReturnUnmodifiableSetWhenKeySetAndNotEmpty() {
        map.put("one", "val_one");
        map.put("two", "val_two");

        assertThat(map.keySet()).hasSize(2);
        assertThatThrownBy(() -> map.keySet().iterator().remove()).isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void shouldRemoveKey() {
        map.put("one", "val_one");
        map.put("two", "val_two");
        assertThat(map.keySet()).hasSize(2);

        assertThat(map.remove("two")).isEqualTo("val_two");
        assertThat(map.keySet()).hasSize(1);
    }

    @Test
    public void shouldReturnValues() {
        map.put("one", "val_one");
        map.put("two", "val_two");
        assertThat(map.values()).hasSize(2);
    }

    @Test
    public void shouldUserLinkedHashMapWhenUseNoArgumentConstructor() {
        map.put("2", "val_2");
        map.put("1", "val_1");
        assertThat(map.keySet()).containsExactly("2", "1");
    }

    @Test
    public void shouldUserProvidedMapWhenUseConstructorWithArgument() {
        map = new AutoCreateMap<>(new TreeMap<>());
        map.put("2", "val_2");
        map.put("1", "val_1");
        assertThat(map.keySet()).containsExactly("1", "2");
    }

    // ========== data ==========

    private static final class CreatorImpl implements AutoCreateMap.Creator<String, String> {
        private final String str;

        public CreatorImpl(String str) {
            this.str = str;
        }

        @Override
        public String create(String key) {
            return str;
        }
    }
}
