package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        User user = userRepository2.findById(userId).get();
        Country userCountry = user.getCountry();

        //get the country and then get the service provider from the country
        CountryName countryName1 = CountryName.valueOf(countryName.toUpperCase());



        if(user.getConnected()) throw new Exception("Already connected");

        else if(countryName1.equals(userCountry.getCountryName())) {
            return user;
        }
        else{
            if(user.getServiceProviderList().size()==0){
                throw new Exception("Unable to connect");
            }
            List<ServiceProvider> serviceProviderList = user.getServiceProviderList();
            int serviceProviderId = Integer.MAX_VALUE;

            for(ServiceProvider serviceProvider: serviceProviderList){
                List<Country> countryList = serviceProvider.getCountryList();
                for(Country country: countryList){
                    if(country.getCountryName().equals(countryName1)){
                        if(serviceProvider.getId()<serviceProviderId){
                            serviceProviderId =serviceProvider.getId();
                        }
                    }
                }
            }
            if(serviceProviderId ==Integer.MAX_VALUE) throw new Exception("Unable to connect");

            user.setConnected(true);
            user.setMaskedIp(countryName1.toCode()+"."+serviceProviderId+"."+userId);

            Connection connection = new Connection();
            connection.setUser(user);
            connection.setServiceProvider(serviceProviderRepository2.findById(serviceProviderId).get());
            ServiceProvider serviceProvider = serviceProviderRepository2.findById(serviceProviderId).get();
            serviceProvider.getConnectionList().add(connection);
            user.getConnectionList().add(connection);
        }
        userRepository2.save(user);
        return user;
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user = userRepository2.findById(userId).get();

        if(!user.getConnected()) throw new Exception("Already disconnected");

        user.setMaskedIp(null);
        user.setConnected(false);

        userRepository2.save(user);
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender = userRepository2.findById(senderId).get();
        User receiver = userRepository2.findById(receiverId).get();

        if(receiver.getMaskedIp()!=null){
            String code = receiver.getMaskedIp().substring(0,3);
            CountryName countryName = CountryName.valueOf(code);
            if(sender.getCountry().getCountryName().equals(countryName)) return sender;
        }
        else{
            if(sender.getCountry().getCountryName().equals(receiver.getCountry().getCountryName())) return sender;
        }

        try{
            sender = connect(senderId,receiver.getCountry().getCountryName().name());
        }catch(Exception e){
            throw new Exception("Cannot establish communication");
        }
        return sender;

    }
}
