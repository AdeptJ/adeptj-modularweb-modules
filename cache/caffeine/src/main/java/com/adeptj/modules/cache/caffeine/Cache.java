/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://adeptj.com)                               #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/

package com.adeptj.modules.cache.caffeine;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * The {@link Cache}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface Cache<K, V> {

    /**
     * Each Cache instance has a name which this method returns to the caller.
     *
     * @return the unique cache name.
     */
    String getName();

    V get(K key, Function<? super K, ? extends V> mappingFunction);

    V getIfPresent(K key);

    Map<K, V> getAllPresent(Iterable<K> keys);

    Map<K, V> getAll();

    void put(K key, V value);

    void remove(K key);

    void remove(Iterable<K> keys);

    void evict();

    long size();

    Set<K> keys();

    Collection<V> values();
}
