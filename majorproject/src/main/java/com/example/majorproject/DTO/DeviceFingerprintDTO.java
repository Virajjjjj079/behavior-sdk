package com.example.majorproject.DTO;

import java.time.LocalDateTime;

public class DeviceFingerprintDTO {
    public Long userID;
    public String userName;

    public String fingerprint; // hashed fingerprint string
    public LocalDateTime timestamp;
}
