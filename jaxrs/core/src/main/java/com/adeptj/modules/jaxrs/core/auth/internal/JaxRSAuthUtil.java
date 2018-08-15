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

import com.adeptj.modules.commons.utils.Loggers;
import com.adeptj.modules.jaxrs.core.auth.JaxRSAuthenticationOutcome;
import com.adeptj.modules.jaxrs.core.auth.SimpleCredentials;
import com.adeptj.modules.jaxrs.core.auth.api.JaxRSAuthenticationRealm;
import org.slf4j.Logger;

/**
 * Utility methods for {@link JaxRSAuthenticationOutcome} and {@link JaxRSAuthenticationRealm}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JaxRSAuthUtil {

    private static final Logger LOGGER = Loggers.get(JaxRSAuthUtil.class);

    private JaxRSAuthUtil() {
    }

    static JaxRSAuthenticationOutcome getJaxRSAuthOutcome(JaxRSAuthenticationRealm realm, SimpleCredentials credentials) {
        JaxRSAuthenticationOutcome outcome = null;
        try {
            outcome = realm.authenticate(credentials);
        } catch (Exception ex) { // NOSONAR
            // Gulping everything so that next realms(if any) get a chance.
            LOGGER.error(ex.getMessage(), ex);
        }
        return outcome;
    }
}
