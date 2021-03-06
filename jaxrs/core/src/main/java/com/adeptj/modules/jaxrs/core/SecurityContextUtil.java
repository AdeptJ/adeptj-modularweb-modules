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

package com.adeptj.modules.jaxrs.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * Utilities for JAX-RS {@link javax.ws.rs.core.SecurityContext}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class SecurityContextUtil {

    // Just static utilities, no instance needed.
    private SecurityContextUtil() {
    }

    public static @Nullable JwtPrincipal getJwtPrincipal(@NotNull SecurityContext securityContext) {
        Principal principal = securityContext.getUserPrincipal();
        return principal instanceof JwtPrincipal ? (JwtPrincipal) principal : null;
    }

    public static boolean isJwtExpired(@NotNull ContainerRequestContext requestContext) {
        JwtPrincipal principal = getJwtPrincipal(requestContext.getSecurityContext());
        return principal == null || principal.isHoldingExpiredJwt();
    }

}
