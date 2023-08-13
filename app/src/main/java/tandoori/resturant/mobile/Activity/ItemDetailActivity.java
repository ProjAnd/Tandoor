package tandoori.resturant.mobile.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tandoori.resturant.mobile.ModelClass.AddressModel;
import tandoori.resturant.mobile.ModelClass.Products;
import tandoori.resturant.mobile.ModelClass.Variation;
import tandoori.resturant.mobile.R;
import tandoori.resturant.mobile.utils.BaseUrl;
import tandoori.resturant.mobile.utils.NetworkAndHideKeyBoard;
import tandoori.resturant.mobile.volly.AppController;

import static java.lang.Math.max;

public class ItemDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MAX_LINES = 1;
    ArrayList<Products> productsArrayList = new ArrayList<>();
    TextView tv_cart;
    int Position;
    private RelativeLayout rlMenu, rlCart;
    private ViewPager viewPager;
    TextView tvTitle;
    NetworkAndHideKeyBoard networkAndHideKeyBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productsArrayList.addAll((ArrayList<Products>) getIntent().getSerializableExtra("productArrayList"));
        setContentView(R.layout.activity_item_detail);
        windowsCode();
        networkAndHideKeyBoard= new NetworkAndHideKeyBoard(this);
        initViews();
        if (AppController.getInstance().containsKey("bucket")) {
            if (AppController.getInstance().useString("bucket").equalsIgnoreCase("")) {
                tv_cart.setVisibility(View.GONE);
            } else {
                tv_cart.setText(AppController.getInstance().useString("Size"));
            }
        } else {
            tv_cart.setVisibility(View.GONE);
        }
        initListeners();
        //AndroidLikeButton androidLikeButton = findViewById(R.id.myView);
        //androidLikeButton.setCurrentlyLiked(false);
