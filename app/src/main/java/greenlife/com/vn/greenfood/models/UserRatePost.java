package greenlife.com.vn.greenfood.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by thean on 11/2/2017.
 */
@IgnoreExtraProperties
public class UserRatePost {

    private String id;
    private String idUser;
    private String idPost;


    private float rate;

    public UserRatePost(String id, String idUser, String idPost, float rate) {
        this.id = id;
        this.idUser = idUser;
        this.idPost = idPost;
        this.rate = rate;
    }

    public UserRatePost() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }
}
