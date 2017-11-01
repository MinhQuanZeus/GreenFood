package greenlife.com.vn.greenfood.models;

/**
 * Created by thean on 11/2/2017.
 */

public class Notification {
    private String title;
    private String image;
    private String description;

    public Notification(String title, String image, String description) {
        this.title = title;
        this.image = image;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }
}

