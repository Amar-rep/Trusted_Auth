package com.example_amar.central_athority.service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

@Service
public class KeyGenerationService {

    private final SecureRandom secureRandom = new SecureRandom();


    public Map<String, String> generateSecp256k1HexKeyPair() throws Exception {
        // Generate random EC key pair
        ECKeyPair keyPair = Keys.createEcKeyPair(secureRandom);

        BigInteger privateKey = keyPair.getPrivateKey();
        BigInteger publicKey  = keyPair.getPublicKey();

        // Convert to hex
        String privHex = privateKey.toString(16);
        String pubHex  = publicKey.toString(16);

        // Optional: pad hex to 64/128 chars for consistency
        privHex = String.format("%064x", privateKey);
        pubHex  = String.format("%0128x", publicKey);

        Map<String, String> keys = new HashMap<>();
        keys.put("privateKey", privHex);
        keys.put("publicKey", pubHex);

        return keys;
    }
}
