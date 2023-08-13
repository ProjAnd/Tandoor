package tandoori.resturant.mobile.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import tandoori.resturant.mobile.CustomToast.CustomToast;
import tandoori.resturant.mobile.ModelClass.AddressModel;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderNowActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {
    TextView tvSubtotal, tvSubQtuy, tvSubPrice, tvDelivry, tvDelivryAmount, tvTip, tvTotal, tvTotalamount, tvInternational, tvFree, tvLocal, tvPaymentType, tvCreatedAddress,
            tvTipAmount, tv_cart, tvTitle, tvAddressAmount, tvLocalAmount,
            tvFreeAmount, tvInternationalAmount, tvInteger_number, tvPickup, tvapplyCode;
    Button btnContinueToBuy;
    double result2;
    ArrayList<ItemModel> itemArrayList;
    ArrayList<FeesAndTaxes> feesAndTaxes;
    ArrayList<Available_checkout_methods> availableCheckoutMethodsArrayList;
    ArrayList<Available_delivery_methods> availableDeliveryMethodsArrayList;
    RelativeLayout rlCart, rlMenu, rlAddNewAddress, rlExistingAddress;
    String currency, addressId;
    String total_amount, sub_total;
    EditText etNote, etCoupon;
    LinearLayout linearView, lnTaxes, linearViewTaxes;
    Dialog dialog;
    RadioGroup radioGroup;
    RadioButton rbShowFirstAddress, rbLocalDelivery, rbFreeDelivery, rbInterntionalDelivery;
    String fees;
    String dummy = "0|";
    List<String> list;
    Spinner spnTip;
    String localValue = "0";
    String text = "";
    NetworkAndHideKeyBoard networkAndHideKeyBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemArrayList = ((ArrayList<ItemModel>) getIntent().getSerializableExtra("itemArrayList"));
        feesAndTaxes = ((ArrayList<FeesAndTaxes>) getIntent().getSerializableExtra("feesAndTaxes"));
        availableCheckoutMethodsArrayList = ((ArrayList<Available_checkout_methods>) getIntent().getSerializableExtra("availableCheckoutMethodsArrayList"));
        availableDeliveryMethodsArrayList = ((ArrayList<Available_delivery_methods>) getIntent().getSerializableExtra("availableDeliveryMethodsArrayList"));
        currency = getIntent().getStringExtra("currency");
        total_amount = getIntent().getStringExtra("total_amount");
        sub_total = getIntent().getStringExtra("sub_total");
        setContentView(R.layout.activity_order_now);
        fees = AppController.getInstance().useString("FEES");
        fees = dummy + fees;
        String str[] = fees.split("\\|");
        list = Arrays.asList(str);
        Log.i("", "");
        windowsCode();
        networkAndHideKeyBoard = new NetworkAndHideKeyBoard(this);
        initViews();
        getMerchantDetails();
        setView();
        ArrayAdapter aa = new ArrayAdapter(OrderNowActivity.this, R.layout.spinner_textview, str);
        aa.setDropDownViewResource(R.layout.custom_spinner);
        spnTip.setAdapter(aa);
        getAddressAPI();
        initListeners();
        rlCart.setVisibility(View.GONE);
    }

    private void getMerchantDetails() {
        JSONObject jsonObject = new JSONObject();
        Log.i("", "");
        final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.GET, BaseUrl.MERCHANT_DETAILS, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() == 0) {
                            Toast.makeText(OrderNowActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                    JSONObject objJson = jsonObject.optJSONObject("object");
                                    String merchantAddress = objJson.optString("description_point");
                                    tvPickup.setText(merchantAddress);
                                    Log.i("", "");

                                } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                } else {
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR1:", "");
//                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                return AppController.getInstance().getParams();
            }

        };
        AppController.getInstance().addToRequestQueue(loginResult);
    }

    private void setView() {
        if (AppController.getInstance().containsKey("available_pickup_methods")) {
            if (AppController.getInstance().useString("available_pickup_methods").equalsIgnoreCase("")) {
                rbShowFirstAddress.setVisibility(View.GONE);
                tvPickup.setVisibility(View.GONE);
                tvAddressAmount.setVisibility(View.GONE);
                rbLocalDelivery.setChecked(true);
            } else {
                rbShowFirstAddress.setVisibility(View.VISIBLE);
                tvPickup.setVisibility(View.VISIBLE);
                tvAddressAmount.setVisibility(View.VISIBLE);
                rbShowFirstAddress.setChecked(true);
            }
        } else {
            rbShowFirstAddress.setVisibility(View.GONE);
            tvPickup.setVisibility(View.GONE);
            tvAddressAmount.setVisibility(View.GONE);
            rbLocalDelivery.setChecked(true);
        }
        rbShowFirstAddress.setText(getResources().getString(R.string.pickup));
//        tvPickup.setText(merchantAddress);
        rbLocalDelivery.setText(AppController.getInstance().removeUnderScore(availableDeliveryMethodsArrayList.get(0).getName()));
        rbFreeDelivery.setText(AppController.getInstance().removeUnderScore(availableDeliveryMethodsArrayList.get(1).getName()));
        rbInterntionalDelivery.setText(AppController.getInstance().removeUnderScore(availableDeliveryMethodsArrayList.get(2).getName()));
        for (int i = 0; i < itemArrayList.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.header_layout, null);
            TextView lnPrice = view.findViewById(R.id.lnPrice);
            TextView lnItemName = view.findViewById(R.id.lnItemName);
            TextView lnQty = view.findViewById(R.id.lnQty);
            TextView lnMutiPrice = view.findViewById(R.id.lnMutiPrice);
            String variation = String.valueOf(itemArrayList.get(i).getVarianceAttribute());
            variation = variation.replaceAll("\\[", "").replaceAll("\\]", "");
            if (!variation.equals("No Variations")) {
                lnItemName.setText(itemArrayList.get(i).getItemName() + "\n(Spicy: " + variation + ")");
            } else {
                lnItemName.setText(itemArrayList.get(i).getItemName());
            }
            lnPrice.setText("$" + itemArrayList.get(i).getUnit_price());
            lnQty.setText("" + itemArrayList.get(i).getQty() + "X");
            final int minteger[] = {itemArrayList.get(i).getQty()};
            double priceDisply = Double.parseDouble(itemArrayList.get(i).getUnit_price() + "");
            double multiply = priceDisply * minteger[0];
            String mult = String.format("%.2f", multiply);
            lnMutiPrice.setText("$" + mult);
            linearView.addView(view);
        }
        tvTitle.setText(getResources().getString(R.string.placeorder));
        tvSubtotal.setText(getResources().getString(R.string.subtotal));
        tvSubPrice.setText("$" + sub_total);
        tvTotalamount.setText("$" + total_amount);
        tvDelivry.setText(getResources().getString(R.string.delivery));
        tvTipAmount.setText(getResources().getString(R.string.tip));
        tvAddressAmount.setText("$0");
        tvLocalAmount.setText("$" + availableDeliveryMethodsArrayList.get(0).getCost());
        tvFreeAmount.setText("$" + availableDeliveryMethodsArrayList.get(1).getCost());
        tvInternationalAmount.setText("$" + availableDeliveryMethodsArrayList.get(2).getCost());
        tvPaymentType.setText(availableCheckoutMethodsArrayList.get(0).getName());

        for (int i = 1; i < feesAndTaxes.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.taxes_layout, null);
            TextView lnItemName = view.findViewById(R.id.lnItemName);
            TextView lnMutiPrice = view.findViewById(R.id.lnMutiPrice);
            if (feesAndTaxes.get(i).getName().equalsIgnoreCase("Shippment Price") || feesAndTaxes.get(i).getName().equalsIgnoreCase("Shippment Tax")) {
                lnItemName.setText(AppController.getInstance().removeUnderScore(feesAndTaxes.get(i).getName()));
            } else {
                lnItemName.setText(AppController.getInstance().removeUnderScore(feesAndTaxes.get(i).getName()) + "(" + feesAndTaxes.get(i).getRate() + "%)");
            }
            double v = Double.parseDouble(feesAndTaxes.get(i).getAmount());
            lnMutiPrice.setText("$" + Math.round(v * 100.0) / 100.0);
            linearViewTaxes.addView(view);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (data != null) {
                addressId = data.getStringExtra("addressId");
                tvCreatedAddress.setText(data.getStringExtra("getUser_address"));
            } else {
                tvCreatedAddress.setText("No address added");
            }
        } else if (resultCode == 102) {
            if (data != null) {
                addressId = data.getStringExtra("addressId");
                tvCreatedAddress.setText(data.getStringExtra("getUser_address"));
            } else {
                tvCreatedAddress.setText("No address added");
            }
        }
    }

    private void getAddressAPI() {
        JSONObject jsonObject = new JSONObject();
        String access_token = AppController.getInstance().useString("access_token");
        String user_id = AppController.getInstance().useString("user_id");
        final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.GET, BaseUrl.GETADDRESS + "access_token=" + access_token + "&user_id=" + user_id, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        pDialog.dismiss();
                        if (response.length() == 0) {
                            Toast.makeText(OrderNowActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                    final JSONArray dataJsonArray = jsonObject.optJSONArray("data");
                                    if (dataJsonArray == null || dataJsonArray.length() == 0) {
                                        tvCreatedAddress.setText("Please Add a Delivery Address");

                                    } else if (dataJsonArray.length() >= 1) {
                                        List<AddressModel> myList = new ArrayList<>();
                                        for (int i = 0; i < dataJsonArray.length(); i++) {
                                            JSONObject jsonObj = dataJsonArray.getJSONObject(i);
                                            AddressModel addressModel = new AddressModel();
                                            String address_id = jsonObj.getString("address_id");
                                            String firstName = jsonObj.getString("firstName");
                                            String middleName = jsonObj.getString("middleName");
                                            String lastName = jsonObj.getString("lastName");
                                            String address1 = jsonObj.getString("address1");
                                            String address2 = jsonObj.getString("address2");
                                            String city = jsonObj.getString("city");
                                            String state = jsonObj.getString("state");
                                            String country = jsonObj.getString("country");
                                            String postalCode = jsonObj.getString("postalCode");
                                            String mobileNumber = jsonObj.getString("mobileNumber");
                                            String email = jsonObj.getString("email");
                                            String user_address = firstName + " " + middleName + " " + lastName + "\n" + address1 + ", " + address2 + ",\n" + city + ", " + state + " - " + postalCode + "\n" + country + "\n" + mobileNumber + "\n" + email;
                                            addressModel.setAddressDetail(address_id);
                                            addressModel.setUser_address(user_address);
                                            myList.add(addressModel);
                                        }
                                        tvCreatedAddress.setText(myList.get(0).getUser_address());
                                        addressId = myList.get(0).getAddressDetail();

                                    }
                                } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                    CustomToast.show(OrderNowActivity.this, getResources().getString(R.string.somethingwrong), true, getResources().getDrawable(R.drawable.red));
                                } else {
                                    CustomToast.show(OrderNowActivity.this, getResources().getString(R.string.serverError), true, getResources().getDrawable(R.drawable.red));
                                }
                            } catch (Exception e) {
//                                CustomToast.show(OrderNowActivity.this, e.toString(), true, getResources().getDrawable(R.drawable.red));
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

//                        Toast.makeText(OrderNowActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                return AppController.getInstance().getParams();
            }

        };
        AppController.getInstance().addToRequestQueue(loginResult);
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
        tvapplyCode.setOnClickListener(this);
        rlAddNewAddress.setOnClickListener(this);
        rlExistingAddress.setOnClickListener(this);
        btnContinueToBuy.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        spnTip.setOnItemSelectedListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlMenu:
                onBackPressed();
                break;
            case R.id.rlCart:
                Intent intent = new Intent(OrderNowActivity.this, MyCartActivity.class);
                startActivity(intent);
                break;
            case R.id.rlAddNewAddress:
                Intent intentCreateUpdateAddressActivity = new Intent(OrderNowActivity.this, CreateUpdateAddressActivity.class);
                intentCreateUpdateAddressActivity.putExtra("comesFrom", "OrderNow");
                startActivityForResult(intentCreateUpdateAddressActivity, 101);
                break;
            case R.id.rlExistingAddress:
                Intent intentExistingAddressActivity = new Intent(OrderNowActivity.this, MyAddressesActivity.class);
                intentExistingAddressActivity.putExtra("comesFrom", "OrderNow");
                startActivityForResult(intentExistingAddressActivity, 102);
                break;
            case R.id.btnContinueToBuy:
                validate();
                break;
            case R.id.tvapplyCode:
                couponValidate();

                break;
        }
    }

    private void couponValidate() {
        String coupon = etCoupon.getText().toString().trim();
        if (coupon.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.enterCoupon), Toast.LENGTH_SHORT).show();
        } else {
            coupon();
        }
    }

    private void coupon() {
        networkAndHideKeyBoard.hideSoftKeyboard();
        if (!networkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "Network is not Available", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
            JSONObject jsonObje = new JSONObject();
            try {
                jsonObje.put("form_id", "");
                jsonObje.put("user_id", AppController.getInstance().useString("user_id"));
                Map<String, String> fieldMap = new HashMap<>();
                fieldMap.put("bucketId", AppController.getInstance().useString("bucket"));
                fieldMap.put("rule", etCoupon.getText().toString().trim());
                jsonObje.put("fields", new JSONObject(fieldMap));

                String access_token = AppController.getInstance().useString("access_token");
                JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.POST, BaseUrl.COUPON + "access_token=" + access_token, jsonObje,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                pDialog.dismiss();
                                if (response.length() == 0) {
                                    Toast.makeText(OrderNowActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                }
//                                else {
//                                    try {
//                                        if (response.optString("request_status").equalsIgnoreCase("true")) {
//                                            JSONObject objectJson = response.optJSONObject("object");
//                                            String couponName = objectJson.optString("name");
//                                            objectJson.optString("description");
//                                            objectJson.optString("couponDiscountType");
//                                            double amountCoupon = objectJson.optDouble("amount");
//                                            objectJson.optString("minBenefit");
//                                            objectJson.optString("maxBenefit");
////                                            double val  = Double.parseDouble(tvTotalamount.getText().toString().replaceAll("\\$", ""));
//
//                                            double val1 = Double.parseDouble(total_amount);
//                                            val1 = val1 - amountCoupon;
//                                            tvTotalamount.setText("$" + val1);
//                                            tvapplyCode.setText("Remove");
//                                            removeCoupon(couponName);
//
//                                        } else if (response.optString("request_status").equalsIgnoreCase("false")) {
//                                            pDialog.dismiss();
//                                            pDialog.hide();
//                                            //JSONObject objJson = response.optJSONObject("object");
//                                            //Toast.makeText(OrderNowActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
//                                        }
//                                    } catch (Exception e) {
//                                        response.optString("request_status").equalsIgnoreCase("false");
//                                        pDialog.dismiss();
//                                        pDialog.hide();
//                                    }
//                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pDialog.dismiss();
                                pDialog.hide();
                                Toast.makeText(OrderNowActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() {
//                        Map<String, String> params = new HashMap<>();
//                        params.put("Content-Type", "application/json;");
                        //return params;
                        return AppController.getInstance().getParams();
                    }
                };
                AppController.getInstance().addToRequestQueue(loginResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeCoupon(String couponName) {
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
            try {
                jsonObject.put("form_id", "");
                jsonObject.put("user_id", AppController.getInstance().useString("user_id"));
                Map<String, String> fieldMap = new HashMap<>();
                fieldMap.put("bucketId", AppController.getInstance().useString("bucket"));
                fieldMap.put("rule", couponName);
                jsonObject.put("fields", new JSONObject(fieldMap));

                String access_token = AppController.getInstance().useString("access_token");
                JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.POST, BaseUrl.REMOVECOUPON + "access_token=" + access_token, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                pDialog.dismiss();
                                if (response.length() == 0) {
                                    Toast.makeText(OrderNowActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.toString());
                                        if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                            JSONObject objectJson = jsonObject.optJSONObject("object");

                                        } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                            pDialog.dismiss();
                                            pDialog.hide();
                                            JSONObject objJson = jsonObject.optJSONObject("object");
                                            Toast.makeText(OrderNowActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            pDialog.dismiss();
                                            pDialog.hide();
                                            JSONObject objJson = jsonObject.optJSONObject("object");
                                            Toast.makeText(OrderNowActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(OrderNowActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();
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


    private void validate() {
        String note = etNote.getText().toString().trim();
        if (tvTipAmount.getText().toString().equals(getResources().getString(R.string.tip))) {
            Toast.makeText(this, getResources().getString(R.string.selectTip), Toast.LENGTH_SHORT).show();
        } else if (tvCreatedAddress.getText().toString().equals("No Address added")) {
            Toast.makeText(this, getResources().getString(R.string.address), Toast.LENGTH_SHORT).show();
        } else {
            placeOrder();
        }
    }

    private void placeOrder() {
        Intent intentPayment = new Intent(OrderNowActivity.this, PaymentActivity.class);
        intentPayment.putExtra("availableCheckoutMethodsArrayList", availableCheckoutMethodsArrayList);
        intentPayment.putExtra("addressId", addressId);
        intentPayment.putExtra("currency", currency);
        intentPayment.putExtra("total", total_amount);
        intentPayment.putExtra("note", etNote.getText().toString());
        startActivity(intentPayment);
    }

    /*private void updateTip(String id) {
        final ProgressDialog pDialog = new ProgressDialog(OrderNowActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);
        pDialog.show();
        JSONObject jsonObject = new JSONObject();
        String access_token = AppController.getInstance().useString("access_token");
        Log.d("Access:", "" + access_token);
        try {
            jsonObject.put("form_id", "");
            jsonObject.put("user_id", AppController.getInstance().useString("user_id"));
            Map<String, String> fieldMap = new HashMap<>();
            fieldMap.put("bucketId", AppController.getInstance().useString("bucket"));
            fieldMap.put("taxId", id);
            fieldMap.put("taxRate", "1");
            jsonObject.put("fields", new JSONObject(fieldMap));

            JsonObjectRequest saveRequest = new JsonObjectRequest(Request.Method.POST, BaseUrl.UPDATE_TIP + "access_token=" + access_token, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("response Tag", response.toString());
                    pDialog.dismiss();
                    if (response.length() == 0) {
                        Toast.makeText(OrderNowActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                JSONObject objJson = jsonObject.optJSONObject("object");
                                objJson.optString("rate");
                                objJson.optString("id");
                                String name = objJson.optString("name");
                                tvTip.setText(name);
                            } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                CustomToast.show(OrderNowActivity.this, jsonObject.getString("cause"), true, getResources().getDrawable(R.drawable.yellowshape));
                            } else {
                                CustomToast.show(OrderNowActivity.this, getResources().getString(R.string.serverError), true, getResources().getDrawable(R.drawable.red));
                            }
                        } catch (Exception e) {
                            CustomToast.show(OrderNowActivity.this, e.toString(), true, getResources().getDrawable(R.drawable.red));
                        }
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();
                            Log.d("ERROR1:", "");
                            Toast.makeText(OrderNowActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json;");
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(saveRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void updateShippingMethod(String id) {
        JSONObject jsonObject = new JSONObject();
        String access_token = AppController.getInstance().useString("access_token");
        Log.d("Access:", "" + access_token);
        try {
            jsonObject.put("form_id", "");
            jsonObject.put("user_id", AppController.getInstance().useString("user_id"));
            Map<String, String> fieldMap = new HashMap<>();
            fieldMap.put("bucketId", AppController.getInstance().useString("bucket"));
            fieldMap.put("shippingId", id);
            jsonObject.put("fields", new JSONObject(fieldMap));
            String url = BaseUrl.UPDATE_SHIPPING_METHODS + "access_token=" + access_token;
            Log.i("", "" + url);
            JsonObjectRequest saveRequest = new JsonObjectRequest(Request.Method.POST, BaseUrl.UPDATE_SHIPPING_METHODS + "access_token=" + access_token, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("response Tag", response.toString());
                    if (response.length() == 0) {
                        Toast.makeText(OrderNowActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                JSONObject objJson = jsonObject.optJSONObject("object");
                                Double cost = objJson.optDouble("cost");
                                objJson.optString("name");
                                objJson.optString("id");
                                objJson.optString("key");
                                tvDelivryAmount.setText("$" + cost);
                                double val1 = Double.parseDouble(total_amount);
                                val1 = val1 + cost + result2;
                                double finalAmount = Math.round(val1 * 100.0) / 100.0;
                                tvTotalamount.setText("$" + finalAmount + "");
                                Log.i("", "");

                            } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                JSONObject objJson = jsonObject.optJSONObject("object");
                                Toast.makeText(OrderNowActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
                            } else {
                                JSONObject objJson = jsonObject.optJSONObject("object");
                                Toast.makeText(OrderNowActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                        }
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("ERROR1:", "");
                            Toast.makeText(OrderNowActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();

                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json;");
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(saveRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        rlMenu = findViewById(R.id.rlMenu);
        rlCart = findViewById(R.id.rlCart);
        rlAddNewAddress = findViewById(R.id.rlAddNewAddress);
        radioGroup = findViewById(R.id.radioGroup);
        rbShowFirstAddress = findViewById(R.id.rbShowFirstAddress);
        rbLocalDelivery = findViewById(R.id.rbLocalDelivery);
        rbFreeDelivery = findViewById(R.id.rbFreeDelivery);
        rbInterntionalDelivery = findViewById(R.id.rbInterntionalDelivery);
        rlExistingAddress = findViewById(R.id.rlExistingAddress);
        tvPickup = findViewById(R.id.tvPickup);
        tvapplyCode = findViewById(R.id.tvapplyCode);
        tvAddressAmount = findViewById(R.id.tvAddressAmount);
        tvLocalAmount = findViewById(R.id.tvLocalAmount);
        tvFreeAmount = findViewById(R.id.tvFreeAmount);
        tvInternationalAmount = findViewById(R.id.tvInternationalAmount);

        tvTitle = findViewById(R.id.tvTitle);
        tv_cart = findViewById(R.id.tv_cart);
        tvCreatedAddress = findViewById(R.id.tvCreatedAddress);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvSubQtuy = findViewById(R.id.tvSubQtuy);
        tvSubPrice = findViewById(R.id.tvSubPrice);
        tvDelivry = findViewById(R.id.tvDelivry);
        tvDelivryAmount = findViewById(R.id.tvDelivryAmount);
        tvTip = findViewById(R.id.tvTip);
        tvInternational = findViewById(R.id.tvInternational);
        tvFree = findViewById(R.id.tvFree);
        tvLocal = findViewById(R.id.tvLocal);
        tvPaymentType = findViewById(R.id.tvPaymentType);
        tvInteger_number = findViewById(R.id.tvInteger_number);
        tvTotal = findViewById(R.id.tvTotal);
        tvTotalamount = findViewById(R.id.tvTotalamount);
        tvTipAmount = findViewById(R.id.tvTipAmount);
        etNote = findViewById(R.id.etNote);
        etCoupon = findViewById(R.id.etCoupon);
        btnContinueToBuy = findViewById(R.id.btnContinueToBuy);
        linearView = findViewById(R.id.linearView);
        linearViewTaxes = findViewById(R.id.linearViewTaxes);
        lnTaxes = findViewById(R.id.lnTaxes);
        spnTip = findViewById(R.id.spnTip);

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rbShowFirstAddress:
                text = tvAddressAmount.getText().toString();
//                localValue = "0";
                tvDelivryAmount.setText(text);
                tvTotalamount.setText("$" + total_amount);

                break;
            case R.id.rbLocalDelivery:
                String id = availableDeliveryMethodsArrayList.get(0).getId();
                Log.i("", "" + id);
                updateShippingMethod(id);

                break;
            case R.id.rbFreeDelivery:
                String idFree = availableDeliveryMethodsArrayList.get(1).getId();
                Log.i("", "" + idFree);
                updateShippingMethod(idFree);
                break;
            case R.id.rbInterntionalDelivery:
                String idInternational = availableDeliveryMethodsArrayList.get(2).getId();
                Log.i("", "" + idInternational);
                updateShippingMethod(idInternational);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        double addAmount = 0;
        String selectedItem = parent.getItemAtPosition(position).toString();
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rbShowFirstAddress:
                Log.i("", "");
                addAmount = Double.parseDouble(tvAddressAmount.getText().toString().replaceAll("\\$", ""));
                break;
            case R.id.rbLocalDelivery:
                Log.i("", "");
                addAmount = Double.parseDouble(tvLocalAmount.getText().toString().replaceAll("\\$", ""));
                Log.i("", "");
                break;

            case R.id.rbFreeDelivery:
                Log.i("", "");
                addAmount = Double.parseDouble(tvFreeAmount.getText().toString().replaceAll("\\$", ""));
                break;

            case R.id.rbInterntionalDelivery:
                addAmount = Double.parseDouble(tvInternationalAmount.getText().toString().replaceAll("$", ""));
                Log.i("", "");
                break;
        }

        tvTipAmount.setText(selectedItem + "%");
        double x2 = Double.parseDouble(selectedItem);
        double val1 = Double.parseDouble(total_amount);
        result2 = (val1) * (x2 / 100);

        val1 = val1 + result2 + addAmount;
        double total1 = Math.round(val1 * 100.0) / 100.0;
        tvTotalamount.setText("$" + total1 + "");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}