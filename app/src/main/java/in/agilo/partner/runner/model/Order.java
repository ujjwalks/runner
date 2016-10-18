package in.agilo.partner.runner.model;

import java.io.Serializable;

/**
 * Created by Ujjwal on 4/6/2016.
 */
public class Order implements Serializable{
    private int  id;
    private ProductPartner  productPartner;
    private ProductAgilo  productAgilo;
    private String  partner;
    private String  timereceived;
    private int  nukkad;
    private RunnerInfo  runner;
    private String  awb;
    private String  barcodeMaps;
    private String  status;
    private ShipEvent  event;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProductPartner getProductPartner() {
        return productPartner;
    }

    public void setProductPartner(ProductPartner productPartner) {
        this.productPartner = productPartner;
    }

    public ProductAgilo getProductAgilo() {
        return productAgilo;
    }

    public void setProductAgilo(ProductAgilo productAgilo) {
        this.productAgilo = productAgilo;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getTimereceived() {
        return timereceived;
    }

    public void setTimereceived(String timereceived) {
        this.timereceived = timereceived;
    }

    public int getNukkad() {
        return nukkad;
    }

    public void setNukkad(int nukkad) {
        this.nukkad = nukkad;
    }

    public RunnerInfo getRunner() {
        return runner;
    }

    public void setRunner(RunnerInfo runner) {
        this.runner = runner;
    }

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    public String getBarcodeMaps() {
        return barcodeMaps;
    }

    public void setBarcodeMaps(String barcodeMaps) {
        this.barcodeMaps = barcodeMaps;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ShipEvent getEvent() {
        return event;
    }

    public void setEvent(ShipEvent event) {
        this.event = event;
    }

}
