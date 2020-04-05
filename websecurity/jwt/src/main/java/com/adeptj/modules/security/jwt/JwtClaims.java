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

import io.jsonwebtoken.Claims;

import java.util.Date;

import static io.jsonwebtoken.Claims.AUDIENCE;
import static io.jsonwebtoken.Claims.ID;
import static io.jsonwebtoken.Claims.ISSUER;
import static io.jsonwebtoken.Claims.SUBJECT;

/**
 * A simple POJO containing the JWT claims and accessor methods.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JwtClaims {

    private boolean expired;

    private final Claims claims;

    public JwtClaims(Claims claims1) {
        this.claims = claims1;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public Date getExpiration() {
        return this.claims.getExpiration();
    }

    public String getSubject() {
        return (String) this.claims.get(SUBJECT);
    }

    public String getIssuer() {
        return (String) this.claims.get(ISSUER);
    }

    public String getAudience() {
        return (String) this.claims.get(AUDIENCE);
    }

    public String getId() {
        return (String) this.claims.get(ID);
    }

    public <T> T getClaim(String key, Class<T> requiredType) {
        return this.claims.get(key, requiredType);
    }
}