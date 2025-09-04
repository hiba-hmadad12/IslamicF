package org.example.islamicf.services;


import lombok.AllArgsConstructor;
import org.example.islamicf.entities.Company;
import org.example.islamicf.repositories.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService{
    private CompanyRepository companyRepository;
    @Override
    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }


    @Override
    public Company getCompanyById(Long id) {
        return companyRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteCompanyById(Long id) {
companyRepository.deleteById(id);
    }

    @Override
    public Company updateCompany(Company company) {
        return companyRepository.save(company);
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }
}
