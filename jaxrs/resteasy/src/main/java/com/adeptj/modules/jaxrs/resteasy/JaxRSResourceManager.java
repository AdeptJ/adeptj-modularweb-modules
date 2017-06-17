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
package com.adeptj.modules.jaxrs.resteasy;

import org.jboss.resteasy.spi.Registry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * JaxRSResourceManager is an OSGi ServiceTrackerCustomizer which registers the services annotated with JAX-RS @Path
 * annotation with RESTEasy resource registry.
 *
 * Note: All the registered JAX-RS resources are Singleton by default.
 * 
 * @author Rakesh.Kumar, AdeptJ.
 */
class JaxRSResourceManager implements ServiceTrackerCustomizer<Object, Object> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSResourceManager.class);

	private Registry registry;

	private BundleContext context;
    
	JaxRSResourceManager(BundleContext context, Registry registry) {
	    this.context = context;
		this.registry = registry;
	}

	@Override
	public Object addingService(ServiceReference<Object> reference) {
		Object resource = this.context.getService(reference);
		LOGGER.info("Adding JAX-RS Resource: [{}]", resource);
        this.addJaxRSResource(resource);
		return resource;
	}

    /**
     * Removes the given Resource from RESTEasy Registry and registers again the modified service.
     */
    @Override
    public void modifiedService(ServiceReference<Object> reference, Object service) {
        LOGGER.info("Service is modified, removing JAX-RS Resource: [{}]", service);
        Optional.ofNullable(service).ifPresent(consumer -> this.registry.removeRegistrations(service.getClass()));
        LOGGER.info("Adding JAX-RS Resource again: [{}]", service);
        this.addJaxRSResource(service);
    }

    @Override
	public void removedService(ServiceReference<Object> reference, Object service) {
        this.context.ungetService(reference);
		LOGGER.info("Removing JAX-RS Resource: [{}]", service);
		this.registry.removeRegistrations(service.getClass());
	}

	private void addJaxRSResource(Object resource) {
        Optional.ofNullable(resource).ifPresent(consumer -> this.registry.addSingletonResource(resource));
    }
}
