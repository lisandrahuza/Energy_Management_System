package ro.tuc.ds2020.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.dtos.UserDetailsDTO;
import ro.tuc.ds2020.entities.User;
import ro.tuc.ds2020.services.UserServices;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@CrossOrigin(origins = "http://localhost/users")
@RequestMapping(value = "/user")
public class UserController {
    private final UserServices userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserServices userService,JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider=jwtTokenProvider;
    }



    @Autowired
    private PasswordEncoder passwordEncoder;

    private UUID getUserIdFromToken(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        System.out.println(token);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String userId = jwtTokenProvider.getIdFromToken(token);
            return UUID.fromString(userId);
        }
        return null;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    @GetMapping()
    public ResponseEntity<List<UserDTO>> getUsers(HttpServletRequest request) {
        System.out.println(request);
        UUID userId = getUserIdFromToken(request);

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if no valid token
        }
        List<UserDTO> dtos = userService.findUsers();
        for (UserDTO dto : dtos) {
            System.out.println(dto.toString());
            Link userLink = linkTo(methodOn(UserController.class)
                    .getUser1(dto.getId())).withRel("userDetails");
            dto.add(userLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<UserDTO>> getAdmins(HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if no valid token
        }
        List<UserDTO> dtos = userService.findAdmins();
        for (UserDTO dto : dtos) {
            System.out.println(dto.toString());
            Link userLink = linkTo(methodOn(UserController.class)
                    .getUser1(dto.getId())).withRel("userDetails");
            dto.add(userLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsersMessages(HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if no valid token
        }
        List<UserDTO> dtos = userService.findUsersMessages();
        for (UserDTO dto : dtos) {
            System.out.println(dto.toString());
            Link userLink = linkTo(methodOn(UserController.class)
                    .getUser1(dto.getId())).withRel("userDetails");
            dto.add(userLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/check/{id}")
    public ResponseEntity<Boolean> check(@PathVariable UUID id){

        System.out.println(id);
        UserDetailsDTO dto = userService.findUserById(id);
        System.out.println(dto.getIsAdmin());
        return new ResponseEntity<>(dto.getIsAdmin(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UUID> insertUser(@Valid @RequestBody UserDetailsDTO userDTO, HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Unauthorized if no valid token
        }

        String username = userDTO.getUsername();
        UserDetailsDTO userGasit = userService.findUserUsernameInsert(username);
        if (userGasit == null) {
            System.out.println(userDTO.toString());
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            UUID userID = userService.insert(userDTO);
            System.out.println(userID);

            // Obține token-ul din request-ul curent
            String token = request.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Unauthorized if token is missing
            }

            RestTemplate restTemplate = new RestTemplate();
            String referenceUrl = "http://device-app:8081/devices/userReference/insert";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", token); // Adaugă token-ul în header

            HttpEntity<UUID> entity = new HttpEntity<>(userID, headers);

            try {
                ResponseEntity<UUID> response = restTemplate.postForEntity(referenceUrl, entity, UUID.class);

                if (response.getStatusCode() == HttpStatus.CREATED) {
                    return new ResponseEntity<>(userID, HttpStatus.CREATED);
                } else {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (HttpClientErrorException e) {
                System.err.println("Error during external service call: " + e.getMessage());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);
    }



    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserDetailsDTO userDTO) {
        String username=userDTO.getUsername();
        String password = userDTO.getPassword();
        UserDetailsDTO user = userService.findUserUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            Map<String, Object> response = new HashMap<>();
            response.put("isAdmin", user.getIsAdmin());
            response.put("id",user.getId());
            response.put("message", "Login successful");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDetailsDTO> getUser(@PathVariable("id") UUID userId,HttpServletRequest request) {
        UUID userIdV = getUserIdFromToken(request);
        if (userIdV == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if no valid token
        }
        UserDetailsDTO dto = userService.findUserById(userId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    public ResponseEntity<UserDetailsDTO> getUser1(UUID userId) {
        UserDetailsDTO dto = userService.findUserById(userId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUserByUsername(@PathVariable String username, HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Unauthorized if no valid token
        }

        UserDetailsDTO userDTO = userService.findUserUsername(username);
        if (userDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 if user does not exist
        }

        UUID userID = userDTO.getId();
        userService.delete(userDTO);
        System.out.println(userID);

        // Obține token-ul din request-ul curent
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Unauthorized if token is missing
        }

        RestTemplate restTemplate = new RestTemplate();
        String referenceUrl = "http://device-app:8081/devices/userReference/delete"; // Endpoint-ul pentru DELETE

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token); // Adaugă token-ul în header

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(referenceUrl + "/" + userID, HttpMethod.DELETE, entity, Void.class);

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Error during external service call: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }



    @PatchMapping(value = "/update")
    public ResponseEntity<Void> updateUser( @RequestBody UserDetailsDTO userDTO,HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if no valid token
        }
        System.out.println(userDTO.toString());
        int rowsUpdated = userService.update(userDTO);
        if (rowsUpdated > 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
