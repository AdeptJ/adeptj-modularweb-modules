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

import com.adeptj.modules.security.jwt.JwtClaims;
import com.adeptj.modules.security.jwt.JwtService;
import com.adeptj.modules.security.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Deserializer;
import io.jsonwebtoken.io.Serializer;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.security.SignatureException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static io.jsonwebtoken.Claims.ID;
import static io.jsonwebtoken.Claims.ISSUER;
import static io.jsonwebtoken.Header.JWT_TYPE;
import static io.jsonwebtoken.Header.TYPE;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Service for signing and parsing JWT with RSA private and public keys respectively.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = JwtConfig.class)
@Component(configurationPolicy = REQUIRE)
public class JwtServiceImpl implements JwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String defaultIssuer;

    private final String[] mandatoryClaims;

    private final Duration expirationDuration;

    private final JwtKeyInfo keyInfo;

    private final JwsHandler jwsHandler;

    private final Serializer<Map<String, ?>> serializer;

    private final Deserializer<Map<String, ?>> deserializer;

    @Activate
    public JwtServiceImpl(JwtConfig config) {
        this.jwsHandler = new JwsHandler();
        this.serializer = new JwtSerializer<>();
        this.deserializer = new JwtDeserializer<>();
        try {
            this.defaultIssuer = config.defaultIssuer();
            this.expirationDuration = Duration.of(config.expirationTime(), MINUTES);
            this.mandatoryClaims = config.mandatoryClaims();
            SignatureAlgorithm algorithm = SignatureAlgorithm.forName(config.signatureAlgorithm());
            LOGGER.info("Selected JWT SignatureAlgorithm: [{}]", algorithm.getJcaName());
            PrivateKey signingKey = JwtKeys.createSigningKey(config, algorithm.getFamilyName());
            PublicKey verificationKey = JwtKeys.createVerificationKey(algorithm.getFamilyName(), config.publicKey());
            this.keyInfo = new JwtKeyInfo(algorithm, signingKey, verificationKey);
        } catch (SignatureException | JwtKeyInitializationException | IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createJwt(String subject, Map<String, Object> claims) {
        Assert.hasText(subject, "Subject can't be blank!!");
        JwtUtil.assertClaims(claims);
        Instant now = Instant.now();
        return Jwts.builder()
                .setHeaderParam(TYPE, JWT_TYPE)
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(this.expirationDuration)))
                .setId(claims.containsKey(ID) ? (String) claims.get(ID) : JwtUtil.generateJwtId())
                .setIssuer(claims.containsKey(ISSUER) ? (String) claims.get(ISSUER) : this.defaultIssuer)
                .signWith(this.keyInfo.getPrivateKey(), this.keyInfo.getSignatureAlgorithm())
                .serializeToJsonWith(this.serializer)
                .compact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createJwt(Map<String, Object> claims) {
        JwtUtil.assertClaims(claims, this.mandatoryClaims);
        return Jwts.builder()
                .setHeaderParam(TYPE, JWT_TYPE)
                .setClaims(claims)
                .signWith(this.keyInfo.getPrivateKey(), this.keyInfo.getSignatureAlgorithm())
                .serializeToJsonWith(this.serializer)
                .compact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JwtClaims verifyJwt(String jwt) {
        JwtClaims claims = null;
        try {
            Assert.hasText(jwt, "JWT can't be blank!!");
            claims = Jwts.parserBuilder()
                    .setSigningKey(this.keyInfo.getPublicKey())
                    .deserializeJsonWith(this.deserializer)
                    .build()
                    .parse(jwt, this.jwsHandler);
        } catch (ExpiredJwtException ex) {
            // to reduce noise in logs.
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(ex.getMessage(), ex);
            }
            claims = new JwtClaims(ex.getClaims());
            claims.setExpired(true);
        } catch (JwtException | IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return claims;
    }
}
