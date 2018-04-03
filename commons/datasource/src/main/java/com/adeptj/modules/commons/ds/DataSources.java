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

package com.adeptj.modules.commons.ds;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Utility for managing {@link HikariDataSource} instances.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class DataSources {

    public static HikariDataSource createDataSource(DataSourceConfig config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(config.poolName());
        hikariConfig.setJdbcUrl(config.jdbcUrl());
        hikariConfig.setDriverClassName(config.driverClassName());
        hikariConfig.setUsername(config.username());
        hikariConfig.setPassword(config.password());
        hikariConfig.setAutoCommit(config.autoCommit());
        hikariConfig.setConnectionTimeout(config.connectionTimeout());
        hikariConfig.setIdleTimeout(config.idleTimeout());
        hikariConfig.setMaxLifetime(config.maxLifetime());
        hikariConfig.setMinimumIdle(config.minimumIdle());
        hikariConfig.setMaximumPoolSize(config.maximumPoolSize());
        return new HikariDataSource(hikariConfig);
    }
}
