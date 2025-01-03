package ro.tuc.ds2020.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;

import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.dtos.UserDetailsDTO;
import ro.tuc.ds2020.dtos.builders.UserBuilder;
import ro.tuc.ds2020.entities.User;
import ro.tuc.ds2020.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServices {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServices.class);
    private final UserRepository userRepository;

    @Autowired
    public UserServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> findUsers() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDetailsDTO findUserById(UUID id) {
        Optional<User> prosumerOptional = userRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }
        return UserBuilder.toUserDetailsDTO(prosumerOptional.get());
    }

    public UserDetailsDTO findUserUsername(String username) {
        Optional<User> users = userRepository.findByUsername(username);
        if (!users.isPresent()) {
            LOGGER.error("User with username {} was not found in db", username);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with username: " + username);
        }
        return UserBuilder.toUserDetailsDTO(users.get());
    }

    public UserDetailsDTO findUserUsernameInsert(String username) {
        Optional<User> users = userRepository.findByUsername(username);
        if (!users.isPresent()) {
            //LOGGER.error("User with username {} was not found in db", username);
            //throw new ResourceNotFoundException(User.class.getSimpleName() + " with username: " + username);
            return null;
        }
        return UserBuilder.toUserDetailsDTO(users.get());
    }

    public UUID insert(UserDetailsDTO userDTO) {

        User user =UserBuilder.toEntity(userDTO);
        user = userRepository.save(user);
        LOGGER.debug("User with id {} was inserted in db", user.getId());
        return user.getId();
    }
    public int update(UserDetailsDTO userDTO) {
        if(userDTO.getId()==null) {
            Optional<User> prosumerOptional = userRepository.findByUsername(userDTO.getUsername());
            if (!prosumerOptional.isPresent()) {
                LOGGER.error("User with username {} was not found in db", userDTO.getUsername());
                throw new ResourceNotFoundException(User.class.getSimpleName() + " with username: " + userDTO.getUsername());
            }
        }
        else {
            Optional<User> prosumerOptional = userRepository.findById(userDTO.getId());
            if (!prosumerOptional.isPresent()) {
                LOGGER.error("User with username {} was not found in db", userDTO.getUsername());
                throw new ResourceNotFoundException(User.class.getSimpleName() + " with username: " + userDTO.getUsername());
            }
        }
        User user=UserBuilder.toEntity(userDTO);
        if(user.getName().equals("neschimbat")) {
            if(user.getPassword().equals("neschimbat")) {
                if(user.getId().equals(null))
                    return userRepository.updateIsAdmin(user.getUsername(), user.getIsAdmin());
                else {
                    System.out.println("aici");
                    return userRepository.updateUsername(user.getId(),user.getUsername());
                }
            }
            else {
                if(user.getId()==null)
                    return userRepository.updatePassword(user.getUsername(), user.getPassword());
                else
                    return userRepository.updateUsernamePassword(user.getId(),user.getUsername(),user.getPassword());
            }
        }
        else {
            if (user.getPassword().equals("neschimbat")) {
                if (user.getId() == null)
                    return userRepository.updateName(user.getUsername(), user.getName());
                else
                    return userRepository.updateUsernameName(user.getId(), user.getUsername(), user.getName());
            } else {
                if(user.getId()==null)
                    return userRepository.updateNamePassword(user.getUsername(), user.getName(),user.getPassword());
                else
                    return userRepository.updateAll(user.getId(),user.getUsername(),user.getName(),user.getPassword());
            }
        }

    }

    public void delete(UserDetailsDTO userDTO) {
        UUID id=userDTO.getId();
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }
        User user=UserBuilder.toEntity(userDTO);
        userRepository.delete(user);
    }

    public List<UserDTO> findAdmins() {
        List<User> userList = userRepository.findAll();
        List<User> admins=new ArrayList<>();
        for(User user: userList)
        {
            if(user.getIsAdmin())
                admins.add(user);
        }
        return admins.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> findUsersMessages() {
        List<User> userList = userRepository.findAll();
        List<User> users=new ArrayList<>();
        for(User user: userList)
        {
            if(!user.getIsAdmin())
                users.add(user);
        }
        return users.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }
}
