package kz.itolstov.demo.security.auth;

import kz.itolstov.demo.model.Role;
import kz.itolstov.demo.model.User;
import kz.itolstov.demo.repository.UserRepository;
import kz.itolstov.demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(Role.USER);

        userRepository.save(newUser);
        String jwtToken = jwtService.generateToken(newUser);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse auth(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                ) );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Failing auth: user not found"));
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }
}
