package com.example.majorproject.REPOSITORY;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.example.majorproject.ENTITY.LocationTest;

@Repository
public interface LocationTestRepository extends JpaRepository<LocationTest, Long> {

    List<LocationTest> findByUserID(Long userID);   // <-- ADD THIS
}
    // Optional: add custom queries if needed
    // List<LocationTest> findByUserID(Long userID);

