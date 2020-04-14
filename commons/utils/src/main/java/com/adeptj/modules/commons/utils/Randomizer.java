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

package com.adeptj.modules.commons.utils;

import com.fasterxml.uuid.Generators;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Provides random bytes using {@link SecureRandom}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class Randomizer {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private Randomizer() {
    }

    public static byte[] randomBytes(int length) {
        byte[] randomBytes = new byte[length];
        SECURE_RANDOM.nextBytes(randomBytes);
        return randomBytes;
    }

    public static void randomBytes(byte[] bytes) {
        SECURE_RANDOM.nextBytes(bytes);
    }

    public static UUID randomUUID() {
        return Generators.randomBasedGenerator().generate();
    }

    public static String randomUUIDString() {
        return randomUUID().toString();
    }
}
