package tandoori.resturant.mobile.volly;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tandoori.resturant.mobile.InternetCheker.InternetAvailabilityChecker;
import tandoori.resturant.mobile.ModelClass.Categories;
import tandoori.resturant.mobile.ModelClass.Products;


public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;
    ProgressDialog progressDialog;
    ArrayList<Categories> categories = new ArrayList<>();
    ArrayList<Products> products = new ArrayList<>();
    Map<String, String> params = new HashMap<>();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private SharedPreferences sharedPreferences;
    private boolean action = false;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        InternetAvailabilityChecker.init(this);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack());
        sharedPreferences = getApplicationContext().getSharedPreferences("TiffinIndiaCafe", 0);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        InternetAvailabilityChecker.getInstance().removeAllInternetConnectivityChangeListeners();
    }

    public Map<String, String> getParams() {
        params.put("Content-Type", "application/json;");
        params.put("key", "tiffin");
        params.put("secret", "tiffin");
        return params;
    }

    public ArrayList<Categories> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Categories> categories) {
        this.categories.clear();
        this.categories = categories;
    }

    public ArrayList<Products> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Products> products) {
        this.products.clear();
        this.products = products;
    }

    public void saveString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    public String removeUnderScore(String stringWithUnderScore) {
        String stringWithOutUnderScore = stringWithUnderScore.replaceAll("_", " ");
        return stringWithOutUnderScore;
    }

    public boolean containsKey(String key) {
        if (sharedPreferences.contains(key)) {
            return true;
        } else {
            return false;
        }
    }

    public void showProgressDialog(Context mContext, String message) {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null || progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.hide();
            progressDialog = null;
        }
    }

    public void clearSharedPreferences() {
        sharedPreferences.edit().clear().apply();
    }

    public String useString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void showDialog(Context context, String title, String message) {

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                action = true;
            }
        });
        alertDialog.show();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
//        req.setTag(TAG);
        req.setShouldCache(false);
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        cache.clear();
        req.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


}
