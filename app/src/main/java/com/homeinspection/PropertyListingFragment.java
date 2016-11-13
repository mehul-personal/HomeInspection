package com.homeinspection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PropertyListingFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static ArrayList<String> id, address1, address2, city, image, country, postcode,
            createdDate, propertytypeid_checkin, propertytypeid_checkout, propertyType_checkin,
            propertyType_checkout, propertyDate_checkin, propertyDate_checkout, propertyType_normal, propertyDate_normal;
    static ArrayList<Boolean> selectedList;
    ListView lsvPropertyList;
    //ImageView imvAddProperty;
    FrameLayout back;
    String INVENTORY_CALL = "";
    PropertyListAdapter adapter;
    Button btnCheckin, btnCheckout, btnUpdateProperty;
    EditText edtSearch;
    private String mParam1;
    private String mParam2;
    private OnPropertyFragmentInteractionListener mListener;

    public PropertyListingFragment() {
    }

    public static PropertyListingFragment newInstance(String param1, String param2) {
        PropertyListingFragment fragment = new PropertyListingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_property_listing, container, false);
        lsvPropertyList = (ListView) view.findViewById(R.id.lsvPropertyList);
        //imvAddProperty = (ImageView) view.findViewById(R.id.imvAddProperty);
        back = (FrameLayout) view.findViewById(R.id.flBack);
        btnCheckin = (Button) view.findViewById(R.id.btnCheckin);
        btnCheckout = (Button) view.findViewById(R.id.btnCheckout);
        btnUpdateProperty = (Button) view.findViewById(R.id.btnUpdateProperty);
        edtSearch = (EditText) view.findViewById(R.id.edtSearch);

        btnCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "").isEmpty()) {
                    Toast.makeText(getActivity(), "Please select any property", Toast.LENGTH_SHORT).show();
                } else if (getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                    Toast.makeText(getActivity(), "Sorry! This property has already been checked In.", Toast.LENGTH_SHORT).show();
                } else {
                    addPropertyType(getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", ""),
                            "checkin");
                }
            }
        });
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "").isEmpty()) {
                    Toast.makeText(getActivity(), "Please select any property", Toast.LENGTH_SHORT).show();
                } else if (getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                    Toast.makeText(getActivity(), "Sorry! This property has already been checked Out.", Toast.LENGTH_SHORT).show();
                } else {
                    addPropertyType(getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", ""),
                            "checkout");

                }
            }
        });
        btnUpdateProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.size() <= 0) {
                    Toast.makeText(getActivity(), "Please add new property", Toast.LENGTH_SHORT).show();
                } else if (getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "").isEmpty()) {
                    Toast.makeText(getActivity(), "Please select any property", Toast.LENGTH_SHORT).show();
                } else if (id.size() > 0) {
                    Intent i = new Intent(getActivity(), AddPropertyActivity.class);
                    i.putExtra("CALL", "EDIT");
                    startActivityForResult(i,13);
                }
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtSearch.getText().toString().length() < 2) {
                    adapter = new PropertyListAdapter(id, address1, address2, city,
                            propertytypeid_checkin, propertytypeid_checkout,
                            propertyDate_checkin, propertyDate_checkout, image, selectedList);
                    lsvPropertyList.setAdapter(adapter);
                } else {
                    ArrayList<String> SEARCH_propertytyppeid = new ArrayList<String>();
                    ArrayList<String> SEARCH_propertyid_checkin = new ArrayList<String>();
                    ArrayList<String> SEARCH_propertyid_checkout = new ArrayList<String>();
                    ArrayList<String> SEARCH_id = new ArrayList<String>();
                    ArrayList<String> SEARCH_address1 = new ArrayList<String>();
                    ArrayList<String> SEARCH_address2 = new ArrayList<String>();
                    ArrayList<String> SEARCH_city = new ArrayList<String>();
                    ArrayList<String> SEARCH_image = new ArrayList<String>();
                    ArrayList<String> SEARCH_country = new ArrayList<String>();
                    ArrayList<String> SEARCH_postcode = new ArrayList<String>();
                    ArrayList<String> SEARCH_createdDate = new ArrayList<String>();
                    ArrayList<String> SEARCH_propertyType = new ArrayList<String>();
                    ArrayList<String> SEARCH_propertyDate = new ArrayList<String>();
                    ArrayList<String> SEARCH_property_date_checkin = new ArrayList<String>();
                    ArrayList<String> SEARCH_property_date_checkout = new ArrayList<String>();
                    ArrayList<Boolean> SEARCH_selectedList = new ArrayList<Boolean>();

                    for (int i = 0; i < id.size(); i++) {

                        boolean flag = false;
                        if (address1.get(i).contains(edtSearch.getText().toString())) {
                            flag = true;
                        } else {
                            if (address2.get(i).contains(edtSearch.getText().toString())) {
                                flag = true;
                            } else {
                                if (city.get(i).contains(edtSearch.getText().toString())) {
                                    flag = true;
                                } else {
                                    if (country.get(i).contains(edtSearch.getText().toString())) {
                                        flag = true;
                                    } else {
                                        if (postcode.get(i).contains(edtSearch.getText().toString())) {
                                            flag = true;
                                        }
                                    }
                                }
                            }
                        }

                        if (flag) {
                            SEARCH_propertytyppeid.add(!propertytypeid_checkout.get(i).isEmpty() ? propertytypeid_checkout.get(i) : propertytypeid_checkin.get(i));
                            SEARCH_id.add(id.get(i));
                            SEARCH_propertyid_checkin.add(propertytypeid_checkin.get(i));
                            SEARCH_propertyid_checkout.add(propertytypeid_checkout.get(i));
                            SEARCH_address1.add(address1.get(i));
                            SEARCH_address2.add(address2.get(i));
                            SEARCH_city.add(city.get(i));
                            SEARCH_image.add(image.get(i));
                            SEARCH_country.add(country.get(i));
                            SEARCH_postcode.add(postcode.get(i));
                            SEARCH_createdDate.add(createdDate.get(i));
                            SEARCH_propertyType.add(!propertyType_checkout.get(i).isEmpty() ? propertyType_checkout.get(i) : propertyType_checkin.get(i));
                            SEARCH_propertyDate.add(!propertyDate_checkout.get(i).isEmpty() ? propertyDate_checkout.get(i) : propertyDate_checkin.get(i));
                            SEARCH_property_date_checkin.add(propertyDate_checkin.get(i));
                            SEARCH_property_date_checkout.add(propertyDate_checkout.get(i));
                            SEARCH_selectedList.add(selectedList.get(i));
                        }
                    }

                    adapter = new PropertyListAdapter(SEARCH_id, SEARCH_address1, SEARCH_address2, SEARCH_city, SEARCH_propertyid_checkin, SEARCH_propertyid_checkout,
                            SEARCH_property_date_checkin, SEARCH_property_date_checkout, SEARCH_image, SEARCH_selectedList);
                    lsvPropertyList.setAdapter(adapter);
                }
            }
        });
        inventoryList();
        return view;
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        inventoryList();
////        if (INVENTORY_CALL.equalsIgnoreCase("checkin")) {
//            inventoryCheckIn();
//        } else if (INVENTORY_CALL.equalsIgnoreCase("checkout")) {
//            inventoryCheckOut();
//        }
    //}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(requestCode==13){
                inventoryList();
            }
        }
    }

    public void addPropertyType(final String propertyId, final String propertyType) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "insertpropertytype";

        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("add Property type", ":" + response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());

                            JSONArray msgArray = dataOb.getJSONArray("Message");
                            JSONObject msgOb = msgArray.getJSONObject(0);
                            if (msgOb.getBoolean("result")) {

                                if (propertyType.equalsIgnoreCase("checkin")) {
                                    Toast.makeText(getActivity(), "Your property Check In process started successfully",
                                            Toast.LENGTH_LONG).show();
                                    if (getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "").equalsIgnoreCase(propertyId)) {
                                        SharedPreferences preferences = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);
                                        SharedPreferences.Editor edit = preferences.edit();
                                        edit.putString("PROPERTY_TYPE", "checkin");
                                        edit.putString("PROPERTY_TYPE_ID", msgOb.getString("id"));
                                        edit.putString("PROPERTY_TYPE_ID_CHECKIN", msgOb.getString("id"));
                                        edit.commit();
                                    }
                                    inventoryList();
                                } else {
                                    Toast.makeText(getActivity(), "Your property Check Out process started successfully",
                                            Toast.LENGTH_LONG).show();
                                    if (getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "").equalsIgnoreCase(propertyId)) {
                                        SharedPreferences preferences = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);
                                        SharedPreferences.Editor edit = preferences.edit();
                                        edit.putString("PROPERTY_TYPE", "checkout");
                                        edit.putString("PROPERTY_TYPE_ID", msgOb.getString("id"));
                                        edit.putString("PROPERTY_TYPE_ID_CHECKOUT", msgOb.getString("id"));
                                        edit.commit();
                                    }
                                    inventoryList();
                                    Intent i = new Intent(getActivity(), AddPropertyActivity.class);
                                    i.putExtra("CALL", "EDIT");
                                    startActivityForResult(i,13);
                                }
                                Log.e("Property details", "Property_id:" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "") +
                                        "PROPERTY_TYPE_ID:" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "") + ",PROPERTY_TYPE:" +
                                        getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", ""));
                            } else {
                                Toast.makeText(getActivity(), "" + msgOb.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Your property updating failed \n Please check all details",
                                    Toast.LENGTH_LONG).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("add propertytype Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("propertyid", "" + propertyId);
                params.put("propertytype", "" + propertyType);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void inventoryList() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "list_property_propertytype";
//        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
//        mProgressDialog.setTitle("");
//        mProgressDialog.setCanceledOnTouchOutside(false);
//        mProgressDialog.setMessage("Please Wait...");
//        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("Inventory property list", response.toString());
                        try {
                            propertytypeid_checkin = new ArrayList<String>();
                            propertytypeid_checkout = new ArrayList<String>();
                            propertyType_checkout = new ArrayList<String>();
                            propertyType_checkin = new ArrayList<String>();
                            propertyDate_checkin = new ArrayList<String>();
                            propertyDate_checkout = new ArrayList<String>();
                            propertyType_normal = new ArrayList<String>();
                            propertyDate_normal = new ArrayList<String>();
                            id = new ArrayList<String>();
                            address1 = new ArrayList<String>();
                            address2 = new ArrayList<String>();
                            city = new ArrayList<String>();
                            image = new ArrayList<String>();
                            country = new ArrayList<String>();
                            postcode = new ArrayList<String>();
                            createdDate = new ArrayList<String>();
                            selectedList = new ArrayList<Boolean>();

//                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("result");
                            if (status.equalsIgnoreCase("true")) {

                                JSONArray PropertyArrayData = object.getJSONArray("propertylistdata");

                                for (int i = 0; i < PropertyArrayData.length(); i++) {
                                    JSONObject PropertyObject = PropertyArrayData.getJSONObject(i);
                                    id.add(PropertyObject.getString("id"));
                                    address1.add(PropertyObject.getString("address1"));
                                    address2.add(PropertyObject.getString("address2"));
                                    city.add(PropertyObject.getString("city"));
                                    image.add(PropertyObject.getString("property_image"));
                                    country.add(PropertyObject.getString("county"));
                                    postcode.add(PropertyObject.getString("postcode"));
                                    createdDate.add(PropertyObject.getString("createdate"));
                                    //PropertyObject.getJSONObject("propertytypecheckinlist");
                                    selectedList.add(false);
                                    if (id.get(i).equalsIgnoreCase(getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", ""))) {
                                        selectedList.set(i, true);
                                    }
                                    JSONArray checkoutArray;
                                    try {
                                        checkoutArray = PropertyObject.getJSONArray("propertytypecheckoutlist");
                                        if (checkoutArray.length() > 0) {
                                            JSONObject checkoutObject = checkoutArray.getJSONObject(0);
                                            if (checkoutObject != null) {
                                                propertyType_checkout.add(checkoutObject.getString("propertytype"));
                                                propertyDate_checkout.add(checkoutObject.getString("propertydate"));
                                                propertytypeid_checkout.add(checkoutObject.getString("propertytypeid"));
                                                propertyType_normal.add(checkoutObject.getString("propertytype"));
                                                propertyDate_normal.add(checkoutObject.getString("propertydate"));
                                            }
                                        } else {
                                            propertyType_checkout.add("");
                                            propertyDate_checkout.add("");
                                            propertytypeid_checkout.add("0");

                                            JSONArray checkinArray = PropertyObject.getJSONArray("propertytypecheckinlist");
                                            if (checkinArray.length() > 0) {
                                                JSONObject checkinObject = checkinArray.getJSONObject(0);
                                                if (checkinObject != null) {
                                                    propertyType_normal.add(checkinObject.getString("propertytype"));
                                                    propertyDate_normal.add(checkinObject.getString("propertydate"));
                                                }

                                            }
                                        }
                                        JSONArray checkinArray = PropertyObject.getJSONArray("propertytypecheckinlist");
                                        if (checkinArray.length() > 0) {
                                            JSONObject checkinObject = checkinArray.getJSONObject(0);
                                            if (checkinObject != null) {
                                                propertyType_checkin.add(checkinObject.getString("propertytype"));
                                                propertyDate_checkin.add(checkinObject.getString("propertydate"));
                                                propertytypeid_checkin.add(checkinObject.getString("propertytypeid"));
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();


                                    }

                                }
                                adapter = new PropertyListAdapter(id, address1, address2, city, propertytypeid_checkin, propertytypeid_checkout,
                                        propertyDate_checkin, propertyDate_checkout, image, selectedList);
                                lsvPropertyList.setAdapter(adapter);
                            }


                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Sorry! we are stuff to fetching data. \n Please try again!",
                                    Toast.LENGTH_SHORT).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                mProgressDialog.dismiss();
                VolleyLog.e("Inventory check in Error", "Error: " + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
// ?emailid="+edtEmail.getText().toString()+"&password="+edtPassword.getText().toString()
                Log.e("userid:", ":" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", "0"));
                params.put("userid", "" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", "0"));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPropertyFragmentInteractionListener) {
            mListener = (OnPropertyFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPropertyFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class PropertyListAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<String> propertyid, address1, address2, city, property_type_id_checkin, property_type_id_checkout, property_checkin_date, property_checkout_date;
        ArrayList<String> imagelist;
        ArrayList<Boolean> selectedList;
        int m = 0;

        public PropertyListAdapter(ArrayList<String> propertyID, ArrayList<String> address1, ArrayList<String> address2, ArrayList<String> city,
                                   ArrayList<String> property_type_id_checkin, ArrayList<String> property_type_id_checkout,
                                   ArrayList<String> property_checkin_date, ArrayList<String> property_checkout_date,
                                   ArrayList<String> image, ArrayList<Boolean> selectedList) {
            // TODO Auto-generated constructor stub
            propertyid = propertyID;
            this.address1 = address1;
            this.address2 = address2;
            this.city = city;
            this.property_type_id_checkin = property_type_id_checkin;
            this.property_type_id_checkout = property_type_id_checkout;
            this.property_checkin_date = property_checkin_date;
            this.property_checkout_date = property_checkout_date;
            this.imagelist = image;


            this.selectedList = selectedList;
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.property_listitem,
                        parent, false);
                holder = new ViewHolder();
                holder.txvName = (TextView) convertView.findViewById(R.id.txvName);
                holder.txvType = (TextView) convertView.findViewById(R.id.txvType);
                holder.imvImage = (ImageView) convertView.findViewById(R.id.imvImage);
                holder.txvAddress = (TextView) convertView.findViewById(R.id.txvAddress);
                holder.txvCheckin = (TextView) convertView.findViewById(R.id.txvCheckin);
                holder.itemRow = (LinearLayout) convertView.findViewById(R.id.llItemRow);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txvName.setText(address1.get(position));
            holder.txvType.setText(address2.get(position));
            holder.txvAddress.setText(city.get(position));
            if (property_type_id_checkout.get(position).isEmpty()) {
                holder.txvCheckin.setText("Check In " + property_checkin_date.get(position));
            } else {
                if (Long.parseLong(property_type_id_checkin.get(position)) > Long.parseLong(property_type_id_checkout.get(position))) {
                    holder.txvCheckin.setText("Check In " + property_checkin_date.get(position));
                } else {
                    holder.txvCheckin.setText("Check Out " + property_checkout_date.get(position));
                }
            }

            Picasso.with(getActivity())
                    .load(imagelist.get(position)).resize(500, 500)
                    .into(holder.imvImage);
            holder.itemRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveData(position);
                }
            });


            if (selectedList.get(position)) {
                holder.itemRow.setBackgroundResource(R.color.colorSelectedButton);
            } else {
                holder.itemRow.setBackgroundResource(0);
            }

            return convertView;
        }

        public void saveData(int position) {
            //id,address1,address2,city,checkin,image,country,postcode,createdDate,propertyType,propertyDate
            for (int i = 0; i < selectedList.size(); i++) {
                selectedList.set(i, false);
            }
            selectedList.set(position, true);
            if (id.size() > 0) {
                SharedPreferences preferences = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("PROPERTY_ID", id.get(position));
                edit.putString("PROPERTY_TYPE_ID_CHECKIN", propertytypeid_checkin.get(position).isEmpty() ? "0" : propertytypeid_checkin.get(position));
                edit.putString("PROPERTY_TYPE_ID_CHECKOUT", propertytypeid_checkout.get(position).isEmpty() ? "0" : propertytypeid_checkout.get(position));
                edit.putString("ADDRESS1", address1.get(position));
                edit.putString("ADDRESS2", address2.get(position));
                edit.putString("CITY", city.get(position));
                edit.putString("IMAGE", image.get(position));
                edit.putString("COUNTRY", country.get(position));
                edit.putString("POSTCODE", postcode.get(position));
                edit.putString("CREATE_DATE", createdDate.get(position));
                edit.putString("PROPERTY_TYPE_CHECKIN", propertyType_checkin.get(position));
                edit.putString("PROPERTY_TYPE_CHECKOUT", propertyType_checkout.get(position));
                edit.putString("PROPERTY_DATE_CHECKIN", propertyDate_checkin.get(position));
                edit.putString("PROPERTY_DATE_CHECKOUT", propertyDate_checkout.get(position));
                if (property_type_id_checkout.get(position).isEmpty()) {
                    edit.putString("INVENTORY_CALL", "Checkin");
                    edit.putString("PROPERTY_TYPE_ID", propertytypeid_checkin.get(position).isEmpty() ? "0" : propertytypeid_checkin.get(position));
                    edit.putString("PROPERTY_TYPE", propertyType_checkin.get(position));
                    edit.putString("PROPERTY_DATE", propertyDate_checkin.get(position));

                } else {
                    if (Long.parseLong(property_type_id_checkin.get(position)) > Long.parseLong(property_type_id_checkout.get(position))) {
                        edit.putString("INVENTORY_CALL", "Checkin");
                        edit.putString("PROPERTY_TYPE_ID", propertytypeid_checkin.get(position).isEmpty() ? "0" : propertytypeid_checkin.get(position));
                        edit.putString("PROPERTY_TYPE", propertyType_checkin.get(position));
                        edit.putString("PROPERTY_DATE", propertyDate_checkin.get(position));
                    } else {
                        edit.putString("INVENTORY_CALL", "Checkout");
                        edit.putString("PROPERTY_TYPE_ID", propertytypeid_checkout.get(position).isEmpty() ? "0" : propertytypeid_checkout.get(position));
                        edit.putString("PROPERTY_TYPE", propertyType_checkout.get(position));
                        edit.putString("PROPERTY_DATE", propertyDate_checkout.get(position));
                    }
                }
                edit.commit();

                Log.e("Property details", "Property_id:" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "") +
                        "PROPERTY_TYPE_ID:" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "") + ",PROPERTY_TYPE:" +
                        getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", ""));

            }
            notifyDataSetChanged();
            //Toast.makeText(getActivity(), "Your selected property saved successfully", Toast.LENGTH_LONG).show();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return address1.size();
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
            TextView txvName, txvType, txvAddress, txvCheckin;
            ImageView imvImage;
            LinearLayout itemRow;
        }
    }
}
