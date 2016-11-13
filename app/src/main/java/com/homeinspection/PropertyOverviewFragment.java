package com.homeinspection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PropertyOverviewFragment  extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    Button btnPropertyDescription, btnGeneralConditionOfDecorations,
            btnGeneralConditionOfFloorings, btnGeneralConditionOfCleanliness, btnExteriorGardenDecoration;
    FrameLayout flBack;
    ImageView imvPropertyOverviewImage;
    TextView txvPropertyOverviewAddress, txvPropertyOverviewAddress2City;
    static String in_property_general = "", in_property_addtional = "", in_decorations_genreral = "", in_decorations_addtional = "", in_floorings_general = "",
            in_floorings_addtional = "", in_cleanliness_general = "", in_cleanliness_addtional = "", in_exterior_deco_general = "", in_exterior_deco_addtional = "";
    static String out_property_general = "", out_property_addtional = "", out_decorations_genreral = "", out_decorations_addtional = "", out_floorings_general = "",
            out_floorings_addtional = "", out_cleanliness_general = "", out_cleanliness_addtional = "", out_exterior_deco_general = "", out_exterior_deco_addtional = "";

    public PropertyOverviewFragment() {
    }

    public static PropertyOverviewFragment newInstance(String param1, String param2) {
        PropertyOverviewFragment fragment = new PropertyOverviewFragment();
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
        View view = inflater.inflate(R.layout.activity_property_overview, container, false);
        flBack = (FrameLayout) view.findViewById(R.id.flBack);
        btnPropertyDescription = (Button) view.findViewById(R.id.btnPropertyDescription);
        btnGeneralConditionOfDecorations = (Button) view.findViewById(R.id.btnGeneralConditionOfDecorations);
        btnGeneralConditionOfFloorings = (Button) view.findViewById(R.id.btnGeneralConditionOfFloorings);
        btnGeneralConditionOfCleanliness = (Button) view.findViewById(R.id.btnGeneralConditionOfCleanliness);
        btnExteriorGardenDecoration = (Button) view.findViewById(R.id.btnExteriorGardenDecoration);

        imvPropertyOverviewImage = (ImageView) view.findViewById(R.id.imvPropertyOverviewImage);
        txvPropertyOverviewAddress = (TextView) view.findViewById(R.id.txvPropertyOverviewAddress);
        txvPropertyOverviewAddress2City = (TextView) view.findViewById(R.id.txvPropertyOverviewAddress2City);

        SharedPreferences preferences = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);
        txvPropertyOverviewAddress.setText(preferences.getString("ADDRESS1", ""));
        txvPropertyOverviewAddress2City.setText(preferences.getString("ADDRESS2", "") + "          " + preferences.getString("CITY", ""));
        if (!preferences.getString("IMAGE", "").isEmpty()) {
            Picasso.with(getActivity())
                    .load(preferences.getString("IMAGE", "")).resize(500, 500)
                    .into(imvPropertyOverviewImage);
        }

       /* flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        btnPropertyDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PropertyOverviewCheckInOutActivity.class);
                i.putExtra("HEADER", "Property Description");
                i.putExtra("APICALL", "property");
                i.putExtra("IN_DESC", "" + in_property_general);
                i.putExtra("IN_ADDITIONAL", "" + in_property_addtional);
                i.putExtra("OUT_DESC", "" + out_property_general);
                startActivityForResult(i,11);
            }
        });
        btnGeneralConditionOfDecorations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PropertyOverviewCheckInOutActivity.class);
                i.putExtra("HEADER", "Decorations");
                i.putExtra("APICALL", "decorations");
                i.putExtra("IN_DESC", "" + in_decorations_genreral);
                i.putExtra("IN_ADDITIONAL", "" + in_decorations_addtional);
                i.putExtra("OUT_DESC", "" + out_decorations_genreral);
                startActivityForResult(i,11);
            }
        });
        btnGeneralConditionOfFloorings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PropertyOverviewCheckInOutActivity.class);
                i.putExtra("HEADER", "Flooring");
                i.putExtra("APICALL", "floorings");
                i.putExtra("IN_DESC", "" + in_floorings_general);
                i.putExtra("IN_ADDITIONAL", "" + in_floorings_addtional);
                i.putExtra("OUT_DESC", "" + out_floorings_general);
                startActivityForResult(i,11);
            }
        });
        btnGeneralConditionOfCleanliness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PropertyOverviewCheckInOutActivity.class);
                i.putExtra("HEADER", "Cleanliness");
                i.putExtra("APICALL", "cleanliness");
                i.putExtra("IN_DESC", "" + in_cleanliness_general);
                i.putExtra("IN_ADDITIONAL", "" + in_cleanliness_addtional);
                i.putExtra("OUT_DESC", "" + out_cleanliness_general);
                startActivityForResult(i,11);
            }
        });
        btnExteriorGardenDecoration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PropertyOverviewCheckInOutActivity.class);
                i.putExtra("HEADER", "Exterior/Garden Decoration");
                i.putExtra("APICALL", "exterior_deco");
                i.putExtra("IN_DESC", "" + in_exterior_deco_general);
                i.putExtra("IN_ADDITIONAL", "" + in_exterior_deco_addtional);
                i.putExtra("OUT_DESC", "" + out_exterior_deco_general);
                startActivityForResult(i,11);
            }
        });
        getPropertyOverviewList();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(requestCode==11){
                getPropertyOverviewList();
            }
        }
    }
/* @Override
    protected void onRestart() {
        super.onRestart();
        getPropertyOverviewList();
    }*/

    public void getPropertyOverviewList() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_property_overview";

        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_property_overview", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONArray dataArray = new JSONArray(response.toString());
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            if (dataObject.getBoolean("result")) {
                                JSONArray propertOverviewCheckIn = dataObject.getJSONArray("propertyoverviewcheckin");
                                JSONArray propertOverviewCheckOut = dataObject.getJSONArray("propertyoverviewcheckout");
                                if (propertOverviewCheckIn.length() > 0) {
                                    JSONObject checkInObject = propertOverviewCheckIn.getJSONObject(0);
                                    in_property_general = checkInObject.getString("property_general");
                                    in_property_addtional = checkInObject.getString("property_addtional");
                                    in_decorations_genreral = checkInObject.getString("decorations_genreral");
                                    in_decorations_addtional = checkInObject.getString("decorations_addtional");
                                    in_floorings_general = checkInObject.getString("floorings_general");
                                    in_floorings_addtional = checkInObject.getString("floorings_addtional");
                                    in_cleanliness_general = checkInObject.getString("cleanliness_general");
                                    in_cleanliness_addtional = checkInObject.getString("cleanliness_addtional");
                                    in_exterior_deco_general = checkInObject.getString("exterior_deco_general");
                                    in_exterior_deco_addtional = checkInObject.getString("exterior_deco_addtional");
                                }
                                if (propertOverviewCheckOut.length() > 0) {
                                    JSONObject checkOutObject = propertOverviewCheckOut.getJSONObject(0);
                                    out_property_general = checkOutObject.getString("property_general");
                                    out_property_addtional = checkOutObject.getString("property_addtional");
                                    out_decorations_genreral = checkOutObject.getString("decorations_genreral");
                                    out_decorations_addtional = checkOutObject.getString("decorations_addtional");
                                    out_floorings_general = checkOutObject.getString("floorings_general");
                                    out_floorings_addtional = checkOutObject.getString("floorings_addtional");
                                    out_cleanliness_general = checkOutObject.getString("cleanliness_general");
                                    out_cleanliness_addtional = checkOutObject.getString("cleanliness_addtional");
                                    out_exterior_deco_general = checkOutObject.getString("exterior_deco_general");
                                    out_exterior_deco_addtional = checkOutObject.getString("exterior_deco_addtional");
                                }
                            } else {
//                                Toast.makeText(getActivity(), "Oops! We can't found your previous History",
//                                        Toast.LENGTH_LONG).show();
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
                VolleyLog.e("list_property_overview Error", "Error: "
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
                Log.e("propertyid","propertytypecheckinid:"+(getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0").isEmpty() ? "0"
                        : getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0"))+"propertytypecheckoutid:"+(getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0").isEmpty() ? "0"
                        : getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0")));
                params.put("propertytypecheckinid",  getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0").isEmpty() ? "0"
                        : getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0"));
                params.put("propertytypecheckoutid", getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0").isEmpty() ? "0"
                        : getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }
}
