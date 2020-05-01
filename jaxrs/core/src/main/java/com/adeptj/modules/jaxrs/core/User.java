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

package com.adeptj.modules.jaxrs.core;

import com.adeptj.modules.security.jwt.JwtClaims;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;

/**
 * The current logged in user with Jwt claims.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class User implements Principal {

    private final String name;

    private final JwtClaims claims;

    public User(JwtClaims claims) {
        this.claims = claims;
        this.name = this.claims.getSubject();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Map<String, Object> getClaims() {
        return this.claims.asMap();
    }

    public boolean isHoldingExpiredJwt() {
        return this.claims.isExpired();
    }

    // <<------------------------- Generated ------------------------->>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return this.name.equals(user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public String toString() {
        return "User{" + "name='" + this.name + '\'' + '}';
    }
}
