package com.example.skripsi;

import java.text.DateFormat;

public class UserItems_home {
    String ID;
    String ProductCode;
    String Name;
   // String Category;
    String Price;
    String count ="0";
    String in;
    String Expired;

    public UserItems_home() {
    }

    public UserItems_home(String ID, String productCode, String name, String price, String count, String in, String expired) {
        this.ID = ID;
        ProductCode = productCode;
        Name = name;
        Price = price;
        this.count = count;
        this.in = in;
        Expired = expired;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getProductCode() {
        return ProductCode;
    }

    public void setProductCode(String productCode) {
        ProductCode = productCode;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getExpired() {
        return Expired;
    }

    public void setExpired(String expired) {
        Expired = expired;
    }
}
