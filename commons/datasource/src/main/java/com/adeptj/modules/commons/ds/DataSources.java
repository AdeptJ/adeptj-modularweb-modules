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
import org.apache.commons.lang3.ArrayUtils;

import java.util.stream.Stream;

import static com.adeptj.modules.commons.utils.Constants.EQ;

/**
 * Utility for creating {@link HikariDataSource} instances.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class DataSources {

    public static HikariDataSource newDataSource(DataSourceConfig config) {
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
        Stream.of(config.dataSourceProperties())
                .filter(row -> ArrayUtils.getLength(row.split(EQ)) == 2)
                .forEach(entry -> {
                    String[] mapping = entry.split(EQ);
                    hikariConfig.addDataSourceProperty(mapping[0], mapping[1]);
                });
        return new HikariDataSource(hikariConfig);
    }
}
