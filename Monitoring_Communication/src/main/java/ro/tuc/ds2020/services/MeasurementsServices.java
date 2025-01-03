package ro.tuc.ds2020.services;

import io.micrometer.core.instrument.Measurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.entities.Measurements;
import ro.tuc.ds2020.repositories.MeasurementsRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MeasurementsServices {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementsServices.class);
    private static MeasurementsRepository measurementsRepository;

    @Autowired
    public void MeasurementsRepository(MeasurementsRepository measurementsRepository) {
        this.measurementsRepository = measurementsRepository;
    }

    public Measurements insertData(Measurements measurements) {
        System.out.println("Inserting measurements: " + measurements);
        return measurementsRepository.save(measurements);
    }


    public Measurements updateData(Measurements measurements) {

        return measurementsRepository.save(measurements);
    }

    public List<Measurements> getMeasuementsMap(Timestamp timestampTimestampValue) {
            List<Measurements> m=measurementsRepository.findByTimestampNative(timestampTimestampValue);
            return m;
    }

    @Transactional
    public List<Measurements> getMeasurements() {
       return measurementsRepository.findAll();
    }

    @Transactional
    public List<Measurements> getMeasurementsForDate(String selectedDate) {
        List<Measurements> m=measurementsRepository.findAll();
        List<Measurements> result=new ArrayList<>();
        for (Measurements measurements : m) {
            Map<Float,String> map=measurements.getMeasurements();
            for (Map.Entry<Float, String> entry : map.entrySet()) {
                Float key = entry.getKey();
                String value = entry.getValue();
                String[] parts = value.split(" ");
                String datePart = parts[0];
                if(datePart.equals(selectedDate))
                    result.add(measurements);
            }

        }
        return result;
    }

    public List<Measurements> getMeasurementsForDeviceAndDate(UUID deviceId, String dateString) {
        List<Measurements> m=measurementsRepository.findByDevice(deviceId);
        List<Measurements> result=new ArrayList<>();
        for (Measurements measurements : m) {
            Map<Float,String> map=measurements.getMeasurements();
            for (Map.Entry<Float, String> entry : map.entrySet()) {
                Float key = entry.getKey();
                String value = entry.getValue();
                String[] parts = value.split(" ");
                String datePart = parts[0];
                if(datePart.equals(dateString))
                    result.add(measurements);
            }

        }
        return result;
    }
}
