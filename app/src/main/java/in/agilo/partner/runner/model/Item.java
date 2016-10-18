package in.agilo.partner.runner.model;

/**
 * Created by Alessandro on 12/01/2016.
 */
public class Item {
    private int id;
    private String name, description, updated, status, multimedia;

    public Item(){
        this.id = 0;
        this.name = "";
        this.description = "";
        this.updated = "";
        this.status = "";
        this.multimedia = "";
    }

    public Item(int id, String name, String description, String updated, String status, String multimedia) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.updated = updated;
        this.status = status;
        this.multimedia = multimedia;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(String multimedia) {
        this.multimedia = multimedia;
    }
}
