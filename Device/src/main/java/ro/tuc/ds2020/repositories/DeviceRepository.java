package ro.tuc.ds2020.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.entities.UserReference;

import java.util.List;
import java.util.UUID;
public interface DeviceRepository extends JpaRepository<Device, UUID> {
    List<Device> findById(Integer id);
    List<Device> findByAddress(String address);
    List<Device> findAll();
    @Modifying
    @Transactional
    @Query("UPDATE Device d SET d.description=:description, d.address = :address, d.maxenergy = :maxenergy WHERE d.id = :id")
    int updateAll(@Param("id") UUID id, @Param("description") String description, @Param("address") String address, @Param("maxenergy") Integer maxenergy );

    @Modifying
    @Transactional
    @Query("UPDATE Device d SET d.description=:description WHERE d.id = :id")
    int updateDescription(@Param("id") UUID id, @Param("description") String description);


    @Modifying
    @Transactional
    @Query("UPDATE Device d SET  d.address = :address WHERE d.id = :id")
    int updateAddress(@Param("id") UUID id,  @Param("address") String address );


    @Modifying
    @Transactional
    @Query("UPDATE Device d SET d.maxenergy = :maxenergy WHERE d.id = :id")
    int updateMaxenergy(@Param("id") UUID id, @Param("maxenergy") Integer maxenergy );

    @Modifying
    @Transactional
    @Query("UPDATE Device d SET d.description=:description, d.address = :address WHERE d.id = :id")
    int updateDescriptionAddress(@Param("id") UUID id, @Param("description") String description, @Param("address") String address);

    @Modifying
    @Transactional
    @Query("UPDATE Device d SET d.description=:description , d.maxenergy = :maxenergy WHERE d.id = :id")
    int updateDescriptionMaxenergy(@Param("id") UUID id, @Param("description") String description,  @Param("maxenergy") Integer maxenergy );

    @Modifying
    @Transactional
    @Query("UPDATE Device d SET d.address = :address,d.maxenergy = :maxenergy WHERE d.id = :id")
    int updateAddressMaxenergy(@Param("id") UUID id, @Param("address") String address, @Param("maxenergy") Integer maxenergy);


    @Query("SELECT d FROM Device d WHERE d.user.id = :userId")
    List<Device> findDevicesByUserId(@Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query("UPDATE Device d SET d.user=:user  WHERE d.id = :id")
    int mapping(@Param("id") UUID id, @Param("user") UserReference user );

    @Modifying
    @Transactional
    @Query("UPDATE Device d SET d.user=null  WHERE d.id = :id")
    int deleteMapping(@Param("id") UUID id);


}
