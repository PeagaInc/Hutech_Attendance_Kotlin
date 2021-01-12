package com.peagainc.hutech;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import android.util.Base64;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class CheckinActivity extends AppCompatActivity {
    ImageView imvInputImage;
    ImageView imvTrainImage;
    ImageView imvInputCrop;
    TextView txtResult;
    CircularProgressButton cirCheckin;
    public static Bitmap inputImage;
    public static Bitmap trainImage;
    public static Bitmap inputCrop;
    public static Float result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        linkView();
        addEvent();

    }

    private void linkView() {
        imvInputImage = findViewById(R.id.imvInputImage);
        imvTrainImage = findViewById(R.id.imvTrainImage);
        imvInputCrop = findViewById(R.id.imvInputCrop);
        txtResult = findViewById(R.id.txtResult);
        cirCheckin = findViewById(R.id.cirCheckin);
        imvInputImage.setImageBitmap(inputImage);
        imvInputCrop.setImageBitmap(inputCrop);
        txtResult.setText("Accuracy "+String.valueOf(result*100)+" %");
        txtResult.setTextColor(getResources().getColor(android.R.color.holo_green_light));
    }
    public static class VolleySingleton {
        private static final String TAG = "VolleySingleton";
        private RequestQueue mRequestQueue;
        private static VolleySingleton sInstance;


        private VolleySingleton(Context context) {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
            }
        }

        public static synchronized VolleySingleton getInstance(Context context) {
            if (sInstance == null)
                sInstance = new VolleySingleton(context);
            return sInstance;
        }

        public RequestQueue getRequestQueue() {
            return mRequestQueue;
        }

    }
    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // Get the Base64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }
    public String jsonArrayToString(JSONArray jsonArray){
        String[] resultArr  = new String[jsonArray.length()];
        for(int i =0; i< resultArr.length;i++){
            resultArr[i] = jsonArray.optString(i);
        }
        String result = String.join(",", resultArr);
        return result;
    }
    public void sendImageToCompare(JSONObject jsonData){
        String URL ="http://api.peaga.xyz/face_comparison";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("result") == "true"){
                        Toast.makeText(getApplicationContext(),"Check in success!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(CheckinActivity.this, HomeActivity.class));
//                      finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Check in faile!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(CheckinActivity.this, DetectFaceActivity.class));
//                      finish(
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("media_type", "application/json");
                return params;
            }
        };
        VolleySingleton.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }
    private void addEvent() {
        cirCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(CheckinActivity.this, HomeActivity.class));
//                finish();
                String imageEncode = getEncoded64ImageStringFromBitmap(inputCrop);
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("image", imageEncode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Test",jsonData.toString());
                sendImageToCompare(jsonData);
            }
        });
    }
}