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

package com.adeptj.modules.jaxrs.core.auth.internal;

import com.adeptj.modules.jaxrs.core.auth.JaxRSAuthenticationOutcome;
import com.adeptj.modules.jaxrs.core.auth.SimpleCredentials;
import com.adeptj.modules.jaxrs.core.auth.api.JaxRSAuthenticationRealm;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of JaxRSAuthenticationRealm.
 *
 * Authenticates using the {@link JaxRSCredentialsCollector}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true)
public class DefaultJaxRSAuthenticationRealm implements JaxRSAuthenticationRealm {

    @Reference
    private JaxRSCredentialsCollector credentialsCollector;

    /**
     * {@inheritDoc}
     */
    @Override
    public int priority() {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JaxRSAuthenticationOutcome authenticate(SimpleCredentials credentials) {
        List<String> roles = new ArrayList<>();
        roles.add(credentials.getUsername());
        roles.add("OSGiAdmin");
        JaxRSAuthenticationOutcome outcome = new JaxRSAuthenticationOutcome().addAttribute("roles", roles);
        return this.credentialsCollector.matchCredentials(credentials) ? outcome : null;
    }
}
