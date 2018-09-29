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

package com.adeptj.modules.security.jwt;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.jsonwebtoken.Claims.SUBJECT;

/**
 * Decorates claims map with extra information such as roles etc. if any.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ClaimsDecorator {

    private static final String KEY_ROLES = "roles";

    private static final String REGEX_COMMA = ",";

    private String subject;

    private Set<String> roles;

    private Map<String, Object> claims;

    public String getSubject() {
        return subject;
    }

    public Map<String, Object> getClaims() {
        return claims;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public ClaimsDecorator addClaims(Map<String, Object> claims) {
        JwtUtil.assertClaims(claims);
        this.subject = (String) claims.get(SUBJECT);
        if (StringUtils.isNotEmpty((String) claims.get(KEY_ROLES))) {
            this.roles = new HashSet<>(Arrays.asList(((String) claims.get(KEY_ROLES)).split(REGEX_COMMA)));
        }
        this.claims = claims;
        return this;
    }
}
