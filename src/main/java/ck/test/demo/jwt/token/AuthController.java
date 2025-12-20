package ck.test.demo.jwt.token;
// AuthController.java
import ck.test.demo.JwtResponse;
import ck.test.demo.Role;
import ck.test.demo.User;
import ck.test.demo.repository.UserRepository;
import ck.test.demo.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    // (You will need LoginRequest and JwtResponse DTO classes for input/output)

    @PostMapping("/register")
    public ResponseEntity<?> authenticateUser(@RequestBody User loginRequest) {

            userRepository.save(loginRequest);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate the token using the authenticated user details
      List<String> roles=  loginRequest.getRoles().stream().map(Role::getName).collect(Collectors.toList());// Example roles
        String jwt = jwtTokenUtil.generateToken(loginRequest.getUsername(), roles);
            return ResponseEntity.ok(new JwtResponse(jwt)); // Return the token
    }
}
