package greenlife.com.vn.greenfood.models;

/**
 * Created by thean on 11/2/2017.
 */

public class UserPost {

    private String id;
    private String idUser;
    private String idPost;


    private double rate;

    public UserPost(String id, String idUser, String idPost, double rate) {
        this.id = id;
        this.idUser = idUser;
        this.idPost = idPost;
        this.rate = rate;
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

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
