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

package com.adeptj.modules.jaxrs.core.auth;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents simple username/password credentials.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class SimpleCredentials {

    private static final char[] EMPTY_CHAR_ARRAY = new char[0];

    private final String username;

    private char[] password;

    private Set<String> roles;

    public SimpleCredentials(@NotNull String username, @NotNull String password) {
        this.username = username;
        this.password = password.toCharArray();
    }

    public String getUsername() {
        return this.username;
    }

    public char[] getPassword() {
        return this.password;
    }

    public void clear() {
        if (EMPTY_CHAR_ARRAY == this.password) {
            return;
        }
        char[] temp = this.password;
        this.password = EMPTY_CHAR_ARRAY;
        Arrays.fill(temp, (char) 0x00);
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void addRoles(String... roles) {
        if (ArrayUtils.isNotEmpty(roles)) {
            this.roles = new HashSet<>(Arrays.asList(roles));
        }
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    // <---------------- Generated ----------------->

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleCredentials that = (SimpleCredentials) o;
        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return String.format("SimpleCredentials for: [%s]", this.username);
    }
}
