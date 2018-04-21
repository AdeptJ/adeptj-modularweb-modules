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

package com.adeptj.modules.commons.utils.service;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Service for generating random salt and hashed text using PBKDF2WithHmacSHA256 algo.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface CryptoService {

    /**
     * Generates a random salt as byte array.
     *
     * @return a random salt as byte array.
     */
    byte[] getSaltBytes();

    /**
     * Generates a random salt as text.
     *
     * @return a random salt as text.
     */
    String getSaltText();

    /**
     * Generates random hashed bytes using PBKDF2WithHmacSHA256 algo.
     *
     * @param plainText the text to be hashed.
     * @param salt      the salt to be added for hashing.
     * @return hashed bytes after applying the given salt.
     * @throws com.adeptj.modules.commons.utils.CryptoException when could not create the hashed bytes.
     */
    byte[] getHashedBytes(String plainText, byte[] salt);

    /**
     * Generates random hashed text using PBKDF2WithHmacSHA256 algo.
     *
     * @param plainText the text to be hashed.
     * @param salt      the salt to be added for hashing.
     * @return hashed bytes after applying the given salt.
     */
    String getHashedText(String plainText, String salt);

    /**
     * Creates the pair of salt and hash of plain text where keys are salt and hash respectively.
     *
     * @return the pair of salt and hash of plain text
     */
    SaltHashPair getSaltHashPair(String plainText);

    /**
     * Compares the hashed text contained in {@link SaltHashPair} to the computed hash of the plainText.
     *
     * @param saltHashPair the {@link SaltHashPair} contained hashed text and the salt.
     * @param plainText    the text whose hash is to be compared.
     * @return boolean to indicate the comparision outcome.
     */
    boolean compareHashes(SaltHashPair saltHashPair, String plainText);

    default String encodeToString(byte[] bytesToEncode, Charset charset) {
        return new String(Base64.getEncoder().encode(bytesToEncode), charset);
    }
}
