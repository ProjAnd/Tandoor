package tandoori.resturant.mobile.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tandoori.resturant.mobile.CircleImageView.CircleImageView;
import tandoori.resturant.mobile.CustomRecyclerView.CustomRecyclerView;
import tandoori.resturant.mobile.CustomToast.CustomToast;
import tandoori.resturant.mobile.ModelClass.AddressModel;
import tandoori.resturant.mobile.ModelClass.CountryModel;
import tandoori.resturant.mobile.ModelClass.StateModel;
import tandoori.resturant.mobile.R;
import tandoori.resturant.mobile.utils.BaseUrl;
import tandoori.resturant.mobile.utils.NetworkAndHideKeyBoard;
import tandoori.resturant.mobile.volly.AppController;

public class MyAddressesActivity extends AppCompatActivity implements View.OnClickListener {
    String addressId, user_address;
    RelativeLayout rlMenu, rlCart, lnOrdering_list;
    TextView tvTitle, tvTag, tv_cart;
    CircleImageView imUserImage;
    AddressAdapter mAddressAdapter;
    LinearLayout lnStart_shopping;
    ImageView imgEmpty;
    NetworkAndHideKeyBoard networkAndHideKeyBoard;
    String state;
    String country;
    private ListView myAddressesListView;
    private FloatingActionButton fabAddAddress;
    ArrayList<CountryModel> myList;
    ArrayList<StateModel> stateList;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);
        windowsCode();
        networkAndHideKeyBoard = new NetworkAndHideKeyBoard(this);
        initViews();
        initListeners();
        getCountry();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAddressAPI();
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
        fabAddAddress.setOnClickListener(this);
        rlCart.setOnClickListener(this);
        rlMenu.setOnClickListener(this);
    }
    private void getCountry() {
        JSONObject jsonObject = new JSONObject();
        String access_token = AppController.getInstance().useString("access_token");
        final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.GET, BaseUrl.COUNTRY + "access_token=" + access_token + "&pageSize=250" + "&status=ACTIVE", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() == 0) {
                            Toast.makeText(MyAddressesActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                    JSONArray dataJsonArray = jsonObject.optJSONArray("data");
                                    if (dataJsonArray == null || dataJsonArray.length() == 0) {
                                        Toast.makeText(MyAddressesActivity.this, "No Country Found", Toast.LENGTH_SHORT).show();
                                    } else {
                                        myList = new ArrayList<>();
                                        for (int i = 0; i < dataJsonArray.length(); i++) {
                                            JSONObject jsonObj = dataJsonArray.getJSONObject(i);
                                            CountryModel countryModel = new CountryModel();
                                            id=jsonObj.optString("id");
                                            countryModel.setId(id);
                                            countryModel.setName(jsonObj.optString("name"));
                                            countryModel.setIso(jsonObj.optString("iso"));
                                            myList.add(countryModel);
                                        }
                                    }
                                } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                    CustomToast.show(MyAddressesActivity.this, jsonObject.getString("cause"), true, getResources().getDrawable(R.drawable.yellowshape));
                                } else {
                                    CustomToast.show(MyAddressesActivity.this, getResources().getString(R.string.serverError), true, getResources().getDrawable(R.drawable.red));
                                }
                            } catch (Exception e) {
                                CustomToast.show(MyAddressesActivity.this, e.toString(), true, getResources().getDrawable(R.drawable.red));
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

    private void initViews() {
        fabAddAddress = findViewById(R.id.fabAddAddress);
        myAddressesListView = findViewById(R.id.myAddressesListView);
        tv_cart = findViewById(R.id.tv_cart);
        tv_cart.setVisibility(View.GONE);
        rlMenu = findViewById(R.id.rlMenu);
        rlCart = findViewById(R.id.rlCart);
        rlCart.setVisibility(View.GONE);
        tvTag = findViewById(R.id.tvTag);
        lnStart_shopping = findViewById(R.id.lnStart_shopping);
        lnOrdering_list = findViewById(R.id.lnOrdering_list);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.rlMyAddress));

    }

    private String fatchCountryName(String code)
    {
        for (int i= 0;i< myList.size();i++)
        {
            if (myList.get(i).getId().equals(code))
            {
                return  myList.get(i).getName();
            }
        }
        return code;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAddAddress:
                Intent createUpdateAddressIntent = new Intent(MyAddressesActivity.this, CreateUpdateAddressActivity.class);
                createUpdateAddressIntent.putExtra("comesFrom", "fabButton");
                startActivity(createUpdateAddressIntent);
                break;

            case R.id.rlMenu:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAddressAdapter.onActivityResult(requestCode, resultCode, data);
    }

    private void getAddressAPI() {
        networkAndHideKeyBoard.hideSoftKeyboard();
        if (!networkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "Network is not Available", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Please Wait while we fetching your saved address");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
            JSONObject jsonObject = new JSONObject();
            String access_token = AppController.getInstance().useString("access_token");
            String user_id = AppController.getInstance().useString("user_id");

            final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.GET, BaseUrl.GETADDRESS +
                    "access_token=" + access_token + "&user_id=" + user_id, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.dismiss();

                            if (response.length() == 0) {

                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                        JSONArray dataJsonArray = jsonObject.optJSONArray("data");
                                        ArrayList<AddressModel> myList = new ArrayList<>();
                                        if (dataJsonArray == null || dataJsonArray.length() == 0) {
                                            lnStart_shopping.setVisibility(View.VISIBLE);
                                            myAddressesListView.setVisibility(View.GONE);
                                            tvTag.setText(getResources().getString(R.string.any_address));
                                        } else {
                                            lnStart_shopping.setVisibility(View.GONE);
                                            myAddressesListView.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < dataJsonArray.length(); i++) {
                                                JSONObject jsonObj = dataJsonArray.getJSONObject(i);
                                                AddressModel addressModel = new AddressModel();
                                                addressId = jsonObj.optString("address_id");
                                                String firstName = jsonObj.optString("firstName");
                                                String middleName = jsonObj.optString("middleName");
                                                String lastName = jsonObj.optString("lastName");
                                                String address1 = jsonObj.optString("address1");
                                                String address2 = jsonObj.optString("address2");
                                                String city = jsonObj.optString("city");
                                                state = jsonObj.optString("state");
                                                country = jsonObj.optString("country");
                                                String postalCode = jsonObj.optString("postalCode");
                                                String mobileNumber = jsonObj.optString("mobileNumber");
                                                String email = jsonObj.optString("email");
                                                user_address = firstName + " " + middleName + " " + lastName + "\n" + email + "\n" + mobileNumber + "\n" + address1 + ", " + address2 + "\n" + city;
//                                            AppController.getInstance().saveString("address_id", address_id);
                                                addressModel.setAddress_id(addressId);
                                                addressModel.setUser_address(user_address);
                                                addressModel.setFirstName(firstName);
                                                addressModel.setMiddleName(middleName);
                                                addressModel.setLastName(lastName);
                                                addressModel.setAddress1(address1);
                                                addressModel.setAddress2(address2);
                                                addressModel.setCity(city);
                                                addressModel.setState(state);
                                                addressModel.setCountry(country);
                                                addressModel.setPostalCode(postalCode);
                                                addressModel.setMobileNumber(mobileNumber);
                                                addressModel.setEmail(email);
                                                myList.add(addressModel);
                                            }

                                            myAddressesListView = findViewById(R.id.myAddressesListView);
//                                        myAddressesRecyclerView.setHasFixedSize(true);
                                            setAddressAdapter(myList, addressId, user_address);
                                        }


                                    } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                        pDialog.dismiss();
                                        pDialog.hide();
                                        Toast.makeText(MyAddressesActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();

                                    } else {
                                        pDialog.dismiss();
                                        pDialog.hide();
                                        Toast.makeText(MyAddressesActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(MyAddressesActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();
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

    private void setAddressAdapter(ArrayList<AddressModel> myList, String addressId, String user_address) {
        mAddressAdapter = new AddressAdapter(myList, addressId, user_address);
        myAddressesListView.setAdapter(mAddressAdapter);
    }

    private void deleteCartItem(String addressId, final int position, final List<AddressModel> itemList) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Deleting...");
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);
        pDialog.show();
        JSONObject jsonObject = new JSONObject();
        String access_token = AppController.getInstance().useString("access_token");
        String user_id = AppController.getInstance().useString("user_id");

        final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.DELETE, BaseUrl.DELETEADDRESS + "access_token=" + access_token + "&user_id=" + user_id + "&address_id=" + addressId, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.dismiss();
                if (response.length() == 0) {
                    Toast.makeText(MyAddressesActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                            JSONObject objJson = jsonObject.optJSONObject("object");
                            itemList.remove(position);
                            ((BaseAdapter) myAddressesListView.getAdapter()).notifyDataSetChanged();
                            //myAddressesListView.getAdapter().notifyItemRangeChanged(position, itemList.size());
                            //removeAt(position);
                        } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
//                            CustomToast.show(MyAddressesActivity.this, jsonObject.getString("cause"), true, getResources().getDrawable(R.drawable.yellowshape));
                        } else {
//                            CustomToast.show(MyAddressesActivity.this, getResources().getString(R.string.serverError), true, getResources().getDrawable(R.drawable.red));
                        }
                    } catch (Exception e) {
//                        CustomToast.show(MyAddressesActivity.this, e.toString(), true, getResources().getDrawable(R.drawable.red));
                    }
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
//                        Toast.makeText(MyAddressesActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return AppController.getInstance().getParams();
            }
        };
        AppController.getInstance().addToRequestQueue(loginResult);
    }

    public class AddressAdapter extends BaseAdapter {
        ArrayList<AddressModel> myList;
        String addressId;
        String user_address;
        TextView myAddress;
        ImageView imgDelete;
        RelativeLayout myLayout;

        public AddressAdapter(ArrayList<AddressModel> myList, String addressId, String user_address) {
            this.myList = myList;
            this.addressId = addressId;
            this.user_address = user_address;
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public Object getItem(int position) {
            return myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_address, parent, false);
            }
            myAddress = convertView.findViewById(R.id.address);
            myLayout = convertView.findViewById(R.id.myLayout);
            imgDelete = convertView.findViewById(R.id.imgDelete);
            myAddress.setText(myList.get(position).getUser_address());
            addressId = myList.get(position).getAddress_id();
            user_address = myList.get(position).getUser_address();
            String come = getIntent().getStringExtra("comesFrom");
            if ("OrderNow".equalsIgnoreCase(come)) {
                myLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("addressId", myList.get(position).getAddress_id());
                        intent.putExtra("getUser_address", myList.get(position).getUser_address());
                        setResult(102, intent);
                        finish();
                    }
                });
                imgDelete.setVisibility(View.GONE);
                fabAddAddress.setVisibility(View.GONE);
            } else {
                imgDelete.setVisibility(View.VISIBLE);
                fabAddAddress.setVisibility(View.VISIBLE);
            }
