package greenlife.com.vn.greenfood.models;



public class FilterTag {
    private String key;
    private int value;

    public FilterTag(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public FilterTag() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FilterTag{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
