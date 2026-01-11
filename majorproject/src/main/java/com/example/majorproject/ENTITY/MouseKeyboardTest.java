package com.example.majorproject.ENTITY;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mousekeyboardtest")
public class MouseKeyboardTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    public Long userID;
    private String userName;

    private Double typingSpeed;
    private Double avgKeyHoldTime;
    private Double avgFlightTime;
    private Double totalTypingDuration;
    private Double straightness;
    private Double speedCv;
    private Double idleRatio;
    private Double directionChangeRate;

    private LocalDateTime timestamp;

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

    public Double getTypingSpeed() {
        return typingSpeed;
    }

    public void setTypingSpeed(Double typingSpeed) {
        this.typingSpeed = typingSpeed;
    }

    public Double getAvgKeyHoldTime() {
        return avgKeyHoldTime;
    }

    public void setAvgKeyHoldTime(Double avgKeyHoldTime) {
        this.avgKeyHoldTime = avgKeyHoldTime;
    }

    public Double getAvgFlightTime() {
        return avgFlightTime;
    }

    public void setAvgFlightTime(Double avgFlightTime) {
        this.avgFlightTime = avgFlightTime;
    }

    public Double getTotalTypingDuration() {
        return totalTypingDuration;
    }

    public void setTotalTypingDuration(Double totalTypingDuration) {
        this.totalTypingDuration = totalTypingDuration;
    }

    public Double getStraightness() {
        return straightness;
    }

    public void setStraightness(Double straightness) {
        this.straightness = straightness;
    }

    public Double getSpeedCv() {
        return speedCv;
    }

    public void setSpeedCv(Double speedCv) {
        this.speedCv = speedCv;
    }

    public Double getIdleRatio() {
        return idleRatio;
    }

    public void setIdleRatio(Double idleRatio) {
        this.idleRatio = idleRatio;
    }

    public Double getDirectionChangeRate() {
        return directionChangeRate;
    }

    public void setDirectionChangeRate(Double directionChangeRate) {
        this.directionChangeRate = directionChangeRate;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public MouseKeyboardTest(Long userID, String userName, Double typingSpeed, Double avgKeyHoldTime,
            Double avgFlightTime, Double totalTypingDuration, Double straightness, Double speedCv, Double idleRatio,
            Double directionChangeRate, LocalDateTime timestamp) {
        this.userID = userID;
        this.userName = userName;
        this.typingSpeed = typingSpeed;
        this.avgKeyHoldTime = avgKeyHoldTime;
        this.avgFlightTime = avgFlightTime;
        this.totalTypingDuration = totalTypingDuration;
        this.straightness = straightness;
        this.speedCv = speedCv;
        this.idleRatio = idleRatio;
        this.directionChangeRate = directionChangeRate;
        this.timestamp = timestamp;
    }

    public MouseKeyboardTest() {
        //TODO Auto-generated constructor stub
    }

    // Generate getters and setters
    
}
