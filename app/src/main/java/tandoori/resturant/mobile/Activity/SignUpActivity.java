package tandoori.resturant.mobile.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import tandoori.resturant.mobile.CountryPicker.Country;
import tandoori.resturant.mobile.CountryPicker.CountryPicker;
import tandoori.resturant.mobile.CountryPicker.CountryPickerListener;
import tandoori.resturant.mobile.CustomToast.CustomToast;
import tandoori.resturant.mobile.R;
import tandoori.resturant.mobile.ShowAndHidePassword.ShowHidePasswordEditText;
import tandoori.resturant.mobile.utils.BaseUrl;
import tandoori.resturant.mobile.utils.NetworkAndHideKeyBoard;
import tandoori.resturant.mobile.volly.AppController;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener,PlaceSelectionListener {
    EditText etUserName, etEmail, etFirstName, etLastName, etCity,etMobileNumber;
    ShowHidePasswordEditText etConfirmPassword, etPassword;
    Button btRegistration;

    TextView tvAlreadyRegistered, tvTermsAndConditions;
    ImageView imGooglePlus, imFacebook;
    NetworkAndHideKeyBoard networkAndHideKeyBoard;
    double srcLat;
    double srcLong;
    private static final String LOG_TAG = "PlaceSelectionListener";
    private static final int REQUEST_SELECT_PLACE = 1000;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private TextView tvCountryCode;
    private CountryPicker mCountryPicker;
    private ProgressDialog progressBar;
   TextInputLayout  tilCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        networkAndHideKeyBoard = new NetworkAndHideKeyBoard(this);
        windowsCode();
        initViews();
        initListeners();
        initCountryPickerListeners();
        getUserCountryInfo();
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);//you can cancel it by pressing back button
        progressBar.setMessage(getResources().getString(R.string.loading));
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);


    }
    private void getUserCountryInfo() {
        Country country = Country.getCountryFromSIM(getApplicationContext());
        if (country != null) {
            tvCountryCode.setText(country.getDialCode());

        }
    }

    private void initCountryPickerListeners() {
        mCountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                tvCountryCode.setText(dialCode);
                mCountryPicker.dismiss();
            }
        });

    }
    private void initListeners() {
        tvAlreadyRegistered.setOnClickListener(this);
        btRegistration.setOnClickListener(this);
        etCity.setOnClickListener(this);
        tilCity.setOnClickListener(this);
        tvCountryCode.setOnClickListener(this);
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
        etUserName =findViewById(R.id.etUserName);
        etPassword =findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        tilCity = findViewById(R.id.tilCity);
        etConfirmPassword =  findViewById(R.id.etConfirmPassword);
        etFirstName =  findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etCity =  findViewById(R.id.etCity);
        tilCity =  findViewById(R.id.tilCity);

        btRegistration =  findViewById(R.id.btRegistration);

        tvAlreadyRegistered = findViewById(R.id.tvAlreadyRegistered);
        tvTermsAndConditions =  findViewById(R.id.tvTermsAndConditions);

        imFacebook = findViewById(R.id.imFacebook);
        imGooglePlus = findViewById(R.id.imGooglePlus); tvCountryCode = findViewById(R.id.tvCountryCode);
        mCountryPicker = CountryPicker.newInstance("Select Country");
        ArrayList<Country> nc = new ArrayList<>();
        for (Country c : Country.getAllCountries()) {
            nc.add(c);
        }
        // and decide, in which order they will be displayed
        Collections.reverse(nc);
        mCountryPicker.setCountriesList(nc);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS,
            }, 101);
        }


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 101: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                    }
                }
                break;
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAlreadyRegistered:
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.btRegistration:
                validate();
                break;
            case R.id.etCity:
                etCity.setInputType(InputType.TYPE_CLASS_TEXT);
                etCity.requestFocus();
                InputMethodManager mgr1 = (InputMethodManager) SignUpActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr1.showSoftInput(etCity, InputMethodManager.SHOW_FORCED);
                try {
                    Intent intentCity = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setBoundsBias(BOUNDS_MOUNTAIN_VIEW)
                            .build(SignUpActivity.this);
                    startActivityForResult(intentCity, REQUEST_SELECT_PLACE);
                } catch (GooglePlayServicesRepairableException |
                        GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tvCountryCode:
                mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                break;
        }

    }

    private void validate() {
        networkAndHideKeyBoard.hideSoftKeyboard();
        String firstName = etFirstName.getText().toString().trim();
        String mobileNumber = etMobileNumber.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        if (firstName.isEmpty() || firstName.length() < 3) {
            Toast.makeText(this, getResources().getString(R.string.atleastCharacter), Toast.LENGTH_SHORT).show();
        } else if (lastName.isEmpty() || lastName.length() < 3) {
            Toast.makeText(this, getResources().getString(R.string.atleastCharacter), Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getResources().getString(R.string.enterValidEmail), Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            Toast.makeText(this, getResources().getString(R.string.passworderror), Toast.LENGTH_SHORT).show();
        }
        else if (city.isEmpty()) {
            Toast.makeText(SignUpActivity.this, getResources().getString(R.string.enterSource), Toast.LENGTH_SHORT).show();
        } else if (!confirmPassword.equals(password)) {
            Toast.makeText(this, getResources().getString(R.string.donotMatchPassword), Toast.LENGTH_SHORT).show();
        } else if (!networkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this,  getResources().getString(R.string.networkError), Toast.LENGTH_SHORT).show();
        } else {
            register();
        }
    }

    private void register() {
        signup();
        if (etMobileNumber.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, R.string.enter_mobile_number, Toast.LENGTH_SHORT).show();
        }
        else {
            checkPermission();
        }
    }
    private void checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            final String[] permissions = new String[]{Manifest.permission.READ_SMS};

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                if (NetworkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
//                    progressBar.show();

                    signup();
                } else {
                    AppController.getInstance().showDialog(SignUpActivity.this, "Internet Error", "No Internet Found!!");
                }
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("To Use this feature you have to gave the permission ");
                    builder.setTitle("Permission Request");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SignUpActivity.this, permissions, 0);
                        }
                    });

                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this, permissions, 0);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Need Permission For This\n1.Go to Permissions \n 2.Check All permissions");
                    builder.setTitle("Permission Error!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });

                    builder.show();
                }
            }
        }
        else
        {
            if (NetworkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
//                progressBar.show();
                signup();
                // AppController.getInstance().addToRequestQueue(stringRequest);
            } else {
                AppController.getInstance().showDialog(SignUpActivity.this, "Internet Error", "No Internet Found!!");
            }
        }
    }

    private void signup() {
        networkAndHideKeyBoard.hideSoftKeyboard();
        if (!networkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "Network is not Available", Toast.LENGTH_SHORT).show();
        }
        else
        {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);
        pDialog.show();
        JSONObject jsonObject = new JSONObject();
        String access_token = AppController.getInstance().useString("access_token");

        try {
            jsonObject.put("form_id", "VbgyAkvqRk");
            Map<String, String> fieldMap = new HashMap<>();
            fieldMap.put("registrationEmail",etEmail.getText().toString().trim());
            fieldMap.put("registrationPassword", etPassword.getText().toString().trim());
            fieldMap.put("registrationMobile", etMobileNumber.getText().toString().trim());
            fieldMap.put("registrationFirstName",etFirstName.getText().toString().trim());
            fieldMap.put("registrationLastName", etLastName.getText().toString().trim());
            String dd= etCity.getText().toString().trim().replaceAll("\\s+","");
            fieldMap.put("registrationCity",dd);
            jsonObject.put("fields", new JSONObject(fieldMap));
            String url= BaseUrl.REGISTER+ "access_token=" + access_token;
            Log.i("","");

            final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.POST, BaseUrl.REGISTER+ "access_token=" + access_token, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.dismiss();
                            if (response.length() == 0) {
                                Toast.makeText(SignUpActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        String state=objJson.optString("state");
                                        AppController.getInstance().saveString("user_id", getIntent().getStringExtra("registrationEmail"));
                                        AppController.getInstance().saveString("FIRSTName", getIntent().getStringExtra("registrationFirstName"));
                                        AppController.getInstance().saveString("LastName", getIntent().getStringExtra("registrationLastName"));
                                        AppController.getInstance().saveString("registrationMobile", etMobileNumber.getText().toString().trim());
                                        Intent intent = new Intent(SignUpActivity.this, VerificationActivity.class);
                                        intent.putExtra("user",etEmail.getText().toString().trim());
                                        intent.putExtra("state",state);
                                        startActivity(intent);
                                        finish();

                                    } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                        pDialog.dismiss();
                                        pDialog.hide();
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        Toast.makeText(SignUpActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();

                                    } else {
                                        pDialog.dismiss();
                                        pDialog.hide();
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        Toast.makeText(SignUpActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();                                    }
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

                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json;");
                    params.put("key", "tiffin");
                    params.put("secret", "tiffin");
                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(loginResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        }
    }
    @Override
    public void onPlaceSelected(Place place) {
        etCity.setText(place.getAddress());
        srcLat = place.getLatLng().latitude;
        srcLong = place.getLatLng().longitude;
        Log.i("", "");
        if (!TextUtils.isEmpty(place.getAttributions())) {
            etCity.setText(Html.fromHtml(place.getAttributions().toString()));
        }
    }

    @Override
    public void onError(Status status) {
        Log.e(LOG_TAG, "onError: Status = " + status.toString());
        Toast.makeText(SignUpActivity.this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(SignUpActivity.this, data);
                this.onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(SignUpActivity.this, data);
                this.onError(status);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
