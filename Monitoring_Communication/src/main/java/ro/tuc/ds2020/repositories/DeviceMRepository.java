package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.entities.DeviceM;

import java.util.List;
import java.util.UUID;

public interface DeviceMRepository extends JpaRepository<DeviceM, UUID> {

    List<DeviceM> findById(Integer id);

    List<DeviceM> findAll();
    @Modifying
    @Transactional
    @Query("UPDATE DeviceM d SET  d.maxenergy = :maxenergy WHERE d.id = :id")
    void updateMaxEnergy(@Param("id") UUID id, @Param("maxenergy") Integer maxenergy );
    @Query("UPDATE DeviceM d SET  d.userId = :userId WHERE d.id = :id")
    void updateUserId(@Param("id") UUID id, @Param("userId") UUID userId );

    List<DeviceM> findDevicesByUserId(UUID id);

    @Query("SELECT d.maxenergy FROM DeviceM d WHERE d.id = :defaultDeviceId")
    public Integer findMaxenergyById(@Param("defaultDeviceId") UUID defaultDeviceId);

}
