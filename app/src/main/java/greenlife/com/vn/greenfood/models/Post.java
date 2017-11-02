package greenlife.com.vn.greenfood.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;



@IgnoreExtraProperties
public class Post implements Serializable {
    private String id;
    private String userID;
    private String title;
    private String description;
    private long price;
    private String time;
    private String image;
    private String address;
    private long numberRatePeople;
    private float rateAvgRating;





    public Post(String id, String userID, String title, String description, long price, String time, String image, String address, long numberRatePeople, float rateAvgRating) {
        this.id = id;
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.price = price;
        this.time = time;
        this.image = image;
        this.address = address;
        this.numberRatePeople = numberRatePeople;
        this.rateAvgRating = rateAvgRating;
    }

    public Post(String id, String userID, String title, String description, long price, String time, String image, String address) {
        this.id = id;
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.price = price;
        this.time = time;
        this.image = image;
        this.address = address;
    }

    public Post() {
    }

    public Post(String userID, String title, String description, long price, String time, String image, String address) {
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.price = price;
        this.time = time;
        this.image = image;
        this.address = address;
    }

    public Post(String id, String userID, String title, String description, long price, String time, String image) {
        this.id = id;
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.price = price;
        this.time = time;
        this.image = image;
    }

    public Post(String id, String title, String description, long price, String time, String image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.time = time;
        this.image = image;
    }

    public Post(String title, String description, long price, String time, String image) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.time = time;
        this.image = image;
    }


    public String getId() {
        return id;
    }

    public String getUserID() {
        return userID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getPrice() {
        return price;
    }

    public String getTime() {
        return time;
    }

    public String getImage() {
        return image;
    }

    public String getAddress() {
        return address;
    }


    public void setNumberRatePeople(long numberRatePeople) {
        this.numberRatePeople = numberRatePeople;
    }

    public void setRateAvgRating(float rateAvgRating) {
        this.rateAvgRating = rateAvgRating;
    }

    public long getNumberRatePeople() {
        return numberRatePeople;
    }

    public float getRateAvgRating() {
        return rateAvgRating;
    }
}

