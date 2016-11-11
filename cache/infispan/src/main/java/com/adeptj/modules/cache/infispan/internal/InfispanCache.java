/* 
 * =============================================================================
 * 
 * Copyright (c) 2016 AdeptJ
 * Copyright (c) 2016 Rakesh Kumar <irakeshk@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * =============================================================================
 */
package com.adeptj.modules.cache.infispan.internal;

import java.util.Collection;
import java.util.Set;

import com.adeptj.modules.cache.api.Cache;

/**
 * Implementation for Cache interface, internally this uses the Infispan for performing the low level operations.
 * 
 * @author Rakesh.Kumar
 */
public class InfispanCache<K, V> implements Cache<K, V> {

	@Override
	public V get(K key) {
		return null;
	}

	@Override
	public V put(K key, V value) {
		return null;
	}

	@Override
	public V remove(K key) {
		return null;
	}

	@Override
	public void clear() {
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Set<K> keys() {
		return null;
	}

	@Override
	public Collection<V> values() {
		return null;
	}
	
}