package com.homeinspection;

import android.app.ProgressDialog;
import android.content.Intent;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddInventoryProviderActivity extends AppCompatActivity {
    static String InventoryId = "", CALL = "";
    FrameLayout back;
    EditText edtName, edtPhone, edtEmail, edtAddress, edtAddress2, edtCity, edtCountry, edtPostCode;
    Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_provider);
        back = (FrameLayout) findViewById(R.id.flBack);
        edtName = (EditText) findViewById(R.id.edtName);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtAddress2 = (EditText) findViewById(R.id.edtAddress2);
        edtCity = (EditText) findViewById(R.id.edtCity);
        edtCountry = (EditText) findViewById(R.id.edtCountry);
        edtPostCode = (EditText) findViewById(R.id.edtPostCode);

        btnDone = (Button) findViewById(R.id.btnDone);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent i = getIntent();
        CALL = i.getStringExtra("CALL");
        if (i.getStringExtra("CALL").equalsIgnoreCase("EDIT")) {
            /*
             i.putExtra("INVENTORY_PROVIDER_ID", INVENTORY_PROVIDER_ID);
                    i.putExtra("NAME", NAME);
                    i.putExtra("PHONE", PHONE);
                    i.putExtra("ADDRESS", ADDRESS);
                    i.putExtra("EMAIL", EMAIL);
                    i.putExtra("ADDRESS2",ADDRESS2);
                    i.putExtra("CITY",CITY);
                    i.putExtra("COUNTY",COUNTY);
                    i.putExtra("POSTCODE",POSTCODE);
             */
            InventoryId = i.getStringExtra("INVENTORY_PROVIDER_ID");
            edtName.setText(i.getStringExtra("NAME"));
            edtPhone.setText(i.getStringExtra("PHONE"));
            edtEmail.setText(i.getStringExtra("EMAIL"));
            edtAddress.setText(i.getStringExtra("ADDRESS"));
            edtAddress2.setText(i.getStringExtra("ADDRESS2"));
            edtCity.setText(i.getStringExtra("CITY"));
            edtCountry.setText(i.getStringExtra("COUNTY"));
            edtPostCode.setText(i.getStringExtra("POSTCODE"));
        }

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtName.getText().toString().isEmpty()) {
                    Toast.makeText(AddInventoryProviderActivity.this, "Please enter inventory provider name", Toast.LENGTH_SHORT).show();
                } else if (edtEmail.getText().toString().isEmpty()) {
                    Toast.makeText(AddInventoryProviderActivity.this, "Please enter email address", Toast.LENGTH_LONG).show();
                } else if (edtAddress.getText().toString().isEmpty()) {
                    Toast.makeText(AddInventoryProviderActivity.this, "Please enter address", Toast.LENGTH_LONG).show();
                } else if (edtCity.getText().toString().isEmpty()) {
                    Toast.makeText(AddInventoryProviderActivity.this, "Please enter city", Toast.LENGTH_LONG).show();
                } else if (edtCountry.getText().toString().isEmpty()) {
                    Toast.makeText(AddInventoryProviderActivity.this, "Please enter country", Toast.LENGTH_LONG).show();
                } else if (edtPostCode.getText().toString().isEmpty()) {
                    Toast.makeText(AddInventoryProviderActivity.this, "Please enter post code", Toast.LENGTH_LONG).show();
                } else if (!isValidEmail(edtEmail.getText().toString())) {
                    Toast.makeText(AddInventoryProviderActivity.this, "Please enter valid email address", Toast.LENGTH_LONG).show();
                } else {
                    addInventoryProvider(getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", ""),
                            edtName.getText().toString(), edtPhone.getText().toString(), edtEmail.getText().toString(),
                            edtAddress.getText().toString(), edtAddress2.getText().toString(),
                            edtCity.getText().toString(), edtCountry.getText().toString(), edtPostCode.getText().toString());
                }//edtName, edtPhone, edtEmail, edtAddress, edtAddress2, edtCity, edtCountry, edtPostCode
            }
        });
        // getInventoryProviderList();
    }

/*
    public void getInventoryProviderList() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_inventoryprovider";

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_inventoryprovider", response.toString());
                        try {

                            JSONArray jsonArr = new JSONArray(response.toString());
                            JSONObject jsonOb = jsonArr.getJSONObject(0);
                            if (jsonOb.getBoolean("result")) {
                                JSONArray tenantData = jsonOb.getJSONArray("inventoryprovider");
                                JSONObject dataOb = tenantData.getJSONObject(0);
                                InventoryId = dataOb.getString("id");
                                dataOb.getString("pid");

                                edtName.setText(dataOb.getString("provider_name"));
                                edtPhone.setText(dataOb.getString("phoneno"));
                                edtEmail.setText(dataOb.getString("emailid"));
                                edtAddress.setText(dataOb.getString("address1"));
                                edtAddress2.setText(dataOb.getString("address2"));
                                edtCity.setText(dataOb.getString("city"));
                                edtCountry.setText(dataOb.getString("county"));
                                edtPostCode.setText(dataOb.getString("postcode"));
                            } else {
                                Toast.makeText(AddInventoryProviderActivity.this, "Opps! We can't find the Provider data",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(AddInventoryProviderActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AddInventoryProviderActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                mProgressDialog.dismiss();
                VolleyLog.e("list_inventoryprovider Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(AddInventoryProviderActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddInventoryProviderActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("propertyid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", ""));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }
*/

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

    //edtName, edtPhone, edtEmail, edtAddress, edtAddress2, edtCity, edtCountry, edtPostCode
    public void addInventoryProvider(final String propertyID, final String providername, final String phone, final String email, final String address1,
                                     final String address2, final String city, final String country, final String zipcode) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "inventoryprovider";

//        JSONObject paramObj = new JSONObject();
//        try {
//            paramObj.put("tenantid",tenantID);
//            paramObj.put("propertyid", propertyID);
//            paramObj.put("name", name);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("add inventory provider", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject jsonOb = new JSONObject(response.toString());
                            JSONObject msgOb = jsonOb.getJSONArray("Message").getJSONObject(0);
                            if (msgOb.getBoolean("result")) {
                                if (CALL.equalsIgnoreCase("EDIT")) {
                                    Toast.makeText(AddInventoryProviderActivity.this, "Inventory provider data updated successfully",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(AddInventoryProviderActivity.this, "New inventory provider added successfully",
                                            Toast.LENGTH_LONG).show();
                                }
                                finish();
                            } else {
                                Toast.makeText(AddInventoryProviderActivity.this, "" + msgOb.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(AddInventoryProviderActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AddInventoryProviderActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("add inventory provider Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(AddInventoryProviderActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddInventoryProviderActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//edtName, edtPhone, edtEmail, edtAddress, edtAddress2, edtCity, edtCountry, edtPostCode
                if (CALL.equalsIgnoreCase("EDIT")) {
                    params.put("providerid", InventoryId);
                } else {
                    params.put("providerid", "0");
                }
                params.put("propertyid", "" + propertyID);
                params.put("providername", "" + providername);
                params.put("address1", "" + address1);
                params.put("address2", "" + address2);
                params.put("phoneno", "" + phone);
                params.put("emailid", "" + email);
                params.put("city", "" + city);
                params.put("county", "" + country);
                params.put("zipcode", "" + zipcode);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

}
