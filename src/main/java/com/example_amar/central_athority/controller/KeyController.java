package com.example_amar.central_athority.controller;

import com.example_amar.central_athority.service.KeyGenerationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.KeyPair;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/key")
@RequiredArgsConstructor
public class KeyController {

    private final KeyGenerationService keyGenerationService;

    @GetMapping("/generate")
    public Map<String, String> generateKeys() throws Exception {
        // directly get hex keys from service
        Map<String, String> keys = keyGenerationService.generateSecp256k1HexKeyPair();


        System.out.println("Private Key: " + keys.get("privateKey"));
        System.out.println("Public Key : " + keys.get("publicKey"));

        return keys; // returns Map<String,String> with hex keys
    }






    @GetMapping("/test")
    public String tester()
    {
        return "Success";
    }
}
