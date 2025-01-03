package ro.tuc.ds2020.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
public class Measurements implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @Column(name = "id_device", nullable = false)
    private UUID id_device;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "measurements_map", joinColumns = @JoinColumn(name = "measurements_id"))
    @MapKeyColumn(name = "measurement_value")
    @Column(name = "measurement_timestamp")
    private Map<Float, String> measurements;

    // Default constructor
    public Measurements() {
        this.measurements = new HashMap<>();
    }

    // Constructor with ID and Device ID
    public Measurements(UUID id, UUID id_device) {
        this.id = id;
        this.id_device = id_device;
        this.measurements = new HashMap<>();
    }

    // Constructor with Device ID and Measurements
    public Measurements(UUID id_device, Map<Float, String> measurements) {
        this.id_device = id_device;
        this.measurements = measurements;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId_device() {
        return id_device;
    }

    public void setId_device(UUID id_device) {
        this.id_device = id_device;
    }

    public Map<Float, String> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Map<Float, String> measurements) {
        this.measurements = measurements;
    }
}
