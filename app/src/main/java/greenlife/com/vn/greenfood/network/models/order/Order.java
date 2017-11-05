package greenlife.com.vn.greenfood.network.models.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Order implements Serializable {
    @SerializedName("buyer_id")
    private String buyerID;
    @SerializedName("buyer_name")
    private String buyerName;
    @SerializedName("check_id")
    private String sellerID;
    @SerializedName("food_name")
    private String foodName;
    @SerializedName("image_food")
    private String foodImgLink;
    private String type;
    private String time;
    private String status;
    private String postId;
    private String phone;

    public Order() {
    }

    public Order(String buyerID, String buyerName, String sellerID, String foodName, String foodImgLink, String type, String time, String status, String postId, String phone) {
        this.buyerID = buyerID;
        this.buyerName = buyerName;
        this.sellerID = sellerID;
        this.foodName = foodName;
        this.foodImgLink = foodImgLink;
        this.type = type;
        this.time = time;
        this.status = status;
        this.postId = postId;
        this.phone = phone;
    }

    public Order(String buyerID, String sellerID, String foodName, String foodImgLink, String type, String time, String status) {
        this.buyerID = buyerID;
        this.sellerID = sellerID;
        this.foodName = foodName;
        this.foodImgLink = foodImgLink;
        this.type = type;
        this.time = time;
        this.status = status;
    }


    public String getBuyerID() {
        return buyerID;
    }

    public String getSellerID() {
        return sellerID;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodImgLink() {
        return foodImgLink;
    }

    public String getType() {
        return type;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getPostId() {
        return postId;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "Order{" +
                "buyerID='" + buyerID + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", sellerID='" + sellerID + '\'' +
                ", foodName='" + foodName + '\'' +
                ", foodImgLink='" + foodImgLink + '\'' +
                ", type='" + type + '\'' +
                ", time='" + time + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
