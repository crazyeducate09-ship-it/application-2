package com.example.tripleapp;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.OutputStream;

public class DrawingActivity extends AppCompatActivity {

    private DrawingView drawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        drawingView = findViewById(R.id.drawingView);

        findViewById(R.id.colorBlack).setOnClickListener(v -> drawingView.setColor(Color.BLACK));
        findViewById(R.id.colorRed).setOnClickListener(v -> drawingView.setColor(Color.parseColor("#F44336")));
        findViewById(R.id.colorOrange).setOnClickListener(v -> drawingView.setColor(Color.parseColor("#FF9800")));
        findViewById(R.id.colorYellow).setOnClickListener(v -> drawingView.setColor(Color.parseColor("#FFEB3B")));
        findViewById(R.id.colorGreen).setOnClickListener(v -> drawingView.setColor(Color.parseColor("#4CAF50")));
        findViewById(R.id.colorBlue).setOnClickListener(v -> drawingView.setColor(Color.parseColor("#2196F3")));
        findViewById(R.id.colorPurple).setOnClickListener(v -> drawingView.setColor(Color.parseColor("#9C27B0")));
        findViewById(R.id.colorPink).setOnClickListener(v -> drawingView.setColor(Color.parseColor("#E91E63")));

        findViewById(R.id.sizeSmall).setOnClickListener(v -> drawingView.setSize(4f));
        findViewById(R.id.sizeMedium).setOnClickListener(v -> drawingView.setSize(10f));
        findViewById(R.id.sizeLarge).setOnClickListener(v -> drawingView.setSize(20f));
        findViewById(R.id.btnEraser).setOnClickListener(v -> drawingView.setEraser());

        findViewById(R.id.btnClear).setOnClickListener(v -> drawingView.clear());
        findViewById(R.id.btnSave).setOnClickListener(v -> saveDrawing());
        findViewById(R.id.btnBackDraw).setOnClickListener(v -> finish());
    }

    private void saveDrawing() {
        Bitmap bm = drawingView.getBitmap();
        if (bm == null) return;
        try {
            String name = "TripleApp_" + System.currentTimeMillis() + ".png";
            ContentValues cv = new ContentValues();
            cv.put(MediaStore.Images.Media.DISPLAY_NAME, name);
            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                cv.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TripleApp");
            }
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
            if (uri != null) {
                try (OutputStream os = getContentResolver().openOutputStream(uri)) {
                    bm.compress(Bitmap.CompressFormat.PNG, 100, os);
                }
                Toast.makeText(this, "Saved to Gallery ✓", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}