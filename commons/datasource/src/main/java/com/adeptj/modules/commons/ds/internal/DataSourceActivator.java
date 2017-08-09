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

package com.adeptj.modules.commons.ds.internal;

import com.adeptj.modules.commons.ds.DataSources;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * A {@link BundleActivator} for closing all the remaining opened {@link com.zaxxer.hikari.HikariDataSource}
 * instances when this bundle stops.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class DataSourceActivator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        // Nothing to do as such on startup
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        DataSources.INSTANCE.closeAll();
    }
}
