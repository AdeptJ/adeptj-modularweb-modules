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

package com.adeptj.modules.jaxrs.resteasy.internal;

import com.adeptj.modules.jaxrs.resteasy.ApplicationExceptionMapper;
import com.adeptj.modules.jaxrs.resteasy.ResteasyConfig;
import org.jboss.resteasy.plugins.validation.ValidatorContextResolverCDI;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.validation.GeneralValidator;
import org.jboss.resteasy.spi.validation.GeneralValidatorCDI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ValidatorFactory;
import javax.ws.rs.Path;
import java.lang.invoke.MethodHandles;

/**
 * Utilities for RESTEasy bootstrap process.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class ResteasyUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private ResteasyUtil() {
    }

    static void registerProviders(ResteasyProviderFactory rpf, ResteasyConfig config, ValidatorFactory vf) {
        rpf.register(new ValidatorContextResolver(vf))
                .register(new ApplicationExceptionMapper(config.sendExceptionTrace()))
                .register(CorsFilterBuilder.newBuilder()
                        .allowCredentials(config.allowCredentials())
                        .corsMaxAge(config.corsMaxAge())
                        .exposedHeaders(config.exposedHeaders())
                        .allowedMethods(config.allowedMethods())
                        .allowedHeaders(config.allowedHeaders())
                        .allowedOrigins(config.allowedOrigins())
                        .build());
    }

    static void removeDefaultValidators(ResteasyProviderFactoryDecorator providerFactory) {
        LOGGER.info("ContextResolver(s) prior to removal: [{}]", providerFactory.getContextResolvers().size());
        providerFactory.removeContextResolvers(GeneralValidator.class, GeneralValidatorCDI.class);
        LOGGER.info("ContextResolver(s) after removal: [{}]", providerFactory.getContextResolvers().size());
    }

    static void removeProviderClasses(ResteasyProviderFactoryDecorator providerFactory) {
        LOGGER.info("ProviderClasses prior to removal: [{}]", providerFactory.getProviderClasses().size());
        providerFactory.removeProviderClasses(ValidatorContextResolver.class, ValidatorContextResolverCDI.class);
        LOGGER.info("ProviderClasses after removal: [{}]", providerFactory.getProviderClasses().size());
    }

    static void removeJaxRSProvider(ResteasyProviderFactoryDecorator providerFactory, Object provider) {
        if (providerFactory.getProviderInstances().remove(provider)) {
            LOGGER.info("Removed JAX-RS Provider: [{}]", provider);
        } else {
            LOGGER.warn("Could not remove JAX-RS Provider: [{}]", provider);
        }
    }

    static boolean isNotAnnotatedWithPath(Object resource) {
        return !resource.getClass().isAnnotationPresent(Path.class);
    }
}
