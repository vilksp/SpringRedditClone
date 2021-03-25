package ksp.vilius.reddit.service;

import ksp.vilius.reddit.dto.AuthenticationResponse;
import ksp.vilius.reddit.dto.LoginRequest;
import ksp.vilius.reddit.dto.RegisterRequest;
import ksp.vilius.reddit.exceptions.SpringRedditException;
import ksp.vilius.reddit.model.NotificationEmail;
import ksp.vilius.reddit.model.SecurityUser;
import ksp.vilius.reddit.model.User;
import ksp.vilius.reddit.model.VerificationToken;
import ksp.vilius.reddit.repositories.UserRepository;
import ksp.vilius.reddit.repositories.VerificationTokenRepository;
import ksp.vilius.reddit.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {


    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Transactional
    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setRole("ROLE_USER");
        user.setEnabled(false);

        userRepository.save(user);

        String token = generateVerificationToken(user);
        mailService.sendEmail(
                new NotificationEmail(
                        "Please activate your account!", user.getEmail(),
                        "Thank you for signing up to Spring Reddit clone, please click on the url below to active your account: "
                                + "http://localhost:8080/api/auth/accountVerification/" + token));
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new SpringRedditException("Invalid token!"));
        fetchUserAndEnableAccount(verificationToken.get());
    }

    @Transactional
    private void fetchUserAndEnableAccount(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SpringRedditException("User not found with username: " + username));
        user.setEnabled(true);
        userRepository.save(user);

    }

    public AuthenticationResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        String token = jwtProvider.generateToken(authenticate);

        return new AuthenticationResponse(token, loginRequest.getUsername());
    }

    public User getCurrentUser() {

        SecurityUser principal = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userRepository
                .findByUsername(principal.getUsername())
                .orElseThrow(() -> new SpringRedditException("No such user found with username"));
    }
}
