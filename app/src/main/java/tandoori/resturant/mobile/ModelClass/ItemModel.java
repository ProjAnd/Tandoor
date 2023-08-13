package tandoori.resturant.mobile.ModelClass;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by HP_Owner_Android on 5/31/2018.
 */

public class ItemModel implements Serializable {
    private boolean isSelected;
    public String ItemImage;
    public String itemName;
    public String item_id;
    public String product_id;
    public Double unit_price;
    public int qty;
    private ArrayList<String> varianceAttribute = new ArrayList<>();

    public ArrayList<String> getVarianceAttribute() {
        return varianceAttribute;
    }

    public void setVarianceAttribute(ArrayList<String> varianceAttribute) {
        this.varianceAttribute = varianceAttribute;
    }

    public String getSelectedItem() {
        return SelectedItem;
    }

    public void setSelectedItem(String selectedItem) {
        SelectedItem = selectedItem;
    }

    public String SelectedItem;


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getItemImage() {
        return ItemImage;
    }

    public void setItemImage(String itemImage) {
        ItemImage = itemImage;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public Double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(Double unit_price) {
        this.unit_price = unit_price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
