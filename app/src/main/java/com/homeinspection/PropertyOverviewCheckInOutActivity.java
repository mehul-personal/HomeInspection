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
import android.widget.TextView;
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

public class PropertyOverviewCheckInOutActivity extends AppCompatActivity {
    FrameLayout back;
    TextView header;
    EditText edtCheckInDescription, edtCheckInAdditionalComment, edtCheckOutAdditionalComment;
    Button btnCheckInDone, btnCheckOutDone;
    String apiCall = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_overview_check_inout);
        back = (FrameLayout) findViewById(R.id.flBack);
        header = (TextView) findViewById(R.id.txvHeader);
        edtCheckInDescription = (EditText) findViewById(R.id.edtCheckInDescription);
        edtCheckInAdditionalComment = (EditText) findViewById(R.id.edtCheckInAdditionalComment);
        edtCheckOutAdditionalComment = (EditText) findViewById(R.id.edtCheckOutAdditionalComment);
        btnCheckInDone = (Button) findViewById(R.id.btnCheckInDone);
        btnCheckOutDone = (Button) findViewById(R.id.btnCheckOutDone);

        Intent i = getIntent();
        header.setText(i.getStringExtra("HEADER"));
        apiCall = i.getStringExtra("APICALL");
        edtCheckInDescription.setText(i.getStringExtra("IN_DESC"));
        edtCheckInAdditionalComment.setText(i.getStringExtra("IN_ADDITIONAL"));
        edtCheckOutAdditionalComment.setText(i.getStringExtra("OUT_DESC"));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnCheckInDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                    insertPropertyOverview(edtCheckInDescription.getText().toString(), edtCheckInAdditionalComment.getText().toString(),
                            apiCall, getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", ""));
                } else {
                    Toast.makeText(PropertyOverviewCheckInOutActivity.this, "Sorry! You have started " +
                            getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + " process", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnCheckOutDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                    insertPropertyOverview(edtCheckOutAdditionalComment.getText().toString(), "",
                            apiCall, getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", ""));
                } else {
                    Toast.makeText(PropertyOverviewCheckInOutActivity.this, "Sorry! You have started " +
                            getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + " process", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void insertPropertyOverview(final String generalDesc, final String additionalDesc, final String type, final String propertyTypeId) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "insertpropertyoverview";

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("insertpropertyoverview", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());

                            JSONArray msgArray = dataOb.getJSONArray("Message");
                            JSONObject msgOb = msgArray.getJSONObject(0);
                            if (msgOb.getBoolean("result")) {

                                Toast.makeText(PropertyOverviewCheckInOutActivity.this, "Property overview comment saved successfully",
                                        Toast.LENGTH_LONG).show();
                                Intent i = new Intent();
                                i.putExtra("msg", "SUCCESS");
                                setResult(11, i);
                                finish();
                            } else {
                                Toast.makeText(PropertyOverviewCheckInOutActivity.this, "" + msgOb.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(PropertyOverviewCheckInOutActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(PropertyOverviewCheckInOutActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("insertpropertyoverview Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(PropertyOverviewCheckInOutActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PropertyOverviewCheckInOutActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
// ?emailid="+edtEmail.getText().toString()+"&password="+edtPassword.getText().toString()
                params.put("propertytypeid", "" + propertyTypeId);
                params.put("overview_general", "" + generalDesc);
                params.put("overview_addtional", "" + additionalDesc);
                params.put("propertytype", "" + type);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

}
