package com.project.backend.security;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
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
}
