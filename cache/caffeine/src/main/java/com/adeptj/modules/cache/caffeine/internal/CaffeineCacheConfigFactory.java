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

package com.adeptj.modules.cache.caffeine.internal;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import static com.adeptj.modules.cache.caffeine.internal.CaffeineCacheConfigFactory.PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Factory for creating CaffeineCache configurations.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = CaffeineCacheConfig.class, factory = true)
@Component(service = CaffeineCacheConfigFactory.class, name = PID, configurationPolicy = REQUIRE)
public class CaffeineCacheConfigFactory {

    static final String PID = "com.adeptj.modules.cache.caffeine.CaffeineCacheConfig.factory";

    private final String cacheName;

    private final String cacheSpec;

    @Activate
    public CaffeineCacheConfigFactory(@NotNull CaffeineCacheConfig cacheConfig) {
        this.cacheName = cacheConfig.cache_name();
        this.cacheSpec = cacheConfig.cache_spec();
        Validate.isTrue(StringUtils.isNotEmpty(this.cacheName), "cacheName can't be blank!!");
        Validate.isTrue(StringUtils.isNotEmpty(this.cacheSpec), "cacheSpec can't be blank!!");
    }

    public String getCacheName() {
        return this.cacheName;
    }

    public String getCacheSpec() {
        return this.cacheSpec;
    }

    @Override
    public String toString() {
        return "CaffeineCacheConfigFactory[" +
                "cacheName='" + cacheName + '\'' +
                ", cacheSpec='" + cacheSpec + '\'' +
                ']';
    }
}