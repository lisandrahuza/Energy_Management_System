package ro.tuc.ds2020.dtos.builders;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceDetailsDTO;
import ro.tuc.ds2020.entities.Device;

public class DeviceBuilder {
    private DeviceBuilder() {
    }

    public static DeviceDTO toDeviceDTO(Device device) {
        return new DeviceDTO(device.getId(), device.getAddress(), device.getDescription(), device.getMaxenergy(), device.getUser());
    }

        public static DeviceDetailsDTO toDeviceDetailsDTO(Device device) {
        return new DeviceDetailsDTO(device.getId(), device.getAddress(), device.getDescription(), device.getMaxenergy(), device.getUser());
    }

    public static Device toEntity(DeviceDetailsDTO deviceDetailsDTO) {
        return new Device(deviceDetailsDTO.getId(),
                deviceDetailsDTO.getAddress(),
                deviceDetailsDTO.getDescription(),
                deviceDetailsDTO.getMaxenergy(),
                deviceDetailsDTO.getUser());
    }
}
