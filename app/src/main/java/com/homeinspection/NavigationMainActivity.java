package com.homeinspection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NavigationMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PropertyListingFragment.OnPropertyFragmentInteractionListener {
    static String InventoryDetail = "", InventoryCheck = "";
    ImageView imvPropertyImage;
    TextView txvPropertyAddress, txvPropertyAddress2City;
    static MenuItem actionViewItem;
    static String chkselected = "PROPERTY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");

//        final Button InventoryCheckIn = (Button) findViewById(R.id.btnInventoryCheckin);
//        final Button InventoryCheckOut = (Button) findViewById(R.id.btnInventoryCheckout);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setItemIconTintList(null);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.parseColor("#8c001a")));
        navigationView.setNavigationItemSelectedListener(this);

        imvPropertyImage = (ImageView) findViewById(R.id.imvPropertyImage);
        txvPropertyAddress = (TextView) findViewById(R.id.txvPropertyAddress);
        txvPropertyAddress2City = (TextView) findViewById(R.id.txvPropertyAddress2City);

//        SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
//        txvPropertyAddress.setText(preferences.getString("ADDRESS1", ""));
//        txvPropertyAddress2City.setText(preferences.getString("ADDRESS2", "") + "          " + preferences.getString("CITY", ""));
//        if (!preferences.getString("IMAGE", "").isEmpty()) {
//            Picasso.with(NavigationMainActivity.this)
//                    .load(preferences.getString("IMAGE", ""))
//                    .into(imvPropertyImage);
//        }

