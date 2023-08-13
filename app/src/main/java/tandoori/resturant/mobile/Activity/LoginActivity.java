package tandoori.resturant.mobile.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import tandoori.resturant.mobile.CustomToast.CustomToast;
import tandoori.resturant.mobile.ModelClass.Categories;
import tandoori.resturant.mobile.ModelClass.Products;
import tandoori.resturant.mobile.ModelClass.Variation;
import tandoori.resturant.mobile.R;
import tandoori.resturant.mobile.utils.BaseUrl;
import tandoori.resturant.mobile.utils.NetworkAndHideKeyBoard;
import tandoori.resturant.mobile.volly.AppController;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    EditText etUserName;
    //ShowHidePasswordEditText etPassword;
    EditText etPassword;
    Button btLogin;
    TextView tvSignUpHere, tvForgetPassword;
    LinearLayout linear;
    ImageView imGooglePlus, imFacebook;
    NetworkAndHideKeyBoard networkAndHideKeyBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        windowsCode();
        networkAndHideKeyBoard = new NetworkAndHideKeyBoard(this);
        initViews();
        initListeners();
    }

    private void initListeners() {
        btLogin.setOnClickListener(this);
        tvSignUpHere.setOnClickListener(this);
    }

    private void windowsCode() {
        Window window = getWindow();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.green));
        }
    }

    private void initViews() {
        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);

        btLogin = findViewById(R.id.btLogin);

        tvSignUpHere = findViewById(R.id.tvSignUpHere);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);

        imFacebook = findViewById(R.id.imFacebook);
        imGooglePlus = findViewById(R.id.imGooglePlus);
        linear = findViewById(R.id.linear);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btLogin:
                validate();
                break;
            case R.id.tvSignUpHere:
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void validate() {
        String username = etUserName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (username.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            etUserName.setError(getResources().getString(R.string.enterValidEmail));
            return;
        } else if (password.isEmpty()) {
            etPassword.setError(getResources().getString(R.string.passworderror));
            return;
        } else if (!networkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
            CustomToast.show(this, getResources().getString(R.string.networkError), true, getResources().getDrawable(R.drawable.shape));
        } else {
            login();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etUserName.getWindowToken(), 0);
        }
    }

    private void login() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Authorizing.....");
        pDialog.show();
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user", etUserName.getText().toString().trim());
            jsonObject.put("password", etPassword.getText().toString().trim());
            final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.POST, BaseUrl.LOGINUSER, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (response.length() == 0) {
                                Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        if (objJson.optString("status").equalsIgnoreCase("true")) {
                                            AppController.getInstance().saveString("expires_in", objJson.optString("expires_in"));
                                            AppController.getInstance().saveString("access_token", objJson.optString("access_token"));
                                            AppController.getInstance().saveString("user_id", etUserName.getText().toString().trim());
                                            AppController.getInstance().saveString("requestId", jsonObject.optString("requestId"));
                                            getMerchant(pDialog);
                                        }
                                    } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                        pDialog.dismiss();
                                        pDialog.hide();
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        Toast.makeText(LoginActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();

                                    } else {
                                        pDialog.dismiss();
                                        pDialog.hide();
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        Toast.makeText(LoginActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    pDialog.dismiss();
                                    pDialog.hide();
                                    Log.i("", "");
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
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

    private void getMerchant(final ProgressDialog pDialog) {
        JSONObject jsonObject = new JSONObject();
        String access_token = AppController.getInstance().useString("access_token");
        Log.d("Access:", "" + access_token);
        final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.GET, BaseUrl.MERCHANT + "access_token=" + access_token, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() == 0) {
                            Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                    JSONObject objJson = jsonObject.optJSONObject("object");
                                    AppController.getInstance().saveString("STATIC_RESOURCE_ENDPOINT", objJson.optString("STATIC_RESOURCE_ENDPOINT"));
                                    AppController.getInstance().saveString("STATIC_RESOURCE_SUFFIX", objJson.optString("STATIC_RESOURCE_SUFFIX"));
                                    AppController.getInstance().saveString("FEES", objJson.optString("FEES"));
                                    AppController.getInstance().saveString("STATIC_RESOURCE_CATEGORIES_PREFIX", objJson.optString("STATIC_RESOURCE_CATEGORIES_PREFIX"));
                                    AppController.getInstance().saveString("MERCHANT_ID", objJson.optString("MERCHANT_ID"));
                                    String URL = AppController.getInstance().useString("STATIC_RESOURCE_ENDPOINT") +
                                            AppController.getInstance().useString("STATIC_RESOURCE_CATEGORIES_PREFIX") +
                                            AppController.getInstance().useString("MERCHANT_ID") + AppController.getInstance().useString("STATIC_RESOURCE_SUFFIX");
                                    AppController.getInstance().saveString("URL", URL);
                                    getCategoryProduct(URL, pDialog);
                                    AppController.getInstance().saveString("requestId", jsonObject.optString("requestId"));
                                } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                    CustomToast.show(LoginActivity.this, jsonObject.getString("cause"), true, getResources().getDrawable(R.drawable.yellowshape));
                                } else {
                                    CustomToast.show(LoginActivity.this, getResources().getString(R.string.serverError), true, getResources().getDrawable(R.drawable.red));
                                }
                            } catch (Exception e) {
                                CustomToast.show(LoginActivity.this, e.toString(), true, getResources().getDrawable(R.drawable.red));
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        pDialog.hide();
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


    private void getCategoryProduct(String URL, final ProgressDialog pDialog) {
        networkAndHideKeyBoard.hideSoftKeyboard();
        if (!networkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "Network is not Available", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pDialog.dismiss();
                    pDialog.hide();
                    if (!response.equalsIgnoreCase(null) || !response.equalsIgnoreCase("")) {
                        try {
                            ArrayList<Categories> categoryArrayList = new ArrayList<>();
                            JSONObject responseJsonObject = new JSONObject(response);
                            if (responseJsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                JSONArray dataJsonArray = responseJsonObject.optJSONArray("data");
                                for (int i = -1; i < dataJsonArray.length(); i++) {
                                    Categories categories = new Categories();
                                    if (i == -1) {
                                        categories.setSlug("home");
                                        categories.setCategoryName("All");
                                    } else {
                                        JSONObject jsonObject = dataJsonArray.getJSONObject(i);
                                        JSONObject categotyObject = jsonObject.optJSONObject("category");
                                        categories.setDataId(categotyObject.getString("categoryId"));
                                        categories.setCategoryName(categotyObject.getString("categoryName"));
                                        categories.setBundleId(categotyObject.getString("bundleId"));
                                        categories.setSlug(categotyObject.getString("slug"));
                                        categories.setLongDescription(categotyObject.getString("longDescription"));

                                        ArrayList<Products> productsArrayList = new ArrayList<>();

                                        JSONArray productsArray = jsonObject.optJSONArray("products");
                                        for (int products = 0; products < productsArray.length(); products++) {
                                            Products productsModel = new Products();
                                            JSONObject productObject = productsArray.optJSONObject(products);
                                            productsModel.setProductId(productObject.optString("productId"));
                                            productsModel.setName(productObject.optString("name"));
                                            productsModel.setSlug(productObject.optString("slug"));
                                            productsModel.setShortDescription(productObject.optString("shortDescription"));
                                            productsModel.setDescriptions(productObject.optString("descriptions"));

                                            ArrayList<String> imageArrayList = new ArrayList<>();
                                            for (int images = 0; images < productObject.optJSONArray("image").length(); images++) {
                                                imageArrayList.add(productObject.optJSONArray("image").get(images).toString());
                                            }
                                            productsModel.setImage(imageArrayList);

                                            ArrayList<String> varianceAttribute = new ArrayList<>();
                                            JSONObject varianceAttributeJsonObject = new JSONObject(productObject.optString("varianceAttribute"));

                                            if (varianceAttributeJsonObject.length() == 0) {
                                                varianceAttribute.add("No Variations");
                                            } else {
                                                JSONArray jsonArrayVarianceAttribute = varianceAttributeJsonObject.optJSONObject("Group").optJSONObject("Category").optJSONArray("Spicy");
                                                for (int varianceAttributes = 0; varianceAttributes < jsonArrayVarianceAttribute.length(); varianceAttributes++) {
                                                    varianceAttribute.add(jsonArrayVarianceAttribute.get(varianceAttributes).toString());
                                                }
                                                ArrayList<Variation> variationArrayList = new ArrayList<>();
                                                for (int variation = 0; variation < productObject.optJSONArray("variations").length(); variation++) {
                                                    Variation variationModel = new Variation();
                                                    JSONObject jsonObjectVariations = productObject.optJSONArray("variations").optJSONObject(variation);
                                                    variationModel.setProductVariationId(jsonObjectVariations.optString("productVariationId"));
                                                    variationModel.setName(jsonObjectVariations.optString("name"));
                                                    variationModel.setSlug(jsonObjectVariations.optString("slug"));
                                                    variationModel.setShortDescription(jsonObjectVariations.optString("shortDescription"));
                                                    variationModel.setDescriptions(jsonObjectVariations.optString("descriptions"));
                                                    ArrayList<String> variationImagesArraylList = new ArrayList<>();
                                                    for (int images = 0; images < jsonObjectVariations.optJSONArray("image").length(); images++) {
                                                        variationImagesArraylList.add(jsonObjectVariations.optJSONArray("image").get(images).toString());
                                                    }
                                                    variationModel.setImage(variationImagesArraylList);
                                                    JSONObject variationAttributeJsonObject = new JSONObject(jsonObjectVariations.optString("varianceAttribute"));
                                                    ArrayList<String> variationAttribute = new ArrayList<>();
                                                    JSONArray jsonArrayVariationAttribute = variationAttributeJsonObject.optJSONObject("Group").optJSONObject("Category").optJSONArray("Spicy");
                                                    for (int variationAttribues = 0; variationAttribues < jsonArrayVariationAttribute.length(); variationAttribues++) {
                                                        variationAttribute.add(jsonArrayVariationAttribute.get(variationAttribues).toString());
                                                    }
                                                    variationModel.setVarianceAttribute(variationAttribute);
                                                    variationModel.setPrice(jsonObjectVariations.optDouble("price"));
                                                    variationArrayList.add(variationModel);
                                                }
                                                productsModel.setVariationArrayList(variationArrayList);
                                            }
                                            productsModel.setVarianceAttribute(varianceAttribute);
                                            productsModel.setPrice(productObject.optDouble("price"));
                                            productsArrayList.add(productsModel);
                                        }
                                        categories.setProductsArrayList(productsArrayList);
                                    }
                                    categoryArrayList.add(categories);
                                }
                                AppController.getInstance().setCategories(categoryArrayList);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, responseJsonObject.toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Server Error.Please Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                    pDialog.hide();
                    Log.i("", "");
                }
            }) {

            };
            AppController.getInstance().addToRequestQueue(stringRequest);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LoginActivity.this.finish();
    }
}
