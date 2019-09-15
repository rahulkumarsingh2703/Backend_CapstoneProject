package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantCategoryDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantCategoryEntity> getAllRestaurantsCategories(Integer restaurantId){
        try{
            return entityManager.createNamedQuery("getAllRestaurantsCategories", RestaurantCategoryEntity.class).setParameter("restaurantId", restaurantId).getResultList();
        } catch (NoResultException nre){
            return null;
        }
    }

    public List<RestaurantCategoryEntity> getAllRestaurantsByCategory(Integer categoryId){
        try{
            return entityManager.createNamedQuery("getAllRestaurantsByCategory", RestaurantCategoryEntity.class).setParameter("categoryId", categoryId).getResultList();
        } catch (NoResultException nre){
            return null;
        }
    }
}
