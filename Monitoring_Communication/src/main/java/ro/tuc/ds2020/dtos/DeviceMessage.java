package ro.tuc.ds2020.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceMessage {

    @JsonProperty("operation")
    private String operation;

    @JsonProperty("id")
    private String id;

    @JsonProperty("maxEnergy")
    private int maxEnergy;

    @JsonProperty("userId")
    private String userId;

    // Getters și setters necesare
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
