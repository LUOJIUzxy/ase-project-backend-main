package com.tum.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Autowired
    private KeyStoreManager keyStoreManager;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities());

        return createToken(claims, userDetails.getUsername());
    }

    // Create JWS with both custom and registered claims, signed by a private key.
    private String createToken(Map<String, Object> claims, String username) {
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuer("aseproject")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5)) // 5 hours
                .signWith(keyStoreManager.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
        return jwt;
    }

    // Create a Parser to read info inside a JWT. This parser use the public key
    // to verify the signature of incoming JWT tokens with public key
    private JwtParser loadJwtParser() {
        return Jwts.parserBuilder()
                .setSigningKey(keyStoreManager.getPublicKey())
                .build();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsReSolver) {
        final Claims claims = extractAllClaims(token);
        return claimsReSolver.apply(claims);
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    ;

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    ;

    private Claims extractAllClaims(String token) {
        return loadJwtParser().parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (loadJwtParser().isSigned(token) && username.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }

    public boolean verifyJwtSignature(String token) {
        return (loadJwtParser().isSigned(token) && !isTokenExpired(token));
    }

    public List<GrantedAuthority> extractAuthoritiesFromJwt(String token) {
        Claims claims = loadJwtParser().parseClaimsJws(token).getBody();
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Map<String, Object> o : (ArrayList<Map<String, Object>>) claims.get("role")) {
            authorities.add(new SimpleGrantedAuthority(o.get("role").toString()));
        }
        return authorities;
    }
}
