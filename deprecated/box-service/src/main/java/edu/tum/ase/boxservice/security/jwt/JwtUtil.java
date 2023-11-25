package edu.tum.ase.boxservice.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KeyConverter keyConverter;

    // Create a Parser to read info inside a JWT. This parser use the public key
    // to verify the signature of incoming JWT tokens with public key
    private JwtParser loadJwtParser() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        return Jwts.parserBuilder()
                .setSigningKey(
                        keyConverter.convertToPublicKey(restTemplate.getForObject("http://localhost:8081/auth/pem", String.class))
                )
                .build();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsReSolver) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        final Claims claims = extractAllClaims(token);
        return claimsReSolver.apply(claims);
    }


    public String extractUsername(String token) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        return extractClaim(token, Claims::getSubject);
    }

    ;

    public Date extractExpiration(String token) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        return extractClaim(token, Claims::getExpiration);
    }

    ;

    private Claims extractAllClaims(String token) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        return loadJwtParser().parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        return extractExpiration(token).before(new Date());
    }


    public boolean validateToken(String token, UserDetails userDetails) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        final String username = extractUsername(token);
        return (loadJwtParser().isSigned(token) && username.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }

    public boolean verifyJwtSignature(String token) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        return (loadJwtParser().isSigned(token) && !isTokenExpired(token));
    }

    public List<GrantedAuthority> extractAuthoritiesFromJwt(String token) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        Claims claims = loadJwtParser().parseClaimsJws(token).getBody();
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Map<String, Object> o : (ArrayList<Map<String, Object>>) claims.get("role")) {
            authorities.add(new SimpleGrantedAuthority(o.get("role").toString()));
        }
        return authorities;
    }
}
