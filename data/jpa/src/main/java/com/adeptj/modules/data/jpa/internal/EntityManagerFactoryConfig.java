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

package com.adeptj.modules.data.jpa.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

import static com.adeptj.modules.data.jpa.internal.LoggingLevel.ALL;
import static com.adeptj.modules.data.jpa.internal.LoggingLevel.CONFIG;
import static com.adeptj.modules.data.jpa.internal.LoggingLevel.FINE;
import static com.adeptj.modules.data.jpa.internal.LoggingLevel.FINER;
import static com.adeptj.modules.data.jpa.internal.LoggingLevel.FINEST;
import static com.adeptj.modules.data.jpa.internal.LoggingLevel.INFO;
import static com.adeptj.modules.data.jpa.internal.LoggingLevel.OFF;
import static com.adeptj.modules.data.jpa.internal.LoggingLevel.SEVERE;
import static com.adeptj.modules.data.jpa.internal.LoggingLevel.WARNING;
import static org.eclipse.persistence.config.PersistenceUnitProperties.CREATE_ONLY;
import static org.eclipse.persistence.config.PersistenceUnitProperties.CREATE_OR_EXTEND;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_BOTH_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_DATABASE_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_SQL_SCRIPT_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DROP_AND_CREATE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DROP_ONLY;
import static org.eclipse.persistence.config.PersistenceUnitProperties.NONE;

/**
 * EntityManagerFactory configurations.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(name = "AdeptJ JPA EntityManagerFactory Configurations", description = "EntityManagerFactory Configurations")
public @interface EntityManagerFactoryConfig {

    static final int CARDINALITY = 100;

    @AttributeDefinition(name = "PersistenceUnit Name", description = "Name of the PersistenceUnit.")
    String unitName();

    @AttributeDefinition(name = "Persistence XML Location", description = "Location of the persistence.xml file")
    String persistenceXmlLocation() default "META-INF/persistence.xml";

    @AttributeDefinition(name = "DataSource Name For Binding", description = "DataSource Name For Binding to this EntityManagerFactory")
    String dataSourceName();

    @AttributeDefinition(name = "EclipseLink LoggingFile", description = "EclipseLink Logging File")
    String loggingFile() default "jpa.log";

    @AttributeDefinition(name = "JPA Properties", description = "JPA Properties(key=value)", cardinality = CARDINALITY)
    String[] jpaProperties();

    @AttributeDefinition(name = "Entity Mapping Files", description = "JPA Mapping Files i.e. orm.xml", cardinality = CARDINALITY)
    String[] mappingFiles();

    @AttributeDefinition(name = "Deploy on Startup", description = "Whether to create PersistenceUnit when the application starts up")
    boolean deployOnStartup() default true;

    @AttributeDefinition(name = "Transaction Type", description = "JPA Transaction Type", options = {
            @Option(label = "RESOURCE_LOCAL", value = "RESOURCE_LOCAL"),
            @Option(label = "JTA", value = "JTA")
    })
    String persistenceUnitTransactionType();

    @AttributeDefinition(name = "L2 Cache Mode", description = "JPA Caching Strategy", options = {
            @Option(label = "NONE", value = "NONE"),
            @Option(label = "ENABLE_SELECTIVE", value = "ENABLE_SELECTIVE"),
            @Option(label = "DISABLE_SELECTIVE", value = "DISABLE_SELECTIVE"),
            @Option(label = "ALL", value = "ALL"),
            @Option(label = "UNSPECIFIED", value = "UNSPECIFIED")
    })
    String sharedCacheMode();

    @AttributeDefinition(name = "Entity Validation Mode", description = "Bean Validation Options", options = {
            @Option(label = "NONE", value = "NONE"),
            @Option(label = "AUTO", value = "AUTO"),
            @Option(label = "CALLBACK", value = "CALLBACK")
    })
    String validationMode();

    @AttributeDefinition(name = "EclipseLink LoggingLevel", description = "EclipseLink Logging Level", options = {
            @Option(label = FINEST, value = FINEST),
            @Option(label = SEVERE, value = SEVERE),
            @Option(label = WARNING, value = WARNING),
            @Option(label = INFO, value = INFO),
            @Option(label = CONFIG, value = CONFIG),
            @Option(label = FINE, value = FINE),
            @Option(label = FINER, value = FINER),
            @Option(label = OFF, value = OFF),
            @Option(label = ALL, value = ALL)
    })
    String loggingLevel();

    @AttributeDefinition(name = "DDL Generation Mode", description = "Where to run the DDL", options = {
            @Option(label = "BOTH", value = DDL_BOTH_GENERATION),
            @Option(label = "DATABASE", value = DDL_DATABASE_GENERATION),
            @Option(label = "SQL_SCRIPT", value = DDL_SQL_SCRIPT_GENERATION),
    })
    String ddlGenerationMode();

    @AttributeDefinition(name = "DDL Generation Strategy", description = "Specifies how the DDL runs", options = {
            @Option(label = "CREATE_OR_EXTEND", value = CREATE_OR_EXTEND),
            @Option(label = "DROP_ONLY", value = DROP_ONLY),
            @Option(label = "CREATE_ONLY", value = CREATE_ONLY),
            @Option(label = "DROP_AND_CREATE", value = DROP_AND_CREATE),
            @Option(label = "NONE", value = NONE),
    })
    String ddlGeneration();
}
