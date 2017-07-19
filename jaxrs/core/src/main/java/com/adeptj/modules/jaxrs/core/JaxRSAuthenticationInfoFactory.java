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

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.adeptj.modules.jaxrs.core.JaxRSAuthenticationInfoFactory.COMPONENT_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.IGNORE;

/**
 * JaxRSAuthenticationInfoFactory.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Designate(ocd = JaxRSAuthenticationConfig.class, factory = true)
@Component(
        immediate = true,
        name = COMPONENT_NAME,
        property = SERVICE_PID + "=" + COMPONENT_NAME,
        configurationPolicy = IGNORE,
        service = {
                JaxRSAuthenticationInfoFactory.class,
                ManagedServiceFactory.class
        }
)
public class JaxRSAuthenticationInfoFactory implements ManagedServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSAuthenticationInfoFactory.class);

    static final String COMPONENT_NAME = "com.adeptj.modules.jaxrs.base.JaxRSAuthenticationInfoFactory.factory";

    private static final String FACTORY_NAME = "AdeptJ JAX-RS AuthenticationInfo Factory";

    private Map<String, JaxRSAuthenticationInfo> authenticationInfoMap = new ConcurrentHashMap<>();

    private Map<String, String> pidVsSubjectMappings = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        String subject = (String) properties.get("subject");
        LOGGER.info("Creating JaxRSAuthenticationInfo for Subject: [{}]", subject);
        this.pidVsSubjectMappings.put(pid, subject);
        this.authenticationInfoMap.put(subject, new JaxRSAuthenticationInfo(subject, (String) properties.get("password")));
    }

    @Override
    public void deleted(String pid) {
        Optional.ofNullable(this.pidVsSubjectMappings.remove(pid)).ifPresent(subject -> {
            LOGGER.info("JaxRSAuthenticationInfo removed for Subject: [{}]", subject);
            this.authenticationInfoMap.remove(subject);
        });
    }

    public JaxRSAuthenticationInfo getAuthenticationInfo(String subject, String password) {
        return this.authenticationInfoMap.get(subject);
    }
}
