package com.example_amar.central_athority.request;

import java.math.BigInteger;

public class TokenRequest {
    private String appId;
    private String deviceId;
    private String registrationId;
    private String challengeId;
    private String clientNonce;
    private String clientHash;
    private BigInteger timestamp;
    private String signature;
}
