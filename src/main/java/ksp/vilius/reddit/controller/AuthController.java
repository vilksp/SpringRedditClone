package ksp.vilius.reddit.controller;

import ksp.vilius.reddit.dto.LoginRequest;
import ksp.vilius.reddit.dto.RegisterRequest;
import ksp.vilius.reddit.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {


    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest) {
        authService.signup(registerRequest);
        return new ResponseEntity<>("User registration successful", OK);
    }

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> accountVerification(@PathVariable String token) {

        authService.verifyAccount(token);
        return new ResponseEntity<>("Account activated successfully", OK);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {

        return new ResponseEntity(authService.authenticateUser(loginRequest), OK);
    }

}
