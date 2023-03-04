package com.driver.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    private String username;

    private String password;

    //------------Mappings---------------//

    @OneToMany(mappedBy = "admin",cascade = CascadeType.ALL)
    private List<ServiceProvider> serviceProviders;



    //----------Constructors--------------//

    public Admin() {
    }



    //---------Getters and setters--------//


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ServiceProvider> getServiceProviders() {
        return serviceProviders;
    }

    public void setServiceProviders(List<ServiceProvider> serviceProviders) {
        this.serviceProviders = serviceProviders;
    }
}
