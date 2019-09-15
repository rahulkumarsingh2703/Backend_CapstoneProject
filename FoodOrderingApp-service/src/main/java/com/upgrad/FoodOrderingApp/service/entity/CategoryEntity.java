package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "category")

@NamedQueries(
        {       @NamedQuery(name = "getAllCategoriesBasedCatId", query = "select cat from CategoryEntity cat where cat.id = :categoryId"),
                @NamedQuery(name = "getCategoryByUUID", query = "select cat from CategoryEntity cat where cat.uuid = :uuid"),
                @NamedQuery(name = "getCategory", query = "select ce from CategoryEntity ce "),
                @NamedQuery(name = "getCategoryData", query = "select ce from CategoryEntity ce where ce.uuid = :uuid")
        }
)

public class CategoryEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    public String uuid;

    @Column(name = "CATEGORY_NAME")
    @Size(max = 255)
    public String category_name;

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

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
