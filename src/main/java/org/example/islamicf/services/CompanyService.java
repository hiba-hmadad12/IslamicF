package org.example.islamicf.services;


import org.example.islamicf.entities.Company;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CompanyService {
    Company saveCompany(Company company);

    Company getCompanyById(Long id);
    void deleteCompanyById(Long id);
    Company updateCompany(Company company);
    List<Company> getAllCompanies();

}