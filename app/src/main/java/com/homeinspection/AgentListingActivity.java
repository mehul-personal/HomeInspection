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

public class AgentListingActivity extends AppCompatActivity {
    SwipeMenuListView lsvAgentList;
    ImageView imvAddAgent;
    FrameLayout back;
    //  private List<ApplicationInfo> mAppList;
    AgentListAdapter adapter;
    ArrayList<String> propertyAgentID, name, phoneno, address, emailid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_listing);
        //   mAppList = getPackageManager().getInstalledApplications(0);

        lsvAgentList = (SwipeMenuListView) findViewById(R.id.lsvAgentList);
        imvAddAgent = (ImageView) findViewById(R.id.imvAddAgent);
        back = (FrameLayout) findViewById(R.id.flBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imvAddAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AgentListingActivity.this, AgentAssignPropertyActivity.class);
                startActivity(i);
            }
        });
        propertyAgentID = new ArrayList<String>();
        name = new ArrayList<String>();
        phoneno = new ArrayList<String>();
        address = new ArrayList<String>();
        emailid = new ArrayList<String>();

        adapter = new AgentListAdapter(propertyAgentID, name, phoneno, address, emailid);
        lsvAgentList.setAdapter(adapter);
        agentListing();

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
        lsvAgentList.setMenuCreator(creator);

        lsvAgentList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        deleteAgent(propertyAgentID.get(position));
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

    @Override
    protected void onRestart() {
        super.onRestart();
        agentListing();
    }

    public void deleteAgent(final String agentInfoId) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "deletepropertyagent";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("deletepropertyagent", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());
                            JSONArray dataArr = dataOb.getJSONArray("Message");
                            JSONObject jsob = dataArr.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                Toast.makeText(AgentListingActivity.this, "Agent deleted successfully on your property",
                                        Toast.LENGTH_LONG).show();
                                agentListing();
                            } else {
                                Toast.makeText(AgentListingActivity.this, "Oops agent deleting failure on your property",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(AgentListingActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AgentListingActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("deletepropertyagent Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(AgentListingActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AgentListingActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("agentinfoid", "" + agentInfoId);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void agentListing() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_agents";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("agent listing", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            propertyAgentID = new ArrayList<String>();
                            name = new ArrayList<String>();
                            phoneno = new ArrayList<String>();
                            address = new ArrayList<String>();
                            emailid = new ArrayList<String>();
                            JSONArray dataOb = new JSONArray(response.toString());
                            JSONObject jsob = dataOb.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                JSONArray agentArray = jsob.getJSONArray("agentsaddress");

                                for (int i = 0; i < agentArray.length(); i++) {
                                    JSONObject dataObject = agentArray.getJSONObject(i);
                                    propertyAgentID.add(dataObject.getString("agentinfoid"));
                                    name.add(dataObject.getString("name"));
                                    phoneno.add(dataObject.getString("phoneno"));
                                    address.add(dataObject.getString("address"));
                                    emailid.add(dataObject.getString("emailid"));
                                }
                                if (agentArray.length() > 0) {
                                    adapter = new AgentListAdapter(propertyAgentID, name, phoneno, address, emailid);
                                    lsvAgentList.setAdapter(adapter);
                                } else {
                                    Toast.makeText(AgentListingActivity.this, "We can't found agent data",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(AgentListingActivity.this, "We can't found agent data",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(AgentListingActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AgentListingActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("agent listing Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(AgentListingActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AgentListingActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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

    public class AgentListAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<String> propertyagentid, name, phone, address, email;
        ArrayList<Integer> imagelist;
        int m = 0;

        public AgentListAdapter(ArrayList<String> propertyagentid, ArrayList<String> name, ArrayList<String> phone, ArrayList<String> address, ArrayList<String> email) {
            // TODO Auto-generated constructor stub
            this.propertyagentid = propertyagentid;
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
           /* holder.txvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(AgentListingActivity.this, AddAgentContactActivity.class);
                    i.putExtra("CALL", "EDIT");
                    i.putExtra("AGENT_ID",propertyagentid.get(position));
                    i.putExtra("NAME", "" + name.get(position));
                    i.putExtra("PHONE", "" + phone.get(position));
                    i.putExtra("ADDRESS", "" + address.get(position));
                    i.putExtra("EMAIL", "" + email.get(position));
                    startActivity(i);
                }
            });*/
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
