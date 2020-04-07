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
package com.adeptj.modules.jaxrs.core.jwt.filter.internal;

import com.adeptj.modules.jaxrs.core.JaxRSProvider;
import com.adeptj.modules.jaxrs.core.jwt.JwtClaimsIntrospector;
import com.adeptj.modules.jaxrs.core.jwt.RequiresJwt;
import com.adeptj.modules.jaxrs.core.jwt.filter.JwtClaimsIntrospectionFilter;
import com.adeptj.modules.security.jwt.JwtService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;

import static com.adeptj.modules.jaxrs.core.jwt.filter.internal.StaticJwtClaimsIntrospectionFilter.FILTER_NAME;
import static javax.ws.rs.Priorities.AUTHORIZATION;
import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

/**
 * This filter will kick in for any resource class/method that is annotated with {@link RequiresJwt}.
 * Filter will try to extract the Jwt first from HTTP Authorization header and if that resolves to null
 * then try to extract from Cookies.
 * <p>
 * A Cookie named as per configuration should be present in request.
 * <p>
 * If a non null Jwt is resolved then verify it using {@link JwtService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@JaxRSProvider(name = "StaticJwtFilter")
@RequiresJwt
@Priority(AUTHORIZATION)
@Provider
@Component(service = JwtClaimsIntrospectionFilter.class, immediate = true, property = FILTER_NAME)
public class StaticJwtClaimsIntrospectionFilter extends AbstractJwtClaimsIntrospectionFilter {

    static final String FILTER_NAME = "jwt.filter.type=static";

    @Reference(cardinality = OPTIONAL, policy = DYNAMIC)
    private volatile JwtClaimsIntrospector claimsIntrospector;

    @Override
    public JwtClaimsIntrospector getClaimsIntrospector() {
        return this.claimsIntrospector;
    }

    // <<------------------------------------------- OSGi INTERNAL ------------------------------------------->>

    @Activate
    protected void start() {
        if (this.claimsIntrospector == null) {
            this.claimsIntrospector = DefaultJwtClaimsIntrospector.INSTANCE;
        }
    }
}
