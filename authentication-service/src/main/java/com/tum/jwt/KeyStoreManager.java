package com.tum.jwt;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;


@Component
public class KeyStoreManager {
    private KeyStore keyStore;
    private String keyAlias;

    private char[] password = "aseproject".toCharArray();

    public KeyStoreManager() throws KeyStoreException, IOException {
        loadKeyStore();
    }

    private void loadKeyStore() throws KeyStoreException, IOException {
        keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream fis = null;
        try {
            // with classpath can reach resources folder
//            File keystoreFile = ResourceUtils.getFile("classpath:ase_project.keystore");
            Resource keystoreFile = new ClassPathResource("ase_project.keystore");
            InputStream is = keystoreFile.getInputStream();
//            fis = new FileInputStream(keystoreFile.getFile());
//            fis = new FileInputStream(keystoreFile);

            keyStore.load(is, password);
            keyAlias = keyStore.aliases().nextElement();
//            System.out.println("public key " + keyStore.getCertificate(keyAlias).getPublicKey());
//            System.out.println("private key " + keyStore.getKey(keyAlias, password));

        } catch (Exception e) {
            System.err.println("Error when loading KeyStore");
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    public PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate(keyAlias).getPublicKey();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Key getPrivateKey() {
        try {
            return keyStore.getKey(keyAlias, password);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

//    public Authentication authenticate(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
//
//
//    }
}
