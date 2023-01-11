package com.aiear.config.session;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

/**
 * @packageName : com.aiear.config.session
 * @fileName	: JwtUtil.java
 * @author		: pcw
 * @date		: 2022. 11. 28.
 * @description	:
 * ===============================================
 * DATE				AUTHOR			NOTE
 * 2022. 11. 28.		pcw				최초 생성
 */
@Service
public class JwtUtil {

    private String secret = "eyJhbGciOiJIUzI1NiJ9cwp";

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String hospitalId, String hospitalPwd, String userType) {
        Map<String, Object> claims = new HashMap<>();
        
        claims.put("id", hospitalId);
        claims.put("type", userType);
        
        return createToken(claims, hospitalId);
    }
    
    public String generateRefreshToken(String hospitalId, String hospitalPwd, String userType) {
        Map<String, Object> claims = new HashMap<>();
        
        claims.put("id", hospitalId);
        claims.put("type", userType);
        
        return createRefreshToken(claims, hospitalId);
    }

    private String createToken(Map<String, Object> claims, String subject) {
    	
//    	//Access Token
//        String accessToken = Jwts.builder()
//                .setClaims(claims) // 정보 저장
//                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 발행 시간 정보
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // set Expire Time
//                .signWith(SignatureAlgorithm.HS256, secret)  // 사용할 암호화 알고리즘과
//                // signature 에 들어갈 secret값 세팅
//                .compact();
//
//        //Refresh Token
//        String refreshToken =  Jwts.builder()
//                .setClaims(claims) // 정보 저장
//                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 발행 시간 정보
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // set Expire Time
//                .signWith(SignatureAlgorithm.HS256, secret)  // 사용할 암호화 알고리즘과
//                // signature 에 들어갈 secret값 세팅
//                .compact();
    	
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 3))
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }
    
    private String createRefreshToken(Map<String, Object> claims, String subject) {
      //Refresh Token
      return Jwts.builder()
              .setClaims(claims) // 정보 저장
              .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 발행 시간 정보
              .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // set Expire Time
              .signWith(SignatureAlgorithm.HS256, secret)  // 사용할 암호화 알고리즘과
              // signature 에 들어갈 secret값 세팅
              .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    public String getLoginIdbyToken(HttpServletRequest request) {
    	String headerAuthToken = request.getHeader("Authorization");
    	String token = null;
        String hospitalId = null;
    	
    	if (headerAuthToken != null && headerAuthToken.startsWith("Bearer ")) {
    		token = headerAuthToken.substring(7);
    		hospitalId = extractUserId(token);
    	}
    	
    	return hospitalId;
    }
}