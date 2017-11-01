package greenlife.com.vn.greenfood.network.models.distance;



public class Element {
    private Distance distance;
    private String status;

    public Element(Distance distance, String status) {
        this.distance = distance;
        this.status = status;
    }

    public Distance getDistance() {
        return distance;
    }

    public String getStatus() {
        return status;
    }
}
