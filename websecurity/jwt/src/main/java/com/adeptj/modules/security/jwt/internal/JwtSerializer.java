package com.adeptj.modules.security.jwt.internal;

import com.adeptj.modules.commons.utils.JsonUtil;
import io.jsonwebtoken.io.SerializationException;
import io.jsonwebtoken.io.Serializer;

import javax.json.bind.JsonbException;
import java.util.Map;

/**
 * The Jwt serializer based on Json-B.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class JwtSerializer implements Serializer<Map<String, ?>> {

    @Override
    public byte[] serialize(Map<String, ?> claims) {
        try {
            return JsonUtil.serializeToBytes(claims);
        } catch (JsonbException ex) {
            throw new SerializationException(ex.getMessage(), ex);
        }
    }
}
