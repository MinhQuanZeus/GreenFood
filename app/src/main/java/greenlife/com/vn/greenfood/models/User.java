package greenlife.com.vn.greenfood.models;


public class User {
    private String userID;
    private String name;
    private String phone;
    private String address;
    private String avatar;
    private long rate;
    private String tokenID;
    private String description;
    private String link;

    public User() {
    }

    public User(String userID, String name, String phone, String address, String avatar, long rating, String tokenID, String description, String link) {
        this.userID = userID;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.avatar = avatar;
        this.rate = rating;
        this.tokenID = tokenID;
        this.description = description;
        this.link = link;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getAvatar() {
        return avatar;
    }

    public long getRating() {
        return rate;
    }

    public String getTokenID() {
        return tokenID;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }
}
