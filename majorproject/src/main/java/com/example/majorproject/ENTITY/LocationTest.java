package com.example.majorproject.ENTITY;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "locationtest")
public class LocationTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userID;
    private String userName;

    private Double latitude;
    private Double longitude;
    private LocalDateTime time;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUserID() {
        return userID;
    }
    public void setUserID(Long userID) {
        this.userID = userID;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public LocalDateTime getTime() {
        return time;
    }
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
    public LocationTest() {
        //TODO Auto-generated constructor stub
    }
    public LocationTest(Long userID, String userName, Double latitude, Double longitude, LocalDateTime time) {
        this.userID = userID;
        this.userName = userName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }
    

    // Getters and Setters
    
}
