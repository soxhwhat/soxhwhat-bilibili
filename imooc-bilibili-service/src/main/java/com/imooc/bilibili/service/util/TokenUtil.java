package com.imooc.bilibili.service.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.imooc.bilibili.domain.exception.ConditionException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class TokenUtil {

    private static final String ISSUER = "签发者";

    public static String generateToken(Long userId) throws Exception{
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        LocalDateTime time = LocalDateTime.now();
        time.plusHours(1);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                //LocalDateTime转Date
                .withExpiresAt(Date.from(time.atZone(ZoneId.systemDefault()).toInstant()))
                .sign(algorithm);
    }

    public static String generateRefreshToken(Long userId) throws Exception{
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        LocalDateTime time = LocalDateTime.now();
        time.plusHours(1);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(Date.from(time.atZone(ZoneId.systemDefault()).toInstant()))
                .sign(algorithm);
    }

    public static Long verifyToken(String token){
        try{
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String userId = jwt.getKeyId();
            return Long.valueOf(userId);
        }catch (TokenExpiredException e){
            throw new ConditionException("555","token过期！");
        }catch (Exception e){
            throw new ConditionException("非法用户token！");
        }


    }


}
