package greenlife.com.vn.greenfood.network.models.distance;

import java.util.List;


public class Row {
    private List<Element> elements;

    public Row(List<Element> elements) {
        this.elements = elements;
    }

    public List<Element> getElements() {
        return elements;
    }
}
