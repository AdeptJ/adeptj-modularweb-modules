/** 
###############################################################################
#                                                                             # 
#    Copyright 2016, AdeptJ (http://adeptj.com)                               #
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
package com.adeptj.modules.cache.geode.internal;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

import com.adeptj.modules.cache.spi.CacheProvider;

/**
 * GeodeActivator.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public class GeodeActivator implements BundleActivator {

	private ServiceRegistration<?> svcReg;

	@Override
	public void start(BundleContext context) throws Exception {
		Hashtable<String, Object> props = new Hashtable<>();
		props.put(Constants.SERVICE_VENDOR, "AdeptJ");
		props.put(Constants.SERVICE_PID, GeodeCacheProvider.SERVICE_PID);
		props.put(Constants.SERVICE_DESCRIPTION, "AdeptJ OSGi CacheProvider Factory");
		this.svcReg = context.registerService(
				new String[] { ManagedServiceFactory.class.getName(), CacheProvider.class.getName() },
				new GeodeCacheProvider(), props);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		svcReg.unregister();
	}

}
