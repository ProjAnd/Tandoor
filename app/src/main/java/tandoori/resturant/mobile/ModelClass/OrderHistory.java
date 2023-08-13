package tandoori.resturant.mobile.ModelClass;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by HP_Owner_Android on 4/20/2018.
 */

public class OrderHistory implements Serializable {

    public String orderId;
    public ArrayList<ItemOrderHistory> itemOrderHistoryArrayList= new ArrayList<>();
    public ArrayList<ItemFeesOrderHistory> itemFeesOrderHistoryArrayList= new ArrayList<>();
    public ArrayList<OrderStatus> orderStatusArrayList= new ArrayList<>();
    public String orderTotal;
    private ArrayList<String> metaInfo = new ArrayList<>();
    public ArrayList<OrderRefuneds> orderRefunedsArrayList= new ArrayList<>();
    public ArrayList<TaxesOrderHistory> taxesOrderHistoryArrayList= new ArrayList<>();
    private String checkoutMethod = new String();
    public String orderedDate;
    private String billingAddress = new String();
    private String shippingAddress = new String();


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(String orderedDate) {
        this.orderedDate = orderedDate;
    }

    public ArrayList<ItemOrderHistory> getItemOrderHistoryArrayList() {
        return itemOrderHistoryArrayList;
    }

    public void setItemOrderHistoryArrayList(ArrayList<ItemOrderHistory> itemOrderHistoryArrayList) {
        this.itemOrderHistoryArrayList = itemOrderHistoryArrayList;
    }

    public ArrayList<ItemFeesOrderHistory> getItemFeesOrderHistoryArrayList() {
        return itemFeesOrderHistoryArrayList;
    }

    public void setItemFeesOrderHistoryArrayList(ArrayList<ItemFeesOrderHistory> itemFeesOrderHistoryArrayList) {
        this.itemFeesOrderHistoryArrayList = itemFeesOrderHistoryArrayList;
    }

    public ArrayList<OrderStatus> getOrderStatusArrayList() {
        return orderStatusArrayList;
    }

    public void setOrderStatusArrayList(ArrayList<OrderStatus> orderStatusArrayList) {
        this.orderStatusArrayList = orderStatusArrayList;
    }

    public ArrayList<String> getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(ArrayList<String> metaInfo) {
        this.metaInfo = metaInfo;
    }

    public ArrayList<OrderRefuneds> getOrderRefunedsArrayList() {
        return orderRefunedsArrayList;
    }

    public void setOrderRefunedsArrayList(ArrayList<OrderRefuneds> orderRefunedsArrayList) {
        this.orderRefunedsArrayList = orderRefunedsArrayList;
    }

    public ArrayList<TaxesOrderHistory> getTaxesOrderHistoryArrayList() {
        return taxesOrderHistoryArrayList;
    }

    public void setTaxesOrderHistoryArrayList(ArrayList<TaxesOrderHistory> taxesOrderHistoryArrayList) {
        this.taxesOrderHistoryArrayList = taxesOrderHistoryArrayList;
    }

    public String getCheckoutMethod() {
        return checkoutMethod;
    }

    public void setCheckoutMethod(String checkoutMethod) {
        this.checkoutMethod = checkoutMethod;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
