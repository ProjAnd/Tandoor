package tandoori.resturant.mobile.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
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

import tandoori.resturant.mobile.CustomRecyclerView.CustomRecyclerView;
import tandoori.resturant.mobile.ModelClass.OrderHistory;
import tandoori.resturant.mobile.ModelClass.OrderStatus;
import tandoori.resturant.mobile.R;
import tandoori.resturant.mobile.utils.BaseUrl;
import tandoori.resturant.mobile.utils.NetworkAndHideKeyBoard;
import tandoori.resturant.mobile.volly.AppController;

public class OrderHistoryActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout rlMenu, rlCart, lnOrdering_list;
    TextView tvTitle, tvTag;
    CustomRecyclerView recyclerViewOrderHistory;
    String user_address, shipping_address;
    String orderId;
    LinearLayout lnStart_shopping;
    NetworkAndHideKeyBoard networkAndHideKeyBoard;
    private List<OrderHistory> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        windowsCode();
        networkAndHideKeyBoard = new NetworkAndHideKeyBoard(this);

        initViews();
        initListeners();
        getOrderDetails();

    }

    private void setRecyclerView(final ArrayList<OrderHistory> myList) {
        recyclerViewOrderHistory.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewOrderHistory.setLayoutManager(linearLayoutManager);
        final OrderHistoryAdapter mAdapter = new OrderHistoryAdapter(myList);
        recyclerViewOrderHistory.setAdapter(mAdapter);
    }


    private void getOrderDetails() {
        networkAndHideKeyBoard.hideSoftKeyboard();
        if (!networkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this, "Network is not Available", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
            JSONObject jsonObject = new JSONObject();
            String access_token = AppController.getInstance().useString("access_token");
            String user_id = AppController.getInstance().useString("user_id");
            final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.GET, BaseUrl.ORDERHISTORY +
                    "access_token=" + access_token + "&user_id=" + user_id + "&pageSize=100" + "&pageNumber=0", jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.dismiss();
                            if (response.length() == 0) {
                                Toast.makeText(OrderHistoryActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                                        ArrayList<OrderHistory> myList = new ArrayList<>();
                                        if (jsonArray == null || jsonArray.length() == 0) {
                                            lnStart_shopping.setVisibility(View.VISIBLE);
                                            lnOrdering_list.setVisibility(View.GONE);
                                            tvTag.setText(getResources().getString(R.string.any_Order));
                                        } else {
                                            lnStart_shopping.setVisibility(View.GONE);
                                            lnOrdering_list.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                                OrderHistory orderHistoryModel = new OrderHistory();
                                                orderId = jsonObj.optString("orderId");
                                                ArrayList<OrderStatus> orderStatusArrayList = new ArrayList<>();
                                                for (int orderst = 0; orderst < jsonObj.optJSONArray("orderStatus").length(); orderst++) {
                                                    OrderStatus orderStatusModel = new OrderStatus();
                                                    JSONObject jsonObjectStatus = jsonObj.optJSONArray("orderStatus").optJSONObject(orderst);
                                                    orderStatusModel.setStatus(jsonObjectStatus.optString("status"));
                                                    orderStatusArrayList.add(orderStatusModel);
                                                }
                                                JSONObject billingAddressJsonObject = new JSONObject(jsonObj.optString("billingAddress"));
                                                String firstName = billingAddressJsonObject.optString("firstName");
                                                String middleName = billingAddressJsonObject.optString("middleName");
                                                String lastName = billingAddressJsonObject.optString("lastName");
                                                String address1 = billingAddressJsonObject.optString("address1");
                                                String address2 = billingAddressJsonObject.optString("address2");
                                                String city = billingAddressJsonObject.optString("city");
                                                String state = billingAddressJsonObject.optString("state");
                                                String country = billingAddressJsonObject.optString("country");
                                                String postalCode = billingAddressJsonObject.optString("postalCode");
                                                String mobileNumber = billingAddressJsonObject.optString("mobileNumber");
                                                String email = billingAddressJsonObject.optString("email");
                                                user_address = firstName + " " + lastName + "\n" + address1 + "\n" + address2 + "\n" + city;

                                                JSONObject shippingAddressJsonObject = new JSONObject(jsonObj.optString("shippingAddress"));
                                                String firstNameShipping = shippingAddressJsonObject.optString("firstName");
                                                String middleNameShipping = shippingAddressJsonObject.optString("middleName");
                                                String lastNameShipping = shippingAddressJsonObject.optString("lastName");
                                                String address1Shipping = shippingAddressJsonObject.optString("address1");
                                                String address2Shipping = shippingAddressJsonObject.optString("address2");
                                                String cityShipping = shippingAddressJsonObject.optString("city");
                                                String stateShipping = shippingAddressJsonObject.optString("state");
                                                String countryShipping = shippingAddressJsonObject.optString("country");
                                                String postalCodeShipping = shippingAddressJsonObject.optString("postalCode");
                                                String mobileNumberShipping = shippingAddressJsonObject.optString("mobileNumber");
                                                String emailShipping = shippingAddressJsonObject.optString("email");
                                                shipping_address = firstNameShipping + " " + lastNameShipping + "\n" + address1Shipping + "\n" + address2Shipping + "\n" + cityShipping;

                                                JSONObject checkoutMethodJsonObject = new JSONObject(jsonObj.optString("checkoutMethod"));
                                                String checkoutMethod = checkoutMethodJsonObject.optString("name");

                                                orderHistoryModel.setOrderStatusArrayList(orderStatusArrayList);
                                                orderHistoryModel.setBillingAddress(user_address);
                                                orderHistoryModel.setShippingAddress(shipping_address);
                                                orderHistoryModel.setCheckoutMethod(checkoutMethod);
                                                orderHistoryModel.setOrderedDate(jsonObj.optString("orderedDate"));
                                                orderHistoryModel.setOrderId(orderId);
                                                orderHistoryModel.setOrderTotal(jsonObj.optString("orderTotal"));
                                                myList.add(orderHistoryModel);
                                            }
                                            recyclerViewOrderHistory = findViewById(R.id.recyclerViewOrderHistory);
                                            setRecyclerView(myList);
                                        }
                                    } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                        Toast.makeText(OrderHistoryActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(OrderHistoryActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();

                                    }
                                } catch (Exception e) {
                                    Toast.makeText(OrderHistoryActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();
                            pDialog.hide();
                            Toast.makeText(OrderHistoryActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_SHORT).show();
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
        rlCart.setOnClickListener(this);
    }

    private void initViews() {
        recyclerViewOrderHistory = findViewById(R.id.recyclerViewOrderHistory);
        rlMenu = findViewById(R.id.rlMenu);
        rlCart = findViewById(R.id.rlCart);
        rlCart.setVisibility(View.GONE);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.orderHistory));
        tvTag = findViewById(R.id.tvTag);
        lnStart_shopping = findViewById(R.id.lnStart_shopping);
        lnOrdering_list = findViewById(R.id.lnOrdering_list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlMenu:
                onBackPressed();
                break;
            case R.id.rlCart:
                Intent intentCart = new Intent(OrderHistoryActivity.this, MyCartActivity.class);
                startActivity(intentCart);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolder> {
        private ArrayList<OrderHistory> itemList;

        public OrderHistoryAdapter(ArrayList<OrderHistory> itemList) {
            this.itemList = itemList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            /// OrderHistory object = itemList.get(position);
            holder.tvDate.setText(itemList.get(position).getOrderedDate());
            holder.tvOrderId.setText("Order no. " + itemList.get(position).getOrderId());
            holder.tvTotal.setText("$" + itemList.get(position).getOrderTotal());
            holder.tvStatus.setText(itemList.get(position).getOrderStatusArrayList().get(0).getStatus());

            holder.btnViewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OrderHistoryActivity.this, ViewDetailActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("itemList", itemList);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate, tvTotal, tvOrderId, tvStatus;
            Button btnViewDetails;

            public MyViewHolder(View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvTotal = itemView.findViewById(R.id.tvTotal);
                tvOrderId = itemView.findViewById(R.id.tvOrderId);
                btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            }
        }
    }
}
