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


        countryName = countryName.toUpperCase();




        if(user.getConnected()) throw new Exception("Already connected");

        if(user.getOriginalCountry().getCountryName().equals(CountryName.valueOf(countryName))) {
            return user;
        }
        if(user.getServiceProviderList().size()==0) throw new Exception("Unable to connect");

            List<ServiceProvider> serviceProviderList = user.getServiceProviderList();
            int serviceProviderId = Integer.MAX_VALUE;
            ServiceProvider ServeProvider = null;
            Country contry = null;

            Boolean flag = false;

            for(ServiceProvider serviceProvider: serviceProviderList) {
                List<Country> countryList = serviceProvider.getCountryList();
                for (Country country : countryList) {

                    if (countryName.equalsIgnoreCase(country.getCountryName().toString())&& serviceProviderId>serviceProvider.getId()) {
                        serviceProviderId = serviceProvider.getId();
                        ServeProvider = serviceProvider;
                        contry = country;
                        flag = true;
                    }
                }
            }

            if(!flag) throw new Exception("Unable to connect");

        Connection connection = new Connection();

            connection.setUser(user);
            connection.setServiceProvider(ServeProvider);

           user.setMaskedIp(contry.getCode()+"."+ServeProvider.getId()+"."+userId);
           user.setConnected(true);
           user.getConnectionList().add(connection);
           ServeProvider.getConnectionList().add(connection);

        userRepository2.save(user);
        serviceProviderRepository2.save(ServeProvider);
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

        CountryName receiverCountryName = null;

        if(receiver.getConnected()){
            String receiverCountryCode;
            String [] arr = receiver.getMaskedIp().split("\\.");
            receiverCountryCode =arr[0];
            for(CountryName countryName : CountryName.values()){
                if(countryName.toCode().equals(receiverCountryCode)){
                    receiverCountryName = countryName;
                    break;
                }
            }
        }
        else{
            receiverCountryName = receiver.getOriginalCountry().getCountryName();
        }
        if(receiverCountryName.equals((sender.getOriginalCountry().getCountryName()))) return sender;

        try{
            sender = connect(senderId,receiverCountryName.name());
        }catch(Exception e){
            throw new Exception("Cannot establish communication");
        }
        return sender;

    }
}
