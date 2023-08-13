package tandoori.resturant.mobile.ModelClass;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by HP_Owner_Android on 4/20/2018.
 */

public class AddToCart implements Serializable {
    public String curreny;
    private ArrayList<ItemModel> itemArrayList = new ArrayList<>();
    private ArrayList<FeesAndTaxes> feesArrayList = new ArrayList<>();
    private ArrayList<Available_delivery_methods> available_delivery_methods = new ArrayList<>();
    private ArrayList<Available_checkout_methods> available_checkout_methods = new ArrayList<>();


    public String getCurreny() {
        return curreny;
    }

    public void setCurreny(String curreny) {
        this.curreny = curreny;
    }

    public ArrayList<ItemModel> getItemArrayList() {
        return itemArrayList;
    }

    public void setItemArrayList(ArrayList<ItemModel> itemArrayList) {
        this.itemArrayList = itemArrayList;
    }

    public ArrayList<FeesAndTaxes> getFeesArrayList() {
        return feesArrayList;
    }

    public void setFeesArrayList(ArrayList<FeesAndTaxes> feesArrayList) {
        this.feesArrayList = feesArrayList;
    }

    public ArrayList<Available_delivery_methods> getAvailable_delivery_methods() {
        return available_delivery_methods;
    }

    public void setAvailable_delivery_methods(ArrayList<Available_delivery_methods> available_delivery_methods) {
        this.available_delivery_methods = available_delivery_methods;
    }

    public ArrayList<Available_checkout_methods> getAvailable_checkout_methods() {
        return available_checkout_methods;
    }

    public void setAvailable_checkout_methods(ArrayList<Available_checkout_methods> available_checkout_methods) {
        this.available_checkout_methods = available_checkout_methods;
    }
}
