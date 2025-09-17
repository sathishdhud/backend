package com.hotelworks.repository;

import com.hotelworks.entity.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NationalityRepository extends JpaRepository<Nationality, String> {
    
    Optional<Nationality> findByNationality(String nationality);
    
    boolean existsByNationality(String nationality);
}