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

package com.adeptj.modules.data.jpa;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;

/**
 * A {@link SessionCustomizer} for setting the query retry attempt count to {@link DatabaseLogin}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ConnectionRetrySessionCustomizer implements SessionCustomizer {

    @Override
    public void customize(Session session) {
        DatabaseLogin login = (DatabaseLogin) session.getDatasourceLogin();
        login.setConnectionHealthValidatedOnError(false);
        login.setQueryRetryAttemptCount(0);
    }
}
