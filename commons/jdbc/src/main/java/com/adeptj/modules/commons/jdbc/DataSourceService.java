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

package com.adeptj.modules.commons.jdbc;

import org.osgi.annotation.versioning.ProviderType;

import javax.sql.DataSource;

/**
 * DataSourceService interface for providing JDBC DataSource.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@ProviderType
public interface DataSourceService {

    /**
     * Returns the configured {@link DataSource} instance against the name passed.
     *
     * @param name the name of the JDBC pool.
     * @return The configured {@link DataSource} instance against the name passed.
     * @throws IllegalStateException if {@link DataSource} is not configured.
     */
    DataSource getDataSource(String name);
}
