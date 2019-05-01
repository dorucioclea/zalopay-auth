package com.zalopay.auth.security;

import com.zalopay.auth.model.User;
import com.zalopay.auth.repository.UserRepository;
import io.jsonwebtoken.*;
import org.keycloak.RSATokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static org.keycloak.common.util.DerUtils.decodePublicKey;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtPublic}")
    private String jwtPublic;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        final String authorities = authentication.getAuthorities().stream()
          .map(GrantedAuthority::getAuthority)
          .collect(Collectors.joining(","));

        return Jwts.builder()
          .setSubject((String) authentication.getPrincipal())
          .claim("ACCESS_TOKEN", authorities)
          .setIssuedAt(new Date())
          .setExpiration(expiryDate)
          .signWith(SignatureAlgorithm.HS512, jwtSecret)
          .compact();
    }

    public UsernamePasswordAuthenticationToken  getUserInfoFromJWT(String token) throws VerificationException {
        AccessToken accessToken = RSATokenVerifier.create(token).getToken();
        SimpleAuthorityMapper simpleAuthorityMapper = new SimpleAuthorityMapper();
        Collection authorities =
          accessToken.getResourceAccess("kong").getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.toUpperCase()))
            .collect(Collectors.toList());

        UserDetails userInJwt = null;
        try {
            userInJwt = customUserDetailsService.loadUserByUsername(accessToken.getPreferredUsername());
        } catch (UsernameNotFoundException ex) {
            User newUser = new User(accessToken.getSubject(), accessToken.getPreferredUsername(),
              accessToken.getPreferredUsername(),
              accessToken.getEmail(), "");
            userRepository.save(newUser);
            userInJwt = UserPrincipal.create(newUser);
        }

        return new UsernamePasswordAuthenticationToken(userInJwt, "",
          simpleAuthorityMapper.mapAuthorities(authorities));
    }

    public boolean validateToken(String authToken) {
        try {
            PublicKey publicKey = decodePublicKey(pemToDer(jwtPublic));
            Jwts.parser().setSigningKey(publicKey).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        } catch (NoSuchAlgorithmException e ) {
            logger.error("JWT claims string is empty.");
        } catch (NoSuchProviderException e) {
            logger.error("JWT Public Key Is Not Correct.");
        } catch (InvalidKeySpecException e) {
            logger.error("JWT Public Key Is Not Correct.");
        }
        return false;
    }

    public static byte[] pemToDer(String pem) {
        return Base64.getDecoder().decode(pem);
    }
}

