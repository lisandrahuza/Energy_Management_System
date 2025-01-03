package ro.tuc.ds2020.dtos;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public class UserReferenceDetailsDTO {
    private UUID id;

    public UserReferenceDetailsDTO(UUID id) {
        this.id = id;
    }
    public UserReferenceDetailsDTO() {
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserReferenceDetailsDTO that = (UserReferenceDetailsDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public String toString() {
        return "UserReferenceDetailsDTO{" +
                "id=" + id +
                '}';
    }
}
