package com.tum.jwt;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JweUtil {
    @Autowired
    KeyStoreManager keyStoreManager;



    public String decryptPasswordInJwe(String password) {

        RSADecrypter decrypter = new RSADecrypter((RSAPrivateKey) keyStoreManager.getPrivateKey());

        String decryptedPw = "";

        try {
            EncryptedJWT jwt = EncryptedJWT.parse(password);
            jwt.decrypt(decrypter);
            decryptedPw = jwt.getJWTClaimsSet().getClaim("password").toString();
            System.out.println("Decryption pw: " + decryptedPw);
        } catch (JOSEException | ParseException e) {
            e.printStackTrace();
        }
        return decryptedPw;
    }

    public String generateToken(UserDetails aseUserDetails) {
        // create jwe header
        JWEHeader jweHeader = new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128GCM).
                        contentType("text/plain").
                        customParam("exp", new Date().getTime()).
                        build();

        // JWS
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("role", aseUserDetails.getAuthorities())
                .subject(aseUserDetails.getUsername())
                .issuer("aseproject")
                .issueTime(new Date(System.currentTimeMillis()))
                .expirationTime(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5)) // 5 hours
                .jwtID(UUID.randomUUID().toString())
                .build();

        // create an encrypter to encrypt jwt
        EncryptedJWT jwt = new EncryptedJWT(jweHeader, claimsSet);
        RSAEncrypter encrypter = new RSAEncrypter((RSAPublicKey) keyStoreManager.getPublicKey());
        // return encrypted jwt
        try{
            jwt.encrypt(encrypter);
        } catch (Exception ex) {
            System.err.println("Error when encrypting token");
            ex.printStackTrace();
        }

//        String jwtCipherText = jwt.getCipherText().decodeToString();

        return jwt.toString();
    }
}
