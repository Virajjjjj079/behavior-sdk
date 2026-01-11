package com.example.majorproject.REPOSITORY;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.majorproject.ENTITY.MouseKeyboardBase;

@Repository
public interface MouseKeyboardBaseRepository extends JpaRepository<MouseKeyboardBase, Long> {
    // You can add custom queries if needed, e.g.,
    Optional<MouseKeyboardBase> findByUserID(Long userID);
}
