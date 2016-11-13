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

public class AgentAssignPropertyActivity extends AppCompatActivity {
    static String AGENT_ID = "", NAME = "", PHONE = "", ADDRESS = "", EMAIL = "";
    ListView lsvAgentList;
    ImageView imvAddAgent;
    FrameLayout back;
    Button btnDone, btnModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_assign_property);
        lsvAgentList = (ListView) findViewById(R.id.lsvAgentList);
        imvAddAgent = (ImageView) findViewById(R.id.imvAddAgent);
        back = (FrameLayout) findViewById(R.id.flBack);
        btnDone = (Button) findViewById(R.id.btnDone);
        btnModify = (Button) findViewById(R.id.btnModify);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imvAddAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AgentAssignPropertyActivity.this, AddAgentContactActivity.class);
                i.putExtra("CALL", "NEW");
                startActivity(i);
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AGENT_ID.isEmpty())
                    assignAgent();
                else
                    Toast.makeText(AgentAssignPropertyActivity.this, "Please select any agent",
                            Toast.LENGTH_LONG).show();
            }
        });
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AGENT_ID.isEmpty()) {
                    Intent i = new Intent(AgentAssignPropertyActivity.this, AddAgentContactActivity.class);
                    i.putExtra("CALL", "EDIT");
                    i.putExtra("AGENT_ID", AGENT_ID);
                    i.putExtra("NAME", NAME);
                    i.putExtra("PHONE", PHONE);
                    i.putExtra("ADDRESS", ADDRESS);
                    i.putExtra("EMAIL", EMAIL);
                    startActivity(i);
                } else
                    Toast.makeText(AgentAssignPropertyActivity.this, "Please select any agent",
                            Toast.LENGTH_LONG).show();
            }
        });

        agentListing();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        agentListing();
    }

    public void assignAgent() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "insertagentinfo";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("insertagentinfo", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());
                            JSONArray array = dataOb.getJSONArray("Message");
                            JSONObject jsob = array.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                Toast.makeText(AgentAssignPropertyActivity.this, "Agent assigned successfully",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AgentAssignPropertyActivity.this, "Agent already assigned!",
                                        Toast.LENGTH_LONG).show();
                            }
                            finish();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(AgentAssignPropertyActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AgentAssignPropertyActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("insertagentinfo Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(AgentAssignPropertyActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AgentAssignPropertyActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", ""));
                params.put("propertyid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", ""));
                params.put("agentid", "" + AGENT_ID);
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
        String url = ApplicationData.serviceURL + "list_userwiseagents";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_userwiseagents", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            ArrayList<String> propertyAgentID = new ArrayList<String>();
                            ArrayList<String> name = new ArrayList<String>();
                            ArrayList<String> phoneno = new ArrayList<String>();
                            ArrayList<String> address = new ArrayList<String>();
                            ArrayList<String> emailid = new ArrayList<String>();
                            JSONArray dataOb = new JSONArray(response.toString());
                            JSONObject jsob = dataOb.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                JSONArray agentArray = jsob.getJSONArray("agentsaddress");

                                for (int i = 0; i < agentArray.length(); i++) {
                                    JSONObject dataObject = agentArray.getJSONObject(i);
                                    propertyAgentID.add(dataObject.getString("id"));
                                    name.add(dataObject.getString("name"));
                                    phoneno.add(dataObject.getString("phoneno"));
                                    address.add(dataObject.getString("address"));
                                    emailid.add(dataObject.getString("emailid"));
                                }
                                if (agentArray.length() > 0) {
                                    AgentListAdapter adapter = new AgentListAdapter(propertyAgentID, name, phoneno, address, emailid);
                                    lsvAgentList.setAdapter(adapter);
                                } else {
                                    Toast.makeText(AgentAssignPropertyActivity.this, "We can't found agent data",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(AgentAssignPropertyActivity.this, "We can't found agent data",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(AgentAssignPropertyActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AgentAssignPropertyActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_userwiseagents Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(AgentAssignPropertyActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AgentAssignPropertyActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
        ArrayList<String> propertyagentid, name, phone, address, email;
        ArrayList<Integer> imagelist;
        ArrayList<Boolean> selectList;
        int m = 0;

        public AgentListAdapter(ArrayList<String> propertyagentid, ArrayList<String> name, ArrayList<String> phone, ArrayList<String> address, ArrayList<String> email) {
            // TODO Auto-generated constructor stub
            this.propertyagentid = propertyagentid;
            this.name = name;
            this.phone = phone;
            this.address = address;
            this.email = email;

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
                    AGENT_ID = propertyagentid.get(position);
                    NAME = name.get(position);
                    PHONE = phone.get(position);
                    ADDRESS = address.get(position);
                    EMAIL = email.get(position);
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
