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

package com.adeptj.modules.jaxrs.core.jwt;

import com.adeptj.modules.jaxrs.core.JaxRSResource;
import com.adeptj.modules.jaxrs.core.auth.JaxRSAuthenticationOutcome;
import com.adeptj.modules.jaxrs.core.auth.SimpleCredentials;
import com.adeptj.modules.jaxrs.core.auth.spi.JaxRSAuthenticator;
import com.adeptj.modules.security.jwt.JwtService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import javax.validation.constraints.NotEmpty;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

/**
 * JAX-RS resource for issuance and verification of JWT.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@JaxRSResource(name = "jwt")
@Path("/auth/jwt")
@Designate(ocd = JwtCookieConfig.class)
@Component(immediate = true, service = JwtResource.class)
public class JwtResource {

    /**
     * The {@link JwtService} is optionally referenced.
     * If unavailable this resource will set a Service Unavailable (503) status.
     * <p>
     * Note: As per Felix SCR, dynamic references should be declared as volatile.
     */
    @Reference(cardinality = OPTIONAL, policy = DYNAMIC, policyOption = GREEDY)
    private volatile JwtService jwtService;

    /**
     * The JaxRSAuthenticator reference;
     */
    @Reference
    private JaxRSAuthenticator authenticator;

    /**
     * Create Jwt for the username with given credentials.
     *
     * @param username the username submitted for authentication
     * @param password the password string submitted for authentication
     * @return JAX-RS Response either having a Jwt or Http error 503
     */
    @POST
    @Path("/create")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response createJwt(@NotEmpty @FormParam("username") String username,
                              @NotEmpty @FormParam("password") String password) {
        if (this.jwtService == null) {
            return Response.status(SERVICE_UNAVAILABLE).build();
        }
        JaxRSAuthenticationOutcome outcome = this.authenticator.handleSecurity(SimpleCredentials.of(username, password));
        return outcome == null
                ? Response.status(UNAUTHORIZED).build()
                : JaxRSUtil.createResponseWithJwt(this.jwtService.createJwt(username, outcome));
    }

    // <----------------------------------------------- OSGi INTERNAL ------------------------------------------------->

    @Modified
    @Activate
    protected void start(JwtCookieConfig cookieConfig) {
        JwtCookieConfigHolder.getInstance().setJwtCookieConfig(cookieConfig);
    }
}
