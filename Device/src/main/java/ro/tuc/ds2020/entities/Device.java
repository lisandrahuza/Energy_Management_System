package ro.tuc.ds2020.entities;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.UUID;

@Entity
public class Device implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-binary")
    private UUID id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "maxenergy", nullable = false)
    private Integer maxenergy;

    @ManyToOne
    @JoinColumn(name = "userId")
    private UserReference user;

    public Device() {
    }

    public Device(UUID id,String description, String address,  Integer maxenergy, UserReference user) {
        this.id = id;
        this.description = description;
        this.address = address;
        this.maxenergy = maxenergy;
        this.user = user;
    }
    public Device( String description,String address,  Integer maxenergy, UserReference user) {
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
}
