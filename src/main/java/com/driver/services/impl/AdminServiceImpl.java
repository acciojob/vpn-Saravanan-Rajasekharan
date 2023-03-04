package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        List<ServiceProvider> serviceProviderList = new ArrayList<>();
        admin.setServiceProviders(serviceProviderList);

        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Admin admin = adminRepository1.findById(adminId).get();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setName(providerName);
        serviceProvider.setAdmin(admin);
        List<ServiceProvider> serviceProviderList = admin.getServiceProviders();
        serviceProviderList.add(serviceProvider);

        adminRepository1.save(admin);

        return admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        ServiceProvider serviceProvider = serviceProviderRepository1.findById(serviceProviderId).get();

        Country country = new Country();
        CountryName countryName1 ;

        try{
            countryName1 = CountryName.valueOf(countryName.toUpperCase());
        }catch(Exception e){
            throw new Exception("Country not found");
        }

        country.setCountryName(countryName1);
        country.setCode(countryName1.toCode()); // Set the code on country using the CountryName1 enum
        country.setServiceProvider(serviceProvider);

        //Get the country list from the service provider
        List<Country> countryList = serviceProvider.getCountryList();
        countryList.add(country);

        serviceProviderRepository1.save(serviceProvider);

        return serviceProvider;
    }
}
