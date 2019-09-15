package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryBusinessService {

    @Autowired
    CategoryDao categoryDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<CategoryEntity> getAllCategories(){
        List<CategoryEntity> categories = categoryDao.getAllCategories();
        return categories;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public CategoryItemEntity getCategory(String categoryId) throws CategoryNotFoundException{
        if (categoryId!=null) {

            CategoryItemEntity categoryItemEntity = categoryDao.getCategoryData(categoryId);
            if (categoryItemEntity != null) {
                return categoryItemEntity;
            }
            else {
                throw new CategoryNotFoundException("CNF-002","No category by this id");
            }
        }
        else {
            throw new CategoryNotFoundException("CNF-001","Category id field should not be empty");
        }
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public List<CategoryItemEntity> getCategoryItemList(String categoryId){
        List<CategoryItemEntity> items = categoryDao.getItems(categoryId);
        return items;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<CategoryEntity> getAllCategoriesBasedCatId(Integer categoryId){
        List<CategoryEntity> allCategoriesBasedCatId = categoryDao.getAllCategoriesBasedCatId(categoryId);

        return allCategoriesBasedCatId;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CategoryEntity getCategoryByUUID(String categoryUUID) throws CategoryNotFoundException {
        if(categoryUUID.equals("")){
            throw new CategoryNotFoundException("CNF-001)", "Category id field should not be empty");
        }
        CategoryEntity categoryByUUID = categoryDao.getCategoryByUUID(categoryUUID);
        if(categoryByUUID == null){
            throw new CategoryNotFoundException("CNF-002","No category by this id");
        }
        return categoryByUUID;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<CategoryEntity> getAllCategoriesByRestId(Integer restuarantId) {
        List<CategoryEntity> allCategoriesByRestId = categoryDao.getAllCategoriesByRestId(restuarantId);
        return allCategoriesByRestId;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<ItemEntity> getAllCategoryItems(Integer categoryId) {
        List<ItemEntity> allCategoryItems = categoryDao.getAllCategoryItems(categoryId);
        return allCategoryItems;
    }
}
