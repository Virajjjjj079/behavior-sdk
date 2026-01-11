package com.example.majorproject.REPOSITORY;




import com.example.majorproject.ENTITY.DeviceFingerprintBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceFingerprintBaseRepository extends JpaRepository<DeviceFingerprintBase, Long> {
    Optional<DeviceFingerprintBase> findByUserID(Long userID);
}
