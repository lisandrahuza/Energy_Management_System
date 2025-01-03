package ro.tuc.ds2020.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;

import ro.tuc.ds2020.dtos.*;
import ro.tuc.ds2020.dtos.UserReferenceDTO;
import ro.tuc.ds2020.dtos.builders.UserReferenceBuilder;
import ro.tuc.ds2020.entities.UserReference;
import ro.tuc.ds2020.repositories.UserReferenceRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserReferenceServices {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceServices.class);
    private final UserReferenceRepository userReferenceRepository;

    @Autowired
    public UserReferenceServices(UserReferenceRepository userReferenceRepository) {
        this.userReferenceRepository = userReferenceRepository;
    }

    public List<UserReferenceDTO> findUsersReference() {
        List<UserReference> userReferenceList = userReferenceRepository.findAll();
        return userReferenceList.stream()
                .map(UserReferenceBuilder::toUserReferenceDTO)
                .collect(Collectors.toList());
    }

    public UserReferenceDetailsDTO findUserReferenceById(UUID id) {
        Optional<UserReference> prosumerOptional = userReferenceRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("UserReference with id {} was not found in db", id);
            throw new ResourceNotFoundException(UserReference.class.getSimpleName() + " with id: " + id);
        }
        return UserReferenceBuilder.toUserReferenceDetailsDTO(prosumerOptional.get());
    }

    public UUID insert(@Valid UserReferenceDetailsDTO userDTO) {
        UserReference userReference =UserReferenceBuilder.toEntity(userDTO);
        userReference = userReferenceRepository.save(userReference);
        LOGGER.debug("UserReference with id {} was inserted in db", userReference.getId());
        return userReference.getId();
    }

    public void delete(UserReferenceDetailsDTO userDTO) {
        UUID id=userDTO.getId();
        Optional<UserReference> userOptional = userReferenceRepository.findById(id);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new ResourceNotFoundException(UserReference.class.getSimpleName() + " with id: " + id);
        }
        UserReference user=UserReferenceBuilder.toEntity(userDTO);
        userReferenceRepository.delete(user);
    }

}
