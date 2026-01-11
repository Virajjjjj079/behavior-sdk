package com.example.majorproject.REPOSITORY;


import com.example.majorproject.ENTITY.LocationBase;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationBaseRepository extends JpaRepository<LocationBase, Long> {

    List<LocationBase> findByUserID(Long userID);   // <-- ADD THIS
}
    // Optional: add custom queries if needed
    // List<LocationBase> findByUserID(Long userID);

