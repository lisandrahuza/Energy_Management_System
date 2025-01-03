package ro.tuc.ds2020.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;
public class UserDetailsDTO {

    @JsonProperty("id")
    private UUID id;
    private String username;

    private String name;

    private Boolean isAdmin;

    private String password;

    public UserDetailsDTO() {
    }

    public UserDetailsDTO(UUID id,String username, String name, Boolean isAdmin, String password) {
        this.username=username;
        this.id = id;
        this.name = name;
        this.isAdmin = isAdmin;
        this.password = password;
    }

    public UserDetailsDTO(String username, String name, Boolean isAdmin, String password) {
        this.username=username;
        this.name = name;
        this.isAdmin = isAdmin;
        this.password = password;
    }


    public UserDetailsDTO(String username,String password) {
        this.username = username;
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
        UserDetailsDTO that = (UserDetailsDTO) o;
        return Objects.equals(id, that.id) &&  Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isAdmin, password);
    }

    @Override
    public String toString() {
        return "UserDetailsDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", isAdmin=" + isAdmin +
                ", password='" + password + '\'' +
                '}';
    }
}
