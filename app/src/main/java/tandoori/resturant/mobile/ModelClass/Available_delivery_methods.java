package tandoori.resturant.mobile.ModelClass;

import java.io.Serializable;

/**
 * Created by HP_Owner_Android on 5/30/2018.
 */

public class Available_delivery_methods implements Serializable {
    public String cost;
    public String id;
    public String key;
    public String name;

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
