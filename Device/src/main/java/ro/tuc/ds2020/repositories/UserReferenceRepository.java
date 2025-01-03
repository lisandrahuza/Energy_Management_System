package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.tuc.ds2020.entities.UserReference;

import java.util.List;
import java.util.UUID;

public interface UserReferenceRepository extends JpaRepository<UserReference, UUID> {
    List<UserReference> findById(Integer id);
    List<UserReference> findAll();
}
