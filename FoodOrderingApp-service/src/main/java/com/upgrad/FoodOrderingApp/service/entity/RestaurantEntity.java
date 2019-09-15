package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "restaurant")

@NamedQueries(
        {       @NamedQuery(name = "getAllRestaurants", query = "select res from RestaurantEntity res"),
                @NamedQuery(name = "getAllRestaurantsByName", query = "select res from RestaurantEntity res where res.restaurantName LIKE :resNameKey"),
                @NamedQuery(name = "getRestaurantByUUID", query = "select res from RestaurantEntity res where res.uuid = :restaurantUUID"),
                @NamedQuery(name = "getRestaurantById", query = "select res from RestaurantEntity res where res.id = :restaurantId")
        }
)

public class RestaurantEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    private String  uuid;

    @Column(name = "restaurant_name")
    @Size(max = 50)
    private String restaurantName;

    @Column(name = "photo_url")
    @Size(max = 225)
    private String photoURL;

    @Column(name = "customer_rating")
    private BigDecimal customeRating;

    @Column(name = "average_price_for_two")
    private Integer avgPriceForTwo;

//    public List<CategoryEntity> getCategories() {
//        return categories;
//    }
//
//    public void setCategories(List<CategoryEntity> categories) {
//        this.categories = categories;
//    }

    @Column(name = "number_of_customers_rated")
    private Integer numbrOfCustomersRated;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private AddressEntity addressEntity;

//    @ManyToMany(cascade=CascadeType.ALL)
//    @JoinTable(name="restaurant_category", joinColumns={@JoinColumn(referencedColumnName="ID")}
//            , inverseJoinColumns={@JoinColumn(referencedColumnName="ID")})
//    private List<CategoryEntity> categories = new ArrayList<>();

//    @ManyToMany(cascade=CascadeType.ALL)
//    @JoinTable(name="restaurant_category", joinColumns={@JoinColumn(name = "RESTAURANT_ID", referencedColumnName="ID")}
//            , inverseJoinColumns={@JoinColumn(name = "CATEGORY_ID", referencedColumnName="ID")})
//    private List<CategoryEntity> categories = new ArrayList<>();

//    @ManyToMany(cascade=CascadeType.ALL)
//    @JoinTable(name="restaurant_category", joinColumns={@JoinColumn(name="restaurant_id")}
//            , inverseJoinColumns={@JoinColumn(name="category_id")})
//    private List<CategoryEntity> categories = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public BigDecimal getCustomeRating() {
        return customeRating;
    }

    public void setCustomeRating(BigDecimal customeRating) {
        this.customeRating = customeRating;
    }

    public Integer getAvgPriceForTwo() {
        return avgPriceForTwo;
    }

    public void setAvgPriceForTwo(Integer avgPriceForTwo) {
        this.avgPriceForTwo = avgPriceForTwo;
    }

    public Integer getNumbrOfCustomersRated() {
        return numbrOfCustomersRated;
    }

    public void setNumbrOfCustomersRated(Integer numbrOfCustomersRated) {
        this.numbrOfCustomersRated = numbrOfCustomersRated;
    }

    public AddressEntity getAddressEntity() {
        return addressEntity;
    }

    public void setAddressEntity(AddressEntity addressEntity) {
        this.addressEntity = addressEntity;
    }
}
