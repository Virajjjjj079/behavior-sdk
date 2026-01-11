package com.example.majorproject.REPOSITORY;




import com.example.majorproject.ENTITY.DeviceFingerprintTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceFingerprintTestRepository extends JpaRepository<DeviceFingerprintTest, Long> {
    Optional<DeviceFingerprintTest> findByUserID(Long userID);
}
