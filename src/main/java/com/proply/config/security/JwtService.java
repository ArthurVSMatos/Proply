package com.proply.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // 1. Chave super segura gerada em Base64 (tem mais de 256 bits)
    // NUNCA uses hifens aqui. Esta chave é apenas um exemplo seguro.
    private final String SECRET_KEY = "NDFhYjI4Y2QyYjU2Y2M0ZTc5ZjQyYmU4OWE1ZTIwZTYxZTY1Y2MyOThiMWRhZDU2OTY4NTFjZTJiNjc2MTExOQ==";

    // 2. Método educativo: Transforma a string Base64 numa Chave Criptográfica
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 3. Gerar o Token
    public String generateToken(String email){
        return Jwts.builder()
                .setSubject(email) // O dono do token
                .setIssuedAt(new Date(System.currentTimeMillis())) // Data de criação
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Válido por 1 dia
                // Usamos a Key gerada e o algoritmo HS256
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 4. Extrair os dados (Claims) do Token
    public String extractEmail(String token){
        Claims claims = Jwts
                .parserBuilder() // Usamos parserBuilder nas versões mais recentes da biblioteca
                .setSigningKey(getSignInKey()) // Usamos a mesma chave para abrir o cadeado
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // 5. Validar se o token pertence ao utilizador
    public boolean isTokenValid(String token, String email){
        return extractEmail(token).equals(email);
    }
}