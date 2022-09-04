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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Oleg Cherednik
 * @since 29.01.2017
 */
public final class AutoCreateMap<K, V> {
    private final Map<K, V> map;

    public AutoCreateMap() {
        this(new LinkedHashMap<>());
    }

    public AutoCreateMap(Map<K, V> map) {
        this.map = map;
    }

    public V put(K key, Creator<K, V> creator) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(creator);

        if (map.containsKey(key))
            return map.get(key);

        V obj = creator.create(key);
        map.put(key, obj);
        return obj;
    }

    public V put(K key, V obj) {
        return map.put(key, obj);
    }

    public Set<K> keySet() {
        return map.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(map.keySet());
    }

    public V get(K key) {
        return map.get(key);
    }

    public V remove(K key) {
        return map.remove(key);
    }

    public Collection<V> values() {
        return map.values();
    }

    // --------- creator ----------

    public interface Creator<K, V> {
        V create(K key);
    }
}
