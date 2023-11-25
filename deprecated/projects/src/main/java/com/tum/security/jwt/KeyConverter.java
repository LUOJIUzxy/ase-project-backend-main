package com.tum.security.jwt;


import org.springframework.stereotype.Component;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;



@Component
public class KeyConverter {
    public PublicKey convertToPublicKey(String pem) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        byte[] publicBytes = Base64.getDecoder().decode(pem);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey public_key = keyFactory.generatePublic(keySpec);
        return public_key;
    }
}
