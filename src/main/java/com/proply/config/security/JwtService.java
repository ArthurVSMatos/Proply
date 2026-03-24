package com.proply.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // 1. CHAVE MESTRA DINÂMICA
    // O Spring vai buscar no application.yml ou variáveis de ambiente.
    // Se não achar nada, ele usa o valor após os dois pontos como fallback (apenas para facilitar seu dev local).
    @Value("${api.security.token.secret:NDFhYjI4Y2QyYjU2Y2M0ZTc5ZjQyYmU4OWE1ZTIwZTYxZTY1Y2MyOThiMWRhZDU2OTY4NTFjZTJiNjc2MTExOQ==}")
    private String secretKey;

    // 2. PREPARAÇÃO DA CHAVE: Converte a String Base64 para uma chave criptográfica real.
    // Usamos o algoritmo HMAC-SHA para garantir que ninguém consiga forjar o token sem essa chave.
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 3. GERAÇÃO DO TOKEN (BUILDER PATTERN):
    public String generateToken(String email){
        return Jwts.builder()
                .setSubject(email) // O Identificador principal (Principal) do usuário.
                .setIssuedAt(new Date(System.currentTimeMillis())) // Timestamp de emissão (IAT).
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Expiração (EXP) - Aqui defini 24h.
                // Assinatura do token para garantir que ele não seja alterado por terceiros (Integridade).
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact(); // Transforma o objeto em uma String JWT.
    }


    // 4. EXTRAÇÃO DE CLAIMS (DADOS): Abre o "envelope" do token.
    public String extractEmail(String token){
        Claims claims = Jwts
                .parserBuilder() // Usamos o builder para configurar o motor de leitura.
                .setSigningKey(getSignInKey()) // Informamos a chave para verificar a assinatura.
                .build()
                .parseClaimsJws(token) // Se o token foi alterado ou expirou, aqui ele lança uma exceção.
                .getBody();

        return claims.getSubject(); // Retorna o email que guardamos no Subject.
    }

    // 5. VALIDAÇÃO LÓGICA:
    // Aqui verificamos se o email extraído do token condiz com o email do usuário do banco.
    public boolean isTokenValid(String token, String email){
        final String userEmail = extractEmail(token);
        // O parserBuilder().build().parseClaimsJws() já valida automaticamente a expiração (EXP).
        // Se o token estiver expirado, ele nem chega nessa linha, explode antes.
        return (userEmail.equals(email));
    }
}