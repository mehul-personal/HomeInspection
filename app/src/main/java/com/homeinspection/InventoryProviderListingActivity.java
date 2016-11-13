package com.homeinspection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InventoryProviderListingActivity extends AppCompatActivity {
    SwipeMenuListView lsvInventoryProviderList;
    ImageView imvAddInventoryProvider;
    FrameLayout back;
    ProviderListAdapter adapter;
    ArrayList<String> providerID, name, phoneno, address, emailid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invetory_provider_listing);
        lsvInventoryProviderList = (SwipeMenuListView) findViewById(R.id.lsvInventoryProviderList);
        imvAddInventoryProvider = (ImageView) findViewById(R.id.imvAddInventoryProvider);
        back = (FrameLayout) findViewById(R.id.flBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imvAddInventoryProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InventoryProviderListingActivity.this, InventoryProviderAssignPropertyActivity.class);
                startActivity(i);
            }
        });
        providerID = new ArrayList<String>();
        name = new ArrayList<String>();
        phoneno = new ArrayList<String>();
        address = new ArrayList<String>();
        emailid = new ArrayList<String>();

        adapter = new ProviderListAdapter(providerID, name, phoneno, address, emailid);
        lsvInventoryProviderList.setAdapter(adapter);

        inventoryProviderList();
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                switch (menu.getViewType()) {
                    case 0:
                        createMenu1(menu);
                        break;

                }
            }

            private void createMenu1(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(
                        getApplicationContext());
                item1.setBackground(R.color.colorPrimary);

                item1.setWidth(dp2px(100));
                item1.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(item1);
            }

        };
        lsvInventoryProviderList.setMenuCreator(creator);

        lsvInventoryProviderList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        deleteInventoryProvider(providerID.get(position));
                        break;
                }
                return false;
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public void deleteInventoryProvider(final String providerInfoId) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "deleteproviderinfo";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("deleteproviderinfo", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());
                            JSONArray dataArr = dataOb.getJSONArray("Message");
                            JSONObject jsob = dataArr.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                Toast.makeText(InventoryProviderListingActivity.this, "Inventory Provider deleted successfully on your property",
                                        Toast.LENGTH_LONG).show();
                                inventoryProviderList();
                            } else {
                                Toast.makeText(InventoryProviderListingActivity.this, "Oops Inventory Provider deleting failure on your property",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(InventoryProviderListingActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(InventoryProviderListingActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("deleteproviderinfo Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(InventoryProviderListingActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(InventoryProviderListingActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("providerinfoid", "" + providerInfoId);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        inventoryProviderList();
    }

    public void inventoryProviderList() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_inventoryprovider";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_inventoryprovider", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            providerID = new ArrayList<String>();
                            name = new ArrayList<String>();
                            phoneno = new ArrayList<String>();
                            address = new ArrayList<String>();
                            emailid = new ArrayList<String>();
                            JSONArray dataOb = new JSONArray(response.toString());
                            JSONObject jsob = dataOb.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                JSONArray agentArray = jsob.getJSONArray("inventoryprovider");

                                for (int i = 0; i < agentArray.length(); i++) {
                                    JSONObject dataObject = agentArray.getJSONObject(i);
                                    providerID.add(dataObject.getString("providerinfoid"));
                                    name.add(dataObject.getString("provider_name"));
                                    phoneno.add(dataObject.getString("phoneno"));
                                    address.add(dataObject.getString("address1"));
                                    emailid.add(dataObject.getString("emailid"));
                                }
                                if (agentArray.length() > 0) {
                                    adapter = new ProviderListAdapter(providerID, name, phoneno, address, emailid);
                                    lsvInventoryProviderList.setAdapter(adapter);
                                } else {
                                    Toast.makeText(InventoryProviderListingActivity.this, "We can't found inventory provider data",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(InventoryProviderListingActivity.this, "We can't found inventory provider data",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(InventoryProviderListingActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(InventoryProviderListingActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_inventoryprovider Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(InventoryProviderListingActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(InventoryProviderListingActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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

    public class ProviderListAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<String> propertyproviderid, name, phone, address, email;
        ArrayList<Integer> imagelist;
        int m = 0;

        public ProviderListAdapter(ArrayList<String> propertyproviderid, ArrayList<String> name, ArrayList<String> phone, ArrayList<String> address, ArrayList<String> email) {
            // TODO Auto-generated constructor stub
            this.propertyproviderid = propertyproviderid;
            this.name = name;
            this.phone = phone;
            this.address = address;
            this.email = email;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_agent_listitem,
                        parent, false);
                holder = new ViewHolder();
                holder.txvName = (TextView) convertView.findViewById(R.id.txvAgentName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txvName.setText(name.get(position));
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
        }
    }

}
