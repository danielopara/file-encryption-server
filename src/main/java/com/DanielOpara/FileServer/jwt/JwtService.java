package com.DanielOpara.FileServer.jwt;

import com.DanielOpara.FileServer.Repository.UserRepository;
import com.DanielOpara.FileServer.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


@Service
public class JwtService {

    @Value("${SECRET_KEY}")
    private String SECRET_KEY;

    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Key getSigningKey(){
        if(SECRET_KEY.isEmpty()){
            throw new RuntimeException("no secrete key");
        }
        byte[] key = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);
    }

    private Claims extractAllClaims(String token){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
            throw new RuntimeException("invalid token");
        }
    }

    private <T>T extractClaims(String token, Function<Claims, T> claimsFunction){
        Claims claims = extractAllClaims(token);
        return claimsFunction.apply(claims);
    }

    private String generateToken(Map<String, Object> getDetails, String username){
        return Jwts.builder()
                .setClaims(getDetails)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (long) 86400000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Date extractExpirationDate(String token){
        return extractClaims(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token){
        return !extractExpirationDate(token).before(new Date());
    }

    public String extractUsername(String token){
        return  extractClaims(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && isTokenExpired(token);
    }

    public String generateAccessToken(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> user = userRepository.findByEmail(userDetails.getUsername());

        if(user.isEmpty()){
            throw new RuntimeException("user not found");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.get().getEmail());

        return generateToken(claims, userDetails.getUsername());
    }

    public String generateAccessTokenByUsername(String username){
        Optional<User> user = userRepository.findByEmail(username);

        if(user.isEmpty()){
            throw new RuntimeException("user not found");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.get().getEmail());

        return generateToken(claims, username);
    }

}
