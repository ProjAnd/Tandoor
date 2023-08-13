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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import tandoori.resturant.mobile.CustomToast.CustomToast;
import tandoori.resturant.mobile.R;
import tandoori.resturant.mobile.utils.BaseUrl;
import tandoori.resturant.mobile.utils.NetworkAndHideKeyBoard;
import tandoori.resturant.mobile.volly.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyAccountActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout rlMenu, rlCart, rlEdit;
    TextView tvUserName, tvEmail, tvMobileNumber, tv_cart, tvTitle;
    LinearLayout lnEdit;
    Button btnSave;
    NetworkAndHideKeyBoard networkAndHideKeyBoard;
    EditText etName, etMiddleName, etLastName, etPhone_number, etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        windowsCode();
        networkAndHideKeyBoard = new NetworkAndHideKeyBoard(this);
        initViews();
        rlCart.setVisibility(View.GONE);
        tv_cart.setVisibility(View.GONE);
        initListeners();
        etEmail.setEnabled(false);
        getProfie();
    }

    private void windowsCode() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.green));
        }
    }

    private void getProfie() {
        networkAndHideKeyBoard.hideSoftKeyboard();
        if (!networkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "Network is not Available", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Please Wait while we fetching your saved data.....");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
            JSONObject jsonObject = new JSONObject();
            String access_token = AppController.getInstance().useString("access_token");
            String user_id = AppController.getInstance().useString("user_id");
            final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.GET, BaseUrl.PROFILE + "access_token=" + access_token + "&user_id=" + user_id, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.dismiss();
                            if (response.length() == 0) {
                                Toast.makeText(MyAccountActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        String user_id = objJson.optString("user_id");
                                        String userName = objJson.optString("userName");
                                        String email = objJson.optString("email");
                                        String primaryPhone = objJson.optString("primaryPhone");
                                        String firstName = objJson.optString("firstName");
                                        String lastName = objJson.optString("lastName");
                                        String middleName = objJson.optString("middleName");
                                        AppController.getInstance().saveString("FIRSTName", firstName);
                                        AppController.getInstance().saveString("LastName", lastName);
                                        AppController.getInstance().saveString("user_id", email);
                                        etEmail.setText(email);
                                        etName.setText(firstName);
                                        etLastName.setText(lastName);
                                        etPhone_number.setText(primaryPhone);
                                        etMiddleName.setText(middleName);

                                        AppController.getInstance().saveString("requestId", jsonObject.optString("requestId"));

                                    } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                        pDialog.dismiss();
                                        pDialog.hide();
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        Toast.makeText(MyAccountActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
                                    } else {
                                        pDialog.dismiss();
                                        pDialog.hide();
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        Toast.makeText(MyAccountActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MyAccountActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
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

    private void initListeners() {
        rlCart.setOnClickListener(this);
        rlMenu.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    private void initViews() {
        rlMenu = findViewById(R.id.rlMenu);
        rlCart = findViewById(R.id.rlCart);
        lnEdit = findViewById(R.id.lnEdit);
        btnSave = findViewById(R.id.btnSave);

        etName = findViewById(R.id.etName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etLastName = findViewById(R.id.etLastName);
        etPhone_number = findViewById(R.id.etPhone_number);
        etEmail = findViewById(R.id.etEmail);

        tv_cart = findViewById(R.id.tv_cart);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.updated));
        tv_cart.setText(AppController.getInstance().useString("Size"));


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlMenu:
                onBackPressed();
                break;
            case R.id.rlCart:
                Intent intent = new Intent(MyAccountActivity.this, MyCartActivity.class);
                startActivity(intent);
                break;
            case R.id.btnSave:
                editProfile();
                break;
        }
    }

    private void editProfile() {
        networkAndHideKeyBoard.hideSoftKeyboard();
        if (!networkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "Network is not Available", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Updating...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
            JSONObject jsonObject = new JSONObject();
            String access_token = AppController.getInstance().useString("access_token");
            try {
                jsonObject.put("form_id", "");
                jsonObject.put("user_id", AppController.getInstance().useString("user_id"));
                Map<String, String> fieldMap = new HashMap<>();
                fieldMap.put("email", etEmail.getText().toString().trim());
                fieldMap.put("primaryPhone", etPhone_number.getText().toString().trim());
                fieldMap.put("firstName", etName.getText().toString().trim());
                fieldMap.put("middleName", etMiddleName.getText().toString().trim());
                fieldMap.put("lastName", etLastName.getText().toString().trim());
                jsonObject.put("fields", new JSONObject(fieldMap));
                Log.i("", "");
                final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.POST, BaseUrl.PROFILE + "access_token=" + access_token, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                pDialog.dismiss();
                                if (response.length() == 0) {
                                    Toast.makeText(MyAccountActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.toString());
                                        if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                            JSONObject objJson = jsonObject.optJSONObject("object");
                                            String firstName = objJson.getString("firstName");
                                            String middleName = objJson.getString("middleName");
                                            String lastName = objJson.getString("lastName");
                                            String primaryPhone = objJson.getString("primaryPhone");
                                            String email = objJson.getString("email");
                                            AppController.getInstance().remove("FIRSTName");
                                            AppController.getInstance().remove("LastName");
                                            AppController.getInstance().remove("user_id");

                                            AppController.getInstance().saveString("FIRSTName", firstName);
                                            AppController.getInstance().saveString("LastName", lastName);
                                            AppController.getInstance().saveString("user_id", email);
                                            Toast.makeText(MyAccountActivity.this, getResources().getString(R.string.successfully), Toast.LENGTH_SHORT).show();
                                        } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                            pDialog.dismiss();
                                            pDialog.hide();
                                            JSONObject objJson = jsonObject.optJSONObject("object");
                                            Toast.makeText(MyAccountActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            pDialog.dismiss();
                                            pDialog.hide();
                                            JSONObject objJson = jsonObject.optJSONObject("object");
                                            Toast.makeText(MyAccountActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(MyAccountActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MyAccountActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}
