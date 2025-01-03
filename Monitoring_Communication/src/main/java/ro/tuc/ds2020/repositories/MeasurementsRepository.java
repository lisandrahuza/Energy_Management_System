package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.entities.Measurements;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface MeasurementsRepository extends JpaRepository<Measurements, UUID> {

    Optional<Measurements> findById(UUID id);

    List<Measurements> findAll();

    @Query(value = "SELECT * FROM measurements m " +
            "JOIN measurements_map mm ON m.id = mm.measurements_id " +
            "WHERE mm.measurement_timestamp = :givenTimestamp", nativeQuery = true)
    List<Measurements> findByTimestampNative(@Param("givenTimestamp") Timestamp givenTimestamp);


    @Query(value = "SELECT m FROM Measurements m " +
            "JOIN m.measurements mm " +
            "WHERE m.id_device = :id_device")
    List<Measurements> findByDevice(@Param("id_device") UUID id_device);


}
