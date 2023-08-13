package tandoori.resturant.mobile.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import tandoori.resturant.mobile.CustomRecyclerView.CustomRecyclerView;
import tandoori.resturant.mobile.ModelClass.Available_checkout_methods;
import tandoori.resturant.mobile.ModelClass.Available_delivery_methods;
import tandoori.resturant.mobile.ModelClass.FeesAndTaxes;
import tandoori.resturant.mobile.ModelClass.ItemModel;
import tandoori.resturant.mobile.R;
import tandoori.resturant.mobile.utils.BaseUrl;
import tandoori.resturant.mobile.utils.NetworkAndHideKeyBoard;
import tandoori.resturant.mobile.volly.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;




public class MyCartActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout rlMenu, rlCart, lnOrdering_list;
    TextView tvTitle, tvEmail, tvUserName, tv_cart;
    Button btnContinueToBuy;
    ImageView ivCart, imgEmpty;

    CustomRecyclerView recyclerViewOrderHistory;
    LinearLayout lnStart_shopping;
    ArrayList<ItemModel> myList;
    ArrayList<ItemModel> btnToContinueArrayList = new ArrayList<>();
    ArrayList<Available_checkout_methods> availableCheckoutMethodsArrayList = new ArrayList<>();
    ArrayList<Available_delivery_methods> availableDeliveryMethodsArrayList = new ArrayList<>();
    String currency;
    String total_amount;
    String sub_total;
    ArrayList<FeesAndTaxes> feesAndTaxesArrayList = new ArrayList<>();
    NetworkAndHideKeyBoard networkAndHideKeyBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);
        windowsCode();
        networkAndHideKeyBoard = new NetworkAndHideKeyBoard(this);
        initViews();
        initListeners();
        if (AppController.getInstance().containsKey("bucket")) {
            if (AppController.getInstance().useString("bucket").equalsIgnoreCase("")) {
                lnStart_shopping.setVisibility(View.VISIBLE);
                lnOrdering_list.setVisibility(View.GONE);
                rlCart.setVisibility(View.GONE);
            } else {
                lnStart_shopping.setVisibility(View.GONE);
                lnOrdering_list.setVisibility(View.VISIBLE);
                getCartDetails();
                rlCart.setVisibility(View.VISIBLE);
            }
        } else {
            lnStart_shopping.setVisibility(View.VISIBLE);
            lnOrdering_list.setVisibility(View.GONE);
            rlCart.setVisibility(View.GONE);
        }
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
        btnContinueToBuy.setOnClickListener(this);

    }

    private void initViews() {
        rlMenu = findViewById(R.id.rlMenu);
        rlCart = findViewById(R.id.rlCart);

        ivCart = findViewById(R.id.ivCart);
        imgEmpty = findViewById(R.id.imgEmpty);
        recyclerViewOrderHistory = findViewById(R.id.recyclerViewOrderHistory);
        btnContinueToBuy = findViewById(R.id.btnContinueToBuy);
        lnStart_shopping = findViewById(R.id.lnStart_shopping);
        lnOrdering_list = findViewById(R.id.lnOrdering_list);
        ivCart.setImageResource(R.drawable.dustbin);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.wallet));
        tv_cart = findViewById(R.id.tv_cart);
        tv_cart.setVisibility(View.GONE);
    }

    private void setRecyclerView(ArrayList<ItemModel> myList) {
        btnToContinueArrayList.addAll(myList);
        recyclerViewOrderHistory.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewOrderHistory.setLayoutManager(linearLayoutManager);
        AddToCartActivityAdapter mAdapter = new AddToCartActivityAdapter(myList);
        recyclerViewOrderHistory.setAdapter(mAdapter);
    }

    private void getCartDetails() {
        networkAndHideKeyBoard.hideSoftKeyboard();
        if (!networkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "Network is not Available", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
            JSONObject jsonObject = new JSONObject();
            String access_token = AppController.getInstance().useString("access_token");
            String user_id = AppController.getInstance().useString("user_id");
            String bucket_id = AppController.getInstance().useString("bucket");
            Log.d("Access:", "" + access_token);

            final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.GET, BaseUrl.DCI +
                    "access_token=" + access_token + "&user_id=" + user_id + "&bucket_id=" + bucket_id, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.dismiss();
                            if (response.length() == 0) {
//                            Toast.makeText(MyCartActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    if (response.optString("request_status").equalsIgnoreCase("true")) {
                                        JSONObject objJson = response.optJSONObject("object");
                                        myList = new ArrayList<>();
                                        for (int i = 0; i < objJson.optJSONArray("items").length(); i++) {
                                            JSONObject itemObject = objJson.optJSONArray("items").optJSONObject(i);
                                            ItemModel itemModel = new ItemModel();
                                            itemModel.setProduct_id(itemObject.optString("product_id"));
                                            itemModel.setItemName(itemObject.optString("itemName"));
                                            itemModel.setUnit_price(itemObject.optDouble("unit_price"));
                                            itemModel.setItem_id(itemObject.optString("item_id"));
                                            itemModel.setQty(itemObject.optInt("qty"));
                                            ArrayList<String> varianceAttribute = new ArrayList<>();
                                            if (itemObject.has("variations_attrubutes")) {
                                                JSONObject varianceAttributeJsonObject = new JSONObject(itemObject.optString("variations_attrubutes"));
                                                JSONArray jsonArrayVarianceAttribute = varianceAttributeJsonObject.optJSONArray("Spicy");
                                                varianceAttribute.add(jsonArrayVarianceAttribute.get(0).toString());
                                            } else {
                                                varianceAttribute.add("No Variations");
                                            }
                                            itemModel.setVarianceAttribute(varianceAttribute);

                                      /*  ArrayList<String> varianceAttribute = new ArrayList<>();
                                        JSONObject varianceAttributeJsonObject = new JSONObject(itemObject.optString("variations_attrubutes"));
                                        if (varianceAttributeJsonObject.length() == 0) {
                                            varianceAttribute.add("No Variations");
                                        } else {
                                            JSONArray jsonArrayVarianceAttribute = varianceAttributeJsonObject.optJSONArray("Spicy");
                                            for (int varianceAttributes = 0; varianceAttributes < jsonArrayVarianceAttribute.length(); varianceAttributes++) {
                                                varianceAttribute.add(jsonArrayVarianceAttribute.get(varianceAttributes).toString());
                                            }
                                        }
                                        itemModel.setVarianceAttribute(varianceAttribute);*/
                                            itemModel.setSelected(false);
                                            myList.add(itemModel);
                                        }
                                        for (int fees = 0; fees < objJson.optJSONArray("fees").length(); fees++) {
                                            JSONObject jsonObjectFess = objJson.optJSONArray("fees").optJSONObject(fees);
                                            FeesAndTaxes feesAndTaxesModel = new FeesAndTaxes();
                                            feesAndTaxesModel.setFee_id(jsonObjectFess.optString("fee_id"));
                                            feesAndTaxesModel.setName(jsonObjectFess.optString("name"));
                                            feesAndTaxesModel.setAmount(jsonObjectFess.optString("amount"));
                                            feesAndTaxesModel.setRate(jsonObjectFess.optDouble("rate"));
                                            feesAndTaxesArrayList.add(feesAndTaxesModel);
                                        }
                                        for (int taxes = 0; taxes < objJson.optJSONArray("taxes").length(); taxes++) {
                                            JSONObject jsonObjectTaxes = objJson.optJSONArray("taxes").optJSONObject(taxes);
                                            FeesAndTaxes feesAndTaxesModel = new FeesAndTaxes();
                                            feesAndTaxesModel.setFee_id(jsonObjectTaxes.optString("tax_id"));
                                            feesAndTaxesModel.setName(jsonObjectTaxes.optString("name"));
                                            feesAndTaxesModel.setAmount(jsonObjectTaxes.optString("amount"));
                                            feesAndTaxesModel.setRate(jsonObjectTaxes.optDouble("rate"));
                                            feesAndTaxesArrayList.add(feesAndTaxesModel);
                                        }
                                        FeesAndTaxes feesAndTaxesModel = new FeesAndTaxes();
                                        feesAndTaxesModel.setAmount(objJson.optString("shippment_price"));
                                        feesAndTaxesModel.setName("Shippment Price");
                                        feesAndTaxesArrayList.add(feesAndTaxesModel);

                                        FeesAndTaxes feesAndTaxesModel1 = new FeesAndTaxes();
                                        feesAndTaxesModel1.setAmount(objJson.optString("shippment_tax"));
                                        feesAndTaxesModel1.setName("Shippment Tax");
                                        feesAndTaxesArrayList.add(feesAndTaxesModel1);
                                   /* FeesAndTaxes feesAndTaxesModel = new FeesAndTaxes();
                                    feesAndTaxesModel.setRate(objJson.optDouble("shippment_price"));
                                    feesAndTaxesModel.setName("Shippment");
                                    feesAndTaxesArrayList.add(feesAndTaxesModel);

                                    FeesAndTaxes feesAndTaxesModelTax = new FeesAndTaxes();
                                    feesAndTaxesModelTax.setRate(objJson.optDouble("shippment_tax"));
                                    feesAndTaxesModelTax.setName("Shippment tax");
                                    feesAndTaxesArrayList.add(feesAndTaxesModelTax);*/

                                        currency = objJson.optString("curreny");
                                        total_amount = objJson.optString("total_amount");
                                        sub_total = objJson.optString("sub_total");

                                        for (int checkoutMethods = 0; checkoutMethods < objJson.optJSONArray("available_checkout_methods").length(); checkoutMethods++) {
                                            Available_checkout_methods availableCheckoutMethodModel = new Available_checkout_methods();
                                            JSONObject jsonObjectCheckoutMethods = objJson.optJSONArray("available_checkout_methods").optJSONObject(checkoutMethods);
                                            availableCheckoutMethodModel.setId(jsonObjectCheckoutMethods.optString("id"));
                                            availableCheckoutMethodModel.setName(jsonObjectCheckoutMethods.optString("name"));
                                            availableCheckoutMethodModel.setNameKey(jsonObjectCheckoutMethods.optString("nameKey"));
                                            availableCheckoutMethodsArrayList.add(availableCheckoutMethodModel);
                                        }
                                        AppController.getInstance().saveString("available_pickup_methods", objJson.optString("available_pickup_methods"));
                                        for (int deliveryMethods = 0; deliveryMethods < objJson.optJSONArray("available_delivery_methods").length(); deliveryMethods++) {
                                            Available_delivery_methods availableDEliveryMethodModel = new Available_delivery_methods();
                                            JSONObject jsonObjectDeliveryMethods = objJson.optJSONArray("available_delivery_methods").optJSONObject(deliveryMethods);
                                            availableDEliveryMethodModel.setId(jsonObjectDeliveryMethods.optString("id"));
                                            availableDEliveryMethodModel.setName(jsonObjectDeliveryMethods.optString("name"));
                                            availableDEliveryMethodModel.setKey(jsonObjectDeliveryMethods.optString("key"));
                                            availableDEliveryMethodModel.setCost(jsonObjectDeliveryMethods.optString("cost"));
                                            availableDeliveryMethodsArrayList.add(availableDEliveryMethodModel);
                                        }
                                        setRecyclerView(myList);


                                    } else if (response.optString("request_status").equalsIgnoreCase("false")) {
                                        pDialog.dismiss();
                                        pDialog.hide();
                                        JSONObject objJson = response.optJSONObject("object");
                                        Toast.makeText(MyCartActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
                                    } else {
                                        pDialog.dismiss();
                                        pDialog.hide();
                                        JSONObject objJson = response.optJSONObject("object");
                                        Toast.makeText(MyCartActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
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
                            Log.d("ERROR1:", "");
                            rlCart.setVisibility(View.GONE);
                            btnContinueToBuy.setEnabled(false);
                            Toast.makeText(MyCartActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();
                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() {
                    return AppController.getInstance().getParams();
                }

            };
            AppController.getInstance().addToRequestQueue(loginResult);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlCart:
                if (myList.size() == 0 || myList == null) {
                    Toast.makeText(this, "No item to delete", Toast.LENGTH_SHORT).show();
                    lnStart_shopping.setVisibility(View.VISIBLE);
                    lnOrdering_list.setVisibility(View.GONE);
                } else {
                    deleteCart(AppController.getInstance().useString("bucket"));
                }
                break;

            case R.id.btnContinueToBuy:
                Intent intent = new Intent(MyCartActivity.this, OrderNowActivity.class);
                intent.putExtra("itemArrayList", btnToContinueArrayList);
                intent.putExtra("feesAndTaxes", feesAndTaxesArrayList);
                intent.putExtra("currency", currency);
                intent.putExtra("total_amount", total_amount);
                intent.putExtra("sub_total", sub_total);

                intent.putExtra("availableCheckoutMethodsArrayList", availableCheckoutMethodsArrayList);
                intent.putExtra("availableDeliveryMethodsArrayList", availableDeliveryMethodsArrayList);
                Log.i("", "");
                startActivity(intent);
                break;

            case R.id.rlMenu:
                onBackPressed();
                break;


        }
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();

    }

    private void deleteCartItem(String itemId, final int position, final ArrayList<ItemModel> itemList) {
        networkAndHideKeyBoard.hideSoftKeyboard();
        if (!networkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "Network is not Available", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Deleting...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
            JSONObject jsonObject = new JSONObject();
            String access_token = AppController.getInstance().useString("access_token");
            String user_id = AppController.getInstance().useString("user_id");

            final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.DELETE, BaseUrl.REMOVECARTITEM + "access_token=" + access_token + "&user_id=" + user_id + "&item_id=" + itemId, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.dismiss();
                            if (response.length() == 0) {
                                Toast.makeText(MyCartActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        itemList.remove(position);
                                        recyclerViewOrderHistory.getAdapter().notifyItemRemoved(position);
                                        recyclerViewOrderHistory.getAdapter().notifyItemRangeChanged(position, itemList.size());

                                    } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                        pDialog.dismiss();
                                        pDialog.hide();
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        Toast.makeText(MyCartActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
                                    } else {
                                        pDialog.dismiss();
                                        pDialog.hide();
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        Toast.makeText(MyCartActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    pDialog.dismiss();
                                    pDialog.hide();
//                                CustomToast.show(MyCartActivity.this, e.toString(), true, getResources().getDrawable(R.drawable.red));
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();
                            Toast.makeText(MyCartActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() {
                    return AppController.getInstance().getParams();
                }
            };
            AppController.getInstance().addToRequestQueue(loginResult);
        }
    }

    private void deleteCart(final String bucket_id) {
        networkAndHideKeyBoard.hideSoftKeyboard();
        if (!networkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "Network is not Available", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Deleting...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
            JSONObject jsonObject = new JSONObject();
            String access_token = AppController.getInstance().useString("access_token");
            String user_id = AppController.getInstance().useString("user_id");

            final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.DELETE, BaseUrl.REMOVECART + "access_token=" + access_token + "&user_id=" + user_id + "&bucket_id=" + bucket_id, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.dismiss();
                            if (response.length() == 0) {
                                Toast.makeText(MyCartActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        if (objJson.equals("{}") || (objJson.length() == 0)) {
                                            Log.i("", "");
                                            lnStart_shopping.setVisibility(View.VISIBLE);
                                            lnOrdering_list.setVisibility(View.GONE);
                                            myList.clear();
                                            recyclerViewOrderHistory.getAdapter().notifyDataSetChanged();
                                            AppController.getInstance().remove("bucket");
                                        } else {
                                            Toast.makeText(MyCartActivity.this, "No item", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                        pDialog.dismiss();
                                        pDialog.hide();
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        Toast.makeText(MyCartActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();

                                    } else {
                                        pDialog.dismiss();
                                        pDialog.hide();
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        Toast.makeText(MyCartActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(MyCartActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() {
                    return AppController.getInstance().getParams();
                }

            };
            AppController.getInstance().addToRequestQueue(loginResult);
        }

    }

    private class AddToCartActivityAdapter extends RecyclerView.Adapter<AddToCartActivityAdapter.MyViewHolder> {
        int size;
        private ArrayList<ItemModel> itemList;

        AddToCartActivityAdapter(ArrayList<ItemModel> itemList) {
            this.itemList = itemList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_addtocart, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final ItemModel addToCart = itemList.get(position);
            size = itemList.size();
            AppController.getInstance().saveString("Size", String.valueOf(size));
            holder.tvUnitPrice.setText("Unit Price : $" + itemList.get(position).getUnit_price());
            holder.tvItemName.setText(itemList.get(position).getItemName());

            String variation = String.valueOf(itemList.get(position).getVarianceAttribute());
            variation = variation.replaceAll("\\[", "").replaceAll("\\]", "");
            if (!variation.equals("No Variations")) {
                holder.tvVariation.setText("(Spicy: " + variation + ")");
            } else {
                holder.tvVariation.setVisibility(View.GONE);
            }

            holder.tvInteger_number.setText(itemList.get(position).getQty() + "");
            final int minteger[] = {itemList.get(position).getQty()};
            double priceDisply = Double.parseDouble(itemList.get(position).getUnit_price() + "");
            double multiply = priceDisply * minteger[0];
            String mult = String.format("%.2f", multiply);
            holder.tvPrice.setText("$" + mult);
            holder.btnDecrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (minteger[0] == 1) {
                        holder.btnDecrease.setEnabled(false);
                        holder.btnIncrease.setEnabled(true);
                        holder.btnDecrease.setClickable(false);
                        holder.btnIncrease.setClickable(true);
                    } else if (minteger[0] > 1) {
                        minteger[0] = minteger[0] - 1;
                        holder.btnDecrease.setEnabled(true);
                        holder.btnIncrease.setEnabled(false);
                        holder.btnDecrease.setClickable(true);
                        holder.btnIncrease.setClickable(false);
                    }
                    double price = Double.parseDouble(itemList.get(position).getUnit_price() + "");
                    double multiply = price * minteger[0];
                    String mult = String.format("%.2f", multiply);
                    holder.tvPrice.setText("$" + mult);
                    holder.tvInteger_number.setText(minteger[0] + "");
                    updateQty(addToCart.getItem_id(), minteger[0] + "");
                }
            });
            holder.btnIncrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    minteger[0] = minteger[0] + 1;
                    if (minteger[0] == 50) {
                        holder.btnIncrease.setClickable(false);
                        holder.btnDecrease.setClickable(true);
                    }
                    double price = Double.parseDouble(itemList.get(position).getUnit_price() + "");
                    double multiply = price * minteger[0];
                    String mult = String.format("%.2f", multiply);
                    holder.tvPrice.setText("$" + mult);
                    holder.tvInteger_number.setText(minteger[0] + "");
                    updateQty(addToCart.getItem_id(), minteger[0] + "");
                }
            });
            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(MyCartActivity.this, v);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.menu_cart, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_delete:
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyCartActivity.this);
                                    alertDialog.setTitle(getResources().getString(R.string.itemDelete));
                                    alertDialog.setMessage(getResources().getString(R.string.deleteAddress));
                                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteCartItem(itemList.get(position).getItem_id(), position, itemList);
                                        }
                                    });
                                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    alertDialog.show();
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });
         /*   holder.checkbox.setSelected(addToCart.isSelected());
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    btnToContinueArrayList.clear();
                    if (isChecked) {
                        itemList.get(position).setSelected(true);
                    } else {
                        itemList.get(position).setSelected(false);
                    }
                    btnToContinueArrayList.addAll(itemList);
                    Log.i("", "" + itemList.size());
                }
            });*/
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        private void updateQty(String item_id, String qty) {
            JSONObject jsonObject = new JSONObject();
            String access_token = AppController.getInstance().useString("access_token");
            Log.d("Access:", "" + access_token);
            try {
                jsonObject.put("form_id", "");
                jsonObject.put("user_id", AppController.getInstance().useString("user_id"));
                Map<String, String> fieldMap = new HashMap<>();
                fieldMap.put("bucketId", AppController.getInstance().useString("bucket"));
                fieldMap.put("bucketItemId", item_id);
                fieldMap.put("quantity", qty);
                jsonObject.put("fields", new JSONObject(fieldMap));
                JsonObjectRequest saveRequest = new JsonObjectRequest(Request.Method.POST, BaseUrl.UPDATEQTY + "access_token=" + access_token, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response Tag", response.toString());
                        if (response.length() == 0) {
                            Toast.makeText(MyCartActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                    JSONObject objJson = jsonObject.optJSONObject("object");
                                    objJson.optString("quantity");

                                } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                    JSONObject objJson = jsonObject.optJSONObject("object");
                                    Toast.makeText(MyCartActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
                                } else {
                                    JSONObject objJson = jsonObject.optJSONObject("object");
                                    Toast.makeText(MyCartActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
//                                CustomToast.show(MyCartActivity.this, e.toString(), true, getResources().getDrawable(R.drawable.red));
                            }
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("ERROR1:", "");
                                Toast.makeText(MyCartActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
                            }
                        }) {

                    @Override
                    public Map<String, String> getHeaders() {
                        return AppController.getInstance().getParams();
                    }
                };
                AppController.getInstance().addToRequestQueue(saveRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivItemImage, imgDelete;
            TextView tvItemName, tvPrice, tvUnitPrice, tvInteger_number, tvVariation;
            CardView cdDiplay_order;
            Button btnDecrease, btnIncrease;
            Spinner tvQty;
            CheckBox checkbox;

            public MyViewHolder(View itemView) {
                super(itemView);
                ivItemImage = itemView.findViewById(R.id.ivItemImage);
                tvItemName = itemView.findViewById(R.id.tvItemName);
                tvVariation = itemView.findViewById(R.id.tvVariation);
                tvUnitPrice = itemView.findViewById(R.id.tvUnitPrice);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvQty = itemView.findViewById(R.id.tvQty);
                tvInteger_number = itemView.findViewById(R.id.tvInteger_number);
                imgDelete = itemView.findViewById(R.id.imgDelete);
                btnDecrease = itemView.findViewById(R.id.btnDecrease);
                btnIncrease = itemView.findViewById(R.id.btnIncrease);
                checkbox = itemView.findViewById(R.id.checkbox);
                cdDiplay_order = itemView.findViewById(R.id.cdDiplay_order);
            }
        }
    }
}
