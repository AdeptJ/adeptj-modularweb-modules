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

/**
 * Thrown when an instance of {@link com.zaxxer.hikari.HikariDataSource} is requested even before it is configured.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class DataSourceNotConfiguredException extends RuntimeException {

    private static final long serialVersionUID = -4649854107775357466L;

    public DataSourceNotConfiguredException(String message) {
        super(message);
    }
}