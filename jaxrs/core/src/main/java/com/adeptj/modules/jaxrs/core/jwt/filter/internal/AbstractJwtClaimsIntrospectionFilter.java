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

import com.adeptj.modules.jaxrs.core.jwt.JwtSecurityContext;
import com.adeptj.modules.jaxrs.core.jwt.filter.JwtClaimsIntrospectionFilter;
import com.adeptj.modules.security.jwt.JwtClaims;
import com.adeptj.modules.security.jwt.JwtService;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import static com.adeptj.modules.jaxrs.core.JaxRSConstants.KEY_JWT_CLAIMS;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.KEY_JWT_EXPIRED;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * Implements the {@link #filter} from {@link ContainerRequestContext}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public abstract class AbstractJwtClaimsIntrospectionFilter implements JwtClaimsIntrospectionFilter {

    /**
     * This method does the following.
     * <p>
     * 1. Checks if the {@link JwtService} is null, if so, then abort the request processing with a 503.
     * 2. Extract Jwt from request (either from cookies or headers), if Jwt is null then abort the request processing with a 400.
     * 3. Verify Jwt from {@link JwtService}, if a null {@link JwtClaims} is returned then abort the request processing with a 401.
     * 4. Last step is to pass the {@link JwtClaims} to the claims introspector implementation for further processing.
     *
     * @param requestContext the JaxRs request context
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        JwtClaims claims = (JwtClaims) requestContext.getProperty(KEY_JWT_CLAIMS);
        if (claims == null || !(requestContext.getSecurityContext() instanceof JwtSecurityContext)) {
            requestContext.abortWith(Response.status(UNAUTHORIZED).build());
            return;
        }
        requestContext.setProperty(KEY_JWT_EXPIRED, claims.isExpired());
        this.getClaimsIntrospector().introspect(requestContext, claims.asMap());
    }
}
