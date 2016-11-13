package com.homeinspection;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddPropertyActivity extends AppCompatActivity {
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;
    static String selectedImage = "", getImage = "";
    static String CALL = "";
    FrameLayout flBack;
    EditText edtAddress, edtAddress2, edtCity, edtCountry, edtPostCode;
    Button btnDone;
    ImageView ivTakePhoto, imvAddProperty;
    Activity activity;
    private Uri mFileUri;

    public static void createDirectory(String filePath) {
        if (!new File(filePath).exists()) {
            new File(filePath).mkdirs();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (!isKitKat) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = context.getContentResolver().query(uri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
                // handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        activity = this;
        flBack = (FrameLayout) findViewById(R.id.flBack);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtAddress2 = (EditText) findViewById(R.id.edtAddress2);
        edtCity = (EditText) findViewById(R.id.edtCity);
        edtCountry = (EditText) findViewById(R.id.edtCountry);
        edtPostCode = (EditText) findViewById(R.id.edtPostCode);
        btnDone = (Button) findViewById(R.id.btnDone);
        ivTakePhoto = (ImageView) findViewById(R.id.ivTakePhoto);
        imvAddProperty = (ImageView) findViewById(R.id.imvAddProperty);
        CALL = getIntent().getStringExtra("CALL");
        if (CALL.equalsIgnoreCase("EDIT")) {
            SharedPreferences preferences = getSharedPreferences("LOGIN_DETAIL", 0);
            preferences.getString("ID", "");
            edtAddress.setText(preferences.getString("ADDRESS1", ""));
            edtAddress2.setText(preferences.getString("ADDRESS2", ""));
            edtCity.setText(preferences.getString("CITY", ""));
            getImage = preferences.getString("IMAGE", "");
            Picasso.with(AddPropertyActivity.this)
                    .load(preferences.getString("IMAGE", ""))
                    .into(ivTakePhoto);
            edtCountry.setText(preferences.getString("COUNTRY", ""));
            edtPostCode.setText(preferences.getString("POSTCODE", ""));
            preferences.getString("CREATE_DATE", "");
            preferences.getString("PROPERTY_TYPE", "");
            preferences.getString("PROPERTY_DATE", "");
            selectedImage = "";
        } else if (CALL.equalsIgnoreCase("NORMAL")) {

        }

        imvAddProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtAddress.setText("");
                edtAddress2.setText("");
                edtCity.setText("");
                edtCountry.setText("");
                edtPostCode.setText("");
                ivTakePhoto.setImageResource(R.drawable.ic_camera);
                CALL = "NORMAL";
            }
        });
        flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtAddress.getText().toString().isEmpty()) {
                    Toast.makeText(AddPropertyActivity.this, "Please add your property address", Toast.LENGTH_SHORT).show();
                } else if (edtCity.getText().toString().isEmpty()) {
                    Toast.makeText(AddPropertyActivity.this, "Please add your city", Toast.LENGTH_SHORT).show();
                } else if (edtCountry.getText().toString().isEmpty()) {
                    Toast.makeText(AddPropertyActivity.this, "Please add your country", Toast.LENGTH_SHORT).show();
                } else if (edtPostCode.getText().toString().isEmpty()) {
                    Toast.makeText(AddPropertyActivity.this, "Please add your postcode", Toast.LENGTH_SHORT).show();
                } else if (selectedImage.isEmpty() && CALL.equalsIgnoreCase("NORMAL")) {
                    Toast.makeText(AddPropertyActivity.this, "Please add Your property image", Toast.LENGTH_SHORT).show();
                } else {
                    insertProperty();
                }
            }
        });
        ivTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Take Photo",
                        "Choose from Gallery", "Cancel"};

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                        AddPropertyActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals("Take Photo")) {
                                    startCamera();
                                } else if (options[item]
                                        .equals("Choose from Gallery")) {
                                    startGallery();
                                } else if (options[item].equals("Cancel")) {
                                    dialog.dismiss();
                                }
                            }
                        });
                builder.show();
            }
        });
    }

    private void startCamera() {

        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mFileUri = getOutputMediaFile(1);
        if (mFileUri != null) {
            intent1.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            startActivityForResult(intent1, REQUEST_CAMERA);
        } else {
            Log.e("image camera", "file not available");
        }

    }

    private void startGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/*");
//        startActivityForResult(intent, REQUEST_GALLERY);

    }

    public Uri getOutputMediaFile(int type) {


        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "HomeInspection");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("survey form second", "could not create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            String imageStoragePath = mediaStorageDir + "/Images/";
            createDirectory(imageStoragePath);
            mediaFile = new File(imageStoragePath + "IMG" + timeStamp + ".jpg");

        } else {
            return null;
        }
        return Uri.fromFile(mediaFile);
    }

    public String getPath(Uri uri, boolean isImage) {
        if (uri == null) {
            return null;
        }
        String[] projection;
        String coloumnName, selection;
        if (isImage) {
            selection = MediaStore.Images.Media._ID + "=?";
            coloumnName = MediaStore.Images.Media.DATA;
        } else {
            selection = MediaStore.Video.Media._ID + "=?";
            coloumnName = MediaStore.Video.Media.DATA;
        }
        projection = new String[]{coloumnName};
        Cursor cursor;
        if (Build.VERSION.SDK_INT > 19) {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            // where id is equal to
            if (isImage) {
                cursor = getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection,
                                new String[]{id}, null);
            } else {
                cursor = getContentResolver()
                        .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection, new String[]{id},
                                null);
            }
        } else {
            cursor = getContentResolver().query(uri, projection, null, null, null);
        }
        String path = null;
        try {
            int column_index = cursor.getColumnIndex(coloumnName);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private String getRealPathFromURI(Uri contentURI) {


        String result = "";
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        }/* else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }*/
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {
                if (mFileUri != null) {
                    Log.d("upload image", "file: " + mFileUri);
                    selectedImage = getRealPathFromURI(mFileUri);
                } else {
                    if (data != null) {
                        try {
                            selectedImage = getPath(data.getData(), true);
                        } catch (Exception e) {
                            selectedImage = getRealPathFromURI(data.getData());
                        }
                    }
                }
                ivTakePhoto.setImageBitmap(getBitmap(new File(selectedImage)));
            } else if (requestCode == REQUEST_GALLERY) {
                if (data != null && data.getData() != null) {


                    Uri selectedImageUri = data.getData();
                    Log.e("Image path: ", selectedImageUri.toString());
                    selectedImage = getPath(AddPropertyActivity.this, selectedImageUri);
                    Log.e("Image path:", selectedImage);
                    System.out.println("Image Path : " + selectedImage);


//                    Bitmap bitmap = BitmapFactory.decodeFile(selectedImage);
//                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

//                    try {
//                        selectedImage = getPath(data.getData(), true);
//                    } catch (Exception e) {
//                        selectedImage = getRealPathFromURI(data.getData());
//                    }
                    ivTakePhoto.setImageBitmap(getThumbnailBitmap(selectedImage,200));
                }

            }
        }

    }

    private Bitmap getThumbnailBitmap(final String path, final int thumbnailSize) {
        Bitmap bitmap;
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1)) {
            bitmap = null;
        }
        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / thumbnailSize;
        bitmap = BitmapFactory.decodeFile(path, opts);
        return bitmap;
    }
    public Bitmap getBitmap(File file) {

        Bitmap bitmap = null;
        if (file != null) {
            String filePath = file.getAbsolutePath();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            bitmap = BitmapFactory.decodeFile(filePath, options);

        }
        return bitmap;
    }

    public String encodeToBase64(Bitmap bitmap) {

        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] b = baos.toByteArray();

        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        if (imageEncoded == null) {
            return "";
        }
        return imageEncoded;
    }

    public String getUrlContent(String urlstring) throws IOException {
        byte[] imageRaw = null;
        URL url = new URL(urlstring);

        HttpURLConnection urlConnection = (HttpURLConnection) url
                .openConnection();
        urlConnection.setUseCaches(false);
        urlConnection.connect();
        if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try {
                InputStream in = new BufferedInputStream(
                        urlConnection.getInputStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int c;
                while ((c = in.read()) != -1) {
                    out.write(c);
                }
                out.flush();

                imageRaw = out.toByteArray();

                urlConnection.disconnect();
                in.close();
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return Base64.encodeToString(imageRaw, Base64.DEFAULT);
        }
        return null;
    }

    public void insertProperty() {
        final String Address = edtAddress.getText().toString();
        final String Address2 = edtAddress2.getText().toString();
        final String City = edtCity.getText().toString();
        final String Country = edtCountry.getText().toString();
        final String PostCode = edtPostCode.getText().toString();
        new AsyncTask<Void, Void, String>() {
            ProgressDialog mProgressDialog;

            @Override
            protected String doInBackground(Void... params) {
                // TODO Auto-generated method stub
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(ApplicationData.serviceURL + "property_insert");

                try {

                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                    if (CALL.equalsIgnoreCase("EDIT")) {
                        entity.addPart("propertyid", new StringBody(getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "")));
                        try {
                            if (!selectedImage.isEmpty()) {
                                Bitmap bmp = getThumbnailBitmap(selectedImage,200);;
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
                                InputStream in = new ByteArrayInputStream(bos.toByteArray());
                                ContentBody foto = new InputStreamBody(in, "image/jpeg", selectedImage.substring(selectedImage.lastIndexOf('/') + 1, selectedImage.lastIndexOf('.')));

                                entity.addPart("property_imageupload", foto);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {

                        //Bitmap bmp = BitmapFactory.decodeFile(selectedImage);
                        Bitmap bmp = getThumbnailBitmap(selectedImage,200);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
                        InputStream in = new ByteArrayInputStream(bos.toByteArray());
                        ContentBody foto = new InputStreamBody(in, "image/jpeg", selectedImage.substring(selectedImage.lastIndexOf('/') + 1, selectedImage.lastIndexOf('.')));

                        entity.addPart("propertyid", new StringBody("0"));
                        entity.addPart("property_imageupload", foto);
                    }
                    entity.addPart("userid", new StringBody(getSharedPreferences("LOGIN_DETAIL", 0).getString("ID", "")));
                    entity.addPart("address1", new StringBody(Address));
                    entity.addPart("address2", new StringBody(Address2));
                    entity.addPart("city", new StringBody(City));
                    entity.addPart("county", new StringBody(Country));
                    entity.addPart("postcode", new StringBody(PostCode));
                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(response.getEntity()
                                    .getContent()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }
                    in.close();

                    return sb.toString();

                } catch (Exception e) {
                    Log.e("property_insert problem", "" + e);
                    return "";
                }

            }

            @Override
            protected void onPostExecute(String result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                Log.e("property_insert", result.toString());
                try {
                    mProgressDialog.dismiss();
                    JSONObject object = new JSONObject(result.toString());
                    JSONArray msgArray = object.getJSONArray("Message");
                    JSONObject msgOb = msgArray.getJSONObject(0);
                    if (msgOb.getBoolean("result")) {
                        if (CALL.equalsIgnoreCase("EDIT")) {
                            Intent i = new Intent();
                            i.putExtra("msg", "SUCCESS");
                            setResult(13, i);
                            finish();
                        }else{
                            addPropertyType(msgOb.getString("id"), "checkin");

                        }
//                        Toast.makeText(activity, "Your property updated successfully",
//                                Toast.LENGTH_LONG).show();
//                        PropertyListingFragment fragment =new PropertyListingFragment();
//                        fragment.inventoryList();

                        // finish();
                    } else {
                        Toast.makeText(activity, "" + msgOb.getString("message"),
                                Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                        Toast.makeText(activity, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(activity, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(activity);
                mProgressDialog.setTitle("");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setMessage("Please Wait...");
                mProgressDialog.show();

            }
        }.execute();
    }


    public void addPropertyType(final String propertyId, final String propertyType) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "insertpropertytype";

//        final ProgressDialog mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setTitle("");
//        mProgressDialog.setCanceledOnTouchOutside(false);
//        mProgressDialog.setMessage("Please Wait...");
//        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("add Property type", response.toString());
                        try {
//                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());

                            JSONArray msgArray = dataOb.getJSONArray("Message");
                            JSONObject msgOb = msgArray.getJSONObject(0);
                            if (msgOb.getBoolean("result")) {

                                Toast.makeText(AddPropertyActivity.this, "Your property added successfully",
                                        Toast.LENGTH_LONG).show();
                                Intent i = new Intent();
                                i.putExtra("msg", "SUCCESS");
                                setResult(1, i);
                                finish();
                            } else {
                                Toast.makeText(AddPropertyActivity.this, "" + msgOb.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(AddPropertyActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AddPropertyActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                mProgressDialog.dismiss();
                VolleyLog.e("add propertytype Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(AddPropertyActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddPropertyActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
// ?emailid="+edtEmail.getText().toString()+"&password="+edtPassword.getText().toString()
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

}
