package com.example.zipzip.Jwt;

import com.example.zipzip.Entity.Role;
import com.example.zipzip.Service.UserService;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.validate}")
    private Long validateInMillisecond;

    private UserService userService;

    @Autowired
    public JwtTokenProvider(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init(){
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(String email, Role role){
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role.getAuthority());

        Date issue = new Date();
        Date ex = new Date(validateInMillisecond + issue.getTime());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issue)
                .setExpiration(ex)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getEmail(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public Authentication authentication(String secret){
        UserDetails userDetails = userService.loadUserByUsername(getEmail(secret));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest request){
        String bearer = request.getHeader("Authorization");
        if(bearer != null && bearer.startsWith("Bearer ")){
            return bearer.substring(7, bearer.length());
        }
        return null;
    }

    public boolean validateToken(String token){
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            if(claimsJws.getBody().getExpiration().before(new Date())){
                return false;
            }
            return true;
        }
        catch (AuthenticationException e){
            throw new RuntimeException("Token invalid");
        }
    }
}
