package ro.tuc.ds2020.dtos;
import org.springframework.hateoas.RepresentationModel;
import ro.tuc.ds2020.entities.UserReference;

import java.util.Objects;
import java.util.UUID;
public class DeviceDTO extends RepresentationModel<DeviceDTO> {
    private UUID id;
    private String description;
    private String address;
    private Integer maxenergy;
    private UserReference user;

    public DeviceDTO() {
    }

    public DeviceDTO(UUID id, String description, String address, Integer maxenergy, UserReference user) {
        this.id = id;
        this.description = description;
        this.address = address;
        this.maxenergy = maxenergy;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getMaxenergy() {
        return maxenergy;
    }

    public void setMaxenergy(Integer maxenergy) {
        this.maxenergy = maxenergy;
    }

    public UserReference getUser() {
        return user;
    }

    public void setUser(UserReference user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDTO that = (DeviceDTO) o;
        return Objects.equals(description, that.description) &&
                Objects.equals(address, that.address)&&
                Objects.equals(maxenergy, that.maxenergy)&&
                Objects.equals(user, that.user);
    }
    @Override
    public int hashCode() {
        return Objects.hash(description, address,maxenergy);
    }

    @Override
    public String toString() {
        return "DeviceDTO{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", maxenergy=" + maxenergy +
                ", user=" + user +
                '}';
    }
}
