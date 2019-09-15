package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;

@Entity
@Table(name = "restaurant_category")
@NamedQueries(
        {       @NamedQuery(name = "getAllRestaurantsCategories", query = "select resCat from RestaurantCategoryEntity resCat where resCat.restaurantId = :restaurantId"),
                @NamedQuery(name = "getAllRestaurantsByCategory", query = "select resCat from RestaurantCategoryEntity resCat where resCat.categoryId = :categoryId")
        }
)
public class RestaurantCategoryEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "restaurant_id")
    private Integer restaurantId;

    @Column(name = "category_id")
    private Integer categoryId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}
