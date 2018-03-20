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
import org.jboss.resteasy.spi.Registry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * JaxRSResourceTracker is an OSGi ServiceTracker which registers the services annotated with JAX-RS
 * &#064;Path annotation with RESTEasy resource registry.
 * <p>
 * Note: As of now all the registered JAX-RS resources are singleton by default.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JaxRSResourceTracker extends ServiceTracker<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSResourceTracker.class);

    private static final String RES_FILTER_EXPR = "(&(objectClass=*)(osgi.jaxrs.resource.base=*))";

    private Registry registry;

    JaxRSResourceTracker(BundleContext context, Registry registry) {
        super(context, OSGiUtils.anyServiceFilter(context, RES_FILTER_EXPR), null);
        this.registry = registry;
    }

    @Override
    public Object addingService(ServiceReference<Object> reference) {
        Object resource = super.addingService(reference);
        if (resource == null) {
            LOGGER.warn("JAX-RS Resource is null for ServiceReference: {}", reference);
        } else {
            LOGGER.info("Adding JAX-RS Resource: [{}]", resource);
            this.registry.addSingletonResource(resource);
        }
        return resource;
    }

    /**
     * Removes the given Resource from RESTEasy Registry and registers again the modified service.
     *
     * @param reference the OSGi service reference of JAX-RS resource.
     * @param service   the OSGi service of JAX-RS resource.
     */
    @Override
    public void modifiedService(ServiceReference<Object> reference, Object service) {
        LOGGER.info("Service is modified, removing JAX-RS Resource: [{}]", service);
        Optional.ofNullable(service).ifPresent(resource -> this.registry.removeRegistrations(resource.getClass()));
        LOGGER.info("Adding JAX-RS Resource again: [{}]", service);
        this.registry.addSingletonResource(service);
    }

    @Override
    public void removedService(ServiceReference<Object> reference, Object service) {
        super.removedService(reference, service);
        LOGGER.info("Removing JAX-RS Resource: [{}]", service);
        this.registry.removeRegistrations(service.getClass());
    }
}
