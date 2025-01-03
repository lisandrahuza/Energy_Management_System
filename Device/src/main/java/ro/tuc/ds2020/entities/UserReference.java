package ro.tuc.ds2020.entities;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.UUID;

@Entity
public class UserReference implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    private UUID id;


    public UserReference() {
    }

    public UserReference(UUID id) {
        this.id=id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


}
