package ro.tuc.ds2020.dtos;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;
import java.util.UUID;
public class UserDTO extends RepresentationModel<UserDTO> {
    private UUID id;
    private String username;
    private String name;
    private Boolean isAdmin;
    private String password;

    public UserDTO() {
    }

    public UserDTO(UUID id,String username, String name, Boolean isAdmin, String password) {
        this.username=username;
        this.id = id;
        this.name = name;
        this.isAdmin = isAdmin;
        this.password = password;
    }


    public UserDTO(String name, Boolean isAdmin, String password) {
        this.name = name;
        this.isAdmin = isAdmin;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserDTO userDTO = (UserDTO) o;
        return  Objects.equals(name, userDTO.name) && Objects.equals(isAdmin, userDTO.isAdmin) && Objects.equals(password, userDTO.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, isAdmin, password);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", isAdmin=" + isAdmin +
                ", password='" + password + '\'' +
                '}';
    }
}