//
//        androidLikeButton.setOnLikeEventListener(new AndroidLikeButton.OnLikeEventListener() {
//            @Override
//            public void onLikeClicked(AndroidLikeButton androidLikeButton) {
//            }
//
//            @Override
//            public void onUnlikeClicked(AndroidLikeButton androidLikeButton) {
//            }
//        });
//        tvItemName.setText(getResources().getString(R.string.itemName));
//        tvItemDescription.setText(getResources().getString(R.string.itemDescription));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i("", "");
                Position = position;
                tvTitle.setText(productsArrayList.get(position).getName());
                //spinnerIndex = 0;
            }

            @Override
            public void onPageSelected(int position) {
                Log.i("", "");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initListeners() {
        rlMenu.setOnClickListener(this);
        rlCart.setOnClickListener(this);
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        rlMenu = findViewById(R.id.rlMenu);
        rlCart = findViewById(R.id.rlCart);
        tv_cart = findViewById(R.id.tv_cart);
        tvTitle = findViewById(R.id.tvTitle);
        Position = getIntent().getIntExtra("position", 0);
        CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(this, productsArrayList);
        viewPager.setAdapter(mCustomPagerAdapter);
        viewPager.setCurrentItem(getIntent().getIntExtra("position", 0), true);
    }

    private void windowsCode() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.green));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlMenu:
                onBackPressed();
                break;
            case R.id.rlCart:
                Intent intent = new Intent(ItemDetailActivity.this, MyCartActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void addToCart(String string, String qty, String productVariationId, String productId) {
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
            String access_token = AppController.getInstance().useString("access_token");
            Log.d("Access:", "" + access_token);
            try {
                jsonObject.put("form_id", "");
                jsonObject.put("user_id", AppController.getInstance().useString("user_id"));
                Map<String, String> fieldMap = new HashMap<>();
                fieldMap.put("bucketId", "");
                fieldMap.put("productId", productId);
                if (string.equalsIgnoreCase("No Variations")) {
                    fieldMap.put("productVariationId", "");
                } else {
                    fieldMap.put("productVariationId", productVariationId);
                }
                fieldMap.put("quantity", qty);
                jsonObject.put("fields", new JSONObject(fieldMap));
                JsonObjectRequest saveRequest = new JsonObjectRequest(Request.Method.POST, BaseUrl.ADDTOCART + "access_token=" + access_token, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response Tag", response.toString());
                        pDialog.dismiss();
                        if (response.length() == 0) {
                            Toast.makeText(ItemDetailActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                    JSONObject objJson = jsonObject.optJSONObject("object");
                                    String id = objJson.optString("bucket");
                                    AppController.getInstance().saveString("bucket", objJson.optString("bucket"));
                                    AppController.getInstance().saveString("products", objJson.optString("products"));
                                    AppController.getInstance().saveString("requestId", jsonObject.optString("requestId"));
                                    Intent intent = new Intent(ItemDetailActivity.this, MyCartActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                    pDialog.dismiss();
                                    pDialog.hide();
                                    JSONObject objJson = jsonObject.optJSONObject("object");
                                    Toast.makeText(ItemDetailActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
                                } else {
                                    pDialog.dismiss();
                                    pDialog.hide();
                                    JSONObject objJson = jsonObject.optJSONObject("object");
                                    Toast.makeText(ItemDetailActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                pDialog.dismiss();
                                pDialog.hide();                            }
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pDialog.dismiss();
                                pDialog.dismiss();
                                Toast.makeText(ItemDetailActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
//                            Toast.makeText(ItemDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
    }



    public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

        List<AddressModel> myList;
        Context mContext;

        public AddressAdapter(Context mContext, List<AddressModel> myList) {
            this.mContext = mContext;
            this.myList = myList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.custom_address_view, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final AddressModel am = myList.get(position);
            holder.heading.setText(am.getAddressType());
            holder.myAddress.setText(am.getUser_address());
            holder.myLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    AppController.getInstance().saveString("address_id", am.getAddressDetail());
//                    AppController.getInstance().saveString("user_address",am.getUser_address());
                    Intent intent = new Intent(mContext, OrderNowActivity.class);
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return myList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView heading, myAddress;
            LinearLayout myLayout;

            public ViewHolder(View itemView) {
                super(itemView);

                heading = itemView.findViewById(R.id.heading);
                myAddress = itemView.findViewById(R.id.address);
                myLayout = itemView.findViewById(R.id.myLayout);
            }
        }
    }

    private class CustomPagerAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;
        ArrayList<Products> productsArrayList;
        String productVariationID = "";
        int minteger = 1;

        public CustomPagerAdapter(Context mContext, ArrayList<Products> productsArrayList) {
            this.mContext = mContext;
            this.productsArrayList = productsArrayList;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return productsArrayList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final View itemView = mLayoutInflater.inflate(R.layout.item_viewpager, container, false);
            ImageView imageView = itemView.findViewById(R.id.imItemImage);
            RelativeLayout rlvariationPrice = itemView.findViewById(R.id.rlvariationPrice);
            LinearLayout lnVariationPrice = itemView.findViewById(R.id.lnVariationPrice);
            Button btOrderNow = itemView.findViewById(R.id.btOrderNow);
            Button btnAddtoCart = itemView.findViewById(R.id.btnAddtoCart);
            final Button btnDecrease = itemView.findViewById(R.id.btnDecrease);
            final Button btnIncrease = itemView.findViewById(R.id.btnIncrease);
            TextView tvItemName = itemView.findViewById(R.id.tvItemName);
            final TextView tvInteger_number = itemView.findViewById(R.id.tvInteger_number);
            TextView tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            TextView tvItemDescription = itemView.findViewById(R.id.tvItemDescription);

            final TextView tvVariationsPrice = itemView.findViewById(R.id.tvVariationsPrice);
            final TextView tvPrice = itemView.findViewById(R.id.tvPrice);
            final TextView tvNoVariation = itemView.findViewById(R.id.tvNoVariation);
            Spinner spnVariation = itemView.findViewById(R.id.spnVariation);
            if (productsArrayList.get(position).getVarianceAttribute().get(0).equalsIgnoreCase("No Variations")) {
                lnVariationPrice.setVisibility(View.GONE);
                rlvariationPrice.setVisibility(View.GONE);
                spnVariation.setVisibility(View.GONE);
                tvItemPrice.setText("$" + productsArrayList.get(position).getPrice() + "");
                btnDecrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (minteger == 1) {
                            btnDecrease.setEnabled(false);
                            btnIncrease.setEnabled(true);
                            btnIncrease.setClickable(true);
                        } else if (minteger > 1) {
                            minteger = minteger - 1;
                            btnDecrease.setClickable(true);
                            btnIncrease.setClickable(false);
                        }
                        tvInteger_number.setText(minteger + "");
                    }
                });
                btnIncrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        minteger = minteger + 1;
                        if (minteger == 50) {
                            btnIncrease.setClickable(false);
                            btnDecrease.setClickable(true);
                        }
                        tvInteger_number.setText(minteger + "");
                    }
                });
            } else {
                tvNoVariation.setVisibility(View.GONE);
                spnVariation.setVisibility(View.VISIBLE);

                Double price = productsArrayList.get(position).getVariationArrayList().get(0).getPrice();
                Double price1 = productsArrayList.get(position).getVariationArrayList().get(1).getPrice();
                Double price2 = productsArrayList.get(position).getVariationArrayList().get(2).getPrice();
                double get_max = max(price, max(price1, price2));
                double get_min = -max(-price, max(-price1, -price2));
                double get_mid = (price + price1 + price2)
                        - (get_max + get_min);
                tvItemPrice.setText("$" + get_min + " - $" + get_max);
                ArrayAdapter aa = new ArrayAdapter(ItemDetailActivity.this, android.R.layout.simple_spinner_item, productsArrayList.get(position).getVarianceAttribute());
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnVariation.setAdapter(aa);
                btnDecrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (minteger == 1) {
                            btnDecrease.setEnabled(false);
                            btnIncrease.setEnabled(true);
                            btnIncrease.setClickable(true);
                        } else if (minteger > 1) {
                            minteger = minteger - 1;
                            btnDecrease.setClickable(true);
                            btnIncrease.setClickable(false);
                        }
                        tvInteger_number.setText(minteger + "");
                    }
                });
                btnIncrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        minteger = minteger + 1;
                        if (minteger == 50) {
                            btnIncrease.setClickable(false);
                            btnDecrease.setClickable(true);
                        }
                        tvInteger_number.setText(minteger + "");
                    }
                });
                spnVariation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int positionVariation, long l) {
                        ArrayList<Variation> variationArrayList = productsArrayList.get(position).getVariationArrayList();
                        tvVariationsPrice.setText("$" + variationArrayList.get(positionVariation).getPrice() + "");
                        tvPrice.setText(getResources().getString(R.string.quantity));
                        productVariationID = productsArrayList.get(Position).getVariationArrayList().get(positionVariation).getProductVariationId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            }
            tvItemName.setText(productsArrayList.get(position).getName());
            tvItemDescription.setText(productsArrayList.get(position).getDescriptions());
//            ResizableCustomView.doResizeTextView(tvItemDescription, MAX_LINES, "View More", true);
            Glide.with(ItemDetailActivity.this)
                    .load(productsArrayList.get(position).getImage().get(0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(ItemDetailActivity.this.getResources().getDrawable(R.drawable.placeholder))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);

            btnAddtoCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i("", "" + productsArrayList.get(Position));
                    if (tvInteger_number.getText().toString().equals("0")) {
                        Toast.makeText(mContext, "Please Select atleast 1 quantity", Toast.LENGTH_SHORT).show();
                    } else {
                        addToCart(tvNoVariation.getText().toString(), tvInteger_number.getText().toString(), productVariationID, productsArrayList.get(Position).getProductId());
                    }
                    Log.i("", "");
                }
            });
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

}