package ck.test.demo.jwt.token;
// AuthController.java
import ck.test.demo.JwtResponse;
import ck.test.demo.Role;
import ck.test.demo.User;
import ck.test.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    
    @Autowired
    private PasswordEncoder passwordEncoder;


    // (You will need LoginRequest and JwtResponse DTO classes for input/output)

    @PostMapping("/register")
    public ResponseEntity<?> authenticateUser(@RequestBody User loginRequest) {
              
    	  String plainPwd=loginRequest.getPassword();
    	 String encryptedPwd =passwordEncoder.encode(loginRequest.getPassword());
         loginRequest.setPassword(encryptedPwd);
         loginRequest.setPassword(encryptedPwd);
           try{
               userRepository.save(loginRequest);
           }
           catch(Exception exception){

        }

            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getUsername(), plainPwd
                        )
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch (Exception exception){
                return ResponseEntity.ok(new JwtResponse("authentication failed"));
            }



        // Generate the token using the authenticated user details
      List<String> roles=  loginRequest.getRoles().stream().map(Role::getName).collect(Collectors.toList());// Example roles
        String jwt = jwtTokenUtil.generateToken(loginRequest.getUsername(), roles);
            return ResponseEntity.ok(new JwtResponse(jwt)); // Return the token
    }
}
