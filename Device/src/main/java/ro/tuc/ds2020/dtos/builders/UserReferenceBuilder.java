package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.UserReferenceDTO;
import ro.tuc.ds2020.dtos.UserReferenceDetailsDTO;
import ro.tuc.ds2020.entities.UserReference;

public class UserReferenceBuilder {
    public UserReferenceBuilder() {
    }
    public static UserReferenceDTO toUserReferenceDTO(UserReference userReference) {
        return new UserReferenceDTO(userReference.getId());
    }

    public static UserReferenceDetailsDTO toUserReferenceDetailsDTO(UserReference userReference) {
        return new UserReferenceDetailsDTO(userReference.getId());
    }

    public static UserReference toEntity(UserReferenceDetailsDTO userReferenceDetailsDTO) {
        return new UserReference(userReferenceDetailsDTO.getId());
    }
}
