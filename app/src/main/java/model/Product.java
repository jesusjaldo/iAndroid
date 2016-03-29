package model;

import android.graphics.Bitmap;

/**
 * Created by inftel07 on 19/3/16.
 */
public class Product {

    String nameproduct;
    String price;
    String description;
    String image;
    String longitude;
    String latitude;
    User username;
    int idproduct;
    Bitmap imageBitMap;

    public Product() {
    }

    public Product(String nameproduct, String price, String description, String image, String longitude, String latitude, User username, int idproduct) {
        this.nameproduct = nameproduct;
        this.price = price;
        this.description = description;
        this.image = image;
        this.longitude = longitude;
        this.latitude = latitude;
        this.username = username;
        this.idproduct = idproduct;
    }

    public String getNameproduct() {
        return nameproduct;
    }

    public void setNameproduct(String nameproduct) {
        this.nameproduct = nameproduct;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public User getUsername() {
        return username;
    }

    public void setUsername(User username) {
        this.username = username;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getIdproduct() {
        return idproduct;
    }

    public void setIdproduct(int idproduct) {
        this.idproduct = idproduct;
    }

    public Bitmap getImageBitMap() {
        return imageBitMap;
    }

    public void setImageBitMap(Bitmap imageBitMap) {
        this.imageBitMap = imageBitMap;
    }

    @Override
    public String toString() {
        return "Product{" +
                "nameproduct='" + nameproduct + '\'' +
                ", price='" + price + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", username=" + username +
                ", idproduct=" + idproduct +
                '}';
    }
}
