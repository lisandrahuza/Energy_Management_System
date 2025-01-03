package ro.tuc.ds2020.dtos;


import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public class DeviceMDetailsDTO {
    private UUID id;

    @NotNull
    private Integer maxenergy;

    private UUID userId;

    public DeviceMDetailsDTO() {
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public DeviceMDetailsDTO(UUID id, Integer maxenergy, UUID userId) {
        this.id = id;
        this.maxenergy = maxenergy;
        this.userId = userId;
    }

    public DeviceMDetailsDTO(UUID id, Integer maxenergy) {
        this.id = id;
        this.maxenergy = maxenergy;
    }

    @Override
    public String toString() {
        return "DeviceMDetailsDTO{" +
                "id=" + id +
                ", maxenergy=" + maxenergy +
                ", userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceMDetailsDTO that = (DeviceMDetailsDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(maxenergy, that.maxenergy) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maxenergy, userId);
    }
}

