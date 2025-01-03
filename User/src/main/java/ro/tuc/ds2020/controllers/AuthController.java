package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.CustomUserDetails;
import ro.tuc.ds2020.dtos.JwtAuthenticationResponse;
import ro.tuc.ds2020.dtos.LoginRequest;
import ro.tuc.ds2020.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.BadCredentialsException;

@RestController
@CrossOrigin(origins = "http://localhost/users")
@RequestMapping(value = "/validare")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        System.out.println("parola primita : " + loginRequest.getPassword());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            System.out.println(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("am trecut ");

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            System.out.println(userDetails.toString());

            String token = jwtTokenProvider.generateToken(userDetails);

            return ResponseEntity.ok(new JwtAuthenticationResponse(token));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}

