package com.tum.ase.security.jwt;

import com.tum.ase.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Autowired
    private AuthService authService;

    public boolean verifyJwtSignature(String token) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return (loadJwtParser().isSigned(token) && !isTokenExpired(token));
    }

    public String extractUsername(String token) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return extractClaim(token, Claims::getSubject);
    }

    public List<GrantedAuthority> extractAuthoritiesFromJwt(String token) throws InvalidKeySpecException, NoSuchAlgorithmException {
        Claims claims = loadJwtParser().parseClaimsJws(token).getBody();
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Map<String, Object> o : (ArrayList<Map<String, Object>>) claims.get("role")) {
            authorities.add(new SimpleGrantedAuthority(o.get("role").toString()));
        }
        return authorities;
    }

    private JwtParser loadJwtParser() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String pem = authService.getPublicKey();
        PublicKey pkey = this.convertToPublicKey(pem);
        return Jwts.parserBuilder()
                .setSigningKey(pkey)
                .build();
    }

    private PublicKey convertToPublicKey(String pem) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicBytes = Base64.getDecoder().decode(pem);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey public_key = keyFactory.generatePublic(keySpec);
        return public_key;
    }

    private boolean isTokenExpired(String token) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsReSolver) throws InvalidKeySpecException, NoSuchAlgorithmException {
        final Claims claims = extractAllClaims(token);
        return claimsReSolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return loadJwtParser().parseClaimsJws(token).getBody();
    }
}
