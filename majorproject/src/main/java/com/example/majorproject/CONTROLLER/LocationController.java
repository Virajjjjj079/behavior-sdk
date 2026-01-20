package com.example.majorproject.CONTROLLER;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import com.example.majorproject.ENTITY.LocationBase;
import com.example.majorproject.ENTITY.LocationTest;
import com.example.majorproject.REPOSITORY.LocationBaseRepository;
import com.example.majorproject.REPOSITORY.LocationTestRepository;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @Autowired
    private LocationBaseRepository baseRepository;

    @Autowired
    private LocationTestRepository testRepository;

    @PostMapping("/save-location")
public ResponseEntity<?> saveLocation(@RequestBody LocationBase input) {

    // 1️⃣ Check BASE
    List<LocationBase> baseList =
            baseRepository.findByUserID(input.getUserID());

    if (baseList.isEmpty()) {
        // 🔹 Save to BASE
        return ResponseEntity.ok(baseRepository.save(input));
    }

    // 2️⃣ BASE exists → check TEST
    List<LocationTest> testList =
            testRepository.findByUserID(input.getUserID());

    LocationTest test;

    if (testList.isEmpty()) {
        // 🔹 Insert into TEST
        test = new LocationTest();
        triggerLocationCompareByUserId(input.getUserID());
        test.setUserID(input.getUserID());
    } else {
        // 🔹 Update TEST (take first record)
        test = testList.get(0); // ✅ FIXED
        triggerLocationCompareByUserId(input.getUserID());
    }

    test.setUserName(input.getUserName());
    test.setLatitude(input.getLatitude());
    test.setLongitude(input.getLongitude());
    test.setTime(input.getTime());

    return ResponseEntity.ok(testRepository.save(test));
}


    // You can add endpoint to fetch base locations or test/compare locations
    // @GetMapping("/get-base/{userID}") ...


//     @PostMapping("/compare/{userID}")
// public ResponseEntity<?> compareLocation(@PathVariable Long userID) {

//     // Fetch all saved base & test locations for the user
//     List<LocationBase> baseList = baseRepository.findByUserID(userID);

//     List<LocationTest> testList = testRepository.findByUserID(userID);

//     // Check if records exist
//     if (baseList == null || baseList.isEmpty() ||
//         testList == null || testList.isEmpty()) {
//         return ResponseEntity
//                 .badRequest()
//                 .body("Base or test location not found for user: " + userID);
//     }

//     // Take the *latest* entry from both tables
//     LocationBase base = baseList.get(baseList.size() - 1);
//     LocationTest test = testList.get(testList.size() - 1);

//     // Prepare JSON request body for Flask API
//     Map<String, Object> body = new HashMap<>();
//     body.put("lat1", base.getLatitude());
//     body.put("lon1", base.getLongitude());
//     body.put("time1", base.getTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//     body.put("lat2", test.getLatitude());
//     body.put("lon2", test.getLongitude());
//     body.put("time2", test.getTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

//     // Flask endpoint URL
//     String flaskUrl = "http://127.0.0.1:5000/location-check";

//     // Send POST to Flask
//     RestTemplate restTemplate = new RestTemplate();
//     ResponseEntity<String> flaskResponse =
//             restTemplate.postForEntity(flaskUrl, body, String.class);

//     // Return Flask result back to frontend
//     return ResponseEntity.ok(flaskResponse.getBody());
// }



    private final RestTemplate restTemplate = new RestTemplate();

@GetMapping("/compare/{userID}")
public ResponseEntity<?> compareLocation(@PathVariable Long userID) {

    // Fetch all saved base & test locations for the user
    List<LocationBase> baseList = baseRepository.findByUserID(userID);
    List<LocationTest> testList = testRepository.findByUserID(userID);

    // Check if records exist
    if (baseList == null || baseList.isEmpty()) {
        return ResponseEntity
                .status(404)
                .body("❌ Base location not found for user: " + userID);
    }

    if (testList == null || testList.isEmpty()) {
        return ResponseEntity
                .status(404)
                .body("❌ Test location not found for user: " + userID);
    }

    // Take the *latest* entry from both tables
    LocationBase base = baseList.get(baseList.size() - 1);
    LocationTest test = testList.get(testList.size() - 1);

    // Prepare request body for Flask API
    Map<String, Object> body = Map.of(
            "lat1", base.getLatitude(),
            "lon1", base.getLongitude(),
            "time1", base.getTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            "lat2", test.getLatitude(),
            "lon2", test.getLongitude(),
            "time2", test.getTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    );

    // Flask endpoint URL
    String flaskUrl = "http://127.0.0.1:5000/location-check";

    

    // Send POST request to Flask
    ResponseEntity<Object> flaskResponse =
            restTemplate.postForEntity(flaskUrl, body, Object.class);

    
    ResponseEntity<Map> flaskResponsedanger =
            restTemplate.postForEntity(flaskUrl, body, Map.class);

    Map<String, Object> result = flaskResponsedanger.getBody();

            if (result != null && "fraud".equalsIgnoreCase(
            String.valueOf(result.get("result")))) {

        // 🔥 add frontend action
        result.put("action", "SHOW_FRAUD_POPUP");
        result.put("severity", "HIGH");
        result.put("message", "Suspicious location activity detected");
    }

    // Return Flask response to frontend
    return ResponseEntity.ok(result);
}



private void triggerLocationCompareByUserId(Long userID) {
    try {
        String url = "http://localhost:8080/location/compare/" + userID;

        ResponseEntity<Object> response =
                restTemplate.getForEntity(url, Object.class);

        System.out.println("✅ Location compare triggered via API for userID " + userID);
        System.out.println("Location Compare response: " + response.getBody());

    } catch (Exception e) {
        System.err.println("❌ Location Compare API call failed for userID "
                + userID + " : " + e.getMessage());
    }
}


}
