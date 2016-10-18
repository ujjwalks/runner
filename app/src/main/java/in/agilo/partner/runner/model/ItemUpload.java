package in.agilo.partner.runner.model;

/**
 * Created by Ujjwal on 4/11/2016.
 */
public class ItemUpload {
    private int id;
    private int type; // 0 for snapshots and 1 for signature
    private String name;
    private String uri;
    private int orderID;

    public ItemUpload(){

    }

    public ItemUpload(int id,int type, String name, String uri, int orderID) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.uri = uri;
        this.orderID = orderID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
