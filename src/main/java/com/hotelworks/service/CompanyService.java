package com.hotelworks.service;

import com.hotelworks.dto.request.CompanyRequest;
import com.hotelworks.dto.response.CompanyResponse;
import com.hotelworks.entity.Company;
import com.hotelworks.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyService {
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private NumberGenerationService numberGenerationService;
    
    /**
     * Create a new company
     */
    public CompanyResponse createCompany(CompanyRequest request) {
        // Generate company ID if not provided
        String companyId = request.getCompanyId();
        if (companyId == null || companyId.trim().isEmpty()) {
            companyId = numberGenerationService.generateCompanyId();
        }
        
        // Check if company already exists
        if (companyRepository.existsById(companyId)) {
            throw new RuntimeException("Company already exists: " + companyId);
        }
        
        Company company = new Company();
        company.setCompanyId(companyId);
        company.setCompanyName(request.getCompanyName());
        company.setAddress1(request.getAddress1());
        company.setAddress2(request.getAddress2());
        company.setAddress3(request.getAddress3());
        company.setGstNumber(request.getGstNumber());
        
        Company savedCompany = companyRepository.save(company);
        return mapToCompanyResponse(savedCompany);
    }
    
    /**
     * Get all companies
     */
    public List<CompanyResponse> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companies.stream()
            .map(this::mapToCompanyResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get company by ID
     */
    public CompanyResponse getCompany(String companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));
        return mapToCompanyResponse(company);
    }
    
    /**
     * Update company
     */
    public CompanyResponse updateCompany(String companyId, CompanyRequest request) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));
        
        company.setCompanyName(request.getCompanyName());
        company.setAddress1(request.getAddress1());
        company.setAddress2(request.getAddress2());
        company.setAddress3(request.getAddress3());
        company.setGstNumber(request.getGstNumber());
        
        Company updatedCompany = companyRepository.save(company);
        return mapToCompanyResponse(updatedCompany);
    }
    
    /**
     * Delete company
     */
    public void deleteCompany(String companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new RuntimeException("Company not found: " + companyId);
        }
        companyRepository.deleteById(companyId);
    }
    
    private CompanyResponse mapToCompanyResponse(Company company) {
        CompanyResponse response = new CompanyResponse();
        response.setCompanyId(company.getCompanyId());
        response.setCompanyName(company.getCompanyName());
        response.setAddress1(company.getAddress1());
        response.setAddress2(company.getAddress2());
        response.setAddress3(company.getAddress3());
        response.setGstNumber(company.getGstNumber());
        return response;
    }
}