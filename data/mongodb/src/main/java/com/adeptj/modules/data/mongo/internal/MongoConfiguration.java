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

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

/**
 * OSGI mongo db connection configuration definition.
 *
 * @author prince.arora, AdeptJ.
 */
@ObjectClassDefinition(
        name = "AdeptJ MongoDB Factory Configurations",
        description = "MongoDB Configuration"
)
public @interface MongoConfiguration {

    @AttributeDefinition(
            name = "Unit Name",
            description = "Note: Must be unique"
    )
    String unitName();

    @AttributeDefinition(
            name = "Host",
            description = "Host name or ip for Mongo DB connection",
            defaultValue = "127.0.0.1"
    )
    String hostName();

    @AttributeDefinition(
            name = "Port",
            description = "Mongo DB connection port",
            defaultValue = "27017"
    )
    int port();

    @AttributeDefinition(
            name = "Database Name",
            description = "MongoDB database name"
    )
    String dbName();

    @AttributeDefinition(
            name = "Username",
            description = "MongoDB authentication username"
    )
    String username();

    @AttributeDefinition(
            name = "Password",
            description = "MongoDB authentication password"
    )
    String password();

    @AttributeDefinition(
            name = "Package To Map",
            description = "MongoDB Collections classes package to Map"
    )
    String mappablePackage();

    @AttributeDefinition(
            name = "ReadPreference",
            description = "ReadPreference for transactions.",
            options = {
                @Option(label = "PRIMARY", value = "PRIMARY"),
                @Option(label = "SECONDARY", value = "SECONDARY"),
                @Option(label = "SECONDARY_PREFERRED", value = "SECONDARY_PREFERRED"),
                @Option(label = "PRIMARY_PREFERRED", value = "PRIMARY_PREFERRED"),
                @Option(label = "NEAREST", value = "NEAREST")
            }
    )
    String readPreference();

    @AttributeDefinition(
            name = "WriteConcern",
            description = "WriteConcern for transactions.",
            options = {
                    @Option(label = "ACKNOWLEDGED", value = "ACKNOWLEDGED"),
                    @Option(label = "JOURNALED", value = "JOURNALED"),
                    @Option(label = "MAJORITY", value = "MAJORITY"),
                    @Option(label = "UNACKNOWLEDGED", value = "UNACKNOWLEDGED")
            }
    )
    String writeConcern();
}
