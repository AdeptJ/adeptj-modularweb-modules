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

package com.adeptj.modules.commons.jdbc.util;

import com.adeptj.modules.commons.jdbc.service.internal.DataSourceConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Utility for creating {@link HikariDataSource} instances.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class DataSources {

    private static final String EQ = "=";

    @Contract("_ -> new")
    public static @NotNull HikariDataSource createHikariDataSource(@NotNull DataSourceConfig config) {
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
                .filter(StringUtils::isNotEmpty)
                .map(row -> row.split(EQ))
                .filter(parts -> ArrayUtils.getLength(parts) == 2)
                .forEach(parts -> hikariConfig.addDataSourceProperty(parts[0].trim(), parts[1].trim()));
        return new HikariDataSource(hikariConfig);
    }
}
