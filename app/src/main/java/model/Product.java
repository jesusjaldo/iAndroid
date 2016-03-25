package model;

/**
 * Created by inftel07 on 19/3/16.
 */
public class Product {

    String name_product;
    String price;
    String description;
    String imageBase64;
    String longitude;
    String latitude;
    String username;


    public Product() {
    }

    public Product(String name_product, String price, String description, String longitude, String imageBase64, String latitude, String username) {
        this.name_product = name_product;
        this.price = price;
        this.description = description;
        this.longitude = longitude;
        this.imageBase64 = imageBase64;
        this.latitude = latitude;
        this.username = username;
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

    public String getName_product() {
        return name_product;
    }

    public void setName_product(String name_product) {
        this.name_product = name_product;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name_product='" + name_product + '\'' +
                ", price='" + price + '\'' +
                ", description='" + description + '\'' +
                ", imageBase64='" + imageBase64 + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
