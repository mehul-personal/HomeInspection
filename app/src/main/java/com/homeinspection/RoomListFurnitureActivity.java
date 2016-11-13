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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class RoomListFurnitureActivity extends AppCompatActivity {
    static ArrayList<String> idList, nameList, descList, additionalList, image1List, image2List, image3List, image4List;
    static FurnitureListAdapter adapter;
    FrameLayout back;
    SwipeMenuListView furnitureList;
    EditText edtFurniture;
    Button done;
    String HEADER = "", API_CALL = "", ROOM_ID = "", call = "";
    ImageView add;
    TextView header, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_furniture);
        back = (FrameLayout) findViewById(R.id.flBack);
        furnitureList = (SwipeMenuListView) findViewById(R.id.lvFurnitureList);
        edtFurniture = (EditText) findViewById(R.id.edtFurniture);
        header = (TextView) findViewById(R.id.txvHeader);
        comment = (TextView) findViewById(R.id.comment);
        add = (ImageView) findViewById(R.id.imvAdd);
        done = (Button) findViewById(R.id.btnDone);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtFurniture.setText("");
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtFurniture.getText().toString().isEmpty()) {
                    idList.add("");
                    nameList.add(edtFurniture.getText().toString());
                    descList.add("");
                    additionalList.add("");
                    image1List.add("");
                    image3List.add("");
                    image4List.add("");

                    adapter = new FurnitureListAdapter(nameList, descList, additionalList, image1List, image2List, image3List, image4List);
                    furnitureList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    edtFurniture.setText("");

                } else {
                    Toast.makeText(RoomListFurnitureActivity.this, "Please enter furniture name", Toast.LENGTH_LONG).show();
                }
            }
        });
        idList = new ArrayList<String>();
        nameList = new ArrayList<String>();
        descList = new ArrayList<String>();
        additionalList = new ArrayList<String>();
        image1List = new ArrayList<String>();
        image2List = new ArrayList<String>();
        image3List = new ArrayList<String>();
        image4List = new ArrayList<String>();

        Intent i = getIntent();
        HEADER = i.getStringExtra("HEADER");
        call = i.getStringExtra("CALL");
        header.setText(HEADER);
        comment.setText(HEADER + " Type");
        edtFurniture.setHint(HEADER);
        API_CALL = i.getStringExtra("API_CALL");
        ROOM_ID = i.getStringExtra("ROOM_ID");
        try {
            if (API_CALL.equalsIgnoreCase("insertstandard_room_furniture") || API_CALL.equalsIgnoreCase("insertkitchen_room_furniture") ||
                    API_CALL.equalsIgnoreCase("insertbathroom_furniture") || API_CALL.equalsIgnoreCase("insertstairs_landing_furniture")) {
                JSONArray ROOM_FURNITURE = new JSONArray(i.getStringExtra("ROOM_FURNITURE"));
                for (int j = 0; j < ROOM_FURNITURE.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_FURNITURE.getJSONObject(j);
                        String MATCHID="";
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("swid");
                        }
                        if (MATCHID.equalsIgnoreCase(ROOM_ID)) {
                            idList.add(jsob.getString("id"));
                            nameList.add(jsob.getString("furniture_title"));
                            descList.add(jsob.getString("general_desc"));
                            additionalList.add(jsob.getString("additional_comm"));
                            image1List.add(jsob.getString("furn_image1"));
                            image2List.add(jsob.getString("furn_image2"));
                            image3List.add(jsob.getString("furn_image3"));
                            image4List.add(jsob.getString("furn_image4"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                adapter = new FurnitureListAdapter(nameList, descList, additionalList, image1List, image2List, image3List, image4List);
                furnitureList.setAdapter(adapter);
            } else if (API_CALL.equalsIgnoreCase("insertkitchen_room_appliances")) {
                JSONArray ROOM_APPLIANCES = new JSONArray(i.getStringExtra("ROOM_APPLIANCES"));
                for (int j = 0; j < ROOM_APPLIANCES.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_APPLIANCES.getJSONObject(j);
                        String MATCHID="";
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("swid");
                        }
                        if (MATCHID.equalsIgnoreCase(ROOM_ID)) {
                            idList.add(jsob.getString("id"));
                            nameList.add(jsob.getString("appliances_title"));
                            descList.add(jsob.getString("general_desc"));
                            additionalList.add(jsob.getString("additional_comm"));
                            image1List.add(jsob.getString("app_image1"));
                            image2List.add(jsob.getString("app_image2"));
                            image3List.add(jsob.getString("app_image3"));
                            image4List.add(jsob.getString("app_image4"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                adapter = new FurnitureListAdapter(nameList, descList, additionalList, image1List, image2List, image3List, image4List);
                furnitureList.setAdapter(adapter);
            } else if (API_CALL.equalsIgnoreCase("insertbathroom_sanitary")) {
                JSONArray ROOM_SANITARY = new JSONArray(i.getStringExtra("ROOM_SANITARY"));
                for (int j = 0; j < ROOM_SANITARY.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_SANITARY.getJSONObject(j);
                        String MATCHID="";
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("swid");
                        }
                        if (MATCHID.equalsIgnoreCase(ROOM_ID)) {
                            idList.add(jsob.getString("id"));
                            nameList.add(jsob.getString("sanitary_title"));
                            descList.add(jsob.getString("general_desc"));
                            additionalList.add(jsob.getString("additional_comm"));
                            image1List.add(jsob.getString("app_image1"));
                            image2List.add(jsob.getString("app_image2"));
                            image3List.add(jsob.getString("app_image3"));
                            image4List.add(jsob.getString("app_image4"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                adapter = new FurnitureListAdapter(nameList, descList, additionalList, image1List, image2List, image3List, image4List);
                furnitureList.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        furnitureList.setMenuCreator(creator);

        furnitureList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        if (call.equalsIgnoreCase("STANDARD")) {
                            deleteStandardRoomFurniture(idList.get(position));
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            if (HEADER.equalsIgnoreCase("Furniture")) {
                                deleteKitchenFurniture(idList.get(position));
                            } else if (HEADER.equalsIgnoreCase("Appliances")) {
                                deleteKitchenAppliances(idList.get(position));
                            }
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            if (HEADER.equalsIgnoreCase("Furniture")) {
                                deleteBathroomFurniture(idList.get(position));
                            } else if (HEADER.equalsIgnoreCase("Sanitary")) {
                                deleteBathroomSanitary(idList.get(position));
                            }
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            deleteStairsLandingFurniture(idList.get(position));
                        }
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

    public void deleteStandardRoomFurniture(final String furnitureid) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "deletestandard_furniture";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("deletestandard_furnitur", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());
                            JSONArray dataArr = dataOb.getJSONArray("Message");
                            JSONObject jsob = dataArr.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                Toast.makeText(RoomListFurnitureActivity.this, "Your Furniture deleted successfully on your property",
                                        Toast.LENGTH_LONG).show();
                                finish();
                                // getStandardRoomList();
                            } else {
                                Toast.makeText(RoomListFurnitureActivity.this, "Oops Your Furniture deleting failure on your property",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomListFurnitureActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomListFurnitureActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("deletestandard_furniture Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomListFurnitureActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomListFurnitureActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("standardfurnitureid", "" + furnitureid);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void deleteKitchenFurniture(final String furnitureid) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "deletekitchen_furniture";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("deletekitchen_furniture", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());
                            JSONArray dataArr = dataOb.getJSONArray("Message");
                            JSONObject jsob = dataArr.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                Toast.makeText(RoomListFurnitureActivity.this, "Your Furniture deleted successfully on your property",
                                        Toast.LENGTH_LONG).show();
                                finish();
                                // getStandardRoomList();
                            } else {
                                Toast.makeText(RoomListFurnitureActivity.this, "Oops Your Furniture deleting failure on your property",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomListFurnitureActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomListFurnitureActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("deletekitchen_furniture Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomListFurnitureActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomListFurnitureActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("kitchenfurnitureid", "" + furnitureid);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void deleteKitchenAppliances(final String furnitureid) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "deletekitchen_appliances";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("deletekitchen_appliance", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());
                            JSONArray dataArr = dataOb.getJSONArray("Message");
                            JSONObject jsob = dataArr.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                Toast.makeText(RoomListFurnitureActivity.this, "Your Kitchen Appliances deleted successfully on your property",
                                        Toast.LENGTH_LONG).show();
                                finish();
                                // getStandardRoomList();
                            } else {
                                Toast.makeText(RoomListFurnitureActivity.this, "Oops Your Kitchen Appliances deleting failure on your property",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomListFurnitureActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomListFurnitureActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("deletekitchen_appliances Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomListFurnitureActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomListFurnitureActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("kitchenappliancesid", "" + furnitureid);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void deleteBathroomFurniture(final String furnitureid) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "deletebathroom_furniture";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("deletebathroom_furnitu", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());
                            JSONArray dataArr = dataOb.getJSONArray("Message");
                            JSONObject jsob = dataArr.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                Toast.makeText(RoomListFurnitureActivity.this, "Your Bathroom Furniture deleted successfully on your property",
                                        Toast.LENGTH_LONG).show();
                                finish();
                                // getStandardRoomList();
                            } else {
                                Toast.makeText(RoomListFurnitureActivity.this, "Oops Your Bathroom Furniture deleting failure on your property",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomListFurnitureActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomListFurnitureActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("deletebathroom_furniture Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomListFurnitureActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomListFurnitureActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("bathroomfurnitureid", "" + furnitureid);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void deleteBathroomSanitary(final String furnitureid) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "deletebathroom_sanitary";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("deletebathroom_sanitary", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());
                            JSONArray dataArr = dataOb.getJSONArray("Message");
                            JSONObject jsob = dataArr.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                Toast.makeText(RoomListFurnitureActivity.this, "Your Bathroom Sanitary deleted successfully on your property",
                                        Toast.LENGTH_LONG).show();
                                finish();
                                // getStandardRoomList();
                            } else {
                                Toast.makeText(RoomListFurnitureActivity.this, "Oops Your Bathroom Sanitary deleting failure on your property",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomListFurnitureActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomListFurnitureActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("deletebathroom_sanitary Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomListFurnitureActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomListFurnitureActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("bathroomsanitaryid", "" + furnitureid);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void deleteStairsLandingFurniture(final String furnitureid) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "deletestairs_landing_furniture";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("deletestairs_landing", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());
                            JSONArray dataArr = dataOb.getJSONArray("Message");
                            JSONObject jsob = dataArr.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                Toast.makeText(RoomListFurnitureActivity.this, "Your Stairs & Landing furniture deleted successfully on your property",
                                        Toast.LENGTH_LONG).show();
                                finish();
                                // getStandardRoomList();
                            } else {
                                Toast.makeText(RoomListFurnitureActivity.this, "Oops Your Stairs & Landing furniture deleting failure on your property",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomListFurnitureActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomListFurnitureActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("deletestairs_landing_furniture Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomListFurnitureActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomListFurnitureActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("stairslandingfurnitureid", "" + furnitureid);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public class FurnitureListAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<String> NAME, DESC, ADDT, IMAGE1, IMAGE2, IMAGE3, IMAGE4;
        int m = 0;

        public FurnitureListAdapter(ArrayList<String> name, ArrayList<String> desc, ArrayList<String> addtional,
                                    ArrayList<String> image1, ArrayList<String> image2, ArrayList<String> image3, ArrayList<String> image4) {
            // TODO Auto-generated constructor stub
            NAME = name;
            DESC = desc;
            ADDT = addtional;
            IMAGE1 = image1;
            IMAGE2 = image2;
            IMAGE3 = image3;
            IMAGE4 = image4;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_furniture_listitem,
                        parent, false);
                holder = new ViewHolder();
                holder.txvFurnitureName = (TextView) convertView.findViewById(R.id.txvFurnitureName);
                holder.imvFurnitureImage = (ImageView) convertView.findViewById(R.id.imvFurnitureImage);
                holder.row = (LinearLayout) convertView.findViewById(R.id.llFurnitureRow);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txvFurnitureName.setText(NAME.get(position));
            holder.imvFurnitureImage.setVisibility(View.GONE);
            holder.txvFurnitureName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(RoomListFurnitureActivity.this, RoomOrderCheckInOutDescCommentActivity.class);
                    i.putExtra("HEADER", "" + NAME.get(position).toUpperCase());
                    i.putExtra("TITLE", "" + NAME.get(position));
                    i.putExtra("API_CALL", API_CALL);
                    i.putExtra("ROOM_ID", ROOM_ID + "");
                    if (DESC.size() == NAME.size()) {
                        i.putExtra("DESCRIPTION", DESC.get(position));
                    } else {
                        i.putExtra("DESCRIPTION", "");
                    }
                    if (ADDT.size() == NAME.size()) {
                        i.putExtra("ADDITIONAL", ADDT.get(position));
                    } else {
                        i.putExtra("ADDITIONAL", "");
                    }
                    if (IMAGE1.size() == NAME.size()) {
                        i.putExtra("IMAGE1", IMAGE1.get(position));
                    } else {
                        i.putExtra("IMAGE1", "");
                    }
                    if (IMAGE2.size() == NAME.size()) {
                        i.putExtra("IMAGE2", IMAGE2.get(position));
                    } else {
                        i.putExtra("IMAGE2", "");
                    }
                    if (IMAGE3.size() == NAME.size()) {
                        i.putExtra("IMAGE3", IMAGE3.get(position));
                    } else {
                        i.putExtra("IMAGE3", "");
                    }
                    if (IMAGE3.size() == NAME.size()) {
                        i.putExtra("IMAGE4", IMAGE4.get(position));
                    } else {
                        i.putExtra("IMAGE4", "");
                    }
                    startActivity(i);
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return NAME.size();
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
            ImageView imvFurnitureImage;
            TextView txvFurnitureName;
            LinearLayout row;
        }
    }


}
