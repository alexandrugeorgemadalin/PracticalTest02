package ro.pub.cs.systems.eim.practicaltest02;

public class Model {
    String value;
    int unixTimestamp;

    public Model(String value, int unixTimestamp) {
        this.value = value;
        this.unixTimestamp = unixTimestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getUnixTimestamp() {
        return unixTimestamp;
    }

    public void setUnixTimestamp(int unixTimestamp) {
        this.unixTimestamp = unixTimestamp;
    }


}
