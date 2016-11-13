package com.homeinspection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    FrameLayout back;
    EditText edtName, edtEmail, edtPhone, edtPassword, edtConfirmPassword;
    Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        back = (FrameLayout) findViewById(R.id.flBack);
        edtName = (EditText) findViewById(R.id.edtName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        btnDone = (Button) findViewById(R.id.btnDone);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtName.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter user name", Toast.LENGTH_LONG).show();
                } else if (edtEmail.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter email address", Toast.LENGTH_LONG).show();
                } else if (edtPhone.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter Phone No.", Toast.LENGTH_LONG).show();
                } else if (edtPassword.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter password", Toast.LENGTH_LONG).show();
                } else if (edtConfirmPassword.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter confirm password", Toast.LENGTH_LONG).show();
                } else if (!isValidEmail(edtEmail.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "Please enter valid email address", Toast.LENGTH_LONG).show();
                } else if (!edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "Your password doesn't match", Toast.LENGTH_LONG).show();
                } else {
                    setSignUp();
                }
            }
        });
    }

    public boolean isValidEmail(String email) {
        boolean isValidEmail = false;
        System.out.println(email);
        String emailExpression = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(emailExpression,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValidEmail = true;
        }
        return isValidEmail;
    }

    public void setSignUp() {


        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "userregister";/*?name="+
                edtName.getText().toString()+"&emailid="+edtEmail.getText().toString()+
                "&password="+edtPassword.getText().toString()+"&phoneno="+edtPhone.getText().toString()+"&userid=";*/
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("userregister", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());
                            JSONArray msgArray = object.getJSONArray("Message");
                            JSONObject msgOb = msgArray.getJSONObject(0);
                            if (msgOb.getBoolean("result")) {

                                Toast.makeText(SignUpActivity.this, "Your account created successfully \n Please login with your credentials",
                                        Toast.LENGTH_LONG).show();

                                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Your registered failed \n Please check all details",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(SignUpActivity.this, "Your registered failed \n Please check all details",
                                    Toast.LENGTH_LONG).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(SignUpActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("userregister Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(SignUpActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);

                Map<String, String> params = new HashMap<String, String>();
                params.put("name", edtName.getText().toString());
                params.put("emailid", edtEmail.getText().toString());
                params.put("password", edtPassword.getText().toString());
                params.put("phoneno", edtPhone.getText().toString());
                params.put("userid", "");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }
}
