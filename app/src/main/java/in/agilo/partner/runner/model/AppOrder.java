package in.agilo.partner.runner.model;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Ujjwal on 4/9/2016.
 */
public class AppOrder implements Serializable {
    private int id;
    private String name;
    private String address;
    private String contact;
    private String extras;
    private String status;
    private String appStatus;
    private int priority;

    //details
    private String customerName;
    private String reason;
    private String shippingID;
    private String awb;
    private String time;
    private String brand;
    private String category;

    public AppOrder(int id, String name, String address, String contact, String extras, String status, String appStatus, int priority,
                    String customerName, String reason, String shippingID, String awb, String time, String brand, String category){
        this.id = id;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.extras = extras;
        this.status = status;
        this.appStatus = appStatus;
        this.priority = priority;

        //details
        this.customerName = customerName;
        this.reason = reason;
        this.shippingID = shippingID;
        this.awb = awb;
        this.time = time;
        this.brand = brand;
        this.category = category;

    }

    public AppOrder(){

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(String appStatus) {
        this.appStatus = appStatus;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getShippingID() {
        return shippingID;
    }

    public void setShippingID(String shippingID) {
        this.shippingID = shippingID;
    }

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDetails(){
        JSONObject details = new JSONObject();
        try{
            details.accumulate("name", customerName);
            details.accumulate("reason", reason);
            details.accumulate("shipping", shippingID);
            details.accumulate("awb", awb);
            details.accumulate("time", time);
            details.accumulate("brand", brand);
            details.accumulate("category", category);
        }catch (Exception e){
            e.printStackTrace();
        }
        return details.toString();
    }

    public void setDetails(String details){
        try{
            JSONObject data = new JSONObject(details);
            if(!data.isNull("name"))
                this.customerName = data.getString("name");
            else customerName = "";

            if(!data.isNull("reason"))
                this.reason = data.getString("reason");
            else reason = "";

            if(!data.isNull("shipping"))
                this.shippingID = data.getString("shipping");
            else shippingID = "";

            if(!data.isNull("awb"))
                this.awb = data.getString("awb");
            else awb = "";

            if(!data.isNull("time"))
                this.time = data.getString("time");
            else time = "";

            if(!data.isNull("brand"))
                this.brand = data.getString("brand");
            else brand = "";

            if(!data.isNull("category"))
                this.category = data.getString("category");
            else category = "";

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

