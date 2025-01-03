package ro.tuc.ds2020.dtos;

import java.util.UUID;

public class DeviceMessage{
    private String operation;
    private UUID id;
    private int maxEnergy;
    private UUID userId;

    public DeviceMessage(String operation, UUID id, int maxEnergy, UUID userId) {
        this.operation=operation;
        this.id = id;
        this.maxEnergy = maxEnergy;
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "DeviceMessage{" +
                "operation='" + operation + '\'' +
                ", id=" + id +
                ", maxEnergy=" + maxEnergy +
                ", userId=" + userId +
                '}';
    }
}
