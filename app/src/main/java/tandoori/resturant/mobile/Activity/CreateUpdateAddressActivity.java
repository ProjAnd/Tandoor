package tandoori.resturant.mobile.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tandoori.resturant.mobile.CustomRecyclerView.CustomRecyclerView;
import tandoori.resturant.mobile.CustomToast.CustomToast;
import tandoori.resturant.mobile.Interface.ItemClickListener;
import tandoori.resturant.mobile.ModelClass.CountryModel;
import tandoori.resturant.mobile.ModelClass.StateModel;
import tandoori.resturant.mobile.R;
import tandoori.resturant.mobile.utils.BaseUrl;
import tandoori.resturant.mobile.utils.NetworkAndHideKeyBoard;
import tandoori.resturant.mobile.volly.AppController;

public class CreateUpdateAddressActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etCity, etAddress1, etAddress2, etPostalCode, etState, etCountry, etName, etMiddleName, etLastName,
            etPhone_number, etEmail;
    RelativeLayout rlMenu, rlCart;
    LinearLayout linear;
    TextView tv_cart, tvTitle;
    Button btnSave;
    String stateId, countryId;
    Dialog dialogCountry, dialogState;
    String addressId, state;
    NetworkAndHideKeyBoard networkAndHideKeyBoard;
    ArrayList<StateModel> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_update_address);
        windowsCode();
        networkAndHideKeyBoard = new NetworkAndHideKeyBoard(this);
        initViews();
        String stringValue = getIntent().getStringExtra("comesFrom");
        if (getIntent() != null) {
            if ("fabButton".equalsIgnoreCase(stringValue)) {
                btnSave.setText(getResources().getString(R.string.add));
            } else if ("updateButton".equalsIgnoreCase(stringValue)) {
                getStatesUpdate(getIntent().getStringExtra("code"));
                Log.i("", "");
                btnSave.setText(getResources().getString(R.string.updateAddress));
                tvTitle.setText(getResources().getString(R.string.updateAddress));
                addressId = getIntent().getStringExtra("addressId");
                etName.setText(getIntent().getStringExtra("firstName"));
                etLastName.setText(getIntent().getStringExtra("lastName"));
                etMiddleName.setText(getIntent().getStringExtra("middleName"));
                etAddress1.setText(getIntent().getStringExtra("address1"));
                etAddress2.setText(getIntent().getStringExtra("address2"));
                etCity.setText(getIntent().getStringExtra("city"));
//                etState.setText(fatchStateName(""));


                etCountry.setText(getIntent().getStringExtra("country"));
                etPostalCode.setText(getIntent().getStringExtra("postalCode"));
                etPhone_number.setText(getIntent().getStringExtra("mobileNumber"));
                etEmail.setText(getIntent().getStringExtra("email"));
            }
        }
        getCountry();
        initListenser();
    }

    private String fatchStateName(String code) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIdState().equals(code))
            {
                etState.setText(list.get(i).getNameState());
                return list.get(i).getNameState();
            }
        }
        return code;
    }

    private void windowsCode() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.green));
        }
    }

    private void initListenser() {
        btnSave.setOnClickListener(this);
        rlCart.setOnClickListener(this);
        rlMenu.setOnClickListener(this);
        etCountry.setOnClickListener(this);
        etState.setOnClickListener(this);
    }

    private void initViews() {
        etCity = findViewById(R.id.etCity);
        etAddress1 = findViewById(R.id.etAddress1);
        etAddress2 = findViewById(R.id.etAddress2);
        etPostalCode = findViewById(R.id.etPostalCode);
        etState = findViewById(R.id.etState);
        etCountry = findViewById(R.id.etCountry);
        etName = findViewById(R.id.etName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etLastName = findViewById(R.id.etLastName);
        etPhone_number = findViewById(R.id.etPhone_number);
        etEmail = findViewById(R.id.etEmail);
        rlMenu = findViewById(R.id.rlMenu);
        rlCart = findViewById(R.id.rlCart);
        linear = findViewById(R.id.linear);
        tv_cart = findViewById(R.id.tv_cart);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.addAddress));
        rlCart.setVisibility(View.GONE);
        tv_cart.setVisibility(View.GONE);
        btnSave = findViewById(R.id.btnSave);
        dialogCountry = new Dialog(CreateUpdateAddressActivity.this);
        dialogState = new Dialog(CreateUpdateAddressActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                validate();
                break;
            case R.id.rlMenu:
              /*  Intent intent = new Intent();
                intent.putExtra("MESSAGE", "No Address added");
                setResult(101, intent);
                finish();*/
                onBackPressed();
                break;

            case R.id.etCountry:
                dialogCountry.show();
                break;
            case R.id.etState:
                //ADAPTER
                dialogState.show();
                break;
        }
    }

    private void validate() {
        String city = etCity.getText().toString().trim();
        String address1 = etAddress1.getText().toString().trim();
        String address2 = etAddress2.getText().toString().trim();
        String postalcode = etPostalCode.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String state = etState.getText().toString().trim();
        String middleName = etMiddleName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone_number = etPhone_number.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        if (city.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.enterCity), Toast.LENGTH_SHORT).show();
        } else if (address1.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.address1), Toast.LENGTH_SHORT).show();
        } else if (address2.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.address2), Toast.LENGTH_SHORT).show();
        } else if (postalcode.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.postalcode), Toast.LENGTH_SHORT).show();
        } else if (middleName.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.middleName), Toast.LENGTH_SHORT).show();
        } else if (name.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.firstname), Toast.LENGTH_SHORT).show();
        } else if (lastName.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.enterlastName), Toast.LENGTH_SHORT).show();
        } else if (country.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.country), Toast.LENGTH_SHORT).show();
        } else if (state.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.enterstate), Toast.LENGTH_SHORT).show();
        } else if (phone_number.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.enterphone_number), Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getResources().getString(R.string.enterValidEmail), Toast.LENGTH_SHORT).show();
        } else {
            saveData();
        }
    }

    private void getCountry() {
        final ProgressDialog progressDialog = new ProgressDialog(CreateUpdateAddressActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        dialogCountry.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogCountry.setCancelable(false);
        dialogCountry.setContentView(R.layout.custom_country);
        final TextView cancel = dialogCountry.findViewById(R.id.cancel);
        final CustomRecyclerView recyclerCountryView = dialogCountry.findViewById(R.id.recyclerCountryView);
        recyclerCountryView.setLayoutManager(new LinearLayoutManager(this));
        recyclerCountryView.setItemAnimator(new DefaultItemAnimator());
        final EditText etSearch = dialogCountry.findViewById(R.id.etSearch);
        JSONObject jsonObject = new JSONObject();
        String access_token = AppController.getInstance().useString("access_token");
        final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.GET, BaseUrl.COUNTRY + "access_token=" + access_token + "&pageSize=250" + "&status=ACTIVE", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        if (response.length() == 0) {
                            Toast.makeText(CreateUpdateAddressActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                    JSONArray dataJsonArray = jsonObject.optJSONArray("data");
                                    if (dataJsonArray == null || dataJsonArray.length() == 0) {
                                        Toast.makeText(CreateUpdateAddressActivity.this, "No Country Found", Toast.LENGTH_SHORT).show();
                                    } else {
                                        ArrayList<CountryModel> myList = new ArrayList<>();
                                        for (int i = 0; i < dataJsonArray.length(); i++) {
                                            JSONObject jsonObj = dataJsonArray.getJSONObject(i);
                                            CountryModel countryModel = new CountryModel();
                                            countryModel.setId(jsonObj.optString("id"));
                                            countryModel.setName(jsonObj.optString("name"));
                                            countryModel.setIso(jsonObj.optString("iso"));
                                            myList.add(countryModel);
                                        }
                                        final MyAdapter adapter = new MyAdapter(CreateUpdateAddressActivity.this, myList);
                                        recyclerCountryView.setAdapter(adapter);
                                        etSearch.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            }

                                            @Override
                                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                adapter.getFilter().filter(charSequence);
                                            }

                                            @Override
                                            public void afterTextChanged(Editable editable) {
                                            }
                                        });
                                        cancel.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View view) {
                                                dialogCountry.dismiss();
                                            }
                                        });
                                    }
                                } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                    CustomToast.show(CreateUpdateAddressActivity.this, jsonObject.getString("cause"), true, getResources().getDrawable(R.drawable.yellowshape));
                                } else {
                                    CustomToast.show(CreateUpdateAddressActivity.this, getResources().getString(R.string.serverError), true, getResources().getDrawable(R.drawable.red));
                                }
                            } catch (Exception e) {
                                CustomToast.show(CreateUpdateAddressActivity.this, e.toString(), true, getResources().getDrawable(R.drawable.red));
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
//                        Toast.makeText(CreateUpdateAddressActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return AppController.getInstance().getParams();
            }
        };
        AppController.getInstance().addToRequestQueue(loginResult);
    }

    private void getStates(final String countryId) {
        final ProgressDialog progressDialog = new ProgressDialog(CreateUpdateAddressActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        //dialogState.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogState.setCancelable(false);
        dialogState.setContentView(R.layout.custom_state);
        final TextView cancelState = dialogState.findViewById(R.id.cancelState);
        final CustomRecyclerView recyclerStateView = dialogState.findViewById(R.id.recyclerStateView);
        recyclerStateView.setLayoutManager(new LinearLayoutManager(this));
        recyclerStateView.setItemAnimator(new DefaultItemAnimator());
        final EditText etSearchState = dialogState.findViewById(R.id.etSearchState);

        JSONObject jsonObject = new JSONObject();
        String access_token = AppController.getInstance().useString("access_token");
        final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.GET, BaseUrl.STATES + "access_token="
                + access_token + "&pageSize=250" + "&status=ACTIVE" + "&country_id=" + countryId, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        if (response.length() == 0) {
                            Toast.makeText(CreateUpdateAddressActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                    JSONArray dataJsonArray = jsonObject.optJSONArray("data");
                                    if (dataJsonArray == null || dataJsonArray.length() == 0) {
                                        Toast.makeText(CreateUpdateAddressActivity.this, "No State Found", Toast.LENGTH_SHORT).show();
                                    } else {
                                        ArrayList<StateModel> stateList  = new ArrayList<>();
                                        for (int i = 0; i < dataJsonArray.length(); i++) {
                                            final JSONObject jsonObj = dataJsonArray.getJSONObject(i);
                                            StateModel stateModel = new StateModel();
                                            stateModel.setIdState(jsonObj.optString("id"));
                                            stateModel.setNameState(jsonObj.optString("name"));
                                            stateList.add(stateModel);
                                        }
                                        final StateAdapter adapterState = new StateAdapter(CreateUpdateAddressActivity.this, stateList, countryId);
                                        recyclerStateView.setAdapter(adapterState);
                                        dialogState.show();
                                        etSearchState.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            }

                                            @Override
                                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                adapterState.getFilter().filter(charSequence);
                                            }

                                            @Override
                                            public void afterTextChanged(Editable editable) {
                                            }
                                        });
                                        cancelState.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View view) {
                                                dialogState.dismiss();
                                            }
                                        });
                                    }
                                } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                    CustomToast.show(CreateUpdateAddressActivity.this, jsonObject.getString("cause"), true, getResources().getDrawable(R.drawable.yellowshape));
                                } else {
                                    CustomToast.show(CreateUpdateAddressActivity.this, getResources().getString(R.string.serverError), true, getResources().getDrawable(R.drawable.red));
                                }
                            } catch (Exception e) {
                                CustomToast.show(CreateUpdateAddressActivity.this, e.toString(), true, getResources().getDrawable(R.drawable.red));
                            }
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        //Log.d("ERROR1:", "");
//                        Toast.makeText(CreateUpdateAddressActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                return AppController.getInstance().getParams();
            }

        };
        AppController.getInstance().addToRequestQueue(loginResult);


    }

    private void getStatesUpdate(final String countryId) {
        JSONObject jsonObject = new JSONObject();
        String access_token = AppController.getInstance().useString("access_token");
        final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.GET, BaseUrl.STATES + "access_token="
                + access_token + "&pageSize=250" + "&status=ACTIVE" + "&country_id=" + countryId, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.length() == 0) {
                            Toast.makeText(CreateUpdateAddressActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                    JSONArray dataJsonArray = jsonObject.optJSONArray("data");
                                    if (dataJsonArray == null || dataJsonArray.length() == 0) {
                                        Toast.makeText(CreateUpdateAddressActivity.this, "No State Found", Toast.LENGTH_SHORT).show();
                                    } else {
                                        list = new ArrayList<>();
                                        for (int i = 0; i < dataJsonArray.length(); i++) {
                                            final JSONObject jsonObj = dataJsonArray.getJSONObject(i);
                                            StateModel stateModel = new StateModel();
                                            state = jsonObj.optString("id");
                                            stateModel.setIdState(state);
                                            stateModel.setNameState(jsonObj.optString("name"));
                                            list.add(stateModel);
                                        }
                                        String c = getIntent().getStringExtra("state");
                                        fatchStateName(c);
                                        Log.i("", "");
                                    }
                                } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                    CustomToast.show(CreateUpdateAddressActivity.this, jsonObject.getString("cause"), true, getResources().getDrawable(R.drawable.yellowshape));
                                } else {
                                    CustomToast.show(CreateUpdateAddressActivity.this, getResources().getString(R.string.serverError), true, getResources().getDrawable(R.drawable.red));
                                }
                            } catch (Exception e) {
                                CustomToast.show(CreateUpdateAddressActivity.this, e.toString(), true, getResources().getDrawable(R.drawable.red));
                            }
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Log.d("ERROR1:", "");
//                        Toast.makeText(CreateUpdateAddressActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                return AppController.getInstance().getParams();
            }

        };
        AppController.getInstance().addToRequestQueue(loginResult);


    }

    private void saveData() {
        networkAndHideKeyBoard.hideSoftKeyboard();
        if (!networkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "Network is not Available", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Adding address....");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
            final String value = getIntent().getStringExtra("comesFrom");

            JSONObject jsonObject = new JSONObject();
            String access_token = AppController.getInstance().useString("access_token");
            String user_id = AppController.getInstance().useString("user_id");
            try {
                jsonObject.put("form_id", "");
                jsonObject.put("user_id", AppController.getInstance().useString("user_id"));
                Map<String, String> fieldMap = new HashMap<>();

                if ("fabButton".equalsIgnoreCase(value) ||
                        "Ordernow".equalsIgnoreCase(value)) {
                    fieldMap.put("address_id", "");
                } else if ("updateButton".equalsIgnoreCase(value)) {
                    fieldMap.put("address_id", getIntent().getStringExtra("addressId"));
                }
                fieldMap.put("firstName", etName.getText().toString().trim());
                fieldMap.put("middleName", etMiddleName.getText().toString().trim());
                fieldMap.put("lastName", etLastName.getText().toString().trim());
                fieldMap.put("address1", etAddress1.getText().toString().trim());
                fieldMap.put("address2", etAddress2.getText().toString().trim());
                fieldMap.put("city", etCity.getText().toString().trim());
                fieldMap.put("state", stateId);
                fieldMap.put("country", countryId);
                fieldMap.put("postalCode", etPostalCode.getText().toString().trim());
                fieldMap.put("mobileNumber", etPhone_number.getText().toString().trim());
                fieldMap.put("email", etEmail.getText().toString().trim());
                jsonObject.put("fields", new JSONObject(fieldMap));
                Log.i("", "");

                Log.i("", "" + BaseUrl.CREATEANDUPDATE + "access_token=" + access_token + "&user_id=" + user_id);
                final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.POST, BaseUrl.CREATEANDUPDATE + "access_token=" + access_token + "&user_id=" + user_id, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                pDialog.dismiss();
                                if (response.length() == 0) {
                                    Toast.makeText(CreateUpdateAddressActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.toString());
                                        if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                            JSONObject objJson = jsonObject.optJSONObject("object");
                                            String address_id = objJson.getString("address_id");
                                            String firstName = objJson.getString("firstName");
                                            String middleName = objJson.getString("middleName");
                                            String lastName = objJson.getString("lastName");
                                            String address1 = objJson.getString("address1");
                                            String address2 = objJson.getString("address2");
                                            String city = objJson.getString("city");
                                            String postalCode = objJson.getString("postalCode");
                                            String mobileNumber = objJson.getString("mobileNumber");
                                            String email = objJson.getString("email");
                                            String user_address = firstName + " " + middleName + " " + lastName + "\n" + address1 + ", " + address2 + ",\n" + city + ", " + " - " + postalCode + "\n" + "\n" + mobileNumber + "\n" + email;
                                            AppController.getInstance().saveString("address_id", address_id);
                                            AppController.getInstance().saveString("user_address", user_address);
                                            if (getIntent() != null) {
                                                if ("Ordernow".equalsIgnoreCase(value)) {
                                                    Intent intent = new Intent();
                                                    intent.putExtra("addressId", address_id);
                                                    intent.putExtra("getUser_address", user_address);
                                                    setResult(101, intent);
                                                    finish();
                                                } else if ("updateButton".equalsIgnoreCase(value)) {
                                                    finish();
                                                } else if ("fabButton".equalsIgnoreCase(value)) {
                                                    finish();
                                                }
                                            }

                                        } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                            pDialog.dismiss();
                                            pDialog.hide();
                                            JSONObject objJson = jsonObject.optJSONObject("object");
                                            Toast.makeText(CreateUpdateAddressActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();

                                        } else {
                                            pDialog.dismiss();
                                            pDialog.hide();
                                            JSONObject objJson = jsonObject.optJSONObject("object");
                                            Toast.makeText(CreateUpdateAddressActivity.this, objJson.optString("error"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(CreateUpdateAddressActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
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
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("MESSAGE", "No Address added");
        setResult(101, intent);
        finish();
    }

    public class CustomFilter extends Filter {

        MyAdapter adapter;
        ArrayList<CountryModel> filterList;

        public CustomFilter(ArrayList<CountryModel> filterList, MyAdapter adapter) {
            this.adapter = adapter;
            this.filterList = filterList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {

                constraint = constraint.toString().toUpperCase();
                ArrayList<CountryModel> filteredPlayers = new ArrayList<>();

                for (int i = 0; i < filterList.size(); i++) {
                    if (filterList.get(i).getName().toUpperCase().contains(constraint)) {
                        filteredPlayers.add(filterList.get(i));
                    }
                }

                results.count = filteredPlayers.size();
                results.values = filteredPlayers;
            } else {
                results.count = filterList.size();
                results.values = filterList;

            }


            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            adapter.players = (ArrayList<CountryModel>) results.values;

            //REFRESH
            adapter.notifyDataSetChanged();
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyHolder> implements Filterable {
        Context context;
        ArrayList<CountryModel> players, filterList;
        CustomFilter filter;

        public MyAdapter(Context ctx, ArrayList<CountryModel> players) {
            this.context = ctx;
            this.players = players;
            this.filterList = players;
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_countryitem, null);
            MyHolder holder = new MyHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, final int position) {
            holder.tvCountry.setText(players.get(position).getName());
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    etCountry.setText(players.get(position).getName());
                    dialogCountry.dismiss();
                    Log.i("", "");
                    countryId = players.get(position).getId();
                    getStates(players.get(position).getId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return players.size();
        }

        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new CustomFilter(filterList, this);
            }
            return filter;
        }
    }

    private class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvCountry;
        RelativeLayout linear;

        ItemClickListener itemClickListener;

        public MyHolder(View itemView) {
            super(itemView);
            this.tvCountry = itemView.findViewById(R.id.tvCountry);
            this.linear = itemView.findViewById(R.id.linear);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v, getLayoutPosition());
        }

        public void setItemClickListener(ItemClickListener ic) {
            this.itemClickListener = ic;
        }
    }

    private class StateAdapter extends RecyclerView.Adapter<MyHolder> implements Filterable {
        Context context;
        ArrayList<StateModel> stateList, filterStateList;
        CustomStateFilter filter;
        String countryId;

        public StateAdapter(Context context, ArrayList<StateModel> stateList, String countryId) {
            this.context = context;
            this.stateList = stateList;
            this.filterStateList = stateList;
            this.countryId = countryId;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_countryitem, null);
            MyHolder holder = new MyHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
            holder.tvCountry.setText(stateList.get(position).getNameState());

            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    etState.setText(stateList.get(position).getNameState());
                    dialogState.dismiss();
                    stateId = stateList.get(position).getIdState();
                    Log.i("", "");


//                    getCity(stateList.get(position).getIdState());

                }
            });
        }

        @Override
        public int getItemCount() {
            return stateList.size();
        }

        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new CustomStateFilter(filterStateList, this);
            }
            return filter;
        }
    }

    public class CustomStateFilter extends Filter {

        StateAdapter adapter;
        ArrayList<StateModel> stateList;

        public CustomStateFilter(ArrayList<StateModel> stateList, StateAdapter adapter) {
            this.adapter = adapter;
            this.stateList = stateList;
        }


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {

                constraint = constraint.toString().toUpperCase();
                ArrayList<StateModel> filteredPlayers = new ArrayList<>();

                for (int i = 0; i < stateList.size(); i++) {
                    if (stateList.get(i).getNameState().toUpperCase().contains(constraint)) {
                        filteredPlayers.add(stateList.get(i));
                    }
                }

                results.count = filteredPlayers.size();
                results.values = filteredPlayers;
            } else {
                results.count = stateList.size();
                results.values = stateList;

            }


            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            adapter.stateList = (ArrayList<StateModel>) results.values;

            //REFRESH
            adapter.notifyDataSetChanged();
        }
    }
}

