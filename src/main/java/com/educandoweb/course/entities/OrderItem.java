package com.educandoweb.course.entities;

import java.io.Serializable;
import java.util.Objects;

import com.educandoweb.course.entities.pk.OrderItemPK;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_order_item")
public class OrderItem implements Serializable {


    @EmbeddedId
    private OrderItemPK id = new OrderItemPK();


    private Integer quantity;


    private Double price;


    public OrderItem() {
    }


    public OrderItem(Order order, Product product, Integer quantity, Double price  ) {
        this.price = price;
        this.quantity = quantity;
        id.setOrder(order);
        id.setProduct(product);
    }

    @JsonIgnore
    public Order getOrder(){
        return  id.getOrder();
    }

    public void setOrder(Order order){
        id.setOrder(order);
    }


    public Product getProduct(){
        return  id.getProduct();
    }

    public void setOrder(Product product){
        id.setProduct(product);
    }




    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}