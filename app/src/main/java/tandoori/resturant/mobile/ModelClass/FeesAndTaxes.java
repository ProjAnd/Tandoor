package tandoori.resturant.mobile.ModelClass;

import java.io.Serializable;

/**
 * Created by HP_Owner_Android on 5/30/2018.
 */

public class FeesAndTaxes implements Serializable {
    public  String fee_id;
    public  String amount;
    public  String name;
    public  Double rate;

    public String getFee_id() {
        return fee_id;
    }

    public void setFee_id(String fee_id) {
        this.fee_id = fee_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