//            holder.setItem(myList.get(position).getAddressDetail());
            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(MyAddressesActivity.this, view);
                    popup.getMenuInflater().inflate(R.menu.options_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_delete:
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyAddressesActivity.this);
                                    alertDialog.setTitle(getResources().getString(R.string.deleteAddressConfirm));
                                    alertDialog.setMessage(getResources().getString(R.string.deleteAddress));
                                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //  int position = myAddressesListView.getPositionForView(parentRow);
                                            deleteCartItem(myList.get(position).getAddress_id(), position, myList);
                                            Log.i("", "");
                                        }
                                    });
                                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    alertDialog.show();
                                    break;

                                case R.id.action_update:
                                    Intent intent = new Intent(getApplicationContext(), CreateUpdateAddressActivity.class);
                                    intent.putExtra("comesFrom", "updateButton");
                                    intent.putExtra("addressId", myList.get(position).getAddress_id());
                                    intent.putExtra("firstName", myList.get(position).getFirstName());
                                    intent.putExtra("middleName", myList.get(position).getMiddleName());
                                    intent.putExtra("lastName", myList.get(position).getLastName());
                                    intent.putExtra("address1", myList.get(position).getAddress1());
                                    intent.putExtra("address2", myList.get(position).getAddress2());
                                    intent.putExtra("city", myList.get(position).getCity());
                                    intent.putExtra("state",state);
                                    intent.putExtra("country", fatchCountryName(country));
                                    intent.putExtra("code",myList.get(position).getCountry());
                                    intent.putExtra("postalCode", myList.get(position).getPostalCode());
                                    intent.putExtra("mobileNumber", myList.get(position).getMobileNumber());
                                    intent.putExtra("email", myList.get(position).getEmail());
                                    startActivity(intent);
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });
            return convertView;
        }


        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            mAddressAdapter.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 101) {
                if (data != null) {
                    String message = data.getStringExtra("MESSAGE");
                    addressId = data.getStringExtra("addressId");
                    myAddress.setText(message);
                } else {
//                    myAddress.setText("No address added");
                }
            }
        }


    }
