package com.example.majorproject.DTO;

import java.time.LocalDateTime;

public class MouseKeyboardDTO {
    public Long userID;
    public String userName;

    public Double typingSpeed;
    public Double avgKeyHoldTime;
    public Double avgFlightTime;
    public Double totalTypingDuration;
    public Double straightness;
    public Double speedCv;
    public Double idleRatio;
    public Double directionChangeRate;

    public LocalDateTime timestamp;
}
