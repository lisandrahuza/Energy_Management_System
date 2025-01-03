package ro.tuc.ds2020.dtos;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;
import java.util.UUID;
public class DeviceMDTO extends RepresentationModel<DeviceMDTO> {
    private UUID id;
    private Integer maxenergy;
    private UUID userId;

    public DeviceMDTO() {
    }

    public DeviceMDTO(UUID id,  Integer maxenergy, UUID userId) {
        this.id = id;
        this.maxenergy = maxenergy;
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    public Integer getMaxenergy() {
        return maxenergy;
    }

    public void setMaxenergy(Integer maxenergy) {
        this.maxenergy = maxenergy;
    }

    public UUID getUser() {
        return userId;
    }

    public void setUser(UUID user) {
        this.userId = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceMDTO that = (DeviceMDTO) o;
        return
                Objects.equals(maxenergy, that.maxenergy)&&
                Objects.equals(userId, that.userId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(maxenergy);
    }

    @Override
    public String toString() {
        return "DeviceDTO{" +
                "id=" + id +
                ", maxenergy=" + maxenergy +
                '}';
    }
}
