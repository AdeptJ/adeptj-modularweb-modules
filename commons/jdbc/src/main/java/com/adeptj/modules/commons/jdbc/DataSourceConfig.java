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

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static com.adeptj.modules.commons.jdbc.DataSourceConstants.AUTO_COMMIT;
import static com.adeptj.modules.commons.jdbc.DataSourceConstants.CONN_TIMEOUT;
import static com.adeptj.modules.commons.jdbc.DataSourceConstants.DATASOURCE_PROPS;
import static com.adeptj.modules.commons.jdbc.DataSourceConstants.DRIVER_CLASS_NAME;
import static com.adeptj.modules.commons.jdbc.DataSourceConstants.IDLE_TIMEOUT;
import static com.adeptj.modules.commons.jdbc.DataSourceConstants.JDBC_URL;
import static com.adeptj.modules.commons.jdbc.DataSourceConstants.MAX_LIFETIME;
import static com.adeptj.modules.commons.jdbc.DataSourceConstants.MAX_POOL_SIZE;
import static com.adeptj.modules.commons.jdbc.DataSourceConstants.MIN_IDLE;
import static com.adeptj.modules.commons.jdbc.DataSourceConstants.POOL_NAME;
import static com.adeptj.modules.commons.jdbc.DataSourceConstants.PWD;
import static com.adeptj.modules.commons.jdbc.DataSourceConstants.USERNAME;
import static org.osgi.service.metatype.annotations.AttributeType.PASSWORD;

/**
 * HikariDataSource configurations, few configurations defaults to MySQL DB.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ JDBC DataSource Configurations",
        description = "Configurations for JDBC DataSource(HikariDataSource)."
)
public @interface DataSourceConfig {

    long DEFAULT_CONN_TIMEOUT = 30000L;

    long DEFAULT_IDLE_TIMEOUT = 600000L;

    long DEFAULT_MAX_LIFETIME = 1800000L;

    int DEFAULT_MIN_IDLE = 32;

    int DEFAULT_MAX_POOL_SIZE = 32;

    String DEFAULT_JDBC_URL = "jdbc:mysql://localhost:3306/db?useSSL=false&nullNamePatternMatchesAll=true";

    String DEFAULT_DRIVER_CLASSNAME = "com.mysql.cj.jdbc.Driver";

    String DEFAULT_USER = "root";

    boolean DEFAULT_AUTO_COMMIT = true;

    // name hint non editable property
    String webconsole_configurationFactory_nameHint() default "JDBC DataSource: {" + "poolName" + "}"; // NOSONAR

    @AttributeDefinition(name = POOL_NAME, description = "DataSource Pool Name")
    String poolName();

    @AttributeDefinition(name = JDBC_URL, description = "JDBC URL for target Database")
    String jdbcUrl() default DEFAULT_JDBC_URL;

    @AttributeDefinition(name = DRIVER_CLASS_NAME, description = "JDBC driver FQCN")
    String driverClassName() default DEFAULT_DRIVER_CLASSNAME;

    @AttributeDefinition(name = USERNAME, description = "JDBC default authentication username")
    String username() default DEFAULT_USER;

    @AttributeDefinition(
            name = PWD,
            description = "JDBC default authentication password",
            type = PASSWORD
    )
    String password();

    @AttributeDefinition(name = AUTO_COMMIT, description = "JDBC auto-commit behavior of connections")
    boolean autoCommit() default DEFAULT_AUTO_COMMIT;

    @AttributeDefinition(
            name = CONN_TIMEOUT,
            description = "Maximum number of milliseconds that a client will wait for a connection from the pool"
    )
    long connectionTimeout() default DEFAULT_CONN_TIMEOUT; // 30 Seconds

    @AttributeDefinition(
            name = IDLE_TIMEOUT,
            description = "Maximum amount of time that a connection is allowed to sit idle in the pool"
    )
    long idleTimeout() default DEFAULT_IDLE_TIMEOUT; // 10 Minutes

    @AttributeDefinition(name = MAX_LIFETIME, description = "Maximum lifetime of a connection in the pool")
    long maxLifetime() default DEFAULT_MAX_LIFETIME; // 30 Minutes

    // Configure HikariDataSource as a fixed size pool.
    @AttributeDefinition(
            name = MIN_IDLE,
            description = "Minimum number of idle connections that HikariCP tries to maintain in the pool"
    )
    int minimumIdle() default DEFAULT_MIN_IDLE; // 32 Connections;

    @AttributeDefinition(
            name = MAX_POOL_SIZE,
            description = "Maximum size that the pool is allowed to reach, including both idle and in-use connections"
    )
    int maximumPoolSize() default DEFAULT_MAX_POOL_SIZE; // 32 Connections;

    @AttributeDefinition(
            name = DATASOURCE_PROPS,
            description = "Underlying JDBC data source specific properties, in key=value format. " +
                    "Provided are for MySQL only!"
    )
    String[] dataSourceProperties() default {
            "prepStmtCacheSize=250",
            "prepStmtCacheSqlLimit=2048",
            "useServerPrepStmts=true",
            "cachePrepStmts=true",
            "useLocalSessionState=true",
            "useLocalTransactionState=true",
            "rewriteBatchedStatements=true",
            "cacheResultSetMetadata=true",
            "cacheServerConfiguration=true",
            "elideSetAutoCommits=true",
            "maintainTimeStats=false",
    };
}
