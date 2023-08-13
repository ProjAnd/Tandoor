package tandoori.resturant.mobile.ModelClass;

import java.io.Serializable;

/**
 * Created by HP_Owner_Android on 4/20/2018.
 */

public class OrderStatus implements Serializable {
    public String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
