package com.educandoweb.course.resources;

import com.educandoweb.course.dto.LoginRequest;
import com.educandoweb.course.dto.LoginResponse;
import com.educandoweb.course.entities.User;
import com.educandoweb.course.repositories.UserRepository;
import com.educandoweb.course.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthResource {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // busca o user pelo email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // compara a senha digitada com o hash salvo
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // gera o token com email, uuid e role dentro
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole()
        );

        return ResponseEntity.ok(new LoginResponse(
                token,
                user.getId().toString(),
                user.getEmail(),
                user.getRole()
        ));
    }
}