package com.hotelworks.repository;

import com.hotelworks.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, String> {
    
    Optional<UserType> findByTypeName(String typeName);
    
    boolean existsByTypeName(String typeName);
    
    List<UserType> findAll();
    
    List<UserType> findByTypeNameContaining(String typeName);
    
    List<UserType> findByTypeNameIgnoreCase(String typeName);
}