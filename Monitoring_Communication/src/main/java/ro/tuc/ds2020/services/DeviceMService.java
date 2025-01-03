package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.DeviceMDTO;
import ro.tuc.ds2020.dtos.DeviceMDetailsDTO;
import ro.tuc.ds2020.dtos.builders.DeviceMBuilder;
import ro.tuc.ds2020.entities.DeviceM;
import ro.tuc.ds2020.repositories.DeviceMRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceMService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceMService.class);
    private final DeviceMRepository deviceRepository;

    @Autowired
    public DeviceMService(DeviceMRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<DeviceMDTO> findDevices() {
        List<DeviceM> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .map(DeviceMBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    public DeviceMDetailsDTO findDeviceById(UUID id) {
        Optional<DeviceM> prosumerOptional = deviceRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(DeviceM.class.getSimpleName() + " with id: " + id);
        }
        return DeviceMBuilder.toDeviceDetailsDTO(prosumerOptional.get());
    }

    public UUID insert(DeviceMDetailsDTO deviceDTO) {
        DeviceM device = DeviceMBuilder.toEntity(deviceDTO);
        device = deviceRepository.save(device);
        LOGGER.debug("Device with id {} was inserted in db", device.getId());
        return device.getId();
    }


    public void delete(DeviceMDetailsDTO deviceDTO) {
        UUID id=deviceDTO.getId();
        Optional<DeviceM> deviceOptional = deviceRepository.findById(id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(DeviceM.class.getSimpleName() + " with id: " + id);
        }
        DeviceM device=DeviceMBuilder.toEntity(deviceDTO);
        deviceRepository.delete(device);
    }
    public List<DeviceMDTO> findDevicesByUser(UUID id) {
        List<DeviceM> deviceList = deviceRepository.findDevicesByUserId(id);
        return deviceList.stream()
                .map(DeviceMBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    public void updateUser(DeviceMDetailsDTO m) {
        System.out.println("update user");
        System.out.println(m.toString());
        DeviceM device = DeviceMBuilder.toEntity(m);
        System.out.println("e bine");
        deviceRepository.updateUserId(m.getId(), m.getUserId());
        System.out.println("am introdus");
    }

    public void updateMaxenergy(DeviceMDetailsDTO m) {
        DeviceM device = DeviceMBuilder.toEntity(m);
        deviceRepository.updateMaxEnergy(m.getId(), m.getMaxenergy());
    }

    public Integer findMax(UUID defaultDeviceId) {
        return deviceRepository.findMaxenergyById(defaultDeviceId);
    }

    public UUID findUser(UUID defaultDeviceId) {
        Optional<DeviceM> device =deviceRepository.findById(defaultDeviceId);
        if(device.isPresent())
            return device.get().getUserId();
        else
            return null;
    }
}
