package in.agilo.partner.runner.model;

import java.io.Serializable;

/**
 * Created by Ujjwal on 4/11/2016.
 */
public class ItemRequest implements Serializable{
    private int id;
    private int orderID;
    private int type; //0 for pickup, 1 for cancel, 2 for reschedule, 3 for ooh, 4 for barcode
    private String body;
    private boolean uploads;
    private String barcode;

    public ItemRequest(){

    }


    public ItemRequest(int id, int orderID, int type, String body) {
        this.id = id;
        this.orderID = orderID;
        this.type = type;
        this.body = body;
        this.uploads = false;
        this.barcode = "";

    }

    public ItemRequest(int id, int orderID, int type, String body, String barcode) {
        this.id = id;
        this.orderID = orderID;
        this.type = type;
        this.body = body;
        this.uploads = false;
        this.barcode = barcode;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public boolean isUploads() {
        return uploads;
    }

    public void setUploads(boolean uploads) {
        this.uploads = uploads;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
