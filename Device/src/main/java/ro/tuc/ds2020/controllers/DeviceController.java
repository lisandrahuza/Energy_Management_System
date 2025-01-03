package ro.tuc.ds2020.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceDetailsDTO;
import ro.tuc.ds2020.dtos.DeviceMessage;
import ro.tuc.ds2020.dtos.UserReferenceDetailsDTO;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.services.DeviceServices;
import ro.tuc.ds2020.services.RabbitMQService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/device")
public class DeviceController {
    private final DeviceServices deviceService;
    private final RabbitMQService rabbitMQSender;

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public DeviceController(DeviceServices deviceService, RabbitMQService rabbitMQSender, ObjectMapper objectMapper,JwtTokenProvider jwtTokenProvider ) {
        this.deviceService = deviceService;
        this.rabbitMQSender = rabbitMQSender;
        this.objectMapper = objectMapper;
        this.jwtTokenProvider=jwtTokenProvider;
    }

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

    @GetMapping()
    public ResponseEntity<List<DeviceDTO>> getDevices(HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if no valid token
        }
        List<DeviceDTO> dtos = deviceService.findDevices();
        for (DeviceDTO dto : dtos) {
            Link deviceLink = linkTo(methodOn(DeviceController.class)
                    .getDevice1(dto.getId())).withRel("deviceDetails");
            dto.add(deviceLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UUID> insert(@Valid @RequestBody DeviceDetailsDTO deviceDTO,HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if no valid token
        }
        UUID deviceID = deviceService.insert(deviceDTO);
        DeviceMessage message;
        // Create a message for RabbitMQ
        if (deviceDTO.getUser() != null)
            message = new DeviceMessage("inserare", deviceID, deviceDTO.getMaxenergy(), deviceDTO.getUser().getId());
        else
            message = new DeviceMessage("inserare", deviceID, deviceDTO.getMaxenergy(), null);

        // Serialize message to JSON string before sending to RabbitMQ
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            rabbitMQSender.sendDeviceMessage(jsonMessage);  // Ensure sendDeviceMessage expects a JSON string
        } catch (JsonProcessingException e) {
            // Handle exception (e.g., logging)
            e.printStackTrace();
        }

        return new ResponseEntity<>(deviceID, HttpStatus.CREATED);
    }



    @PutMapping(value = "/{id_device}")
    public ResponseEntity<Void> mapping(@PathVariable UUID id_device, @RequestBody UserReferenceDetailsDTO userReference,HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if no valid token
        }
        System.out.println(id_device);
        System.out.println(userReference.getId());
        System.out.println(userReference.toString());
        deviceService.mapping(id_device, userReference);
        DeviceMessage message;
        message = new DeviceMessage("update_user", id_device,10, userReference.getId());
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            rabbitMQSender.sendDeviceMessage(jsonMessage);  // Ensure sendDeviceMessage expects a JSON string
        } catch (JsonProcessingException e) {
            // Handle exception (e.g., logging)
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/deleteMapping/{id_device}")
    public ResponseEntity<Void> deleteMapping(@PathVariable UUID id_device,HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if no valid token
        }  // Unauthorized if no valid token

        System.out.println(id_device);
        deviceService.deleteMapping(id_device);
        DeviceMessage message;
        message = new DeviceMessage("update_user", id_device,10,null);
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            rabbitMQSender.sendDeviceMessage(jsonMessage);  // Ensure sendDeviceMessage expects a JSON string
        } catch (JsonProcessingException e) {
            // Handle exception (e.g., logging)
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<DeviceDetailsDTO> getDevice(@PathVariable("id") UUID deviceId,HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if no valid token
        }
        DeviceDetailsDTO dto = deviceService.findDeviceById(deviceId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    public ResponseEntity<DeviceDetailsDTO> getDevice1(UUID deviceId) {

        DeviceDetailsDTO dto = deviceService.findDeviceById(deviceId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable("id") UUID deviceId,HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if no valid token
        }
        DeviceDetailsDTO deviceDTO = deviceService.findDeviceById(deviceId);
        deviceService.delete(deviceDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PatchMapping(value = "/update")
    public ResponseEntity<Void> updateDevice(@RequestBody DeviceDetailsDTO deviceDTO,HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if no valid token
        }
        System.out.println(deviceDTO.toString());
        int rowsUpdated = deviceService.update(deviceDTO);
        if (rowsUpdated > 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/user/{id}")
    public ResponseEntity<List<DeviceDTO>> getDevicesByUser(@PathVariable("id") UUID userId,HttpServletRequest request) {
        UUID userIdV = getUserIdFromToken(request);
        if (userIdV == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // Unauthorized if no valid token
        }
        System.out.println(userId);
        List<DeviceDTO> dto = deviceService.findDevicesByUser(userId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
