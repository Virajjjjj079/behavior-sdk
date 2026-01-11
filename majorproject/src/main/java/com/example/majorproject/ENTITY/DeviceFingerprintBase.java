package com.example.majorproject.ENTITY;




import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "devicefingerprintbase")
public class DeviceFingerprintBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userID;
    private String userName;

    // Store full JSON string if you want to keep raw data
    @Lob
    private String deviceData;

    // Store hash/fingerprint for comparison
    private String fingerprint;

    private LocalDateTime timestamp;

    public DeviceFingerprintBase() {}

    public DeviceFingerprintBase(Long userID, String userName, String deviceData, String fingerprint, LocalDateTime timestamp) {
        this.userID = userID;
        this.userName = userName;
        this.deviceData = deviceData;
        this.fingerprint = fingerprint;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserID() { return userID; }
    public void setUserID(Long userID) { this.userID = userID; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getDeviceData() { return deviceData; }
    public void setDeviceData(String deviceData) { this.deviceData = deviceData; }

    public String getFingerprint() { return fingerprint; }
    public void setFingerprint(String fingerprint) { this.fingerprint = fingerprint; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
