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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddAgentContactActivity extends AppCompatActivity {
    Button btnDone;
    FrameLayout back;
    EditText edtName, edtPhoneNo, edtEmail, edtAddress;
    static String agentID="",CALL="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_agentcontact);
        back = (FrameLayout) findViewById(R.id.flBack);
        btnDone = (Button) findViewById(R.id.btnDone);
        edtName = (EditText) findViewById(R.id.edtName);
        edtPhoneNo = (EditText) findViewById(R.id.edtPhoneNo);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        btnDone = (Button) findViewById(R.id.btnDone);

        Intent i=getIntent();
        CALL=i.getStringExtra("CALL");
        if(i.getStringExtra("CALL").equalsIgnoreCase("EDIT")){
            agentID=i.getStringExtra("AGENT_ID");
            edtName.setText(i.getStringExtra("NAME"));
            edtPhoneNo.setText(i.getStringExtra("PHONE"));
            edtEmail.setText(i.getStringExtra("EMAIL"));
            edtAddress.setText(i.getStringExtra("ADDRESS"));
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtName.getText().toString().isEmpty()){
                    Toast.makeText(AddAgentContactActivity.this, "Please add agent name", Toast.LENGTH_SHORT).show();
                }else if(edtPhoneNo.getText().toString().isEmpty()){
                    Toast.makeText(AddAgentContactActivity.this, "Please add agent phone no", Toast.LENGTH_SHORT).show();
                }else if(edtEmail.getText().toString().isEmpty()){
                    Toast.makeText(AddAgentContactActivity.this, "Please add agent email address", Toast.LENGTH_SHORT).show();
                }else if(edtAddress.getText().toString().isEmpty()){
                    Toast.makeText(AddAgentContactActivity.this, "Please add agent address", Toast.LENGTH_SHORT).show();
                }else if(!isValidEmail(edtEmail.getText().toString())){
                    Toast.makeText(AddAgentContactActivity.this, "Please add agent valid email address", Toast.LENGTH_SHORT).show();
                }else {
                    addAgent();
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
    public void addAgent() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "agents_register";

       /* JSONObject paramObj = new JSONObject();
        try {
            if(CALL.equalsIgnoreCase("EDIT")) {
                paramObj.put("agentid", agentID+"");
            }else{
                paramObj.put("agentid", "0");
            }
            paramObj.put("propertyid", getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID",""));
            paramObj.put("name", edtName.getText().toString());
            paramObj.put("phoneno", edtPhoneNo.getText().toString());
            paramObj.put("address", edtAddress.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("addAgent", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataob=new JSONObject(response.toString());
                            JSONArray msgArray = dataob.getJSONArray("Message");
                            JSONObject msgOb = msgArray.getJSONObject(0);
                            if (msgOb.getBoolean("result")) {
                                if(CALL.equalsIgnoreCase("EDIT")) {
                                    Toast.makeText(AddAgentContactActivity.this, "Agent data updated successfully",
                                            Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(AddAgentContactActivity.this, "New agent added successfully",
                                            Toast.LENGTH_LONG).show();
                                }
                                finish();
                            } else {
                                Toast.makeText(AddAgentContactActivity.this, "" + msgOb.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(AddAgentContactActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AddAgentContactActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("add agent Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(AddAgentContactActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddAgentContactActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if(CALL.equalsIgnoreCase("EDIT")) {
                    params.put("agentid", agentID+"");
                }else{
                    params.put("agentid", "0");
                }
                params.put("emailid", edtEmail.getText().toString());
                params.put("propertyid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("ID",""));
                params.put("name", "" + edtName.getText().toString());
                params.put("phoneno", "" + edtPhoneNo.getText().toString());
                params.put("address", "" + edtAddress.getText().toString());
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

}
