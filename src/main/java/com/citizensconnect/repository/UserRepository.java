package com.citizensconnect.repository;

import com.citizensconnect.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAadhaar(String aadhaar);

    Boolean existsByAadhaar(String aadhaar);
}
