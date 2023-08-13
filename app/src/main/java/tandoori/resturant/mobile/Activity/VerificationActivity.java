package tandoori.resturant.mobile.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tandoori.resturant.mobile.CustomToast.CustomToast;
import tandoori.resturant.mobile.R;
import tandoori.resturant.mobile.receiver.SmsListener;
import tandoori.resturant.mobile.receiver.SmsReceiver;
import tandoori.resturant.mobile.utils.BaseUrl;
import tandoori.resturant.mobile.utils.NetworkAndHideKeyBoard;
import tandoori.resturant.mobile.volly.AppController;

public class VerificationActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etEnterCode;
    Button btVerify, btSkip;
    TextView tvTimeCount;
    ProgressBar mProgressBar;

    private ProgressDialog progressBar;
    private String tag_string_req = "string_req", otp1;
    private CountDownTimer countDownTimer; // built in android class
    // milliseconds
    private long totalTimeCountInMilliseconds; // total count down time in
    // CountDownTimer
    private long timeBlinkInMilliseconds; // start time of start blinking
    private boolean blink; // controls the blinking .. on and off
    private String getOtp;
    private NetworkAndHideKeyBoard networkAndHideKeyBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_screen);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            getOtp = bundle.getString("otp");
        }
        networkAndHideKeyBoard = new NetworkAndHideKeyBoard(getApplicationContext());
        windowsCode();
        initViews();
        initListeners();
        setTimer();
        startCountDownTimer();
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);//you can cancel it by pressing back button
        progressBar.setMessage(getResources().getString(R.string.loading));
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressBar = findViewById(R.id.progressBarCircle);
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                Log.d("Text", messageText);
                etEnterCode.setText(messageText);
                //Toast.makeText(VerificationOtpScreen.this, "Message: " + messageText, Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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

    private void initListeners() {
        btVerify.setOnClickListener(this);
        btSkip.setOnClickListener(this);
    }

    private void setTimer() {
        int time = 0;
        time = Integer.parseInt("60");
        totalTimeCountInMilliseconds = time * 1000;
        timeBlinkInMilliseconds = 30 * 1000;
    }
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                tvTimeCount.setText(String.format("%02d", seconds / 60)
                        + ":" + String.format("%02d", seconds % 60));
                mProgressBar.setProgress((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {

                tvTimeCount.setText("00:00");
                if (etEnterCode.getText().toString().trim().equalsIgnoreCase("")) {
                    btVerify.setText("Resend");
                    mProgressBar.setProgress(0);
                } else {
                    btVerify.setText("Verify");
                }
            }
        }.start();
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViews() {
        tvTimeCount = findViewById(R.id.tvTimeCount);
        mProgressBar = findViewById(R.id.progressBarCircle);
        etEnterCode = findViewById(R.id.etEnterCode);
        btVerify = findViewById(R.id.btVerify);
        btSkip = findViewById(R.id.btSkip);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btVerify:
                if (btVerify.getText().toString().equalsIgnoreCase("Resend")) {
                    Log.i("", "");
                    setTimer();
                    startCountDownTimer();
                } else if (btVerify.getText().toString().equalsIgnoreCase("Verify")) {
                    Log.i("", "");
                    if (NetworkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
                        progressBar.show();
                        verify_user();
                    } else {
                        AppController.getInstance().showDialog(VerificationActivity.this, "Internet Error", "No Internet Found!!");
                    }
                }

               /* if (!etEnterCode.getText().toString().equalsIgnoreCase(getOtp)) {
                    Toast.makeText(VerificationActivity.this, R.string.correwctOtp, Toast.LENGTH_SHORT).show();
                } else {
                    if (NetworkAndHideKeyBoard.isNetworkAvailable(getApplicationContext())) {
                        progressBar.show();
                        verify_user();
                    } else {
                        AppController.getInstance().showDialog(VerificationActivity.this, "Internet Error", "No Internet Found!!");
                    }
                }*/
                break;
            case R.id.btSkip:
                Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
                startActivity(intent);
        }
    }

    private void verify_user() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);
        pDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user",getIntent().getStringExtra("user"));
            jsonObject.put("state",getIntent().getStringExtra("state"));
            jsonObject.put("otp",etEnterCode.getText().toString().trim());
            final JsonObjectRequest loginResult = new JsonObjectRequest(Request.Method.POST, BaseUrl.OTP, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.dismiss();
                            if (response.length() == 0) {
                                Toast.makeText(VerificationActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    countDownTimer.cancel();
                                    if (jsonObject.optString("request_status").equalsIgnoreCase("true")) {
                                        JSONObject objJson = jsonObject.optJSONObject("object");
                                        if (objJson.optString("status").equalsIgnoreCase("true")) {
                                            AppController.getInstance().saveString("expires_in", objJson.optString("expires_in"));
                                            AppController.getInstance().saveString("access_token", objJson.optString("access_token"));
                                            Intent intent = new Intent(VerificationActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            CustomToast.show(VerificationActivity.this, objJson.getString("error"), true, getResources().getDrawable(R.drawable.yellowshape));
                                        }
                                        AppController.getInstance().saveString("requestId", jsonObject.optString("requestId"));
                                    } else if (jsonObject.optString("request_status").equalsIgnoreCase("false")) {
                                        CustomToast.show(VerificationActivity.this, jsonObject.getString("error"), true, getResources().getDrawable(R.drawable.yellowshape));
                                    } else {
                                        CustomToast.show(VerificationActivity.this, getResources().getString(R.string.serverError), true, getResources().getDrawable(R.drawable.red));
                                    }
                                } catch (Exception e) {
                                    CustomToast.show(VerificationActivity.this, e.toString(), true, getResources().getDrawable(R.drawable.red));
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();
                            Log.d("ERROR1:", "");
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(VerificationActivity.this);
                            alertDialog.setMessage(getResources().getString(R.string.serverError));
                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog.show();
//                            Toast.makeText(VerificationActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        countDownTimer.cancel();
        finish();
    }
}