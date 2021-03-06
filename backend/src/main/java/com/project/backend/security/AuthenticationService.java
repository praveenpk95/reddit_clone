package com.project.backend.security;

import com.project.backend.email.MailService;
import com.project.backend.email.NotificationEmail;
import com.project.backend.exceptions.SpringRedditException;
import com.project.backend.user.User;
import com.project.backend.user.UserRepository;
import com.project.backend.verification.VerificationToken;
import com.project.backend.verification.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.project.backend.utils.Constants.ACTIVATION_EMAIL;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final VerificationTokenRepository verificationTokenRepository;

    private MailService mailService;

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    @Transactional
    public void signUp(AuthenticationRequest authenticationRequest) {
        User user = new User();
        user.setUsername(authenticationRequest.getUsername());
        user.setEmail(authenticationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authenticationRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);

        String token = generateVerificationToken(user);

        mailService.sendMail(new NotificationEmail("Please Activate your account", user.getEmail(), "Thank you for signing up to Spring Reddit, please click on the below url to activate your account : " + ACTIVATION_EMAIL + token));
    }

    // NOTE : After user details are saved in the DB, a token is sent to the user's email and after verification user is enabled.
    public String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();

        // Store the token in the Database, so when the user verifies email, we can look up the token and enable the user.
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        // NOTE : Verification token is saved in the database
        verificationTokenRepository.save(verificationToken);

        return token;
    }

    // get the token from the db and verify the user token
    public void verifyAccountToken(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new SpringRedditException("Verification token invalid!"));
        fetchAndEnableUser(verificationToken.get());
    }

    private void fetchAndEnableUser(VerificationToken verificationToken) {
        long userId = verificationToken.getUser().getUserId();
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new SpringRedditException("User Name not found!"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return new AuthenticationResponse(token, loginRequest.getUsername());
    }
}
