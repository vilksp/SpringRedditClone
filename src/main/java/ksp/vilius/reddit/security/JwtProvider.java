package ksp.vilius.reddit.security;

import io.jsonwebtoken.*;
import ksp.vilius.reddit.model.SecurityUser;
import ksp.vilius.reddit.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {


    String secret = "3207C63AFF1E40703056B951343BD22C16851D3ED2D5DA257AB094E71BBD1712";

    public String generateToken(Authentication authentication) {

        User user = (SecurityUser) authentication.getPrincipal();

        long expire = 60 * 60 * 24 * 7 * 1000;


        Date now = new Date(System.currentTimeMillis());
        Date expiry = new Date(now.getTime() + expire);

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("id", user.getUserId());
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setIssuer("Spring reddit clone")
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException |
                IllegalArgumentException e) {
            throw new RuntimeException("Invalid token");
        }
    }


    public Long getUserIdFromJwt(String token) {
        Claims claim = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

        String id = "";
        try {
            id = (String) claim.get("id");
        } catch (Exception e) {
            return Long.valueOf(claim.get("id").toString());
        }
        return Long.parseLong(id);
    }

    public String getUsernameFromJwt(String token) {
        Claims claim = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

        String username = "";
        try {
            username = (String) claim.get("username");
        } catch (Exception e) {
            return claim.get("username").toString();
        }
        return username;
    }
}
