package com.hotelworks.controller;

import com.hotelworks.dto.request.CompanyRequest;
import com.hotelworks.dto.response.CompanyResponse;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@Tag(name = "Companies", description = "Company Management APIs")
public class CompanyController {
    
    @Autowired
    private CompanyService companyService;
    
    @PostMapping
    @Operation(summary = "Create company", description = "Create a new company")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
            @Valid @RequestBody CompanyRequest request) {
        try {
            CompanyResponse response = companyService.createCompany(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Company created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to create company: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all companies", description = "Get all available companies")
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getAllCompanies() {
        try {
            List<CompanyResponse> responses = companyService.getAllCompanies();
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to get companies: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{companyId}")
    @Operation(summary = "Get company by ID", description = "Get company details by company ID")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(
            @Parameter(description = "Company ID") @PathVariable String companyId) {
        try {
            CompanyResponse response = companyService.getCompany(companyId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Company not found: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{companyId}")
    @Operation(summary = "Update company", description = "Update company details")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @Parameter(description = "Company ID") @PathVariable String companyId,
            @Valid @RequestBody CompanyRequest request) {
        try {
            CompanyResponse response = companyService.updateCompany(companyId, request);
            return ResponseEntity.ok(ApiResponse.success("Company updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to update company: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{companyId}")
    @Operation(summary = "Delete company", description = "Delete company by ID")
    public ResponseEntity<ApiResponse<String>> deleteCompany(
            @Parameter(description = "Company ID") @PathVariable String companyId) {
        try {
            companyService.deleteCompany(companyId);
            return ResponseEntity.ok(ApiResponse.success("Company deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to delete company: " + e.getMessage()));
        }
    }
}