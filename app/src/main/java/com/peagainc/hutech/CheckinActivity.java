package com.peagainc.hutech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.peagainc.hutech.adapter.MobileFaceNetUtil;
import com.peagainc.hutech.mobilefacenet.MobileFaceNet;
import com.peagainc.hutech.mtcnn.Align;
import com.peagainc.hutech.mtcnn.Box;
import com.peagainc.hutech.mtcnn.MTCNN;

import java.io.IOException;
import java.util.Vector;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class CheckinActivity extends AppCompatActivity {
    ImageView imvInputImage;
    ImageView imvTrainImage;
    ImageView imvInputCrop;
    TextView txtResult;
    CircularProgressButton cirClose;
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
        cirClose = findViewById(R.id.cirClose);
        imvInputImage.setImageBitmap(inputImage);
        imvTrainImage.setImageBitmap(trainImage);
        imvInputCrop.setImageBitmap(inputCrop);
        txtResult.setText("Accuracy "+String.valueOf(result*100)+" %");
        txtResult.setTextColor(getResources().getColor(android.R.color.holo_green_light));
    }

    private void addEvent() {
        cirClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CheckinActivity.this, HomeActivity.class));
                finish();
            }
        });
    }
}