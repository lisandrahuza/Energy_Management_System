package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceDetailsDTO;
import ro.tuc.ds2020.dtos.UserReferenceDetailsDTO;
import ro.tuc.ds2020.dtos.builders.DeviceBuilder;
import ro.tuc.ds2020.dtos.builders.UserReferenceBuilder;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.entities.UserReference;
import ro.tuc.ds2020.repositories.DeviceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceServices {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceServices.class);
    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceServices(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<DeviceDTO> findDevices() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    public DeviceDetailsDTO findDeviceById(UUID id) {
        Optional<Device> prosumerOptional = deviceRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return DeviceBuilder.toDeviceDetailsDTO(prosumerOptional.get());
    }

    public UUID insert(DeviceDetailsDTO deviceDTO) {
        Device device = DeviceBuilder.toEntity(deviceDTO);
        device = deviceRepository.save(device);
        LOGGER.debug("Device with id {} was inserted in db", device.getId());
        return device.getId();
    }

    public void mapping(UUID device_id, UserReferenceDetailsDTO userReferenceDetailsDTO) {
        UserReference userReference = UserReferenceBuilder.toEntity(userReferenceDetailsDTO);
        deviceRepository.mapping(device_id,userReference);
    }
    public void deleteMapping(UUID device_id) {
        deviceRepository.deleteMapping(device_id);
    }
    public int update(DeviceDetailsDTO deviceDTO) {
        UUID id=deviceDTO.getId();
        Optional<Device> prosumerOptional = deviceRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        Device device=DeviceBuilder.toEntity(deviceDTO);
        if(device.getAddress().equals("neschimbat"))
        {
            if(device.getDescription().equals("neschimbat"))
            {
                return deviceRepository.updateMaxenergy(device.getId(), device.getMaxenergy());
            }
            else
            {
                if(device.getMaxenergy().equals(0))
                    return deviceRepository.updateDescription(device.getId(), device.getDescription());
                else
                    return deviceRepository.updateDescriptionMaxenergy(device.getId(), device.getDescription(), device.getMaxenergy());
            }
        }
        else
        {
            if(device.getDescription().equals("neschimbat"))
            {
                if(device.getMaxenergy().equals(0))
                    return deviceRepository.updateAddress(device.getId(),device.getAddress());
                else
                    return deviceRepository.updateAddressMaxenergy(device.getId(), device.getAddress(), device.getMaxenergy());
            }
            else
            {
                if(device.getMaxenergy().equals(0))
                    return deviceRepository.updateDescriptionAddress(device.getId(),device.getDescription(),device.getAddress());
                else
                    return deviceRepository.updateAll(device.getId(), device.getDescription(),device.getAddress(), device.getMaxenergy());
            }
        }

    }
    public void delete(DeviceDetailsDTO deviceDTO) {
        UUID id=deviceDTO.getId();
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        Device device=DeviceBuilder.toEntity(deviceDTO);
        deviceRepository.delete(device);
    }
    public List<DeviceDTO> findDevicesByUser(UUID id) {
        List<Device> deviceList = deviceRepository.findDevicesByUserId(id);
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

}
