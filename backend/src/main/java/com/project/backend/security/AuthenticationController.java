package com.project.backend.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody AuthenticationRequest authRequest) {
        authService.signUp(authRequest);
        return new ResponseEntity<>("User Registration Successful", HttpStatus.OK);
    }

    @GetMapping("/accountVerification{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        authService.verifyAccountToken(token);
        return new ResponseEntity<>("Account verification Successful", HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

}
