package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;
import java.util.List;

@Repository
public class RestaurantDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantEntity> getAllRestaurants(){
        try{
            return entityManager.createNamedQuery("getAllRestaurants", RestaurantEntity.class).getResultList();
        } catch (NoResultException nre){
            return null;
        }
    }

    public List<RestaurantEntity> getAllRestaurantsByName(String resNameKey){
        try{
            return entityManager.createNamedQuery("getAllRestaurantsByName", RestaurantEntity.class).setParameter("resNameKey", resNameKey).getResultList();
        } catch (NoResultException nre){
            return null;
        }
    }

    public List<RestaurantEntity> getAllRestaurantsByCategory(Integer categoryId){
        try{
            return entityManager.createNativeQuery("select r.* from restaurant_category rc inner join\n" +
                    "category c on rc.category_id = c.id\n" +
                    "inner join restaurant r on rc.restaurant_id = r.id\n" +
                    "where c.id = ?",RestaurantEntity.class).setParameter(1,categoryId).getResultList();
        } catch (NoResultException nre){
            return null;
        }
    }

    public RestaurantEntity getRestaurantByUUID(String restaurantUUID){
        try{
            return entityManager.createNamedQuery("getRestaurantByUUID", RestaurantEntity.class).setParameter("restaurantUUID", restaurantUUID).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public RestaurantEntity updateRestaurantDetails(Integer restaurantId, BigDecimal customerRating, RestaurantEntity restaurantEntity){
        entityManager.merge(restaurantEntity);
        return restaurantEntity;
    }

    public RestaurantEntity getRestaurantById(Integer restaurantId){
        try{
            return entityManager.createNamedQuery("getRestaurantById", RestaurantEntity.class).setParameter("restaurantId", restaurantId).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }
}
