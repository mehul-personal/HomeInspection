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
import android.widget.RadioButton;
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

public class RoomTypeActivity extends AppCompatActivity {
    static String call = "";
    static ArrayList<String> IN_SID, IN_ROOMNAME, IN_IMAGE1, IN_IMAGE2, IN_IMAGE3, IN_IMAGE4,
            OUT_SID, OUT_ROOMNAME, OUT_IMAGE1, OUT_IMAGE2, OUT_IMAGE3, OUT_IMAGE4;
    static ArrayList<Boolean> IN_CHECK_LIST, OUT_CHECK_LIST;
    static String IN_ROOM_DESC, IN_ROOM_FURNITURE, IN_ROOM_APPLIANCES, IN_ROOM_SANITARY,
            OUT_ROOM_DESC, OUT_ROOM_FURNITURE, OUT_ROOM_APPLIANCES, OUT_ROOM_SANITARY;
    static String chkCheck = "";
    static StandardRoomListAdapter adapter;
    SwipeMenuListView lsvStandardRoom;
    ImageView imvAdd;
    FrameLayout back;
    Button Done;
    TextView header,title;
    EditText edtStandardRoom;
    RadioButton rdbcheckIn, rdbcheckOut;
   static ArrayList<String> name, id, image1, image2, image3, image4;
   static ArrayList<Boolean> checkList;
   static String room_desc, room_furniture, room_appliances, room_saintary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roomtype);
        lsvStandardRoom = (SwipeMenuListView) findViewById(R.id.lsvStandardRoom);
        imvAdd = (ImageView) findViewById(R.id.imvAdd);
        back = (FrameLayout) findViewById(R.id.flBack);
        Done = (Button) findViewById(R.id.btnDone);
        header = (TextView) findViewById(R.id.txvHeader);
        edtStandardRoom = (EditText) findViewById(R.id.edtStandardRoom);
        rdbcheckIn = (RadioButton) findViewById(R.id.checkIn);
        rdbcheckOut = (RadioButton) findViewById(R.id.checkOut);
        title=(TextView) findViewById(R.id.title);

        IN_CHECK_LIST = new ArrayList<Boolean>();
        IN_SID = new ArrayList<String>();
        IN_ROOMNAME = new ArrayList<String>();
        IN_IMAGE1 = new ArrayList<String>();
        IN_IMAGE2 = new ArrayList<String>();
        IN_IMAGE3 = new ArrayList<String>();
        IN_IMAGE4 = new ArrayList<String>();
        IN_ROOM_DESC = "";
        IN_ROOM_FURNITURE = "";
        IN_ROOM_APPLIANCES = "";
        IN_ROOM_SANITARY = "";

        OUT_CHECK_LIST = new ArrayList<Boolean>();
        OUT_SID = new ArrayList<String>();
        OUT_ROOMNAME = new ArrayList<String>();
        OUT_IMAGE1 = new ArrayList<String>();
        OUT_IMAGE2 = new ArrayList<String>();
        OUT_IMAGE3 = new ArrayList<String>();
        OUT_IMAGE4 = new ArrayList<String>();
        OUT_ROOM_DESC = "";
        OUT_ROOM_FURNITURE = "";
        OUT_ROOM_APPLIANCES = "";
        OUT_ROOM_SANITARY = "";

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtStandardRoom.setText("");
            }
        });


        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtStandardRoom.getText().toString().isEmpty()) {
                    if (chkCheck.equalsIgnoreCase("checkin")) {
                        IN_SID.add("0");
                        IN_ROOMNAME.add(edtStandardRoom.getText().toString());
                        IN_CHECK_LIST.add(false);
                        IN_IMAGE1.add("");
                        IN_IMAGE2.add("");
                        IN_IMAGE3.add("");
                        IN_IMAGE4.add("");
//                        IN_ROOM_DESC = "[]";
//                        IN_ROOM_FURNITURE = "[]";
//                        IN_ROOM_APPLIANCES = "[]";
//                        IN_ROOM_SANITARY = "[]";
                        adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE,
                                IN_IMAGE1, IN_IMAGE2, IN_IMAGE3, IN_IMAGE4, IN_ROOM_APPLIANCES, IN_ROOM_SANITARY);
                        lsvStandardRoom.setAdapter(adapter);
                    } else if (chkCheck.equalsIgnoreCase("checkout")) {
                        OUT_SID.add("0");
                        OUT_ROOMNAME.add(edtStandardRoom.getText().toString());
                        OUT_CHECK_LIST.add(false);
                        OUT_IMAGE1.add("");
                        OUT_IMAGE2.add("");
                        OUT_IMAGE3.add("");
                        OUT_IMAGE4.add("");
//                        OUT_ROOM_DESC = "[]";
//                        OUT_ROOM_FURNITURE = "[]";
//                        OUT_ROOM_APPLIANCES = "[]";
//                        OUT_ROOM_SANITARY = "[]";
                        adapter = new StandardRoomListAdapter(OUT_SID, OUT_ROOMNAME, OUT_CHECK_LIST, OUT_ROOM_DESC, OUT_ROOM_FURNITURE,
                                OUT_IMAGE1, OUT_IMAGE2, OUT_IMAGE3, OUT_IMAGE4, OUT_ROOM_APPLIANCES, OUT_ROOM_SANITARY);
                        lsvStandardRoom.setAdapter(adapter);
                    }
                    edtStandardRoom.setText("");
                    adapter.notifyDataSetChanged();
                }
            }
        });
        rdbcheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkCheck = "checkin";
//                adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE,
//                        IN_IMAGE1, IN_IMAGE2, IN_IMAGE3, IN_IMAGE4, IN_ROOM_APPLIANCES, IN_ROOM_SANITARY);
//                lsvStandardRoom.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
            }
        });
        rdbcheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkCheck = "checkout";
