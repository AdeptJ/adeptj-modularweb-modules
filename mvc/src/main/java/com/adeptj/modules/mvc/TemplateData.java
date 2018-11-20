/*
###############################################################################
#                                                                             # 
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
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

package com.adeptj.modules.mvc;

import org.trimou.engine.resolver.Mapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * TemplateData for storing data used in template rendering.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public final class TemplateData implements Mapper, Iterable<Entry<String, Object>> {

    private final Map<String, Object> data;

    private TemplateData() {
        this.data = new HashMap<>();
    }

    public static TemplateData newTemplateData() {
        return new TemplateData();
    }

    public TemplateData with(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    @Override
    public Object get(String key) {
        return this.data.get(key);
    }

    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return this.data.entrySet().iterator();
    }
}
