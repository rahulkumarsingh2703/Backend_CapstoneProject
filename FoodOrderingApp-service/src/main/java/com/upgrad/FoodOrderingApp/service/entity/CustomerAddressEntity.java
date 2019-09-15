package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "CUSTOMER_ADDRESS")
@NamedQueries({
        @NamedQuery(name = "getCustomerAddress", query = "select cae from CustomerAddressEntity cae where cae.customer = :customer"),
        @NamedQuery(name = "getAddress", query = "select cae from CustomerAddressEntity cae where cae.address = :address")
})
public class CustomerAddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CUSTOMER_ID")
    private CustomerEntity customer;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ADDRESS_ID")
    private AddressEntity address;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public CustomerEntity getCustomerEntity() {
        return customer;
    }

    public void setCustomerEntity(CustomerEntity customerEntity) {
        this.customer = customerEntity;
    }


    public AddressEntity getAddressEntity() {
        return address;
    }

    public void setAddressEntity(AddressEntity addressEntity) {
        this.address = addressEntity;
    }
}
