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

package com.adeptj.modules.security.jwt.internal;

import com.adeptj.modules.commons.utils.Loggers;
import com.adeptj.modules.security.jwt.JwtConfig;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;

/**
 * Utility for loading JWT signing key.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JwtSigningKeys {

    private static final String UTF8 = "UTF-8";

    private static final String KEY_HEADER = "-----BEGIN PRIVATE KEY-----\n";

    private static final String KEY_FOOTER = "-----END PRIVATE KEY-----";

    private static final String REGEX_SPACE = "\\s";

    private static final String ALGO_RSA = "RSA";

    private static final String DEFAULT_KEY_FILE = "/default.pem";


    static Key createSigningKey(JwtConfig jwtConfig) {
        Logger logger = Loggers.get(JwtSigningKeys.class);
        try {
            SignatureAlgorithm signatureAlgo = SignatureAlgorithm.forName(jwtConfig.signatureAlgo());
            Key hmacSecretKey = JwtSigningKeys.getHmacSecretKey(signatureAlgo, jwtConfig.hmacSecretKey());
            return hmacSecretKey == null ? JwtSigningKeys.getRSAPrivateKey(jwtConfig, logger) : hmacSecretKey;
        } catch (Exception ex) { // NOSONAR
            logger.error(ex.getMessage(), ex);
            // Let the exception be rethrown so that SCR would not create a service object of this component.
            throw new RuntimeException(ex); // NOSONAR
        }
    }

    private static Key getHmacSecretKey(SignatureAlgorithm signatureAlgo, String secretKey) throws UnsupportedEncodingException {
        Key signingKey = null;
        if (signatureAlgo.isHmac()) {
            if (StringUtils.isEmpty(secretKey)) {
                throw new IllegalStateException("hmacSecretKey property can't be empty when SignatureAlgorithm is Hmac!!");
            } else {
                signingKey = new SecretKeySpec(Base64.getEncoder().encode(secretKey.getBytes(UTF8)), signatureAlgo.getJcaName());
            }
        } else if (StringUtils.isNotEmpty(secretKey)) {
            throw new IllegalStateException("Can't have RSA SignatureAlgorithm when hmacSecretKey property is provided!!");
        }
        return signingKey;
    }

    private static Key getRSAPrivateKey(JwtConfig jwtConfig, Logger logger) throws NoSuchAlgorithmException {
        // 1. try the jwtConfig provided keyFileLocation
        KeyFactory keyFactory = KeyFactory.getInstance(ALGO_RSA);
        Path keyFileLocation = Paths.get(USER_DIR, jwtConfig.keyFileLocation());
        Key signingKey = null;
        if (Files.exists(keyFileLocation)) {
            signingKey = loadKeyFromLocation(keyFactory, keyFileLocation, logger);
        } else if (jwtConfig.useDefaultKey()) {
            logger.warn("Loading the default signing key, please check if that is intended!!");
            // 2. Use the default one that is embedded with this module.
            signingKey = loadDefaultKey(keyFactory, logger);
        }
        if (signingKey == null) {
            throw new IllegalStateException("Couldn't initialize the RSAPrivateKey!!");
        }
        return signingKey;
    }

    private static Key loadKeyFromLocation(KeyFactory keyFactory, Path keyFileLocation, Logger logger) {
        logger.info("Loading signing key: [{}]", keyFileLocation);
        Key signingKey = null;
        try (InputStream data = Files.newInputStream(keyFileLocation)) {
            signingKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes(data)));
        } catch (IOException | InvalidKeySpecException ex) {
            logger.error("Exception while loading Key file!!", ex);
        }
        return signingKey;
    }

    private static Key loadDefaultKey(KeyFactory keyFactory, Logger logger) {
        Key signingKey = null;
        try (InputStream data = JwtSigningKeys.class.getResourceAsStream(DEFAULT_KEY_FILE)) {
            signingKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes(data)));
        } catch (IOException | InvalidKeySpecException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return signingKey;
    }

    private static byte[] keyBytes(InputStream data) throws IOException {
        return Base64.getDecoder().decode(IOUtils.toString(data, UTF8)
                .replace(KEY_HEADER, EMPTY)
                .replace(KEY_FOOTER, EMPTY)
                .replaceAll(REGEX_SPACE, EMPTY)
                .getBytes(UTF8));
    }
}
