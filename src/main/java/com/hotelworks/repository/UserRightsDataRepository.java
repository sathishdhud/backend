package com.hotelworks.repository;

import com.hotelworks.entity.UserRightsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRightsDataRepository extends JpaRepository<UserRightsData, String> {
    
    List<UserRightsData> findByUserId(String userId);
    
    List<UserRightsData> findByModuleName(String moduleName);
    
    @Query("SELECT u FROM UserRightsData u WHERE u.userId = :userId AND u.moduleName = :moduleName")
    List<UserRightsData> findByUserIdAndModuleName(@Param("userId") String userId, @Param("moduleName") String moduleName);
    
    List<UserRightsData> findByPermissionType(String permissionType);
    
    void deleteByUserId(String userId);
}