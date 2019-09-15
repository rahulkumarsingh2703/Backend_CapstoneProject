package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "coupon")
@NamedQueries({
        @NamedQuery(name = "getCouponByName", query = "select ce from CouponEntity ce where ce.couponName = :couponName"),
        @NamedQuery(name = "getCouponByUUID", query = "select ce from CouponEntity ce where ce.uuid = :uuid"),
})
public class CouponEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    private String uuid;

    @Column(name = "COUPON_NAME")
    @Size(max = 255)
    private String couponName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }
}
