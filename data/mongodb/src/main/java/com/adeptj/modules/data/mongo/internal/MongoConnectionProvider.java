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

package com.adeptj.modules.data.mongo.internal;

import com.adeptj.modules.commons.utils.PropertiesUtil;
import com.adeptj.modules.data.mongo.api.MongoCrudRepository;
import com.adeptj.modules.data.mongo.exception.InvalidMongoDatabaseException;
import com.adeptj.modules.data.mongo.exception.InvalidMongoUnitException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.mongodb.morphia.Morphia;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.adeptj.modules.data.mongo.internal.MongoConnectionProvider.COMPONENT_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.IGNORE;

/**
 * Managed service factory to manage MongoDB connections as OSGI configurations.
 *
 * @author prince.arora, AdeptJ.
 */
@Designate(ocd = MongoConfiguration.class, factory = true)
@Component(
        immediate = true,
        name = COMPONENT_NAME,
        property = SERVICE_PID + "=" + COMPONENT_NAME,
        configurationPolicy = IGNORE
)
public class MongoConnectionProvider implements ManagedServiceFactory {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(MongoConnectionProvider.class);

    static final String COMPONENT_NAME = "com.adeptj.modules.data.mongo.MongoConnectionProvider.factory";

    private static final String FACTORY_NAME = "AdeptJ MongoDB Connection Provider";

    private Map<String, MongoCrudRepository> serviceContainer = new HashMap<>();

    private Map<String, String> unitMapping = new HashMap<>();

    private static final String UNITNAME_PROP = "unitName";

    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    @Override
    public void updated(String s, Dictionary<String, ?> properties) throws ConfigurationException {
        this.buildCrudService(properties, s);
    }

    @Override
    public void deleted(String id) {
        if (this.unitMapping.containsKey(id)) {
            //Closing connection to mongodb server for delete action
            //for any configuration.
            Optional.of(this.serviceContainer.remove(this.unitMapping.get(id)))
                    .ifPresent(crudRepository -> {
                LOGGER.debug("Closing connection for unit {}", unitMapping.get(id));
                crudRepository.getDatastore().getMongo().close();
                crudRepository = null;
                unitMapping.remove(id);
            });
        }
    }

    private void buildCrudService(Dictionary<String, ?> properties, String id) {
        try {
            if (!PropertiesUtil.toString(properties.get(UNITNAME_PROP), "").equals("")) {
                //Aboard in case of nothing provided in dbname.
                //Avoiding db connection creation with blank db name.
                if (PropertiesUtil.toString(properties.get("dbName"), "").equals("")) {
                    throw new InvalidMongoDatabaseException("Invalid mongo database name. Provide a valid database name.");
                }
                MongoCredential credential = null;
                if (!PropertiesUtil.toString(properties.get("username"), "").equals("")) {
                    credential = MongoCredential.createCredential(
                            PropertiesUtil.toString(properties.get("username"), ""),
                            PropertiesUtil.toString(properties.get("dbName"), ""),
                            PropertiesUtil.toString(properties.get("password"), "").toCharArray()
                    );
                }

                Morphia morphia = new Morphia();
                if (!PropertiesUtil.toString(properties.get("mappablePackage"), "").equals("")) {
                    morphia.mapPackage(properties.get("mappablePackage").toString());
                }

                ServerAddress serverAddress = new ServerAddress(
                        PropertiesUtil.toString(properties.get("hostName"), ""),
                        PropertiesUtil.toInteger(properties.get("port"), 0)
                );

                MongoClient mongoClient = null;
                if (credential != null) {
                    mongoClient = new MongoClient(
                            serverAddress,
                            Arrays.asList(credential),
                            this.buildMongoOptions(properties)
                    );
                } else {
                    mongoClient = new MongoClient(
                            serverAddress,
                            this.buildMongoOptions(properties)
                    );
                }

                this.serviceContainer.put(
                        PropertiesUtil.toString(properties.get(UNITNAME_PROP), ""),
                        new MongoCrudRepositoryImpl(
                                morphia.createDatastore(
                                        mongoClient,
                                        PropertiesUtil.toString(properties.get("dbName"), "")
                                )
                        )
                );

                this.unitMapping.put(id,
                        PropertiesUtil.toString(properties.get(UNITNAME_PROP), ""));
            } else {
                throw new InvalidMongoUnitException("Invalid Unit name provided for mongo db configuration. Provide Valid and unique name for unit");
            }
        } catch (Exception ex) {
            LOGGER.error("Unable to create mongo crud service for config id {} full exception ", id, ex);
        }
    }

    private MongoClientOptions buildMongoOptions(Dictionary<String, ?> properties) {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        //TODO Mongo config for connection pool  configurations.
        return builder.build();
    }


}
