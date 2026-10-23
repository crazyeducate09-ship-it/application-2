package com.example.tripleapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View cardMusic = findViewById(R.id.cardMusic);
        View cardDrawing = findViewById(R.id.cardDrawing);
        View cardCalc = findViewById(R.id.cardCalculator);

        cardMusic.setOnClickListener(v ->
                startActivity(new Intent(this, MusicPlayerActivity.class)));
        cardDrawing.setOnClickListener(v ->
                startActivity(new Intent(this, DrawingActivity.class)));
        cardCalc.setOnClickListener(v ->
                startActivity(new Intent(this, CalculatorActivity.class)));
    }
}