/*
   public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder>  {

        ArrayList<AddressModel> myList;

        String addressId;
        String user_address;
       TextView myAddress;

        public AddressAdapter(ArrayList<AddressModel> myList, String addressId, String user_address) {

            this.myList = myList;
            this.addressId = addressId;
            this.user_address = user_address;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_address, parent, false);
            return new ViewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            myAddress.setText(myList.get(position).getUser_address());
            addressId = myList.get(position).getAddressDetail();
            user_address = myList.get(position).getUser_address();
           *//* holder.myLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("addressId", addressId);
                    intent.putExtra("MESSAGE", user_address);
                    setResult(101, intent);
                    finish();

                }
            });*//*
           holder.setItem(myList.get(position).getAddressDetail());
           Log.i("","");

            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(getApplicationContext(), view);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.options_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_delete:
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyAddressesActivity.this);
                                    alertDialog.setTitle(getResources().getString(R.string.deleteAddressConfirm));
                                    alertDialog.setMessage(getResources().getString(R.string.deleteAddress));
                                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteCartItem(myList.get(position).getAddressDetail(), position, myList);
                                        }
                                    });
                                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    alertDialog.show();
                                    break;

                                case R.id.action_update:
                                   Intent intent= new Intent(getApplicationContext(),UpdateAddressActivity.class);
                                   intent.putExtra("addressId",myList.get(position).getAddressDetail());
                                    startActivityForResult(intent, 101);
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });
        }
        @Override
        public int getItemCount() {
            return myList.size();
        }


        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            mAddressAdapter.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 101) {
                if (data != null) {
                    String message = data.getStringExtra("MESSAGE");
                    addressId = data.getStringExtra("addressId");
                    myAddress.setText(message);
                } else {
                    myAddress.setText("No address added");
                }
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            RelativeLayout myLayout;
            Button btnDelete;
            ImageView imgDelete;
            String mItem;
            public ViewHolder(View itemView) {
                super(itemView);
                myAddress = itemView.findViewById(R.id.address);
                myLayout = itemView.findViewById(R.id.myLayout);
                btnDelete = itemView.findViewById(R.id.btnDelete);
                imgDelete = itemView.findViewById(R.id.imgDelete);
                itemView.setOnClickListener(this);
            }
                public void setItem(String item)
                {
                    mItem=item;
                }
            @Override
            public void onClick(View v) {

            }
        }
    }

    */

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int itemPosition = myAddressesListView.indexOfChild(v);
            Log.i("", "");
        }
    }
}