package tandoori.resturant.mobile.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import tandoori.resturant.mobile.ModelClass.Available_checkout_methods;
import tandoori.resturant.mobile.R;
import tandoori.resturant.mobile.utils.BaseUrl;
import tandoori.resturant.mobile.utils.NetworkAndHideKeyBoard;
import tandoori.resturant.mobile.volly.AppController;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout rlCart, rlMenu;
    CardInputWidget cardInputWidget;
    Stripe stripe;
    Token tok;
    Button button;
    TextView tvTotal, tvTitle;
    ArrayList<Available_checkout_methods> availableCheckoutMethodsArrayList;
    String addressId, currency, total, note;
    NetworkAndHideKeyBoard networkAndHideKeyBoard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        availableCheckoutMethodsArrayList = ((ArrayList<Available_checkout_methods>) getIntent().getSerializableExtra("availableCheckoutMethodsArrayList"));
        addressId = getIntent().getStringExtra("addressId");
        currency = getIntent().getStringExtra("currency");
        total = getIntent().getStringExtra("total");
        note = getIntent().getStringExtra("note");
        windowsCode();
        networkAndHideKeyBoard= new NetworkAndHideKeyBoard(this);
        initViews();
        initListeners();
        rlCart.setVisibility(View.GONE);
        tvTitle.setText("Pay ( $" + total + ")");


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

        rlMenu.setOnClickListener(this);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlMenu:
                onBackPressed();
                break;

            case R.id.submitButton:
                Card cardToSave = cardInputWidget.getCard();
                final ProgressDialog progressDialog = new ProgressDialog(PaymentActivity.this);
                progressDialog.setMessage("Payment...");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                if (cardToSave == null) {
                    Toast.makeText(this, "Invalid Card Data", Toast.LENGTH_SHORT).show();
                } else {
                    cardToSave.setCurrency(currency);
                }
                stripe.createToken(cardToSave, "pk_test_IkvHgAPOhO9A64esd1VtBukj", new TokenCallback() {

                    public void onSuccess(Token token) {
                        // TODO: Send Token information to your backend to initiate a charge
//                        Toast.makeText(getApplicationContext(), "Token created: " + token.getId(), Toast.LENGTH_LONG).show();
                        tok = token;
                        String id = token.getId();
                        Log.e("", "" + id);
                        double demo = Double.parseDouble(total);
                        if (demo < 30.0) {
                            Toast.makeText(PaymentActivity.this, "Required minimum order amount $30.0", Toast.LENGTH_SHORT).show();
                        } else {
                            placeOrder(id, progressDialog);
                        }
                    }

                    public void onError(Exception error) {
                        Log.d("Stripe", error.getLocalizedMessage());
                    }
                });
                break;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void placeOrder(String id, final ProgressDialog progressDialog) {
        networkAndHideKeyBoard.hideSoftKeyboard();
        if (!networkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "Network is not Available", Toast.LENGTH_SHORT).show();
        } else {
            JSONObject jsonObject = new JSONObject();
            String access_token = AppController.getInstance().useString("access_token");
            Log.d("Access:", "" + access_token);
            try {
                jsonObject.put("form_id", "");
                jsonObject.put("user_id", AppController.getInstance().useString("user_id"));
                Map<String, String> fieldMap = new HashMap<>();
                fieldMap.put("bucketId", AppController.getInstance().useString("bucket"));
                fieldMap.put("addressId", addressId);
                fieldMap.put("notes", note);
                fieldMap.put("orderDate", "");
                fieldMap.put("orderTime", "");
                fieldMap.put("paymentType", availableCheckoutMethodsArrayList.get(0).getNameKey());
                fieldMap.put("instrumentMode", "cc");
                fieldMap.put("gatewayId", availableCheckoutMethodsArrayList.get(0).getId());
                fieldMap.put("cardToken", id);
                jsonObject.put("fields", new JSONObject(fieldMap));
                JsonObjectRequest saveRequest = new JsonObjectRequest(Request.Method.POST, BaseUrl.PLACEORDER + "access_token=" + access_token, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response Tag", response.toString());
                        progressDialog.dismiss();
                        progressDialog.hide();
                        if (response.length() == 0) {
                            Toast.makeText(PaymentActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                    JSONObject objJson = jsonObject.optJSONObject("object");
                                    objJson.optString("orderId");
                                    Intent intent = new Intent(PaymentActivity.this, OrderHistoryActivity.class);
                                    startActivity(intent);
                                    finish();
                                    AppController.getInstance().saveString("orderId", objJson.optString("orderId"));
                                    AppController.getInstance().remove("bucket");

                                    Log.i("", "");
                                } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                    progressDialog.dismiss();
                                    progressDialog.hide();
                                    JSONObject objJson = jsonObject.optJSONObject("object");
                                    Toast.makeText(PaymentActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
                                } else {
                                    progressDialog.dismiss();
                                    progressDialog.hide();
                                    JSONObject objJson = jsonObject.optJSONObject("object");
                                    Toast.makeText(PaymentActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();                                }
                            } catch (Exception e) {
                                progressDialog.dismiss();
                                progressDialog.hide();
                                                 }
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                progressDialog.hide();
                                Log.d("ERROR1:", "");
                                Toast.makeText(PaymentActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();
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

    }

    private void initViews() {
        rlMenu = findViewById(R.id.rlMenu);
        rlCart = findViewById(R.id.rlCart);
        cardInputWidget = findViewById(R.id.cardInputWidget);
        button = findViewById(R.id.submitButton);
        tvTotal = findViewById(R.id.tvTotal);
        tvTitle = findViewById(R.id.tvTitle);

        stripe = new Stripe(this, "pk_test_IkvHgAPOhO9A64esd1VtBukj");


    }

}
