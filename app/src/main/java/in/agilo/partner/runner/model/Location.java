package in.agilo.partner.runner.model;

/**
 * Created by Ujjwal on 4/5/2016.
 */

import java.io.Serializable;

public class Location implements Serializable {
    private int id = 0;
    private String address1 = "";
    private String address2 = "";
    private float latitude = 0.0f;
    private float longitude = 0.0f;
    private int gridx = 0;
    private int gridy = 0;

    public Location(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getGridx() {
        return gridx;
    }

    public void setGridx(int gridx) {
        this.gridx = gridx;
    }

    public int getGridy() {
        return gridy;
    }

    public void setGridy(int gridy) {
        this.gridy = gridy;
    }
}

