package ro.tuc.ds2020.dtos.builders;
import ro.tuc.ds2020.dtos.DeviceMDTO;
import ro.tuc.ds2020.dtos.DeviceMDetailsDTO;
import ro.tuc.ds2020.entities.DeviceM;

public class DeviceMBuilder {
    private DeviceMBuilder() {
    }

    public static DeviceMDTO toDeviceDTO(DeviceM device) {
        return new DeviceMDTO(device.getId(),  device.getMaxenergy(), device.getUserId());
    }

    public static DeviceMDetailsDTO toDeviceDetailsDTO(DeviceM device) {
        return new DeviceMDetailsDTO(device.getId(),  device.getMaxenergy(), device.getUserId());
    }

    public static DeviceM toEntity(DeviceMDetailsDTO deviceDetailsDTO) {
        return new DeviceM(deviceDetailsDTO.getId(),
                deviceDetailsDTO.getMaxenergy(),
                deviceDetailsDTO.getUserId());
    }
}
