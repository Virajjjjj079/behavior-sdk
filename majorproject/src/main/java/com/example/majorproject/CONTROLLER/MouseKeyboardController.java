package com.example.majorproject.CONTROLLER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.example.majorproject.ENTITY.MouseKeyboardBase;
import com.example.majorproject.ENTITY.MouseKeyboardTest;
import com.example.majorproject.REPOSITORY.MouseKeyboardBaseRepository;
import com.example.majorproject.REPOSITORY.MouseKeyboardTestRepository;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/mouse-keyboard")
@CrossOrigin
public class MouseKeyboardController {

    @Autowired
    private MouseKeyboardBaseRepository baseRepo;

    @Autowired
    private MouseKeyboardTestRepository testRepo;

    @PostMapping("/base")
public Object saveBase(@RequestBody MouseKeyboardBase data) {
    // Check if userID exists in Test
    Long userIdLong = Long.valueOf(data.getUserID());
    System.out.println("Processing userID: " + userIdLong); 
    Optional<MouseKeyboardTest> testOptional = testRepo.findByUserID(userIdLong);
    if (testOptional.isPresent()) {
        // Update existing Test record
        MouseKeyboardTest existingTest = testOptional.get();
        existingTest.setUserName(data.getUserName());
        existingTest.setTypingSpeed(data.getTypingSpeed());
        existingTest.setAvgKeyHoldTime(data.getAvgKeyHoldTime());
        existingTest.setAvgFlightTime(data.getAvgFlightTime());
        existingTest.setTotalTypingDuration(data.getTotalTypingDuration());
        existingTest.setStraightness(data.getStraightness());
        existingTest.setSpeedCv(data.getSpeedCv());
        existingTest.setIdleRatio(data.getIdleRatio());
        existingTest.setDirectionChangeRate(data.getDirectionChangeRate());
        existingTest.setTimestamp(data.getTimestamp());
        triggerMouseKeyboardCompareByUserId(userIdLong);
        return testRepo.save(existingTest);
    } else {
        // Check if userID exists in Base
       
        Optional<MouseKeyboardBase> baseOptional = baseRepo.findByUserID(userIdLong);
        if (baseOptional.isEmpty()) {
            // Save in Base
            return baseRepo.save(data);
        } else {
            // Save new record in Test if Base exists but no Test
            MouseKeyboardTest newTest = new MouseKeyboardTest();
            newTest.setUserID(data.getUserID());
            newTest.setUserName(data.getUserName());
            newTest.setTypingSpeed(data.getTypingSpeed());
            newTest.setAvgKeyHoldTime(data.getAvgKeyHoldTime());
            newTest.setAvgFlightTime(data.getAvgFlightTime());
            newTest.setTotalTypingDuration(data.getTotalTypingDuration());
            newTest.setStraightness(data.getStraightness());
            newTest.setSpeedCv(data.getSpeedCv());
            newTest.setIdleRatio(data.getIdleRatio());
            newTest.setDirectionChangeRate(data.getDirectionChangeRate());
            newTest.setTimestamp(data.getTimestamp());
            triggerMouseKeyboardCompareByUserId(userIdLong);
            return testRepo.save(newTest);
        }
    }
}








    private final String FLASK_URL = "http://127.0.0.1:5000";

    private final RestTemplate restTemplate = new RestTemplate();


    // // -------------------------------------------------------
    // // 1️⃣ NEW ENDPOINT → send TEST data → Flask /predict
    // // -------------------------------------------------------
    // @GetMapping("/predict/{userID}")
    // public ResponseEntity<?> sendToPredict(@PathVariable Long userID) {

    //     Optional<MouseKeyboardTest> testOpt = testRepo.findByUserID(userID);

    //     if (testOpt.isEmpty()) {
    //         return ResponseEntity.status(404).body("❌ No test data found for userID " + userID);
    //     }

    //     MouseKeyboardTest test = testOpt.get();

    //     Map<String, Object> payload = Map.of(
    //             "typing_speed", test.getTypingSpeed(),
    //             "avg_key_hold_time", test.getAvgKeyHoldTime(),
    //             "avg_flight_time", test.getAvgFlightTime(),
    //             "total_typing_duration", test.getTotalTypingDuration(),
    //             "straightness", test.getStraightness(),
    //             "speed_cv", test.getSpeedCv(),
    //             "idle_ratio", test.getIdleRatio(),
    //             "direction_change_rate", test.getDirectionChangeRate()
    //     );

    //     ResponseEntity<Object> flaskResponse =
    //             restTemplate.postForEntity(FLASK_URL + "/predict", payload, Object.class);

    //     return ResponseEntity.ok(flaskResponse.getBody());
    // }



    // -------------------------------------------------------
    // 2️⃣ NEW ENDPOINT → compare BASE vs TEST → Flask /compare
    // -------------------------------------------------------
    @GetMapping("/compare/{userID}")
    public ResponseEntity<?> sendToCompare(@PathVariable Long userID) {

        Optional<MouseKeyboardBase> baseOpt = baseRepo.findByUserID(userID);
        Optional<MouseKeyboardTest> testOpt = testRepo.findByUserID(userID);

        if (baseOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ No base data found for userID " + userID);
        }
        if (testOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ No test data found for userID " + userID);
        }

        MouseKeyboardBase base = baseOpt.get();
        MouseKeyboardTest test = testOpt.get();

        Map<String, Object> user1 = Map.of(
                "typingSpeed", base.getTypingSpeed(),
                "avgKeyHoldTime", base.getAvgKeyHoldTime(),
                "avgFlightTime", base.getAvgFlightTime(),
                "totalTypingDuration", base.getTotalTypingDuration(),
                "straightness", base.getStraightness(),
                "speedCv", base.getSpeedCv(),
                "idleRatio", base.getIdleRatio(),
                "directionChangeRate", base.getDirectionChangeRate()
        );

        Map<String, Object> user2 = Map.of(
                "typingSpeed", test.getTypingSpeed(),
                "avgKeyHoldTime", test.getAvgKeyHoldTime(),
                "avgFlightTime", test.getAvgFlightTime(),
                "totalTypingDuration", test.getTotalTypingDuration(),
                "straightness", test.getStraightness(),
                "speedCv", test.getSpeedCv(),
                "idleRatio", test.getIdleRatio(),
                "directionChangeRate", test.getDirectionChangeRate()
        );

        Map<String, Object> body = Map.of("user1", user1, "user2", user2);

        ResponseEntity<Object> flaskResponse =
                restTemplate.postForEntity(FLASK_URL + "/compare", body, Object.class);

        return ResponseEntity.ok(flaskResponse.getBody());
    }



    private void triggerMouseKeyboardCompareByUserId(Long userID) {
    try {
        String url = "http://localhost:8080/mouse-keyboard/compare/" + userID;

        ResponseEntity<Object> response =
                restTemplate.getForEntity(url, Object.class);

        System.out.println("✅ Mouse & Keyboard compare triggered via API for userID " + userID);
        System.out.println("MouseKeyboard Compare response: " + response.getBody());

    } catch (Exception e) {
        System.err.println("❌ Mouse & Keyboard Compare API call failed for userID "
                + userID + " : " + e.getMessage());
    }
}

}
