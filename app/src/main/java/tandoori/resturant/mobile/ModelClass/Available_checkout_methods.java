package tandoori.resturant.mobile.ModelClass;

import java.io.Serializable;

/**
 * Created by HP_Owner_Android on 5/30/2018.
 */

public class Available_checkout_methods implements Serializable {
    public String id;
    public String name;
    public String nameKey;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameKey() {
        return nameKey;
    }

    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }
}
