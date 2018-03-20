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

import com.adeptj.modules.commons.utils.OSGiUtils;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JaxRSProviderTracker is an OSGi ServiceTracker which registers the services annotated with JAX-RS
 * &#064;Provider annotation with RESTEasy provider registry.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JaxRSProviderTracker extends ServiceTracker<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSProviderTracker.class);

    private static final String PROVIDER_FILTER_EXPR = "(&(objectClass=*)(osgi.jaxrs.provider=*))";

    private ResteasyProviderFactory providerFactory;

    JaxRSProviderTracker(BundleContext context, ResteasyProviderFactory providerFactory) {
        super(context, OSGiUtils.anyServiceFilter(context, PROVIDER_FILTER_EXPR), null);
        this.providerFactory = providerFactory;
    }

    @Override
    public Object addingService(ServiceReference<Object> reference) {
        Object resource = super.addingService(reference);
        if (resource == null) {
            LOGGER.warn("JAX-RS Provider is null for ServiceReference: {}", reference);
        } else {
            LOGGER.info("Adding JAX-RS Provider: [{}]", resource);
            this.providerFactory.register(resource);
        }
        return resource;
    }

    /**
     * Removes the given Provider from RESTEasy ProviderFactory and registers again the modified service.
     *
     * @param reference the OSGi service reference of JAX-RS Provider.
     * @param service   the OSGi service of JAX-RS Provider.
     */
    @Override
    public void modifiedService(ServiceReference<Object> reference, Object service) {
        LOGGER.info("Service is modified, removing JAX-RS Provider: [{}]", service);
        JaxRSUtil.removeJaxRSProvider(this.providerFactory, service);
        LOGGER.info("Adding JAX-RS Provider again: [{}]", service);
        this.providerFactory.register(service);
    }

    @Override
    public void removedService(ServiceReference<Object> reference, Object service) {
        super.removedService(reference, service);
        LOGGER.info("Removing JAX-RS Provider: [{}]", service);
        JaxRSUtil.removeJaxRSProvider(this.providerFactory, service);
    }
}
