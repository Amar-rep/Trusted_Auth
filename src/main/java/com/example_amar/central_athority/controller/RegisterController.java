package com.example_amar.central_athority.controller;

import com.example_amar.central_athority.request.RegisterRequest;
import com.example_amar.central_athority.response.RegisterResponse;
import com.example_amar.central_athority.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class RegisterController {
    private  final RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {

            try {
                RegisterResponse response=registrationService.register(registerRequest);
                return ResponseEntity.status(200).body(response);
            }catch (IllegalArgumentException e)
            {
                return ResponseEntity.status(400).body(e.getMessage());
            }catch (SecurityException e)
            {
                return ResponseEntity.status(401).body(e.getMessage());
            }catch (Exception e)
            {
                return ResponseEntity.status(500).body("Internal server error");
            }


    }
}
