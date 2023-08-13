package tandoori.resturant.mobile.ModelClass;

import java.util.ArrayList;

/**
 * Created by HP_Owner_Android on 6/9/2018.
 */

public class ItemOrderHistory {
    public String itemName;
    public int qty;
    public double unit_price;
    private ArrayList<String> varianceAttribute = new ArrayList<>();

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }

    public ArrayList<String> getVarianceAttribute() {
        return varianceAttribute;
    }

    public void setVarianceAttribute(ArrayList<String> varianceAttribute) {
        this.varianceAttribute = varianceAttribute;
    }
}
