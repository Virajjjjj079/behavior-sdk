package com.example.majorproject.REPOSITORY;



import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import com.example.majorproject.ENTITY.MouseKeyboardTest;

@Repository
public interface MouseKeyboardTestRepository extends CrudRepository<MouseKeyboardTest, Long> {

    Optional<MouseKeyboardTest> findByUserID(Long userID);

}

