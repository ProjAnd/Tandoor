package tandoori.resturant.mobile.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tandoori.resturant.mobile.CircleImageView.CircleImageView;
import tandoori.resturant.mobile.CustomToast.CustomToast;
import tandoori.resturant.mobile.Fragment.HomeFragment;
import tandoori.resturant.mobile.Fragment.OthersFragment;
import tandoori.resturant.mobile.R;
import tandoori.resturant.mobile.utils.BaseUrl;
import tandoori.resturant.mobile.volly.AppController;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final ArrayList<String> tabTitles = new ArrayList<>();
    private final ArrayList<String> mSlugs = new ArrayList<>();
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    boolean doubleBackToExitPressedOnce = false;
    RelativeLayout rlMyAccount, rlOrderHistory, rlMenu, rlCart,
            rlOrderStatus, rlShareApp, rlLogout, rlMyAddress;
    TextView tvEmail, tvUserName, tv_cart, tv_title;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabsPagerAdapter adapter;
    CircleImageView imUserImage;
    FrameLayout frame_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        windowsCode();
       /* toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.humbuger_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);*/
        viewPager = findViewById(R.id.viewPager);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
      /*  ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toggle.getDrawerArrowDrawable().setColor(getColor(R.color.white));
        } else {
            toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        }
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();*/
        tabLayout = findViewById(R.id.tabLayout);
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
        getProfie();
        initListener();
        for (int i = 0; i < AppController.getInstance().getCategories().size(); i++) {
            tabLayout.addTab(tabLayout.newTab());
            tabTitles.add(AppController.getInstance().getCategories().get(i).getCategoryName());
            mSlugs.add(AppController.getInstance().getCategories().get(i).getSlug());
        }
        adapter = new TabsPagerAdapter(getSupportFragmentManager(), tabTitles, mSlugs);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initListener() {
        rlMyAccount.setOnClickListener(this);
        rlOrderHistory.setOnClickListener(this);
        rlOrderStatus.setOnClickListener(this);
        rlShareApp.setOnClickListener(this);
        rlLogout.setOnClickListener(this);
        rlMyAddress.setOnClickListener(this);
        rlMenu.setOnClickListener(this);
        rlCart.setOnClickListener(this);
        imUserImage.setOnClickListener(this);
        tvUserName.setOnClickListener(this);
        tv_title.setText(getResources().getString(R.string.app_name));

//        if (AppController.getInstance().containsKey("bucket")) {
//            if (AppController.getInstance().useString("bucket").equalsIgnoreCase("")) {
//                tv_cart.setVisibility(View.GONE);
//            } else {
//                tv_cart.setText(AppController.getInstance().useString("Size"));
//            }
//        } else {
//            tv_cart.setVisibility(View.GONE);
//        }
    }

    private void windowsCode() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.green));
        }
    }

    private void initViews() {
        frame_container = findViewById(R.id.frame_container);
        tv_title = findViewById(R.id.tvTitle);
        tvEmail = findViewById(R.id.tvEmail);
        tvUserName = findViewById(R.id.tvUserName);
        rlMyAccount = findViewById(R.id.rlMyAccount);
        rlMenu = findViewById(R.id.rlMenu);
        rlCart = findViewById(R.id.rlCart);
        rlOrderHistory = findViewById(R.id.rlOrderHistory);
        rlOrderStatus = findViewById(R.id.rlMyCart);
        rlShareApp = findViewById(R.id.rlShareApp);
        rlLogout = findViewById(R.id.rlLogout);
        rlMyAddress = findViewById(R.id.rlMyAddress);
        tv_cart = findViewById(R.id.tv_cart);
        imUserImage = findViewById(R.id.imUserImage);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        drawerLayout.closeDrawer(navigationView);
        this.doubleBackToExitPressedOnce = true;
        if (this.doubleBackToExitPressedOnce) {
            Toast.makeText(this, getResources().getString(R.string.close), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    private void getProfie() {
        JSONObject jsonObject = new JSONObject();
        String access_token = AppController.getInstance().useString("access_token");
        String user_id = AppController.getInstance().useString("user_id");
        final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.GET, BaseUrl.PROFILE + "access_token=" + access_token + "&user_id=" + user_id, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() == 0) {
                            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                    JSONObject objJson = jsonObject.optJSONObject("object");
                                    String user_id = objJson.optString("user_id");
                                    String userName = objJson.optString("userName");
                                    String email = objJson.optString("email");
                                    String primaryPhone = objJson.optString("primaryPhone");
                                    String middleName = objJson.optString("middleName");
                                    String firstName = objJson.optString("firstName");
                                    String lastName = objJson.optString("lastName");
                                    AppController.getInstance().saveString("FIRSTName", firstName);
                                    AppController.getInstance().saveString("LastName", lastName);
                                    AppController.getInstance().saveString("user_id", email);
                                    tvEmail.setText(email);
                                    tvUserName.setText(firstName + " " + middleName + " " + lastName);
                                    AppController.getInstance().saveString("requestId", jsonObject.optString("requestId"));

                                } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                    CustomToast.show(MainActivity.this, jsonObject.getString("error"), true, getResources().getDrawable(R.drawable.yellowshape));
                                } else {
                                    CustomToast.show(MainActivity.this, getResources().getString(R.string.serverError), true, getResources().getDrawable(R.drawable.red));
                                }
                            } catch (Exception e) {
                                CustomToast.show(MainActivity.this, e.toString(), true, getResources().getDrawable(R.drawable.red));
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR1:", "");
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return AppController.getInstance().getParams();
            }
        };
        AppController.getInstance().addToRequestQueue(loginResult);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlMenu:
                drawerLayout.openDrawer(navigationView);
                break;
            case R.id.rlCart:
                Intent intentCart = new Intent(MainActivity.this, MyCartActivity.class);
                startActivity(intentCart);
                break;
            case R.id.rlMyAccount:
                drawerLayout.closeDrawer(navigationView);
