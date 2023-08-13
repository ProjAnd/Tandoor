package tandoori.resturant.mobile.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import tandoori.resturant.mobile.CustomToast.CustomToast;
import tandoori.resturant.mobile.InternetCheker.InternetAvailabilityChecker;
import tandoori.resturant.mobile.InternetCheker.InternetConnectivityListener;
import tandoori.resturant.mobile.ModelClass.Categories;
import tandoori.resturant.mobile.ModelClass.Products;
import tandoori.resturant.mobile.ModelClass.Variation;
import tandoori.resturant.mobile.R;
import tandoori.resturant.mobile.utils.BaseUrl;
import tandoori.resturant.mobile.utils.NetworkAndHideKeyBoard;
import tandoori.resturant.mobile.volly.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class SplashActivity extends AppCompatActivity implements InternetConnectivityListener {
    ImageView imSplash;
    Handler handler = new Handler();
    NetworkAndHideKeyBoard networkAndHideKeyBoard;
    private InternetAvailabilityChecker mInternetAvailabilityChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        mInternetAvailabilityChecker.addInternetConnectivityListener(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        networkAndHideKeyBoard = new NetworkAndHideKeyBoard(this);
        initViews();
        getMerchantToken();
        if (AppController.getInstance().containsKey("URL")) {
            if (AppController.getInstance().useString("URL").equalsIgnoreCase("")) {
                handlerToLoginActivity();
            } else {
                getCategoryProduct();
            }
        } else {
            handlerToLoginActivity();
        }
    }

    private void getMerchantToken() {
//        final ProgressDialog pDialog = new ProgressDialog(this);
//        pDialog.setMessage("Loading....");
//        pDialog.setCancelable(false);
//        pDialog.setIndeterminate(true);
//        pDialog.show();
        JSONObject jsonObject = new JSONObject();

            final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.POST, BaseUrl.GETMERCHANTTOKEN, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                          //  pDialog.dismiss();
                            if (response.length() == 0) {
                                Toast.makeText(SplashActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        if (objJson.optString("status").equalsIgnoreCase("true")) {
                                            AppController.getInstance().saveString("expires_in", objJson.optString("expires_in"));
                                            AppController.getInstance().saveString("access_token", objJson.optString("access_token"));

                                        } else {
                                            CustomToast.show(SplashActivity.this, objJson.getString("error"), true, getResources().getDrawable(R.drawable.yellowshape));
                                        }
                                        AppController.getInstance().saveString("requestId", jsonObject.optString("requestId"));

                                    } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                        CustomToast.show(SplashActivity.this, jsonObject.getString("error"), true, getResources().getDrawable(R.drawable.yellowshape));
                                    } else {
                                        CustomToast.show(SplashActivity.this, getResources().getString(R.string.serverError), true, getResources().getDrawable(R.drawable.red));
                                    }
                                } catch (Exception e) {
                                    CustomToast.show(SplashActivity.this, e.toString(), true, getResources().getDrawable(R.drawable.red));
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        //    pDialog.dismiss();
                            Log.d("ERROR1:", "");
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
                            alertDialog.setMessage(getResources().getString(R.string.serverError));
                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog.show();                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    return AppController.getInstance().getParams();
                }
            };
            AppController.getInstance().addToRequestQueue(loginResult);


    }

    public void handlerToLoginActivity() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, BaseUrl.handlerTime);
    }


    private void getCategoryProduct() {
        networkAndHideKeyBoard.hideSoftKeyboard();
        if (!networkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "Network is not Available", Toast.LENGTH_SHORT).show();
        } else {
            String acces=AppController.getInstance().useString("access_token");
            String url=AppController.getInstance().useString("URL");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, AppController.getInstance().useString("URL"), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
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
                                                    for (int images = 0; images < jsonObjectVariations.optJSONArray("image").length(); images++)
                                                    {
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
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SplashActivity.this, responseJsonObject.toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(SplashActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SplashActivity.this, "Server Error.Please Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("", "");
                }
            }) {

            };
            AppController.getInstance().addToRequestQueue(stringRequest);
        }
    }
    private void initViews() {
        imSplash = findViewById(R.id.imSplash);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (isConnected) {
//            mTvStatus.setText("connected");
        } else {
//            mTvStatus.setText("not connected");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(this);
    }
}
