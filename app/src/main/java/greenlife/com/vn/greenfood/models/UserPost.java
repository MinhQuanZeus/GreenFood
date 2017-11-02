package greenlife.com.vn.greenfood.models;

/**
 * Created by thean on 11/2/2017.
 */

public class UserPost {

    private int id;
    private int idUser;
    private int idPost;
    private double rate;

    public UserPost(int id, int idUser, int idPost, double rate) {
        this.id = id;
        this.idUser = idUser;
        this.idPost = idPost;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public int getIdUser() {
        return idUser;
    }

    public int getIdPost() {
        return idPost;
    }

    public double getRate() {
        return rate;
    }
}
