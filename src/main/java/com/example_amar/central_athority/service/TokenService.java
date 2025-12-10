package com.example_amar.central_athority.service;

import com.example_amar.central_athority.model.Application;
import com.example_amar.central_athority.model.AuthChallenge;
import com.example_amar.central_athority.model.Device;
import com.example_amar.central_athority.repository.AuthChallengeRepository;
import com.example_amar.central_athority.request.TokenRequest;
import com.example_amar.central_athority.response.TokenResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
   private final AuthChallengeRepository challengeRepository;
   private final SecretKeyService secretKeyService;

   @Value("${jwt.secret}")
   private String secretKeystring;

   @Value("${jwt.expiration}")
   private long expiration;
   private SecretKey secretKey;

   @PostConstruct
   public void init() {
       this.secretKey= Keys.hmacShaKeyFor(secretKeystring.getBytes());
   }



    public void verifyToken (TokenRequest tokenRequest) throws Exception
    {
       // step-1 verify the fields exists
       AuthChallenge challenge= challengeRepository.findByChallengeId(tokenRequest.getChallengeId())
               .orElseThrow(()->new IllegalArgumentException("Invalid Challenge Id:"));
       String serverRandom=challenge.getServerRandom();
       Device device=challenge.getRegistration();
       Application app=device.getApp();
       //step 2 verify the time stamp data
        if (tokenRequest.getTimestamp() > challenge.getExpiresEpoch()) {
            throw new IllegalArgumentException("Challenge has expired");
        }
        // step 3 challengeId:server_rand:clinet_nonce:code_hash:timestamp:registraionId
       String hashDataString=String.join(":",tokenRequest.getChallengeId(),
               serverRandom,tokenRequest.getClientNonce(),app.getCodeHash()
               ,Long.toString(tokenRequest.getTimestamp()),tokenRequest.getRegistrationId());

        if( !secretKeyService.tokenHmacVerification(hashDataString, tokenRequest.getSignature(), device.getAppSecret()))
        {
            throw new SecurityException("Invalid signature");
        }


    }

    public TokenResponse generateTokenResponse(TokenRequest tokenRequest) throws Exception
    {
        Instant now= Instant.now();
        Instant expiry=now.plusSeconds(expiration);

        String tokenId="TOKEN-"+UUID.randomUUID();
        long issuedEpoch=now.getEpochSecond();
        long expiresIn=expiration;
        Map<String,Object> claims= Map.of("tokenId",tokenId,"deviceId",tokenRequest.getDeviceId(),"appId",tokenRequest.getAppId(),
                "scope","read/write");
        String token= Jwts.builder().setIssuer("Central_Authority").setSubject(tokenRequest.getRegistrationId()).setIssuedAt(Date.from(now)).
                setExpiration(Date.from(expiry)).setClaims(claims).signWith(secretKey, SignatureAlgorithm.HS256).compact();

       return  TokenResponse.builder().acessToken(token).expiresIn(expiresIn).issuedAt(issuedEpoch).tokenId(tokenId).build();


    }
}