//        InventoryCheckIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                InventoryCheckIn.setBackgroundResource(R.drawable.style_button_selected_background);
//                InventoryCheckOut.setBackgroundResource(R.drawable.style_button_background);
//                inventoryCheckIn();
//            }
//        });
//        InventoryCheckOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                InventoryCheckOut.setBackgroundResource(R.drawable.style_button_selected_background);
//                InventoryCheckIn.setBackgroundResource(R.drawable.style_button_background);
//                inventoryCheckOut();
//            }
//        });
        selectMenuItem(R.id.nav_property_listing);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        actionViewItem = menu.findItem(R.id.action_create);
        View v = MenuItemCompat.getActionView(actionViewItem);
        ImageView add = (ImageView) v.findViewById(R.id.txvCustomAction);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkselected.equalsIgnoreCase("PROPERTY")) {
                    Intent i = new Intent(NavigationMainActivity.this, AddPropertyActivity.class);
                    i.putExtra("CALL", "NORMAL");
                    startActivityForResult(i, 1);
                } else if (chkselected.equalsIgnoreCase("DECLARATION")) {
                   // DeclarationFragment.addData();
                }
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 1) {
                Fragment mapFragment = new PropertyListingFragment();
                if (mapFragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.frmContentLayout, mapFragment);
                    fragmentTransaction.commit();
                }
                getSupportActionBar().setTitle("Property Listing");
            }
        }
    }

    public void selectMenuItem(int id) {
        if (id == R.id.nav_property_listing) {
            Fragment mapFragment = new PropertyListingFragment();
            if (mapFragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                fragmentTransaction.replace(R.id.frmContentLayout, mapFragment);
                fragmentTransaction.commit();
            }
            getSupportActionBar().setTitle("Property Listing");
            chkselected = "PROPERTY";
//            Intent i = new Intent(NavigationMainActivity.this, PropertyListingFragment.class);
//            i.putExtra("GET_DATA", InventoryDetail);
//            i.putExtra("CALL", InventoryCheck);
//            startActivity(i);

        } else if (id == R.id.nav_tenancy_details) {
            if (!getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "").isEmpty()) {
//                Intent i = new Intent(NavigationMainActivity.this, TenancyDetailsActivity.class);
//                startActivity(i);
                Fragment fragment = new TenancyDetailsFragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.frmContentLayout, fragment);
                    fragmentTransaction.commit();
                }
                getSupportActionBar().setTitle("Tenancy Details");
                actionViewItem.setVisible(false);
            } else {
                Toast.makeText(this, "Please select any property", Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.nav_meter_readings) {
            if (!getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "").isEmpty()) {
//                Intent i = new Intent(NavigationMainActivity.this, MeterReadingsFragment.class);
//                startActivity(i);
                Fragment fragment = new MeterReadingsFragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.frmContentLayout, fragment);
                    fragmentTransaction.commit();
                }
                getSupportActionBar().setTitle("Meter Readings");
                actionViewItem.setVisible(false);
            } else {
                Toast.makeText(this, "Please select any property", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_safety_general) {
            if (!getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "").isEmpty()) {
//                Intent i = new Intent(NavigationMainActivity.this, SafetyAndGeneralFragment.class);
//                startActivity(i);
                Fragment fragment = new SafetyAndGeneralFragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.frmContentLayout, fragment);
                    fragmentTransaction.commit();
                }
                getSupportActionBar().setTitle("Safety & General");
                actionViewItem.setVisible(false);
            } else {
                Toast.makeText(this, "Please select any property", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_property_overview) {
            if (!getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "").isEmpty()) {
//                Intent i = new Intent(NavigationMainActivity.this, PropertyOverviewFragment.class);
//                startActivity(i);
                Fragment fragment = new PropertyOverviewFragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.frmContentLayout, fragment);
                    fragmentTransaction.commit();
                }
                getSupportActionBar().setTitle("Property Overview");
                actionViewItem.setVisible(false);
            } else {
                Toast.makeText(this, "Please select any property", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_room_order) {
            if (!getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "").isEmpty()) {
//                Intent i = new Intent(NavigationMainActivity.this, RoomOrderFragment.class);
//                startActivity(i);
                Fragment fragment = new RoomOrderFragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.frmContentLayout, fragment);
                    fragmentTransaction.commit();
                }
                getSupportActionBar().setTitle("Room Order");
                actionViewItem.setVisible(false);
            } else {
                Toast.makeText(this, "Please select any property", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_declaration) {
            if (!getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "").isEmpty()) {
//                Intent i = new Intent(NavigationMainActivity.this, DeclarationFragment.class);
//                startActivity(i);
                Fragment fragment = new DeclarationFragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.frmContentLayout, fragment);
                    fragmentTransaction.commit();
                }
                if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                    getSupportActionBar().setTitle("Check In Declaration");
                } else if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                    getSupportActionBar().setTitle("Check Out Declaration");
                }
                chkselected = "DECLARATION";
                //getSupportActionBar().setTitle("Room Order");
                actionViewItem.setVisible(false);
            } else {
                Toast.makeText(this, "Please select any property", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_generate_pdf) {
            if (!getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "").isEmpty()) {
                Fragment fragment = new GenearatePDFFragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frmContentLayout, fragment);
                    fragmentTransaction.commit();
                }
                    getSupportActionBar().setTitle("Generate PDF");
                actionViewItem.setVisible(false);
            } else {
                Toast.makeText(this, "Please select any property", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_signedout) {
            SharedPreferences sharedPreferences5 = getSharedPreferences(
                    "LOGIN_DETAIL", 0);
            sharedPreferences5.edit().clear().commit();
            Intent intent = new Intent(NavigationMainActivity.this,
                    LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        selectMenuItem(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void inventoryCheckIn() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "list_property_checkin";
        //?userid=" + getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", "0");
        final ProgressDialog mProgressDialog = new ProgressDialog(NavigationMainActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("Inventory check in", response.toString());
                        try {
                            InventoryCheck = "checkin";
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("result");
                            if (status.equalsIgnoreCase("true")) {
                                InventoryDetail = response.toString();
                            } else {
                                InventoryDetail = "";
                            }
                            Intent i = new Intent(NavigationMainActivity.this, PropertyListingFragment.class);
                            i.putExtra("GET_DATA", InventoryDetail);
                            i.putExtra("CALL", InventoryCheck);
                            startActivity(i);

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(NavigationMainActivity.this,
                                    "Sorry! we are stuff to fetching data. \n Please try again!",
                                    Toast.LENGTH_SHORT).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(NavigationMainActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(NavigationMainActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("Inventory check in Error", "Error: " + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(NavigationMainActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(NavigationMainActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //?userid=" + getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", "0");

                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", "0"));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void inventoryCheckOut() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "list_property_checkout";//?userid=" + getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", "0");
        final ProgressDialog mProgressDialog = new ProgressDialog(NavigationMainActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("Inventory check out", response.toString());
                        try {
                            InventoryCheck = "checkout";
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String status = object.getString("result");
                            if (status.equalsIgnoreCase("true")) {
                                InventoryDetail = response.toString();
                            } else {
                                InventoryDetail = "";
                            }
                            Intent i = new Intent(NavigationMainActivity.this, PropertyListingFragment.class);
                            i.putExtra("GET_DATA", InventoryDetail);
                            i.putExtra("CALL", InventoryCheck);
                            startActivity(i);

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(NavigationMainActivity.this,
                                    "Sorry! we are stuff to fetching data. \n Please try again!",
                                    Toast.LENGTH_SHORT).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(NavigationMainActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(NavigationMainActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("Inventory check out Error", "Error: " + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(NavigationMainActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(NavigationMainActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