//                adapter = new StandardRoomListAdapter(OUT_SID, OUT_ROOMNAME, OUT_CHECK_LIST, OUT_ROOM_DESC, OUT_ROOM_FURNITURE,
//                        OUT_IMAGE1, OUT_IMAGE2, OUT_IMAGE3, OUT_IMAGE4, OUT_ROOM_APPLIANCES, OUT_ROOM_SANITARY);
//                lsvStandardRoom.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
            }
        });

        if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
            chkCheck = "checkin";
            rdbcheckIn.setChecked(true);

            adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE,
                    IN_IMAGE1, IN_IMAGE2, IN_IMAGE3, IN_IMAGE4, IN_ROOM_APPLIANCES, IN_ROOM_SANITARY);
            lsvStandardRoom.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
            chkCheck = "checkout";
            rdbcheckOut.setChecked(true);


            adapter = new StandardRoomListAdapter(OUT_SID, OUT_ROOMNAME, OUT_CHECK_LIST, OUT_ROOM_DESC, OUT_ROOM_FURNITURE,
                    OUT_IMAGE1, OUT_IMAGE2, OUT_IMAGE3, OUT_IMAGE4, OUT_ROOM_APPLIANCES, OUT_ROOM_SANITARY);
            lsvStandardRoom.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        Intent i = getIntent();
        if (i.getStringExtra("CALL").equalsIgnoreCase("Standard Room")) {
            title.setText("Standard Room Type");
            edtStandardRoom.setHint("Standard Room");
            header.setText("Standard Room");
            call = "STANDARD";
            getStandardRoomList();
        } else if (i.getStringExtra("CALL").equalsIgnoreCase("Kitchen")) {
            title.setText("Kitchen Room Type");
            edtStandardRoom.setHint("Kitchen Room");
            header.setText("Kitchen Room");
            call = "KITCHEN";
            getKitchenRoomList();
        } else if (i.getStringExtra("CALL").equalsIgnoreCase("Bathroom")) {
            title.setText("Bathroom Type");
            edtStandardRoom.setHint("Bath Room");
            header.setText("Bathroom");
            call = "BATHROOM";
            getBathRoomList();
        } else if (i.getStringExtra("CALL").equalsIgnoreCase("Stairs & Landing")) {
            title.setText("Stairs & Landing Type");
            header.setText("Stairs & Landing");
            edtStandardRoom.setHint("Stairs & Landing");
            call = "STAIRS_LANDING";
            getStairsAndListing();
        } else if (i.getStringExtra("CALL").equalsIgnoreCase("Outside Spaces")) {
            title.setText("Outside Spaces Type");
            edtStandardRoom.setHint("Outside Spaces");
            header.setText("Outside Spaces");
            call = "OUTSIDE_SPACES";
            getOutsideSpaceListing();
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
        lsvStandardRoom.setMenuCreator(creator);

        lsvStandardRoom.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        if (call.equalsIgnoreCase("STANDARD")) {
                            deleteStandardRoom(id.get(position));
                        }else if(call.equalsIgnoreCase("KITCHEN")){
                            deleteKitchen(id.get(position));
                        }else if(call.equalsIgnoreCase("BATHROOM")){
                            deleteBathroom(id.get(position));
                        }else if(call.equalsIgnoreCase("STAIRS_LANDING")){
                            deleteStairsLanding(id.get(position));
                        }else if(call.equalsIgnoreCase("OUTSIDE_SPACES"))
                        {
                            deleteOutsideSpace(id.get(position));
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

    @Override
    protected void onRestart() {
        super.onRestart();
        if (call.equalsIgnoreCase("STANDARD")) {
            header.setText("Standard Room");
            call = "STANDARD";
            getStandardRoomList();
        } else if (call.equalsIgnoreCase("KITCHEN")) {
            header.setText("Kitchen Room");
            call = "KITCHEN";
            getKitchenRoomList();
        } else if (call.equalsIgnoreCase("BATHROOM")) {
            header.setText("Bathroom");
            call = "BATHROOM";
            getBathRoomList();
        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
            header.setText("Stairs & Landing");
            call = "STAIRS_LANDING";
            getStairsAndListing();
        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
            header.setText("Outside Spaces");
            call = "OUTSIDE_SPACES";
            getOutsideSpaceListing();
        }
    }

    public void getStandardRoomList() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_standarroom";
//Property_id:61PROPERTY_TYPE_ID:222,PROPERTY_TYPE:checkinPROPERTY_TYPE_ID_CHECKIN222PROPERTY_TYPE_ID_CHECKOUT0
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_standarroom", response.toString());
                        IN_CHECK_LIST = new ArrayList<Boolean>();
                        IN_SID = new ArrayList<String>();
                        IN_ROOMNAME = new ArrayList<String>();
                        IN_IMAGE1 = new ArrayList<String>();
                        IN_IMAGE2 = new ArrayList<String>();
                        IN_IMAGE3 = new ArrayList<String>();
                        IN_IMAGE4 = new ArrayList<String>();
                        IN_ROOM_DESC = "";
                        IN_ROOM_FURNITURE = "";
                        IN_ROOM_APPLIANCES = "";
                        IN_ROOM_SANITARY = "";

                        OUT_CHECK_LIST = new ArrayList<Boolean>();
                        OUT_SID = new ArrayList<String>();
                        OUT_ROOMNAME = new ArrayList<String>();
                        OUT_IMAGE1 = new ArrayList<String>();
                        OUT_IMAGE2 = new ArrayList<String>();
                        OUT_IMAGE3 = new ArrayList<String>();
                        OUT_IMAGE4 = new ArrayList<String>();
                        OUT_ROOM_DESC = "";
                        OUT_ROOM_FURNITURE = "";
                        OUT_ROOM_APPLIANCES = "";
                        OUT_ROOM_SANITARY = "";
                        try {
                            mProgressDialog.dismiss();
                            JSONArray dataArray = new JSONArray(response.toString());
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            if (dataObject.getBoolean("result")) {
                                JSONArray standard_roomcheckin = dataObject.getJSONArray("standard_roomcheckin");
                                JSONArray standard_roomcheckout = dataObject.getJSONArray("standard_roomcheckout");

                                JSONArray standardroom_array_in = new JSONArray();
                                JSONArray standardroom_array_out = new JSONArray();
                                if (standard_roomcheckin.length() > 0) {
                                    JSONObject checkInObject = standard_roomcheckin.getJSONObject(0);
                                    standardroom_array_in = checkInObject.getJSONArray("standard_room");
                                    JSONArray standardroomdesc_array = checkInObject.getJSONArray("standard_room_desc");
                                    JSONArray standardroomfurniture_array = checkInObject.getJSONArray("standard_furniture");
                                    IN_ROOM_DESC = standardroomdesc_array.toString();
                                    IN_ROOM_FURNITURE = standardroomfurniture_array.toString();
                                    for (int i = 0; i < standardroom_array_in.length(); i++) {
                                        JSONObject dataObj = standardroom_array_in.getJSONObject(i);
                                        IN_SID.add(dataObj.getString("id"));
                                        IN_CHECK_LIST.add(false);
                                        IN_ROOMNAME.add(dataObj.getString("roomtype"));
                                        IN_IMAGE1.add(dataObj.getString("image1"));
                                        IN_IMAGE2.add(dataObj.getString("image2"));
                                        IN_IMAGE3.add(dataObj.getString("image3"));
                                        IN_IMAGE4.add(dataObj.getString("image4"));
                                    }
                                    IN_ROOM_APPLIANCES = "[]";
                                    IN_ROOM_SANITARY = "[]";
//                                    adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE);
//                                    lsvStandardRoom.setAdapter(adapter);

                                }

                                if (standard_roomcheckout.length() > 0) {
                                    JSONObject checkOutObject = standard_roomcheckout.getJSONObject(0);
                                    standardroom_array_out = checkOutObject.getJSONArray("standard_room");
                                    JSONArray standardroomdesc_array = checkOutObject.getJSONArray("standard_room_desc");
                                    JSONArray standardroomfurniture_array = checkOutObject.getJSONArray("standard_furniture");
                                    OUT_ROOM_DESC = standardroomdesc_array.toString();
                                    OUT_ROOM_FURNITURE = standardroomfurniture_array.toString();
                                    for (int i = 0; i < standardroom_array_out.length(); i++) {
                                        JSONObject dataObj = standardroom_array_out.getJSONObject(i);
                                        OUT_SID.add(dataObj.getString("id"));
                                        OUT_CHECK_LIST.add(false);
                                        OUT_ROOMNAME.add(dataObj.getString("roomtype"));
                                        OUT_IMAGE1.add(dataObj.getString("image1"));
                                        OUT_IMAGE2.add(dataObj.getString("image2"));
                                        OUT_IMAGE3.add(dataObj.getString("image3"));
                                        OUT_IMAGE4.add(dataObj.getString("image4"));
                                    }
                                    OUT_ROOM_APPLIANCES = "[]";
                                    OUT_ROOM_SANITARY = "[]";
                                }
                                if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                                    if (standardroom_array_in.length() > 0) {
                                        adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE,
                                                IN_IMAGE1, IN_IMAGE2, IN_IMAGE3, IN_IMAGE4, IN_ROOM_APPLIANCES, IN_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    } else {
                                        adapter = new StandardRoomListAdapter(OUT_SID, OUT_ROOMNAME, OUT_CHECK_LIST, OUT_ROOM_DESC, OUT_ROOM_FURNITURE,
                                                OUT_IMAGE1, OUT_IMAGE2, OUT_IMAGE3, OUT_IMAGE4, OUT_ROOM_APPLIANCES, OUT_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    }
                                } else if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                                    if (standardroom_array_out.length() > 0) {
                                        adapter = new StandardRoomListAdapter(OUT_SID, OUT_ROOMNAME, OUT_CHECK_LIST, OUT_ROOM_DESC, OUT_ROOM_FURNITURE,
                                                OUT_IMAGE1, OUT_IMAGE2, OUT_IMAGE3, OUT_IMAGE4, OUT_ROOM_APPLIANCES, OUT_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    } else {
                                        adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE,
                                                IN_IMAGE1, IN_IMAGE2, IN_IMAGE3, IN_IMAGE4, IN_ROOM_APPLIANCES, IN_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Oops! We can't found your previous History",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_standarroom Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Log.e("Property details", "Property_id:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "") +
                        "PROPERTY_TYPE_ID:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "") + ",PROPERTY_TYPE:" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + "PROPERTY_TYPE_ID_CHECKIN" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0") + "PROPERTY_TYPE_ID_CHECKOUT" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));

                Map<String, String> params = new HashMap<String, String>();
                params.put("propertytypecheckinid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0"));
                params.put("propertytypecheckoutid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void getKitchenRoomList() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_kitchenroom";

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_kitchenroom", response.toString());
                        IN_CHECK_LIST = new ArrayList<Boolean>();
                        IN_SID = new ArrayList<String>();
                        IN_ROOMNAME = new ArrayList<String>();
                        IN_IMAGE1 = new ArrayList<String>();
                        IN_IMAGE2 = new ArrayList<String>();
                        IN_IMAGE3 = new ArrayList<String>();
                        IN_IMAGE4 = new ArrayList<String>();
                        IN_ROOM_DESC = "";
                        IN_ROOM_FURNITURE = "";
                        IN_ROOM_APPLIANCES = "";
                        IN_ROOM_SANITARY = "";

                        OUT_CHECK_LIST = new ArrayList<Boolean>();
                        OUT_SID = new ArrayList<String>();
                        OUT_ROOMNAME = new ArrayList<String>();
                        OUT_IMAGE1 = new ArrayList<String>();
                        OUT_IMAGE2 = new ArrayList<String>();
                        OUT_IMAGE3 = new ArrayList<String>();
                        OUT_IMAGE4 = new ArrayList<String>();
                        OUT_ROOM_DESC = "";
                        OUT_ROOM_FURNITURE = "";
                        OUT_ROOM_APPLIANCES = "";
                        OUT_ROOM_SANITARY = "";
                        try {
                            mProgressDialog.dismiss();
                            JSONArray dataArray = new JSONArray(response.toString());
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            if (dataObject.getBoolean("result")) {
                                JSONArray standard_roomcheckin = dataObject.getJSONArray("kitchenroomcheckin");
                                JSONArray standard_roomcheckout = dataObject.getJSONArray("kitchenroomcheckout");
                                JSONArray kitchenroom_array_in=new JSONArray();
                                if (standard_roomcheckin.length() > 0) {
                                    JSONObject checkInObject = standard_roomcheckin.getJSONObject(0);
                                    kitchenroom_array_in = checkInObject.getJSONArray("kitchen_room");
                                    JSONArray kitchenroomdesc_array = checkInObject.getJSONArray("kitchen_room_desc");
                                    JSONArray kitchenroomfurniture_array = checkInObject.getJSONArray("kitchen_furniture");
                                    JSONArray kitchenroomAppliances = checkInObject.getJSONArray("kitchen_appliances");
                                    IN_ROOM_DESC = kitchenroomdesc_array.toString();
                                    IN_ROOM_FURNITURE = kitchenroomfurniture_array.toString();
                                    for (int i = 0; i < kitchenroom_array_in.length(); i++) {
                                        JSONObject dataObj = kitchenroom_array_in.getJSONObject(i);
                                        IN_SID.add(dataObj.getString("id"));
                                        IN_CHECK_LIST.add(false);
                                        IN_ROOMNAME.add(dataObj.getString("kitchentype"));
                                        IN_IMAGE1.add(dataObj.getString("image1"));
                                        IN_IMAGE2.add(dataObj.getString("image2"));
                                        IN_IMAGE3.add(dataObj.getString("image3"));
                                        IN_IMAGE4.add(dataObj.getString("image4"));
                                    }
                                    IN_ROOM_APPLIANCES = kitchenroomAppliances.toString();
                                    IN_ROOM_SANITARY = "[]";
//                                    adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE);
//                                    lsvStandardRoom.setAdapter(adapter);


                                }
                                JSONArray kitchenroom_array_out=new JSONArray();
                                if (standard_roomcheckout.length() > 0) {
                                    JSONObject checkOutObject = standard_roomcheckout.getJSONObject(0);
                                    kitchenroom_array_out = checkOutObject.getJSONArray("kitchen_room");
                                    JSONArray kitchenroomdesc_array = checkOutObject.getJSONArray("kitchen_room_desc");
                                    JSONArray kitchenroomfurniture_array = checkOutObject.getJSONArray("kitchen_furniture");
                                    JSONArray kitchenroomAppliances = checkOutObject.getJSONArray("kitchen_appliances");
                                    OUT_ROOM_DESC = kitchenroomdesc_array.toString();
                                    OUT_ROOM_FURNITURE = kitchenroomfurniture_array.toString();
                                    for (int i = 0; i < kitchenroom_array_out.length(); i++) {
                                        JSONObject dataObj = kitchenroom_array_out.getJSONObject(i);
                                        OUT_SID.add(dataObj.getString("id"));
                                        OUT_CHECK_LIST.add(false);
                                        OUT_ROOMNAME.add(dataObj.getString("kitchentype"));
                                        OUT_IMAGE1.add(dataObj.getString("image1"));
                                        OUT_IMAGE2.add(dataObj.getString("image2"));
                                        OUT_IMAGE3.add(dataObj.getString("image3"));
                                        OUT_IMAGE4.add(dataObj.getString("image4"));
                                    }
                                    OUT_ROOM_APPLIANCES = kitchenroomAppliances.toString();
                                    OUT_ROOM_SANITARY = "[]";
                                }
                                if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                                    if (kitchenroom_array_in.length() > 0) {
                                        adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE,
                                                IN_IMAGE1, IN_IMAGE2, IN_IMAGE3, IN_IMAGE4, IN_ROOM_APPLIANCES, IN_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    } else {
                                        adapter = new StandardRoomListAdapter(OUT_SID, OUT_ROOMNAME, OUT_CHECK_LIST, OUT_ROOM_DESC, OUT_ROOM_FURNITURE,
                                                OUT_IMAGE1, OUT_IMAGE2, OUT_IMAGE3, OUT_IMAGE4, OUT_ROOM_APPLIANCES, OUT_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    }
                                } else if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                                    if (kitchenroom_array_out.length() > 0) {
                                        adapter = new StandardRoomListAdapter(OUT_SID, OUT_ROOMNAME, OUT_CHECK_LIST, OUT_ROOM_DESC, OUT_ROOM_FURNITURE,
                                                OUT_IMAGE1, OUT_IMAGE2, OUT_IMAGE3, OUT_IMAGE4, OUT_ROOM_APPLIANCES, OUT_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    } else {
                                        adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE,
                                                IN_IMAGE1, IN_IMAGE2, IN_IMAGE3, IN_IMAGE4, IN_ROOM_APPLIANCES, IN_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Oops! We can't found your previous History",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_kitchenroom Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Log.e("Property details", "Property_id:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "") +
                        "PROPERTY_TYPE_ID:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "") + ",PROPERTY_TYPE:" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + "PROPERTY_TYPE_ID_CHECKIN" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0") + "PROPERTY_TYPE_ID_CHECKOUT" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));

                Map<String, String> params = new HashMap<String, String>();
                params.put("propertytypecheckinid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0"));
                params.put("propertytypecheckoutid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void getBathRoomList() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_bathroom";

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_bathroom", response.toString());
                        IN_CHECK_LIST = new ArrayList<Boolean>();
                        IN_SID = new ArrayList<String>();
                        IN_ROOMNAME = new ArrayList<String>();
                        IN_IMAGE1 = new ArrayList<String>();
                        IN_IMAGE2 = new ArrayList<String>();
                        IN_IMAGE3 = new ArrayList<String>();
                        IN_IMAGE4 = new ArrayList<String>();
                        IN_ROOM_DESC = "";
                        IN_ROOM_FURNITURE = "";
                        IN_ROOM_APPLIANCES = "";
                        IN_ROOM_SANITARY = "";

                        OUT_CHECK_LIST = new ArrayList<Boolean>();
                        OUT_SID = new ArrayList<String>();
                        OUT_ROOMNAME = new ArrayList<String>();
                        OUT_IMAGE1 = new ArrayList<String>();
                        OUT_IMAGE2 = new ArrayList<String>();
                        OUT_IMAGE3 = new ArrayList<String>();
                        OUT_IMAGE4 = new ArrayList<String>();
                        OUT_ROOM_DESC = "";
                        OUT_ROOM_FURNITURE = "";
                        OUT_ROOM_APPLIANCES = "";
                        OUT_ROOM_SANITARY = "";
                        try {
                            mProgressDialog.dismiss();
                            JSONArray dataArray = new JSONArray(response.toString());
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            if (dataObject.getBoolean("result")) {
                                JSONArray standard_roomcheckin = dataObject.getJSONArray("bathroomlistcheckin");
                                JSONArray standard_roomcheckout = dataObject.getJSONArray("bathroomlistcheckout");
                                JSONArray bathroom_array_in=new JSONArray();
                                if (standard_roomcheckin.length() > 0) {
                                    JSONObject checkInObject = standard_roomcheckin.getJSONObject(0);
                                    bathroom_array_in = checkInObject.getJSONArray("bathroom");
                                    JSONArray bathroomdesc_array = checkInObject.getJSONArray("bathroom_desc");
                                    JSONArray bathroomfurniture_array = checkInObject.getJSONArray("bathroom_furniture");
                                    JSONArray bathroomsanitary = checkInObject.getJSONArray("bathroom_sanitary");
                                    IN_ROOM_DESC = bathroomdesc_array.toString();
                                    IN_ROOM_FURNITURE = bathroomfurniture_array.toString();
                                    for (int i = 0; i < bathroom_array_in.length(); i++) {
                                        JSONObject dataObj = bathroom_array_in.getJSONObject(i);
                                        IN_SID.add(dataObj.getString("id"));
                                        IN_CHECK_LIST.add(false);
                                        IN_ROOMNAME.add(dataObj.getString("bathroomtype"));
                                        IN_IMAGE1.add(dataObj.getString("image1"));
                                        IN_IMAGE2.add(dataObj.getString("image2"));
                                        IN_IMAGE3.add(dataObj.getString("image3"));
                                        IN_IMAGE4.add(dataObj.getString("image4"));
                                    }
                                    IN_ROOM_APPLIANCES = "[]";
                                    IN_ROOM_SANITARY = bathroomsanitary.toString();
//                                    adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE);
//                                    lsvStandardRoom.setAdapter(adapter);


                                }
                                JSONArray bathroom_array_out=new JSONArray();
                                if (standard_roomcheckout.length() > 0) {
                                    JSONObject checkOutObject = standard_roomcheckout.getJSONObject(0);
                                    bathroom_array_out = checkOutObject.getJSONArray("bathroom");
                                    JSONArray bathroomdesc_array = checkOutObject.getJSONArray("bathroom_desc");
                                    JSONArray bathroomfurniture_array = checkOutObject.getJSONArray("bathroom_furniture");
                                    JSONArray bathroomsanitary = checkOutObject.getJSONArray("bathroom_sanitary");
                                    OUT_ROOM_DESC = bathroomdesc_array.toString();
                                    OUT_ROOM_FURNITURE = bathroomfurniture_array.toString();
                                    for (int i = 0; i < bathroom_array_out.length(); i++) {
                                        JSONObject dataObj = bathroom_array_out.getJSONObject(i);
                                        OUT_SID.add(dataObj.getString("id"));
                                        OUT_CHECK_LIST.add(false);
                                        OUT_ROOMNAME.add(dataObj.getString("bathroomtype"));
                                        OUT_IMAGE1.add(dataObj.getString("image1"));
                                        OUT_IMAGE2.add(dataObj.getString("image2"));
                                        OUT_IMAGE3.add(dataObj.getString("image3"));
                                        OUT_IMAGE4.add(dataObj.getString("image4"));
                                    }
                                    OUT_ROOM_APPLIANCES = "[]";
                                    OUT_ROOM_SANITARY = bathroomsanitary.toString();
                                }
                                if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                                    if (bathroom_array_in.length() > 0) {
                                        adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE,
                                                IN_IMAGE1, IN_IMAGE2, IN_IMAGE3, IN_IMAGE4, IN_ROOM_APPLIANCES, IN_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    } else {
                                        adapter = new StandardRoomListAdapter(OUT_SID, OUT_ROOMNAME, OUT_CHECK_LIST, OUT_ROOM_DESC, OUT_ROOM_FURNITURE,
                                                OUT_IMAGE1, OUT_IMAGE2, OUT_IMAGE3, OUT_IMAGE4, OUT_ROOM_APPLIANCES, OUT_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    }
                                } else if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                                    if (bathroom_array_out.length() > 0) {
                                        adapter = new StandardRoomListAdapter(OUT_SID, OUT_ROOMNAME, OUT_CHECK_LIST, OUT_ROOM_DESC, OUT_ROOM_FURNITURE,
                                                OUT_IMAGE1, OUT_IMAGE2, OUT_IMAGE3, OUT_IMAGE4, OUT_ROOM_APPLIANCES, OUT_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    } else {
                                        adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE,
                                                IN_IMAGE1, IN_IMAGE2, IN_IMAGE3, IN_IMAGE4, IN_ROOM_APPLIANCES, IN_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Oops! We can't found your previous History",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_bathroom Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Log.e("Property details", "Property_id:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "") +
                        "PROPERTY_TYPE_ID:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "") + ",PROPERTY_TYPE:" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + "PROPERTY_TYPE_ID_CHECKIN" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0") + "PROPERTY_TYPE_ID_CHECKOUT" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));

                Map<String, String> params = new HashMap<String, String>();
                params.put("propertytypecheckinid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0"));
                params.put("propertytypecheckoutid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void getStairsAndListing() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_stairslanding";

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_stairslanding", response.toString());
                        IN_CHECK_LIST = new ArrayList<Boolean>();
                        IN_SID = new ArrayList<String>();
                        IN_ROOMNAME = new ArrayList<String>();
                        IN_IMAGE1 = new ArrayList<String>();
                        IN_IMAGE2 = new ArrayList<String>();
                        IN_IMAGE3 = new ArrayList<String>();
                        IN_IMAGE4 = new ArrayList<String>();
                        IN_ROOM_DESC = "";
                        IN_ROOM_FURNITURE = "";
                        IN_ROOM_APPLIANCES = "";
                        IN_ROOM_SANITARY = "";

                        OUT_CHECK_LIST = new ArrayList<Boolean>();
                        OUT_SID = new ArrayList<String>();
                        OUT_ROOMNAME = new ArrayList<String>();
                        OUT_IMAGE1 = new ArrayList<String>();
                        OUT_IMAGE2 = new ArrayList<String>();
                        OUT_IMAGE3 = new ArrayList<String>();
                        OUT_IMAGE4 = new ArrayList<String>();
                        OUT_ROOM_DESC = "";
                        OUT_ROOM_FURNITURE = "";
                        OUT_ROOM_APPLIANCES = "";
                        OUT_ROOM_SANITARY = "";
                        try {
                            mProgressDialog.dismiss();
                            JSONArray dataArray = new JSONArray(response.toString());
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            if (dataObject.getBoolean("result")) {
                                JSONArray standard_roomcheckin = dataObject.getJSONArray("stairs_landinglistcheckin");
                                JSONArray standard_roomcheckout = dataObject.getJSONArray("stairs_landinglistcheckout");
                                JSONArray bathroom_array_in=new JSONArray();
                                if (standard_roomcheckin.length() > 0) {
                                    JSONObject checkInObject = standard_roomcheckin.getJSONObject(0);
                                    bathroom_array_in = checkInObject.getJSONArray("stairs_landing");
                                    JSONArray bathroomdesc_array = checkInObject.getJSONArray("stairs_landing_desc");
                                    JSONArray bathroomfurniture_array = checkInObject.getJSONArray("stairs_landing_furniture");
                                    IN_ROOM_DESC = bathroomdesc_array.toString();
                                    IN_ROOM_FURNITURE = bathroomfurniture_array.toString();
                                    for (int i = 0; i < bathroom_array_in.length(); i++) {
                                        JSONObject dataObj = bathroom_array_in.getJSONObject(i);
                                        IN_SID.add(dataObj.getString("id"));
                                        IN_CHECK_LIST.add(false);
                                        IN_ROOMNAME.add(dataObj.getString("stairslandingtype"));
                                        IN_IMAGE1.add(dataObj.getString("image1"));
                                        IN_IMAGE2.add(dataObj.getString("image2"));
                                        IN_IMAGE3.add(dataObj.getString("image3"));
                                        IN_IMAGE4.add(dataObj.getString("image4"));
                                    }
                                    IN_ROOM_APPLIANCES = "[]";
                                    IN_ROOM_SANITARY = "[]";
//                                    adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE);
//                                    lsvStandardRoom.setAdapter(adapter);


                                }
                                JSONArray bathroom_array_out=new JSONArray();
                                if (standard_roomcheckout.length() > 0) {
                                    JSONObject checkOutObject = standard_roomcheckout.getJSONObject(0);
                                    bathroom_array_out = checkOutObject.getJSONArray("stairs_landing");
                                    JSONArray bathroomdesc_array = checkOutObject.getJSONArray("stairs_landing_desc");
                                    JSONArray bathroomfurniture_array = checkOutObject.getJSONArray("stairs_landing_furniture");
                                    OUT_ROOM_DESC = bathroomdesc_array.toString();
                                    OUT_ROOM_FURNITURE = bathroomfurniture_array.toString();
                                    for (int i = 0; i < bathroom_array_out.length(); i++) {
                                        JSONObject dataObj = bathroom_array_out.getJSONObject(i);
                                        OUT_SID.add(dataObj.getString("id"));
                                        OUT_CHECK_LIST.add(false);
                                        OUT_ROOMNAME.add(dataObj.getString("stairslandingtype"));
                                        OUT_IMAGE1.add(dataObj.getString("image1"));
                                        OUT_IMAGE2.add(dataObj.getString("image2"));
                                        OUT_IMAGE3.add(dataObj.getString("image3"));
                                        OUT_IMAGE4.add(dataObj.getString("image4"));
                                    }
                                    OUT_ROOM_APPLIANCES = "[]";
                                    OUT_ROOM_SANITARY = "[]";
                                }
                                if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                                    if (bathroom_array_in.length() > 0) {
                                        adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE,
                                                IN_IMAGE1, IN_IMAGE2, IN_IMAGE3, IN_IMAGE4, IN_ROOM_APPLIANCES, IN_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    } else {
                                        adapter = new StandardRoomListAdapter(OUT_SID, OUT_ROOMNAME, OUT_CHECK_LIST, OUT_ROOM_DESC, OUT_ROOM_FURNITURE,
                                                OUT_IMAGE1, OUT_IMAGE2, OUT_IMAGE3, OUT_IMAGE4, OUT_ROOM_APPLIANCES, OUT_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    }
                                } else if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                                    if (bathroom_array_out.length() > 0) {
                                        adapter = new StandardRoomListAdapter(OUT_SID, OUT_ROOMNAME, OUT_CHECK_LIST, OUT_ROOM_DESC, OUT_ROOM_FURNITURE,
                                                OUT_IMAGE1, OUT_IMAGE2, OUT_IMAGE3, OUT_IMAGE4, OUT_ROOM_APPLIANCES, OUT_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    } else {
                                        adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE,
                                                IN_IMAGE1, IN_IMAGE2, IN_IMAGE3, IN_IMAGE4, IN_ROOM_APPLIANCES, IN_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Oops! We can't found your previous History",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_stairslanding Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Log.e("Property details", "Property_id:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "") +
                        "PROPERTY_TYPE_ID:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "") + ",PROPERTY_TYPE:" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + "PROPERTY_TYPE_ID_CHECKIN" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0") + "PROPERTY_TYPE_ID_CHECKOUT" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));

                Map<String, String> params = new HashMap<String, String>();
                params.put("propertytypecheckinid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0"));
                params.put("propertytypecheckoutid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void getOutsideSpaceListing() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_outsidespace";

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_outsidespace", response.toString());
                        IN_CHECK_LIST = new ArrayList<Boolean>();
                        IN_SID = new ArrayList<String>();
                        IN_ROOMNAME = new ArrayList<String>();
                        IN_IMAGE1 = new ArrayList<String>();
                        IN_IMAGE2 = new ArrayList<String>();
                        IN_IMAGE3 = new ArrayList<String>();
                        IN_IMAGE4 = new ArrayList<String>();
                        IN_ROOM_DESC = "";
                        IN_ROOM_FURNITURE = "";
                        IN_ROOM_APPLIANCES = "";
                        IN_ROOM_SANITARY = "";

                        OUT_CHECK_LIST = new ArrayList<Boolean>();
                        OUT_SID = new ArrayList<String>();
                        OUT_ROOMNAME = new ArrayList<String>();
                        OUT_IMAGE1 = new ArrayList<String>();
                        OUT_IMAGE2 = new ArrayList<String>();
                        OUT_IMAGE3 = new ArrayList<String>();
                        OUT_IMAGE4 = new ArrayList<String>();
                        OUT_ROOM_DESC = "";
                        OUT_ROOM_FURNITURE = "";
                        OUT_ROOM_APPLIANCES = "";
                        OUT_ROOM_SANITARY = "";
                        try {
                            mProgressDialog.dismiss();
                            JSONArray dataArray = new JSONArray(response.toString());
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            if (dataObject.getBoolean("result")) {
                                JSONArray standard_roomcheckin = dataObject.getJSONArray("outsidespacelistcheckin");
                                JSONArray standard_roomcheckout = dataObject.getJSONArray("outsidespacelistcheckout");
                                JSONArray outsideroom_array_in=new JSONArray();
                                if (standard_roomcheckin.length() > 0) {
                                    JSONObject checkInObject = standard_roomcheckin.getJSONObject(0);
                                    outsideroom_array_in = checkInObject.getJSONArray("outside_space");
                                    JSONArray outsideroomdesc_array = checkInObject.getJSONArray("outside_space_desc");
                                    IN_ROOM_DESC = outsideroomdesc_array.toString();
                                    IN_ROOM_FURNITURE = "[]";
                                    for (int i = 0; i < outsideroom_array_in.length(); i++) {
                                        JSONObject dataObj = outsideroom_array_in.getJSONObject(i);
                                        IN_SID.add(dataObj.getString("id"));
                                        IN_CHECK_LIST.add(false);
                                        IN_ROOMNAME.add(dataObj.getString("outsidespacetype"));
                                        IN_IMAGE1.add(dataObj.getString("image1"));
                                        IN_IMAGE2.add(dataObj.getString("image2"));
                                        IN_IMAGE3.add(dataObj.getString("image3"));
                                        IN_IMAGE4.add(dataObj.getString("image4"));
                                    }
                                    IN_ROOM_APPLIANCES = "[]";
                                    IN_ROOM_SANITARY = "[]";
//                                    adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE);
//                                    lsvStandardRoom.setAdapter(adapter);


                                }
                                JSONArray outsideroom_array_out=new JSONArray();
                                if (standard_roomcheckout.length() > 0) {
                                    JSONObject checkOutObject = standard_roomcheckout.getJSONObject(0);
                                    outsideroom_array_out = checkOutObject.getJSONArray("outside_space");
                                    JSONArray outsideroomdesc_array = checkOutObject.getJSONArray("outside_space_desc");
                                    OUT_ROOM_DESC = outsideroomdesc_array.toString();
                                    OUT_ROOM_FURNITURE = "[]";
                                    for (int i = 0; i < outsideroom_array_out.length(); i++) {
                                        JSONObject dataObj = outsideroom_array_out.getJSONObject(i);
                                        OUT_SID.add(dataObj.getString("id"));
                                        OUT_CHECK_LIST.add(false);
                                        OUT_ROOMNAME.add(dataObj.getString("outsidespacetype"));
                                        OUT_IMAGE1.add(dataObj.getString("image1"));
                                        OUT_IMAGE2.add(dataObj.getString("image2"));
                                        OUT_IMAGE3.add(dataObj.getString("image3"));
                                        OUT_IMAGE4.add(dataObj.getString("image4"));
                                    }
                                    OUT_ROOM_APPLIANCES = "[]";
                                    OUT_ROOM_SANITARY = "[]";
                                }
                                if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                                    if (outsideroom_array_in.length() > 0) {
                                        adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE,
                                                IN_IMAGE1, IN_IMAGE2, IN_IMAGE3, IN_IMAGE4, IN_ROOM_APPLIANCES, IN_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    } else {
                                        adapter = new StandardRoomListAdapter(OUT_SID, OUT_ROOMNAME, OUT_CHECK_LIST, OUT_ROOM_DESC, OUT_ROOM_FURNITURE,
                                                OUT_IMAGE1, OUT_IMAGE2, OUT_IMAGE3, OUT_IMAGE4, OUT_ROOM_APPLIANCES, OUT_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    }
                                } else if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                                    if (outsideroom_array_out.length() > 0) {
                                        adapter = new StandardRoomListAdapter(OUT_SID, OUT_ROOMNAME, OUT_CHECK_LIST, OUT_ROOM_DESC, OUT_ROOM_FURNITURE,
                                                OUT_IMAGE1, OUT_IMAGE2, OUT_IMAGE3, OUT_IMAGE4, OUT_ROOM_APPLIANCES, OUT_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    } else {
                                        adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE,
                                                IN_IMAGE1, IN_IMAGE2, IN_IMAGE3, IN_IMAGE4, IN_ROOM_APPLIANCES, IN_ROOM_SANITARY);
                                        lsvStandardRoom.setAdapter(adapter);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Oops! We can't found your previous History",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_outsidespace Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Log.e("Property details", "Property_id:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "") +
                        "PROPERTY_TYPE_ID:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "") + ",PROPERTY_TYPE:" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + "PROPERTY_TYPE_ID_CHECKIN" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0") + "PROPERTY_TYPE_ID_CHECKOUT" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));

                Map<String, String> params = new HashMap<String, String>();
                params.put("propertytypecheckinid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0"));
                params.put("propertytypecheckoutid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public class StandardRoomListAdapter extends BaseAdapter {

        LayoutInflater inflater;

        int m = 0;

        public StandardRoomListAdapter(ArrayList<String> id1, ArrayList<String> name1, ArrayList<Boolean> checkList1,
                                       String room_desc1, String room_furniture1,
                                       ArrayList<String> image11, ArrayList<String> image21, ArrayList<String> image31,
                                       ArrayList<String> image41, String rooom_applia1, String room_saintary1) {
            // TODO Auto-generated constructor stub
            name = name1;
            id = id1;
            checkList = checkList1;
            room_desc = room_desc1;
            room_furniture = room_furniture1;
            image1 = image11;
            image2 = image21;
            image3 = image31;
            image4 = image41;
            room_appliances = rooom_applia1;
            room_saintary = room_saintary1;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_standard_room_listitem,
                        parent, false);
                holder = new ViewHolder();
                holder.txvName = (TextView) convertView.findViewById(R.id.txvStandardName);
                holder.checkbox = (ImageView) convertView.findViewById(R.id.imvCheckbox);
                holder.row = (LinearLayout) convertView.findViewById(R.id.llStandardRoomRow);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txvName.setText(name.get(position));
            if (checkList.get(position)) {
                holder.checkbox.setImageResource(R.drawable.ic_checkbox);
            } else {
                holder.checkbox.setImageResource(0);
            }
            holder.txvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RoomTypeActivity.this, RoomOrderCheckInOutActivity.class);
                    if (call.equalsIgnoreCase("STANDARD")) {
                        intent.putExtra("API_CALL", "insertstandard_room");
                        intent.putExtra("HEADER", name.get(position));
                    } else if (call.equalsIgnoreCase("KITCHEN")) {
                        intent.putExtra("API_CALL", "insertkitchen_room");
                        intent.putExtra("HEADER", name.get(position));
                    } else if (call.equalsIgnoreCase("BATHROOM")) {
                        intent.putExtra("API_CALL", "insertbathroom");
                        intent.putExtra("HEADER", name.get(position));
                    } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                        intent.putExtra("API_CALL", "insertstairs_landing");
                        intent.putExtra("HEADER", name.get(position));
                    } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                        intent.putExtra("API_CALL", "insertoutside_space");
                        intent.putExtra("HEADER", name.get(position));
                    }
                    if(id.size()>1&& position>0) {
                        intent.putExtra("PREVIOUS_ROOM_ID", id.get(position-1));
                    }else{
                        intent.putExtra("PREVIOUS_ROOM_ID", "0");
                    }
                    intent.putExtra("ROOM_ID", id.get(position));
                    intent.putExtra("ROOM_DESC", room_desc);
                    intent.putExtra("ROOM_FURNITURE", room_furniture.isEmpty() ? "[]" : room_furniture);
                    intent.putExtra("ROOM_APPLIANCES", room_appliances.isEmpty() ? "[]" : room_appliances);
                    intent.putExtra("ROOM_SAINTARY", room_saintary.isEmpty() ? "[]" : room_saintary);
                    intent.putExtra("IMAGE1", image1.get(position));
                    intent.putExtra("IMAGE2", image2.get(position));
                    intent.putExtra("IMAGE3", image3.get(position));
                    intent.putExtra("IMAGE4", image4.get(position));
                    intent.putExtra("CALL", call);
                    startActivity(intent);
//                    Intent intent = new Intent(RoomTypeActivity.this, RoomConstructionListActivity.class);
//                    intent.putExtra("CALL", call);
//                    intent.putExtra("HEADER", "" + name.get(position));
//                    startActivity(intent);
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
            ImageView checkbox;
            TextView txvName;
            LinearLayout row;
        }
    }

    public void deleteStandardRoom(final String roomid) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "deletestandard_room";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("deletestandard_room", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());
                            JSONArray dataArr = dataOb.getJSONArray("Message");
                            JSONObject jsob = dataArr.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                Toast.makeText(RoomTypeActivity.this, "Your room deleted successfully on your property",
                                        Toast.LENGTH_LONG).show();
                                getStandardRoomList();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Oops Your room deleting failure on your property",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("deletestandard_room Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("standerroomid", "" + roomid);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }
    public void deleteKitchen(final String roomid) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "deletekitchen_room";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("deletekitchen_room", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());
                            JSONArray dataArr = dataOb.getJSONArray("Message");
                            JSONObject jsob = dataArr.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                Toast.makeText(RoomTypeActivity.this, "Your kitchen deleted successfully on your property",
                                        Toast.LENGTH_LONG).show();
                                getKitchenRoomList();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Oops Your kitchen deleting failure on your property",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("deletekitchen_room Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("kitchenroomid", "" + roomid);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void deleteBathroom(final String roomid) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "deletebathroom";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("deletebathroom", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());
                            JSONArray dataArr = dataOb.getJSONArray("Message");
                            JSONObject jsob = dataArr.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                Toast.makeText(RoomTypeActivity.this, "Your Bathroom deleted successfully on your property",
                                        Toast.LENGTH_LONG).show();
                                getBathRoomList();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Oops Your Bathroom deleting failure on your property",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("deletebathroom Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("bathroomid", "" + roomid);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void deleteStairsLanding(final String roomid) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "deletestairs_landing";
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
                                Toast.makeText(RoomTypeActivity.this, "Your Stairs and Landing deleted successfully on your property",
                                        Toast.LENGTH_LONG).show();
                                getStairsAndListing();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Oops Your Stairs and Landing deleting failure on your property",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("deletestairs_landing Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("stairslandingid", "" + roomid);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void deleteOutsideSpace(final String roomid) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "deleteoutside_space";
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("deleteoutside_space", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());
                            JSONArray dataArr = dataOb.getJSONArray("Message");
                            JSONObject jsob = dataArr.getJSONObject(0);
                            if (jsob.getBoolean("result")) {
                                Toast.makeText(RoomTypeActivity.this, "Your Outside space deleted successfully on your property",
                                        Toast.LENGTH_LONG).show();
                                getOutsideSpaceListing();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Oops Your Outside space deleting failure on your property",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("deleteoutside_space Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomTypeActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomTypeActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("outsidespaceid", "" + roomid);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

}
