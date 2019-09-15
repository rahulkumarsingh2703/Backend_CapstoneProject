package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantCategoryDAO;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantCategoryBusinessService {

    @Autowired
    private RestaurantCategoryDAO restaurantCategoryDAO;

    public List<RestaurantCategoryEntity> getAllRestaurantsCategories(Integer restaurantId){
        List<RestaurantCategoryEntity> allRestaurantsCategories = restaurantCategoryDAO.getAllRestaurantsCategories(restaurantId);

        return allRestaurantsCategories;
    }

    public List<RestaurantCategoryEntity> getAllRestaurantsByCategory(Integer categoryId){
        List<RestaurantCategoryEntity> allRestaurantsByCategory = restaurantCategoryDAO.getAllRestaurantsByCategory(categoryId);

        return allRestaurantsByCategory;
    }
}
