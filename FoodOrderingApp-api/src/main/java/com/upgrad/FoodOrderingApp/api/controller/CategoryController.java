package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.business.CategoryBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/")
@CrossOrigin
public class CategoryController {
    @Autowired
    CategoryBusinessService categoryBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/category", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategories(){
        List<CategoryEntity> categories = categoryBusinessService.getAllCategories();
        CategoriesListResponse allCategories = new CategoriesListResponse();

        for(CategoryEntity c : categories) {
            CategoryListResponse singleCategory = new CategoryListResponse();
            singleCategory.categoryName(c.getCategory_name()).id(UUID.fromString(c.getUuid()));
            allCategories.addCategoriesItem(singleCategory);

        }
        return new ResponseEntity<CategoriesListResponse>(allCategories, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/category/{categoryId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategory( @PathVariable("categoryId") final String categoryId ) throws CategoryNotFoundException {
        List<CategoryItemEntity> categoryItemList = categoryBusinessService.getCategoryItemList(categoryId);
        List<ItemList> itemsList = new ArrayList<>();
        for(CategoryItemEntity ct: categoryItemList )
        {
            ItemEntity itemEntity = ct.getItemEntity();
            ItemList itemList = new ItemList()
                    .id(UUID.fromString(itemEntity.getUuid()))
                    .itemName(itemEntity.getItemName())
                    .itemType(itemEntity.getType().equals("0") ? ItemList.ItemTypeEnum.VEG : ItemList.ItemTypeEnum.NON_VEG)
                    .price(itemEntity.getPrice());
            itemsList.add(itemList);
        }

        CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse();
        categoryDetailsResponse.itemList(itemsList);
        categoryDetailsResponse.categoryName(categoryItemList.get(0).getCategoryEntity().getCategory_name());
        categoryDetailsResponse.id(UUID.fromString(categoryId));

        return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse,HttpStatus.OK);
    }
}