//                Intent intentMyAccount = new Intent(MainActivity.this, MainActivity.class);
//                startActivity(intentMyAccount);
                break;
            case R.id.tvUserName:
                drawerLayout.closeDrawer(navigationView);
                Intent intentEdit = new Intent(MainActivity.this, MyAccountActivity.class);
                startActivity(intentEdit);
                finish();
                break;
            case R.id.imUserImage:
                drawerLayout.closeDrawer(navigationView);
                Intent intentimUserImage = new Intent(MainActivity.this, MyAccountActivity.class);
                startActivity(intentimUserImage);
                finish();
                break;
            case R.id.rlOrderHistory:
                drawerLayout.closeDrawer(navigationView);
                Intent intentOrderHistory = new Intent(MainActivity.this, OrderHistoryActivity.class);
                startActivity(intentOrderHistory);
                break;
            case R.id.rlMyCart:
                drawerLayout.closeDrawer(navigationView);
                Intent intentOrderStatus = new Intent(MainActivity.this, MyCartActivity.class);
                startActivity(intentOrderStatus);
                break;
            case R.id.rlMyAddress:
                drawerLayout.closeDrawer(navigationView);
                Intent intentMyAddress = new Intent(MainActivity.this, MyAddressesActivity.class);
                startActivity(intentMyAddress);
                break;
            case R.id.rlShareApp:
                drawerLayout.closeDrawer(navigationView);
                try {
                    final String appPackageName = getPackageName();
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    String sAux = "\n" + Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName) + "\n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch (Exception e) {
                }
                break;
            case R.id.rlLogout:
                drawerLayout.closeDrawer(navigationView);
                logoutDialog();
                break;
        }
    }

    private void logoutDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.delete));
        alertDialog.setMessage(getResources().getString(R.string.confirm));
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //logout();
                AppController.getInstance().clearSharedPreferences();
                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void logout() {
        Log.i("", "" + BaseUrl.LOGOUTUSER);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BaseUrl.LOGOUTUSER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() == 0) {
                    CustomToast.show(MainActivity.this, response.toString(), true, getResources().getDrawable(R.drawable.yellowshape));
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("status").equalsIgnoreCase("success")) {
                            AppController.getInstance().clearSharedPreferences();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            CustomToast.show(MainActivity.this, "Unsuccessful", true, getResources().getDrawable(R.drawable.yellowshape));

                        }
                    } catch (JSONException e) {
                        CustomToast.show(MainActivity.this, e.toString(), true, getResources().getDrawable(R.drawable.red));
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("", "");
                CustomToast.show(MainActivity.this, error.toString(), true, getResources().getDrawable(R.drawable.red));
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public class TabsPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<String> tabTitles;
        ArrayList<String> mSlugs;

        public TabsPagerAdapter(FragmentManager fm, ArrayList<String> tabTitles, ArrayList<String> mSlugs) {
            super(fm);
            this.tabTitles = tabTitles;
            this.mSlugs = mSlugs;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeFragment();
            } else {
                return OthersFragment.newInstance(mSlugs.get(position));
            }
        }

        @Override
        public int getCount() {
            return tabTitles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles.get(position);
        }
    }
}