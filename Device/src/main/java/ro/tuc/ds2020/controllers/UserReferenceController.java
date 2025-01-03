package ro.tuc.ds2020.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.DeviceDetailsDTO;
import ro.tuc.ds2020.dtos.UserReferenceDTO;
import ro.tuc.ds2020.dtos.UserReferenceDetailsDTO;

import ro.tuc.ds2020.dtos.UserReferenceDTO;
import ro.tuc.ds2020.entities.UserReference;
import ro.tuc.ds2020.services.UserReferenceServices;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/userReference")
public class UserReferenceController {
    private final UserReferenceServices userReferenceService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserReferenceController(UserReferenceServices userReferenceService,JwtTokenProvider jwtTokenProvider) {
        this.userReferenceService = userReferenceService;
        this.jwtTokenProvider=jwtTokenProvider;
    }

    /*@GetMapping()
    public ResponseEntity<List<UserReferenceDTO>> getUsersReference() {
        List<UserReferenceDTO> dtos = userReferenceService.findUsersReference();
        for (UserReferenceDTO dto : dtos) {
            Link userReferenceLink = linkTo(methodOn(UserReferenceController.class)
                    .getUserReference(dto.getId())).withRel("userReferenceDetails");
            dto.add(userReferenceLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }*/
    private UUID getUserIdFromToken(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
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
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserReferenceDetailsDTO> getUserReferenceById(@PathVariable("id") UUID user_id,HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if no valid token
        }
        UserReferenceDetailsDTO dto = userReferenceService.findUserReferenceById(user_id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping(value = "/insert")
    public ResponseEntity<UUID> insertUserReference(@RequestBody UUID id,HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if no valid token
        }
        System.out.println("Received UUID: " + id);
        UserReferenceDetailsDTO userDTO = new UserReferenceDetailsDTO(id);
        UUID referenceId = userReferenceService.insert(userDTO);

        return new ResponseEntity<>(referenceId, HttpStatus.CREATED);
    }


    @DeleteMapping(value = "/delete/{userId}")
    public ResponseEntity<Void> deleteUser(@Valid @PathVariable("userId") UUID userId,HttpServletRequest request) {
        UUID userIdV = getUserIdFromToken(request);
        if (userIdV == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if no valid token
        }
        System.out.println(userId);
        UserReferenceDetailsDTO userDTO = userReferenceService.findUserReferenceById(userId);
        userReferenceService.delete(userDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
