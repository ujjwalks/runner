package in.agilo.partner.runner.model;

import java.io.Serializable;

/**
 * Created by Ujjwal on 4/6/2016.
 */
public class ProductPartner implements Serializable{
    private String id;
    private String agiloAWB;
    private String itemID;
    private String pickupaddress;
    private String pickupstreet;
    private String pickupcity;
    private String pickupstate;
    private String pickuppincode;
    private String pickupmobile;
    private String pikcupaltmobile;
    private String pickupemail;
    private String pickupname;
    private String deliveryname;
    private String deliveryaddress;
    private String deliverystreet;
    private String deliverycity;
    private String deliverystate;
    private String deliverypincode;
    private String deliverymobile;
    private String deliveryaltmobile;
    private String deliveryemail;
    private String pickuptime;
    private String deliverytime;
    private String brand;
    private String category;
    private String color;
    private String subcategory;
    private String itemname;
    private String description;
    private String material;
    private String model;
    private String otherfeatures;
    private String reasonforreturn1;
    private String reasonforreturn2;
    private String reasonforreturn3;
    private String reasonforreturn4;
    private String remarks;
    private String shipmentImage;
    private String imei;
    private String upc_ean;
    private float weight;
    private float height;
    private float length;
    private float width;
    private int quantity;
    private float value;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getUpc_ean() {
        return upc_ean;
    }

    public void setUpc_ean(String upc_ean) {
        this.upc_ean = upc_ean;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getShipmentImage() {
        return shipmentImage;
    }

    public void setShipmentImage(String shipmentImage) {
        this.shipmentImage = shipmentImage;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getReasonforreturn4() {
        return reasonforreturn4;
    }

    public void setReasonforreturn4(String reasonforreturn4) {
        this.reasonforreturn4 = reasonforreturn4;
    }

    public String getReasonforreturn3() {
        return reasonforreturn3;
    }

    public void setReasonforreturn3(String reasonforreturn3) {
        this.reasonforreturn3 = reasonforreturn3;
    }

    public String getReasonforreturn2() {
        return reasonforreturn2;
    }

    public void setReasonforreturn2(String reasonforreturn2) {
        this.reasonforreturn2 = reasonforreturn2;
    }

    public String getReasonforreturn1() {
        return reasonforreturn1;
    }

    public void setReasonforreturn1(String reasonforreturn1) {
        this.reasonforreturn1 = reasonforreturn1;
    }

    public String getOtherfeatures() {
        return otherfeatures;
    }

    public void setOtherfeatures(String otherfeatures) {
        this.otherfeatures = otherfeatures;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgiloAWB() {
        return agiloAWB;
    }

    public void setAgiloAWB(String agiloAWB) {
        this.agiloAWB = agiloAWB;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getPickupaddress() {
        return pickupaddress;
    }

    public void setPickupaddress(String pickupaddress) {
        this.pickupaddress = pickupaddress;
    }

    public String getPickupstreet() {
        return pickupstreet;
    }

    public void setPickupstreet(String pickupstreet) {
        this.pickupstreet = pickupstreet;
    }

    public String getPickupcity() {
        return pickupcity;
    }

    public void setPickupcity(String pickupcity) {
        this.pickupcity = pickupcity;
    }

    public String getPickupstate() {
        return pickupstate;
    }

    public void setPickupstate(String pickupstate) {
        this.pickupstate = pickupstate;
    }

    public String getPikcupaltmobile() {
        return pikcupaltmobile;
    }

    public void setPikcupaltmobile(String pikcupaltmobile) {
        this.pikcupaltmobile = pikcupaltmobile;
    }

    public String getPickupmobile() {
        return pickupmobile;
    }

    public void setPickupmobile(String pickupmobile) {
        this.pickupmobile = pickupmobile;
    }

    public String getPickuppincode() {
        return pickuppincode;
    }

    public void setPickuppincode(String pickuppincode) {
        this.pickuppincode = pickuppincode;
    }

    public String getPickupemail() {
        return pickupemail;
    }

    public void setPickupemail(String pickupemail) {
        this.pickupemail = pickupemail;
    }

    public String getPickupname() {
        return pickupname;
    }

    public void setPickupname(String pickupname) {
        this.pickupname = pickupname;
    }

    public String getDeliveryname() {
        return deliveryname;
    }

    public void setDeliveryname(String deliveryname) {
        this.deliveryname = deliveryname;
    }

    public String getDeliveryaddress() {
        return deliveryaddress;
    }

    public void setDeliveryaddress(String deliveryaddress) {
        this.deliveryaddress = deliveryaddress;
    }

    public String getDeliverystreet() {
        return deliverystreet;
    }

    public void setDeliverystreet(String deliverystreet) {
        this.deliverystreet = deliverystreet;
    }

    public String getDeliverycity() {
        return deliverycity;
    }

    public void setDeliverycity(String deliverycity) {
        this.deliverycity = deliverycity;
    }

    public String getDeliverystate() {
        return deliverystate;
    }

    public void setDeliverystate(String deliverystate) {
        this.deliverystate = deliverystate;
    }

    public String getDeliverypincode() {
        return deliverypincode;
    }

    public void setDeliverypincode(String deliverypincode) {
        this.deliverypincode = deliverypincode;
    }

    public String getDeliverymobile() {
        return deliverymobile;
    }

    public void setDeliverymobile(String deliverymobile) {
        this.deliverymobile = deliverymobile;
    }

    public String getDeliveryaltmobile() {
        return deliveryaltmobile;
    }

    public void setDeliveryaltmobile(String deliveryaltmobile) {
        this.deliveryaltmobile = deliveryaltmobile;
    }

    public String getDeliveryemail() {
        return deliveryemail;
    }

    public void setDeliveryemail(String deliveryemail) {
        this.deliveryemail = deliveryemail;
    }

    public String getDeliverytime() {
        return deliverytime;
    }

    public void setDeliverytime(String deliverytime) {
        this.deliverytime = deliverytime;
    }

    public String getPickuptime() {
        return pickuptime;
    }

    public void setPickuptime(String pickuptime) {
        this.pickuptime = pickuptime;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
