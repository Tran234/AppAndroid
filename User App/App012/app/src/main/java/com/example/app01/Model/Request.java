package com.example.app01.Model;

import android.widget.TextView;

import java.util.List;

public class Request {
    private String phone;
    private String name;
    private String address;
    private String total;
    private List<Order> foodRequest;
    private String paymentMethod;
    private String paymentState;
    private String status;
    private String comment;
    private String date;
    private String time;

    public Request() {
    }

    public Request(String phone, String name, String address, String total, List<Order> foodRequest, String paymentMethod, String paymentState, String status, String comment, String date, String time) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.foodRequest = foodRequest;
        this.paymentMethod = paymentMethod;
        this.paymentState = paymentState;
        this.status = status;
        this.comment = comment;
        this.date = date;
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getFoodRequest() {
        return foodRequest;
    }

    public void setFoodRequest(List<Order> foodRequest) {
        this.foodRequest = foodRequest;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
