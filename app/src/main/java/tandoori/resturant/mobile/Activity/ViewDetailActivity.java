package tandoori.resturant.mobile.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tandoori.resturant.mobile.ModelClass.ItemFeesOrderHistory;
import tandoori.resturant.mobile.ModelClass.ItemOrderHistory;
import tandoori.resturant.mobile.ModelClass.OrderHistory;
import tandoori.resturant.mobile.ModelClass.OrderStatus;
import tandoori.resturant.mobile.ModelClass.TaxesOrderHistory;
import tandoori.resturant.mobile.R;
import tandoori.resturant.mobile.utils.BaseUrl;
import tandoori.resturant.mobile.utils.NetworkAndHideKeyBoard;
import tandoori.resturant.mobile.volly.AppController;

public class ViewDetailActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvOrderId, tvDate, tvShippingAddress,
            tvTotalamount, tvBillingAddress, tvPaymentType, tvPaymentStatus, tvTitle;
    LinearLayout linearView, linearViewTaxes, lnTaxes;
    ArrayList<OrderHistory> itemList = new ArrayList<>();
    RelativeLayout rlCart, rlMenu;
    int position;
    NetworkAndHideKeyBoard networkAndHideKeyBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemList.addAll((ArrayList<OrderHistory>) getIntent().getSerializableExtra("itemList"));
        position = getIntent().getIntExtra("position", 0);
        setContentView(R.layout.activity_view_detail);
        windowsCode();
        networkAndHideKeyBoard= new NetworkAndHideKeyBoard(this);
        initViews();
        initListeners();
        viewOrderDetails(itemList.get(position).getOrderId());
    }

    private void windowsCode() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.green));
        }
    }

    private void initListeners() {
        rlCart.setOnClickListener(this);
        rlMenu.setOnClickListener(this);
    }

    private void initViews() {
        rlMenu = findViewById(R.id.rlMenu);
        rlCart = findViewById(R.id.rlCart);
        rlCart.setVisibility(View.GONE);

        tvOrderId = findViewById(R.id.tvOrderId);
        tvDate = findViewById(R.id.tvDate);
        tvTitle = findViewById(R.id.tvTitle);

        tvShippingAddress = findViewById(R.id.tvShippingAddress);
        tvTotalamount = findViewById(R.id.tvTotalamount);
        tvBillingAddress = findViewById(R.id.tvBillingAddress);
        tvPaymentType = findViewById(R.id.tvPaymentType);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        linearView = findViewById(R.id.linearView);
        linearViewTaxes = findViewById(R.id.linearViewTaxes);
        lnTaxes = findViewById(R.id.lnTaxes);
        tvTitle.setText(getResources().getString(R.string.orderDetails));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlMenu:
                onBackPressed();
                break;
        }
    }

    private void viewOrderDetails(String orderId) {
        networkAndHideKeyBoard.hideSoftKeyboard();
        if (!networkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "Network is not Available", Toast.LENGTH_SHORT).show();
        }
        else {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("form_id", "");
                jsonObject.put("user_id", AppController.getInstance().useString("user_id"));
                Map<String, String> fieldMap = new HashMap<>();
                fieldMap.put("orderId", orderId);
                jsonObject.put("fields", new JSONObject(fieldMap));

                String access_token = AppController.getInstance().useString("access_token");
                JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.POST, BaseUrl.PAYMENT_STATUS + "access_token=" + access_token, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                pDialog.dismiss();
                                if (response.length() == 0) {
                                    Toast.makeText(ViewDetailActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.toString());
                                        if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                            JSONObject objectJson = jsonObject.optJSONObject("object");
                                            String orderId = objectJson.optString("orderId");
                                            ArrayList<ItemOrderHistory> itemOrderHistoryArrayList = new ArrayList<>();
                                            for (int orderst = 0; orderst < objectJson.optJSONArray("items").length(); orderst++) {
                                                ItemOrderHistory itemOrderHistoryModel = new ItemOrderHistory();
                                                JSONObject jsonObjectItem = objectJson.optJSONArray("items").optJSONObject(orderst);
                                                itemOrderHistoryModel.setItemName(jsonObjectItem.optString("itemName"));
                                                itemOrderHistoryModel.setQty(jsonObjectItem.optInt("qty"));
                                                itemOrderHistoryModel.setUnit_price(jsonObjectItem.optDouble("unit_price"));
                                                ArrayList<String> varianceAttribute = new ArrayList<>();
                                                JSONObject varianceAttributeJsonObject = new JSONObject(jsonObjectItem.optString("variations_attrubutes"));
                                                if (varianceAttributeJsonObject.length() == 0) {
                                                    varianceAttribute.add("No Variations");
                                                } else {
                                                    JSONArray jsonArrayVarianceAttribute = varianceAttributeJsonObject.optJSONArray("Spicy");
                                                    for (int varianceAttributes = 0; varianceAttributes < jsonArrayVarianceAttribute.length(); varianceAttributes++) {
                                                        varianceAttribute.add(jsonArrayVarianceAttribute.get(varianceAttributes).toString());
                                                    }
                                                }
                                                itemOrderHistoryModel.setVarianceAttribute(varianceAttribute);
                                                itemOrderHistoryArrayList.add(itemOrderHistoryModel);
                                            }
                                            ArrayList<ItemFeesOrderHistory> itemFeesOrderHistoryArrayList = new ArrayList<>();
                                            for (int feesOrderHistory = 0; feesOrderHistory < objectJson.optJSONArray("itemsFees").length(); feesOrderHistory++) {
                                                ItemFeesOrderHistory itemFeesOrderHistoryModel = new ItemFeesOrderHistory();
                                                JSONObject jsonObjectFees = objectJson.optJSONArray("itemsFees").optJSONObject(feesOrderHistory);
                                                itemFeesOrderHistoryModel.setName(jsonObjectFees.optString("name"));
                                                itemFeesOrderHistoryModel.setAmount(jsonObjectFees.optInt("amount"));
                                                itemFeesOrderHistoryArrayList.add(itemFeesOrderHistoryModel);
                                            }
                                            ArrayList<OrderStatus> orderStatusArrayList = new ArrayList<>();
                                            for (int orderStatus = 0; orderStatus < objectJson.optJSONArray("orderStatus").length(); orderStatus++) {
                                                OrderStatus orderStatusModel = new OrderStatus();
                                                JSONObject jsonObjectFees = objectJson.optJSONArray("orderStatus").optJSONObject(orderStatus);
                                                orderStatusModel.setStatus(jsonObjectFees.optString("status"));
                                                orderStatusArrayList.add(orderStatusModel);
                                            }
                                            String orderTotal = objectJson.optString("orderTotal");
                                            JSONObject metaInfoJsonObject = new JSONObject(objectJson.optString("metaInfo"));
                                            metaInfoJsonObject.optString("SHIPPING_METHOD");
                                            metaInfoJsonObject.optString("BUCKET");
                                            metaInfoJsonObject.optString("ORDER_TXN_ID");

                                            ArrayList<TaxesOrderHistory> taxesOrderHistoryArrayList = new ArrayList<>();
                                            for (int taxesOrderHistory = 0; taxesOrderHistory < objectJson.optJSONArray("taxes").length(); taxesOrderHistory++) {
                                                TaxesOrderHistory taxesOrderHistoryModel = new TaxesOrderHistory();
                                                JSONObject jsonObjectTaxes = objectJson.optJSONArray("taxes").optJSONObject(taxesOrderHistory);
                                                taxesOrderHistoryModel.setName(jsonObjectTaxes.optString("name"));
                                                taxesOrderHistoryModel.setAmount(jsonObjectTaxes.optDouble("amount"));
                                                taxesOrderHistoryArrayList.add(taxesOrderHistoryModel);
                                            }
                                            setViews(orderId, itemOrderHistoryArrayList, itemFeesOrderHistoryArrayList, taxesOrderHistoryArrayList, orderTotal);
                                            Log.i("", "");
                                        } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                            pDialog.dismiss();
                                            pDialog.hide();
                                            JSONObject objJson = jsonObject.optJSONObject("object");
                                            Toast.makeText(ViewDetailActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            pDialog.dismiss();
                                            pDialog.hide();
                                            JSONObject objJson = jsonObject.optJSONObject("object");
                                            Toast.makeText(ViewDetailActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        pDialog.dismiss();
                                        pDialog.hide();
                                    }
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pDialog.dismiss();
                                pDialog.hide();
                                Toast.makeText(ViewDetailActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        return AppController.getInstance().getParams();
                    }
                };
                AppController.getInstance().addToRequestQueue(loginResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setViews(String orderId, ArrayList<ItemOrderHistory> itemOrderHistoryArrayList, ArrayList<ItemFeesOrderHistory> itemFeesOrderHistoryArrayList, ArrayList<TaxesOrderHistory> taxesOrderHistoryArrayList, String orderTotal) {
        tvOrderId.setText("Order : " + orderId);
        tvTotalamount.setText("$" + orderTotal);
        tvDate.setText(itemList.get(position).getOrderedDate());
        tvPaymentType.setText("Payment (" + itemList.get(position).getCheckoutMethod() + "):");
        tvBillingAddress.setText(itemList.get(position).getBillingAddress());
        tvShippingAddress.setText(itemList.get(position).getShippingAddress());
        tvPaymentStatus.setText(itemList.get(position).getOrderStatusArrayList().get(0).getStatus());

        for (int i = 0; i < itemOrderHistoryArrayList.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.header_layout, null);
            TextView lnPrice = view.findViewById(R.id.lnPrice);
            TextView lnItemName = view.findViewById(R.id.lnItemName);
            TextView lnQty = view.findViewById(R.id.lnQty);
            TextView lnMutiPrice = view.findViewById(R.id.lnMutiPrice);
            String variation = String.valueOf(itemOrderHistoryArrayList.get(i).getVarianceAttribute());
            variation = variation.replaceAll("\\[", "").replaceAll("\\]", "");
            if (!variation.equalsIgnoreCase("No Variations")) {
                lnItemName.setText(itemOrderHistoryArrayList.get(i).getItemName() + "\n(Spicy: " + variation + ")");
            } else {
                lnItemName.setText(itemOrderHistoryArrayList.get(i).getItemName());
            }
            lnPrice.setText("$" + itemOrderHistoryArrayList.get(i).getUnit_price());
            lnQty.setText("" + itemOrderHistoryArrayList.get(i).getQty() + "X");
            final int minteger[] = {itemOrderHistoryArrayList.get(i).getQty()};
            double priceDisply = Double.parseDouble(itemOrderHistoryArrayList.get(i).getUnit_price() + "");
            double multiply = priceDisply * minteger[0];
            String mult = String.format("%.2f", multiply);
            lnMutiPrice.setText("$" + mult);
            linearView.addView(view);
        }

        for (int i = 0; i < itemFeesOrderHistoryArrayList.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.taxes_layout, null);
            TextView lnItemName = view.findViewById(R.id.lnItemName);
            TextView lnMutiPrice = view.findViewById(R.id.lnMutiPrice);
            String name = itemFeesOrderHistoryArrayList.get(i).getName();
            name = name.replaceAll("_", " ");
            lnItemName.setText(name);
            lnMutiPrice.setText("$" + Math.round(itemFeesOrderHistoryArrayList.get(i).getAmount() * 100.0) / 100.0);
            linearViewTaxes.addView(view);
        }
        for (int i = 0; i < taxesOrderHistoryArrayList.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.taxes_layout, null);
            TextView lnItemName = view.findViewById(R.id.lnItemName);
            TextView lnMutiPrice = view.findViewById(R.id.lnMutiPrice);
            lnItemName.setText(taxesOrderHistoryArrayList.get(i).getName());
            lnMutiPrice.setText("$" + Math.round(taxesOrderHistoryArrayList.get(i).getAmount() * 100.0) / 100.0);
            lnTaxes.addView(view);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
