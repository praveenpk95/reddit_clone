package com.project.backend.security;

import com.project.backend.email.MailContentBuilder;
import com.project.backend.email.MailService;
import com.project.backend.email.NotificationEmail;
import com.project.backend.user.User;
import com.project.backend.user.UserRepository;
import com.project.backend.verification.VerificationToken;
import com.project.backend.verification.VerificationTokenRepository;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;

import static com.project.backend.utils.Constants.ACTIVATION_EMAIL;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final VerificationTokenRepository verificationTokenRepository;

    private MailService mailService;
    private MailContentBuilder mailContentBuilder;

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
}
