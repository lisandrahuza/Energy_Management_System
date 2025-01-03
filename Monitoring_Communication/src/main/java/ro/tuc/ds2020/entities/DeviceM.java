package ro.tuc.ds2020.entities;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.UUID;

@Entity
public class DeviceM implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    private UUID id;

    @Column(name = "maxenergy", nullable = false)
    private Integer maxenergy;

    @Column(name = "userId")
    private UUID userId;

    public DeviceM() {
    }



    public DeviceM(UUID id, Integer maxenergy, UUID userId) {
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }


}
