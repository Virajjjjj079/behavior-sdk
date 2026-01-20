package com.example.majorproject.CONTROLLER;




import com.example.majorproject.ENTITY.DeviceFingerprintBase;
import com.example.majorproject.ENTITY.DeviceFingerprintTest;
import com.example.majorproject.REPOSITORY.DeviceFingerprintBaseRepository;
import com.example.majorproject.REPOSITORY.DeviceFingerprintTestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//input body example 
// {
//   "userID": 2,
//   "userName": "Anurag",
//   "deviceData": "{ \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36\", \"platform\": \"Win32\", \"language\": \"en-US\", \"screenResolution\": \"1366x768\", \"timezone\": \"Asia/Calcutta\", \"timezoneOffset\": -330, \"colorDepth\": 24, \"plugins\": \"PDF Viewer, Chrome PDF Viewer, Chromium PDF Viewer, Microsoft Edge PDF Viewer, WebKit built-in PDF\", \"fonts\": \"Arial, Verdana, Courier New, Times New Roman, Comic Sans MS, Georgia\" }"
// }


@RestController
@RequestMapping("/device-fingerprint")
public class DeviceFingerprintController {

    @Autowired
    private DeviceFingerprintBaseRepository baseRepo;

    @Autowired
    private DeviceFingerprintTestRepository testRepo;

    // ------------------------------
    // Endpoint to save or update fingerprint
    // ------------------------------
    @PostMapping("/save")
    public ResponseEntity<?> saveFingerprint(@RequestBody DeviceFingerprintBase input) {

        try {
            Long userId = input.getUserID();
            String deviceJson = input.getDeviceData();

            // 1️⃣ Compute SHA-256 hash of device data
            String fingerprint = hashDeviceData(deviceJson);

            LocalDateTime now = LocalDateTime.now();

            Optional<DeviceFingerprintBase> baseOpt = baseRepo.findByUserID(userId);
            Optional<DeviceFingerprintTest> testOpt = testRepo.findByUserID(userId);

            if (baseOpt.isEmpty()) {
                // Base does not exist → save in Base
                DeviceFingerprintBase base = new DeviceFingerprintBase(
                        input.getUserID(),
                        input.getUserName(),
                        deviceJson,
                        fingerprint,
                        now
                );
                baseRepo.save(base);
                return ResponseEntity.ok("✅ Base fingerprint saved.");
            } else if (testOpt.isEmpty()) {
                // Base exists, Test does not → save in Test
                DeviceFingerprintTest test = new DeviceFingerprintTest(
                        input.getUserID(),
                        input.getUserName(),
                        deviceJson,
                        fingerprint,
                        now
                );
                testRepo.save(test);
                triggerCompareByUserId(userId);
                return ResponseEntity.ok("✅ Test fingerprint saved.");
            } else {
                // Base exists, Test exists → update Test
                DeviceFingerprintTest test = testOpt.get();
                test.setDeviceData(deviceJson);
                test.setFingerprint(fingerprint);
                test.setTimestamp(now);
                testRepo.save(test);
                triggerCompareByUserId(userId);
                return ResponseEntity.ok("✅ Test fingerprint updated.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error: " + e.getMessage());
        }
    }

    // ------------------------------
    // Helper: SHA-256 hash
    // ------------------------------
    private String hashDeviceData(String deviceJson) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(deviceJson.getBytes("UTF-8"));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }



    //Compare request t0 flask

   // Flask URL (update if needed)
    private static final String FLASK_URL = "http://127.0.0.1:5000/device-compare";
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    // =========================
    // Compare endpoint
    // =========================
    @GetMapping("/compare/{userID}")
    public ResponseEntity<?> compareDevice(@PathVariable Long userID) {
        try {
            Optional<DeviceFingerprintBase> baseOpt = baseRepo.findByUserID(userID);
            Optional<DeviceFingerprintTest> testOpt = testRepo.findByUserID(userID);

            if (baseOpt.isEmpty()) {
                return ResponseEntity.status(404).body("❌ No base fingerprint found for userID " + userID);
            }
            if (testOpt.isEmpty()) {
                return ResponseEntity.status(404).body("❌ No test fingerprint found for userID " + userID);
            }

            DeviceFingerprintBase base = baseOpt.get();
            DeviceFingerprintTest test = testOpt.get();

            // Prepare JSON body to send to Flask
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("base", objectMapper.readValue(base.getDeviceData(), Map.class));
            requestBody.put("test", objectMapper.readValue(test.getDeviceData(), Map.class));

            // Call Flask endpoint
            ResponseEntity<Object> flaskResponse = restTemplate.postForEntity(FLASK_URL, requestBody, Object.class);

            // Return Flask response to frontend
            return ResponseEntity.ok(flaskResponse.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error: " + e.getMessage());
        }
    }


// =========================
// NEW FUNCTION 🔥
// Calls compare API using userID only
// =========================
private void triggerCompareByUserId(Long userID) {
    try {
        String url = "http://localhost:8080/device-fingerprint/compare/" + userID;

        ResponseEntity<Object> response =
                restTemplate.getForEntity(url, Object.class);

        System.out.println("✅ Compare triggered via API for userID " + userID);
        System.out.println("Compare response: " + response.getBody());

    } catch (Exception e) {
        System.err.println("❌ Compare API call failed for userID "
                + userID + " : " + e.getMessage());
    }
}


}



    

