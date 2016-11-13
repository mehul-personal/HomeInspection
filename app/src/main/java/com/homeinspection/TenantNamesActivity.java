package com.homeinspection;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TenantNamesActivity extends AppCompatActivity {
    static ArrayList<String> TENANTID, PROPERTYID, TENANTNAME;
    static TenantNameAdapter adapter;
    Button done;
    FrameLayout back;
    ListView lsvTenantListName;
    ImageView imvAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenants_names);
        back = (FrameLayout) findViewById(R.id.flBack);
        done = (Button) findViewById(R.id.btnDone);
        lsvTenantListName = (ListView) findViewById(R.id.lsvTenantListName);
        imvAdd = (ImageView) findViewById(R.id.imvAdd);

        TENANTID = new ArrayList<String>();
        PROPERTYID = new ArrayList<String>();
        TENANTNAME = new ArrayList<String>();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        done = (Button) findViewById(R.id.btnDone);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < TENANTID.size(); i++) {
                    if (!TENANTNAME.get(i).isEmpty()) {
                        addTenantName(TENANTID.get(i), PROPERTYID.get(i), TENANTNAME.get(i));
                    }
                }
            }
        });
        imvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TENANTID.add("0");
                PROPERTYID.add(getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", ""));
                TENANTNAME.add("");

                adapter.notifyDataSetChanged();

            }
        });


        getTenantList();

    }

    public void getTenantList() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_tenantlist";

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_tenantlist", response.toString());
                        try {
                            TENANTID = new ArrayList<String>();
                            PROPERTYID = new ArrayList<String>();
                            TENANTNAME = new ArrayList<String>();

                            JSONArray jsonArr = new JSONArray(response.toString());
                            JSONObject jsonOb = jsonArr.getJSONObject(0);
                            if (jsonOb.getBoolean("result")) {
                                JSONArray tenantData = jsonOb.getJSONArray("tenantnamelist");
                                for (int i = 0; i < tenantData.length(); i++) {
                                    JSONObject dataOb = tenantData.getJSONObject(i);
                                    TENANTID.add(dataOb.getString("id"));
                                    PROPERTYID.add(dataOb.getString("pid"));
                                    TENANTNAME.add(dataOb.getString("name"));
                                }
                                // adapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(TenantNamesActivity.this, "Opps! We can't find the tenant list data",
                                        Toast.LENGTH_LONG).show();
                            }
                            adapter = new TenantNameAdapter(TENANTID, PROPERTYID, TENANTNAME);
                            lsvTenantListName.setAdapter(adapter);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(TenantNamesActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(TenantNamesActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                mProgressDialog.dismiss();
                VolleyLog.e("list_tenantlist Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(TenantNamesActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TenantNamesActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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

    public void addTenantName(final String tenantID, final String propertyID, final String name) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "inserttenant";

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("inserttenant", response.toString());
                        try {
                            JSONObject jsonOb = new JSONObject(response.toString());
                            JSONObject msgOb = jsonOb.getJSONArray("Message").getJSONObject(0);
                            if (msgOb.getBoolean("result")) {

                                Toast.makeText(TenantNamesActivity.this, "New tenant added successfully",
                                        Toast.LENGTH_LONG).show();

                                finish();
                            } else {
                                Toast.makeText(TenantNamesActivity.this, "" + msgOb.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(TenantNamesActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(TenantNamesActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                mProgressDialog.dismiss();
                VolleyLog.e("inserttenant Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(TenantNamesActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TenantNamesActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("tenantid", tenantID);
                params.put("propertyid", "" + propertyID);
                params.put("name", "" + name);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public class TenantNameAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<String> tenantId, propertyId, tenantName;
        private int editingPosition = 0;
        private TextWatcher watcher = new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tenantName.set(editingPosition,s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void afterTextChanged(Editable s) { }
        };
        public TenantNameAdapter(ArrayList<String> tenantId, ArrayList<String> propertyId, ArrayList<String> tenantName) {
            // TODO Auto-generated constructor stub
            this.tenantId = tenantId;
            this.propertyId = propertyId;
            this.tenantName = tenantName;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            final ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_add_tenantname_listitem,
                        parent, false);
                holder = new ViewHolder();
                holder.edtName = (EditText) convertView.findViewById(R.id.edtName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.edtName.removeTextChangedListener(watcher);

            holder.edtName.setText(tenantName.get(position));

            holder.edtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus) editingPosition = position;
                }
            });

            holder.edtName.addTextChangedListener(watcher);
            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return tenantId.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        class ViewHolder {
            EditText edtName;
        }
    }

}
