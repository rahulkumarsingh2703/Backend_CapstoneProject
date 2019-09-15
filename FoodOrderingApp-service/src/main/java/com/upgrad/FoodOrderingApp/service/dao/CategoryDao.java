package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class CategoryDao {

    @PersistenceContext
    EntityManager entityManager;

    public List<CategoryEntity> getAllCategories() {

        List<CategoryEntity> categories = entityManager.createNamedQuery("getCategory", CategoryEntity.class).getResultList();
        return categories;
    }

    public CategoryItemEntity getCategoryData(String uuid) {
        try {

            CategoryItemEntity categoryItemEntity = entityManager.createNamedQuery("getCategoryData", CategoryItemEntity.class).setParameter("uuid",uuid).getSingleResult();
            return categoryItemEntity;
        } catch ( NoResultException nre) {
            return null;
        }
    }
    public List<CategoryItemEntity> getItems(String categoryId) {
        try {

            List<CategoryItemEntity> items = entityManager.createNamedQuery("getItems",CategoryItemEntity.class).setParameter("categoryId", categoryId).getResultList();
            return items;
        } catch ( NoResultException nre) {
            return null;
        }
    }

    public List<CategoryEntity> getAllCategoriesBasedCatId(Integer categoryId){
        try{
            return entityManager.createNamedQuery("getAllCategoriesBasedCatId", CategoryEntity.class).setParameter("categoryId", categoryId).getResultList();
        } catch (NoResultException nre){
            return null;
        }
    }

    public CategoryEntity getCategoryByUUID(String categoryUUID){
        try{
            return entityManager.createNamedQuery("getCategoryByUUID", CategoryEntity.class).setParameter("uuid", categoryUUID).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public List<CategoryEntity> getAllCategoriesByRestId(Integer restuarantId){
        try{
            return entityManager.createNativeQuery("select c.* from restaurant_category rc inner join\n" +
                    "category c on rc.category_id = c.id\n" +
                    "inner join restaurant r on rc.restaurant_id = r.id\n" +
                    "where r.id = ?",CategoryEntity.class).setParameter(1,restuarantId).getResultList();
        } catch (NoResultException nre){
            return null;
        }
    }

    public List<ItemEntity> getAllCategoryItems(Integer categoryId){
        try{
            return entityManager.createNativeQuery("select i.* from category_item ci inner join\n" +
                    "item i on ci.item_id = i.id\n" +
                    "inner join category c on ci.category_id = c.id\n" +
                    "where c.id = ?",ItemEntity.class).setParameter(1,categoryId).getResultList();
        } catch (NoResultException nre){
            return null;
        }
    }

}
