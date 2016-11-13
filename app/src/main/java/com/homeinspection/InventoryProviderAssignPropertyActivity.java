package com.homeinspection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InventoryProviderAssignPropertyActivity extends AppCompatActivity {
    static String INVENTORY_PROVIDER_ID = "", NAME = "", PHONE = "", ADDRESS = "", EMAIL = "", ADDRESS2 = "", CITY = "", COUNTY = "", POSTCODE = "";
    ListView lsvInventoryProviderList;
    ImageView imvAddInventoryProvider;
    FrameLayout back;
    Button btnDone, btnModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_provider_assign_property);
        lsvInventoryProviderList = (ListView) findViewById(R.id.lsvInventoryProviderList);
        imvAddInventoryProvider = (ImageView) findViewById(R.id.imvAddInventoryProvider);
        back = (FrameLayout) findViewById(R.id.flBack);
        btnDone = (Button) findViewById(R.id.btnDone);
        btnModify = (Button) findViewById(R.id.btnModify);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imvAddInventoryProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InventoryProviderAssignPropertyActivity.this, AddInventoryProviderActivity.class);
                i.putExtra("CALL", "NEW");
                startActivity(i);
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!INVENTORY_PROVIDER_ID.isEmpty())
                    assignInventoryProvider();
                else
                    Toast.makeText(InventoryProviderAssignPropertyActivity.this, "Please select any inventory provider",
                            Toast.LENGTH_LONG).show();
            }
        });
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!INVENTORY_PROVIDER_ID.isEmpty()) {
                    Intent i = new Intent(InventoryProviderAssignPropertyActivity.this, AddInventoryProviderActivity.class);
                    i.putExtra("CALL", "EDIT");
                    i.putExtra("INVENTORY_PROVIDER_ID", INVENTORY_PROVIDER_ID);
                    i.putExtra("NAME", NAME);
                    i.putExtra("PHONE", PHONE);
                    i.putExtra("ADDRESS", ADDRESS);
                    i.putExtra("EMAIL", EMAIL);
                    i.putExtra("ADDRESS2", ADDRESS2);
                    i.putExtra("CITY", CITY);
                    i.putExtra("COUNTY", COUNTY);
                    i.putExtra("POSTCODE", POSTCODE);
                    startActivity(i);
                } else
                    Toast.makeText(InventoryProviderAssignPropertyActivity.this, "Please select any inventory provider",
                            Toast.LENGTH_LONG).show();
            }
        });

        inventoryProviderList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        inventoryProviderList();
    }

    public void assignInventoryProvider() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "insert_inventoryproviderinfo";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("insert_inventprovinfo", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());
                            JSONArray array = dataOb.getJSONArray("Message");
                            JSONObject jsob = array.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                Toast.makeText(InventoryProviderAssignPropertyActivity.this, "Inventory provider assigned successfully",
                                        Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(InventoryProviderAssignPropertyActivity.this, "Inventory provider already assigned",
                                        Toast.LENGTH_LONG).show();
                            }
                            finish();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(InventoryProviderAssignPropertyActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(InventoryProviderAssignPropertyActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("insert_inventprovinfo Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(InventoryProviderAssignPropertyActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(InventoryProviderAssignPropertyActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", ""));
                params.put("propertyid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", ""));
                params.put("providerid", "" + INVENTORY_PROVIDER_ID);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void inventoryProviderList() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_inventoryprovideruserwise";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_invenryprideruser", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            ArrayList<String> propertyAgentID = new ArrayList<String>();
                            ArrayList<String> name = new ArrayList<String>();
                            ArrayList<String> phoneno = new ArrayList<String>();
                            ArrayList<String> emailid = new ArrayList<String>();
                            ArrayList<String> address = new ArrayList<String>();
                            ArrayList<String> address2 = new ArrayList<String>();
                            ArrayList<String> city = new ArrayList<String>();
                            ArrayList<String> county = new ArrayList<String>();
                            ArrayList<String> postcode = new ArrayList<String>();
                            JSONArray dataOb = new JSONArray(response.toString());
                            JSONObject jsob = dataOb.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                JSONArray agentArray = jsob.getJSONArray("inventoryprovider");

                                for (int i = 0; i < agentArray.length(); i++) {
                                    JSONObject dataObject = agentArray.getJSONObject(i);
                                    propertyAgentID.add(dataObject.getString("id"));
                                    name.add(dataObject.getString("provider_name"));
                                    phoneno.add(dataObject.getString("phoneno"));
                                    emailid.add(dataObject.getString("emailid"));
                                    address.add(dataObject.getString("address1"));
                                    address2.add(dataObject.getString("address2"));
                                    city.add(dataObject.getString("city"));
                                    county.add(dataObject.getString("county"));
                                    postcode.add(dataObject.getString("postcode"));
                                }
                                if (agentArray.length() > 0) {
                                    AgentListAdapter adapter = new AgentListAdapter(propertyAgentID, name, phoneno, address, emailid, address2, city, county, postcode);
                                    lsvInventoryProviderList.setAdapter(adapter);
                                } else {
                                    Toast.makeText(InventoryProviderAssignPropertyActivity.this, "We can't found inventory provider data",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(InventoryProviderAssignPropertyActivity.this, "We can't found inventory provider data",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(InventoryProviderAssignPropertyActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(InventoryProviderAssignPropertyActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_invenryprideruser Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(InventoryProviderAssignPropertyActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(InventoryProviderAssignPropertyActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", ""));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public class AgentListAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<String> propertyagentid, name, phone, address, email, address2, city, county, postcode;
        ArrayList<Integer> imagelist;
        ArrayList<Boolean> selectList;
        int m = 0;

        public AgentListAdapter(ArrayList<String> propertyagentid, ArrayList<String> name,
                                ArrayList<String> phone, ArrayList<String> address, ArrayList<String> email,
                                ArrayList<String> address2, ArrayList<String> city, ArrayList<String> county, ArrayList<String> postcode) {
            // TODO Auto-generated constructor stub
            this.propertyagentid = propertyagentid;
            this.name = name;
            this.phone = phone;
            this.address = address;
            this.email = email;
            this.address2 = address2;
            this.city = city;
            this.county = county;
            this.postcode = postcode;
            selectList = new ArrayList<Boolean>();
            for (int i = 0; i < name.size(); i++) {
                selectList.add(false);
            }
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            final ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_agent_listitem,
                        parent, false);
                holder = new ViewHolder();
                holder.txvName = (TextView) convertView.findViewById(R.id.txvAgentName);
                holder.itemRow = (LinearLayout) convertView.findViewById(R.id.itemRow);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txvName.setText(name.get(position));

            if (selectList.get(position)) {
                holder.itemRow.setBackgroundResource(R.color.colorSelectedButton);
            } else {
                holder.itemRow.setBackgroundResource(0);
            }
            holder.txvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < name.size(); i++) {
                        selectList.set(i, false);
                    }
                    selectList.set(position, true);
                    holder.itemRow.setBackgroundResource(R.color.colorSelectedButton);
                    INVENTORY_PROVIDER_ID = propertyagentid.get(position);
                    NAME = name.get(position);
                    PHONE = phone.get(position);
                    ADDRESS = address.get(position);
                    EMAIL = email.get(position);
                    ADDRESS2 = address2.get(position);
                    CITY = city.get(position);
                    COUNTY = county.get(position);
                    POSTCODE = postcode.get(position);
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return name.size();
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
            TextView txvName;
            LinearLayout itemRow;
        }
    }

}
