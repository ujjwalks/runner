package in.agilo.partner.runner.model;

import java.io.Serializable;

/**
 * Created by Ujjwal on 4/6/2016.
 */
public class ShipEvent implements Serializable{
    private int id;
    private String order;
    private String location;
    private String timestamp;
    private String description;
    private ShipEvent nextEvent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ShipEvent getNextEvent() {
        return nextEvent;
    }

    public void setNextEvent(ShipEvent nextEvent) {
        this.nextEvent = nextEvent;
    }
}
