package eu.seatter.homeheating.collector.Services;

import eu.seatter.homeheating.collector.Domain.SensorRecord;
import eu.seatter.homeheating.collector.Sensor.Sensor;
import eu.seatter.homeheating.collector.Sensor.SensorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 29/01/2019
 * Time: 17:13
 */
@Service
@Slf4j
public class SensorMeasurement {

    private Sensor sensorReader;
    private String sensorDescription;

    public void collect(List<SensorRecord> sensorList) {
        log.info("Start measurement processing");
        for (SensorRecord sensorRecord : sensorList) {
            SensorRecord srWithMeasurement;
            sensorDescription = "Sensor [" + sensorRecord.getSensorID() + "/" + sensorRecord.getSensorType() + "/" + sensorRecord.getFamilyId() + "]";
            log.info(sensorDescription);
            try {
                srWithMeasurement = readSensorValue(sensorRecord);
                log.debug(sensorDescription + " : Value returned - " + srWithMeasurement.getValue());
            } catch (Exception ex) {
                log.error("Error reading sensor with ID: " + sensorRecord.getSensorID() + ". " + ex.getMessage());
            }
            //todo Send to edge
        }
        log.info("Completed measurement processing");
    }

    private SensorRecord readSensorValue(SensorRecord sensorRecord) {
        sensorReader = SensorFactory.getSensor(sensorRecord.getSensorType());
        log.debug(sensorDescription + " : Reading value");
        try {
            sensorRecord.setValue(sensorReader.readSensorData(sensorRecord));
            sensorRecord.setMeasureTime(LocalDateTime.now(ZoneOffset.UTC));
            log.debug(sensorDescription + " : Returned value - " + sensorRecord.getValue());
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        return sensorRecord;
    }
}