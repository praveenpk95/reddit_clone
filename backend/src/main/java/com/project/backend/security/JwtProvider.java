package com.project.backend.security;


import com.project.backend.exceptions.SpringRedditException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

import static io.jsonwebtoken.Jwts.parser;

@Service
public class JwtProvider {

    private KeyStore keyStore;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceStream = getClass().getResourceAsStream("/JWTSecretKey.jks");
            keyStore.load(resourceStream, "password".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringRedditException("Exception occurred while storing keystore");
        }
    }


    public String generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        return Jwts.builder()
                   .setSubject(principal.getUsername())
                   .signWith(getPrivateKey())
                   .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey("secretKey", "password".toCharArray());
        }
        catch (Exception e){
            throw new SpringRedditException("Exception occurred while retrieving public key from keystore");
        }
    }

    public boolean validateJWT(String JWT) {
        parser().setSigningKey(getPublicKey());
        return true;
    }

    private PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate("secretKey").getPublicKey();
        }
        catch (Exception e) {
            throw new SpringRedditException("Exception Occurred while" +
                    "retriving public key from keyStore");
        }
    }

    public String getUserNameFromJWT(String token) {
        Claims claims = parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

}