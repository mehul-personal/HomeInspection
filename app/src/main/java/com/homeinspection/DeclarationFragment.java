package com.homeinspection;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DeclarationFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static int year, month, day;
    public static Calendar calendar;
    static ArrayList<String> declarationId, name, type, date, signature, TENANTNAME;
    static ExpandableHeightGridView lsvDeclarationList;
    static DeclarationListAdapter adapter;
    static int SELECTED_POSITION;
    Button done;
    FrameLayout back;
    TextView txvContent, header;
    ImageView imvAdd;
    private String mParam1;
    private String mParam2;
    private DatePicker datePicker;

    public DeclarationFragment() {
    }

    public static DeclarationFragment newInstance(String param1, String param2) {
        DeclarationFragment fragment = new DeclarationFragment();
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
        View view = inflater.inflate(R.layout.activity_declaration, container, false);
        back = (FrameLayout) view.findViewById(R.id.flBack);
        done = (Button) view.findViewById(R.id.btnDone);
        txvContent = (TextView) view.findViewById(R.id.txvContent);
        header = (TextView) view.findViewById(R.id.header);
        lsvDeclarationList = (ExpandableHeightGridView) view.findViewById(R.id.lsvDeclarationList);
        lsvDeclarationList.setExpanded(true);
        imvAdd = (ImageView) view.findViewById(R.id.imvAdd);
        txvContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.txvContent) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
        declarationId = new ArrayList<String>();
        name = new ArrayList<String>();
        type = new ArrayList<String>();
        date = new ArrayList<String>();
        signature = new ArrayList<String>();

      /*  back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        done = (Button) view.findViewById(R.id.btnDone);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < declarationId.size(); i++) {
                    if (!name.get(i).isEmpty() && !date.get(i).isEmpty() && !signature.get(i).isEmpty())
                        addDeclaration(declarationId.get(i), name.get(i), type.get(i), date.get(i), signature.get(i), (i == (declarationId.size() - 1)));
                    else if (i == (declarationId.size() - 1)) {
                        getDeclarationDetailList();
                    }
                }
            }
        });


        View v = MenuItemCompat.getActionView(NavigationMainActivity.actionViewItem);
        ImageView add = (ImageView) v.findViewById(R.id.txvCustomAction);
       /* add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declarationId.add("0");
                name.add("");
                date.add("");
                signature.add("");

                adapter = new DeclarationListAdapter(name, date, signature);
                lsvDeclarationList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });*/
        /*imvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declarationId.add("0");
                name.add("");
                date.add("");
                signature.add("");

                adapter = new DeclarationListAdapter(name, date, signature);
                lsvDeclarationList.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        });*/
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        adapter = new DeclarationListAdapter(name, date, signature);
        lsvDeclarationList.setAdapter(adapter);


        getTenantList();

        getDeclarationDescription();
        return view;
    }

    public void getDeclarationDetailList() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_declaration";

        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_declaration", response.toString());
                        declarationId = new ArrayList<String>();
                        name = new ArrayList<String>();
                        type = new ArrayList<String>();
                        date = new ArrayList<String>();
                        signature = new ArrayList<String>();
                        try {

                            JSONArray dataArray = new JSONArray(response.toString());
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            if (dataObject.getBoolean("result")) {

                                if (getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                                    JSONArray checkInArray = dataObject.getJSONArray("outside_spacecheckin");
                                    for (int i = 0; i < checkInArray.length(); i++) {
                                        JSONObject checkInOb = checkInArray.getJSONObject(i);
                                        declarationId.add(checkInOb.getString("id"));
                                        name.add(checkInOb.getString("name"));
                                        type.add(checkInOb.getString("type"));
                                        date.add(checkInOb.getString("sdate"));
                                        signature.add(checkInOb.getString("signature_picture"));

                                    }
                                } else if (getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                                    JSONArray checkOutArray = dataObject.getJSONArray("outside_spacecheckout");
                                    for (int i = 0; i < checkOutArray.length(); i++) {
                                        JSONObject checkInOb = checkOutArray.getJSONObject(i);
                                        declarationId.add(checkInOb.getString("id"));
                                        name.add(checkInOb.getString("name"));
                                        type.add(checkInOb.getString("type"));
                                        date.add(checkInOb.getString("sdate"));
                                        signature.add(checkInOb.getString("signature_picture"));

                                    }
                                }

                                //TENANTNAME LIST GET TO SET DATA

                                for (int j = 0; j < TENANTNAME.size(); j++) {
                                    int flagname = 0;
                                    for (int i = 0; i < name.size(); i++) {
                                        if (name.get(i).compareTo(TENANTNAME.get(j)) == 0) {
                                            flagname++;
                                        }
                                    }
                                    if (flagname <= 0) {
                                        declarationId.add("0");
                                        name.add(TENANTNAME.get(j));
                                        type.add("tenant");
                                        date.add("");
                                        signature.add("");
                                    }
                                }

                                if (name.size() <= 0) {
                                    declarationId.add("0");
                                    name.add("");
                                    type.add("agent");
                                    date.add("");
                                    signature.add("");
                                } else {
                                    int flag = 0;
                                    for (int i = 0; i < type.size(); i++) {
                                        if (type.get(i).equalsIgnoreCase("agent")) {
                                            flag++;
                                        }
                                    }
                                    if (flag <= 0) {
                                        declarationId.add("0");
                                        name.add("");
                                        type.add("agent");
                                        date.add("");
                                        signature.add("");
                                    }
                                }
                                adapter = new DeclarationListAdapter(name, date, signature);
                                lsvDeclarationList.setAdapter(adapter);
                            } else {
                                for (int j = 0; j < TENANTNAME.size(); j++) {
                                    int flagname = 0;
                                    for (int i = 0; i < name.size(); i++) {
                                        if (name.get(i).compareTo(TENANTNAME.get(j)) == 0) {
                                            flagname++;
                                        }
                                    }
                                    if (flagname <= 0) {
                                        declarationId.add("0");
                                        name.add(TENANTNAME.get(j));
                                        type.add("tenant");
                                        date.add("");
                                        signature.add("");
                                    }
                                }

                                declarationId.add("0");
                                name.add("");
                                type.add("agent");
                                date.add("");
                                signature.add("");

                                adapter = new DeclarationListAdapter(name, date, signature);
                                lsvDeclarationList.setAdapter(adapter);

                                Toast.makeText(getActivity(), "Oops! We can't found your previous History",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                        mProgressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_declaration Error", "Error: "
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
                Log.e("Property details", "Property_id:" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "") +
                        "PROPERTY_TYPE_ID:" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "") + ",PROPERTY_TYPE:" +
                        getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + "PROPERTY_TYPE_ID_CHECKIN" +
                        getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0") + "PROPERTY_TYPE_ID_CHECKOUT" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));

                Map<String, String> params = new HashMap<String, String>();
                params.put("propertytypecheckinid", "" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0"));
                params.put("propertytypecheckoutid", "" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
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
//                            TENANTID = new ArrayList<String>();
//                            PROPERTYID = new ArrayList<String>();
                            TENANTNAME = new ArrayList<String>();

                            JSONArray jsonArr = new JSONArray(response.toString());
                            JSONObject jsonOb = jsonArr.getJSONObject(0);
                            if (jsonOb.getBoolean("result")) {
                                JSONArray tenantData = jsonOb.getJSONArray("tenantnamelist");
                                for (int i = 0; i < tenantData.length(); i++) {
                                    JSONObject dataOb = tenantData.getJSONObject(i);
//                                    TENANTID.add(dataOb.getString("id"));
//                                    PROPERTYID.add(dataOb.getString("pid"));
                                    TENANTNAME.add(dataOb.getString("name"));
                                }
                                // adapter.notifyDataSetChanged();
                            }
                            getDeclarationDetailList();

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
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
                VolleyLog.e("list_tenantlist Error", "Error: "
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
                params.put("propertyid", "" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", ""));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void addDeclaration(final String declarationId, final String name, final String type, final String date, final String signature, final boolean chkLast) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "insertdeclaration";

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("insertdeclaration", response.toString());
                        try {
                            JSONObject jsonOb = new JSONObject(response.toString());
                            JSONObject msgOb = jsonOb.getJSONArray("Message").getJSONObject(0);
                            if (msgOb.getBoolean("result")) {
                                if (declarationId.equalsIgnoreCase("0")) {
                                    Toast.makeText(getActivity(), "New Declaration added successfully",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), "Declaration updated successfully",
                                            Toast.LENGTH_LONG).show();
                                }
                                if (chkLast) {
                                    getDeclarationDetailList();
                                }
                                //  finish();
                            } else {
                                Toast.makeText(getActivity(), "" + msgOb.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
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
                VolleyLog.e("insertdeclaration Error", "Error: "
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

                params.put("declarationid", declarationId);
                if (getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                    params.put("propertytypeid", "" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", ""));
                } else if (getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                    params.put("propertytypeid", "" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", ""));
                }
                params.put("name", "" + name);
                params.put("sdate", "" + date);
                params.put("type", "" + type);
                params.put("signature", "" + signature);
                Log.e("param", ":" + params.toString());
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void getDeclarationDescription() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_declaration_description";

        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_declaration_descr", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONArray dataArray = new JSONArray(response.toString());
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            if (dataObject.getBoolean("result")) {
                                if (getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                                    txvContent.setText(dataObject.getString("declarationdescriptioncehckin"));
                                    header.setText("Check In Declaration");
                                } else if (getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                                    txvContent.setText(dataObject.getString("declarationdescriptioncehckout"));
                                    header.setText("Check Out Declaration");
                                }

                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
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
                VolleyLog.e("list_declaration_descr Error", "Error: "
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
//                Log.e("Property details", "Property_id:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "") +
//                        "PROPERTY_TYPE_ID:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "") + ",PROPERTY_TYPE:" +
//                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + "PROPERTY_TYPE_ID_CHECKIN" +
//                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0") + "PROPERTY_TYPE_ID_CHECKOUT" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));

                Map<String, String> params = new HashMap<String, String>();
//                params.put("propertytypecheckinid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0"));
//                params.put("propertytypecheckoutid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }


    public class DeclarationListAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<String> NAME, DATE, SIGNATURE;
        int m = 0;

        public DeclarationListAdapter(ArrayList<String> name, ArrayList<String> date, ArrayList<String> signature) {
            // TODO Auto-generated constructor stub
            NAME = name;
            DATE = date;
            SIGNATURE = signature;
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            final ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_declaration_listitem,
                        parent, false);
                holder = new ViewHolder();
                holder.edtName = (EditText) convertView.findViewById(R.id.edtName);
                holder.txvDate = (TextView) convertView.findViewById(R.id.txvDate);
                holder.edtSignature = (EditText) convertView.findViewById(R.id.edtSignature);
                holder.txvNameHeader = (TextView) convertView.findViewById(R.id.txvNameHeader);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.edtName.setText(NAME.get(position));
            holder.txvDate.setText(DATE.get(position));
            holder.edtSignature.setText(SIGNATURE.get(position));
            if (type.get(position).equalsIgnoreCase("agent")) {
                holder.txvNameHeader.setText("Agent Name");
            } else {
                holder.txvNameHeader.setText("Name");
            }
            holder.edtName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    NAME.set(position, holder.edtName.getText().toString());
                }
            });

            holder.edtSignature.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    SIGNATURE.set(position, holder.edtSignature.getText().toString());
                }
            });

            holder.txvDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatePickerDialog mDatePicker;
                    mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            DATE.set(position, new StringBuilder().append(dayOfMonth).append("/").append(monthOfYear).append("/").append(year).toString());
                            notifyDataSetChanged();
                        }
                    }, year, month, day);
                    mDatePicker.setTitle("Select Date");
                    mDatePicker.show();
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
            EditText edtName, edtSignature;
            TextView txvDate, txvNameHeader;
        }
    }

}
