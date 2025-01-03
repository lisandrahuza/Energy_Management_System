package ro.tuc.ds2020.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findById(UUID id);
    List<User> findByName(String address);
    List<User> findAll();
    Optional<User> findByUsername(String username);
    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.username=:username, u.name = :name, u.password = :password WHERE u.id = :id")
    int updateAll(@Param ("id") UUID id, @Param ("username") String username, @Param("name") String name, @Param("password") String password);

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET  u.name = :name WHERE u.username = :username")
    int updateName(@Param ("username") String username,@Param("name") String name);
    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.password = :password WHERE u.username = :username")
    int updatePassword(@Param ("username") String username,@Param("password") String password);
    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.isAdmin=:isAdmin WHERE u.username = :username")
    int updateIsAdmin(@Param ("username") String username, @Param("isAdmin") Boolean isAdmin);
    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.username = :username WHERE u.id = :id")
    int updateUsername(@Param ("id") UUID id ,@Param ("username") String username);
    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.username = :username, u.password=:password WHERE u.id = :id")
    int updateUsernamePassword(@Param ("id") UUID id ,@Param ("username") String username,@Param ("password") String password);
    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.username = :username, u.name=:name WHERE u.id = :id")
    int updateUsernameName(@Param ("id") UUID id ,@Param ("username") String username,@Param ("name") String name);

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.password = :password, u.name=:name WHERE u.username = :username")
    int updateNamePassword(@Param ("username") String username,@Param ("name") String name,@Param("password") String password);

}
