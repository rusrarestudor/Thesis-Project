package com.example.phonesensorreader;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class HistoryFragment extends AppCompatActivity implements SensorEventListener, View.OnClickListener  {

    Button goToLast7days;
    Button goToLastMonth;
    Button goToLastYear;
    Button goToMain;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);



        goToLast7days = findViewById(R.id.goToLast7Days);
        goToLast7days.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(HistoryFragment.this,Last7days.class);
                        startActivity(i);
                    }
                }
        );

        goToLastMonth = findViewById(R.id.goToLastMonth);
        goToLastMonth.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(HistoryFragment.this,LastMonth.class);
                        startActivity(i);
                    }
                }
        );

        goToLastYear = findViewById(R.id.goToLastYear);
        goToLastYear.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(HistoryFragment.this,LastYear.class);
                        startActivity(i);
                    }
                }
        );

        goToMain = findViewById(R.id.goToMain);
        goToMain.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(HistoryFragment.this,MainActivity.class);
                        startActivity(i);
                    }
                }
        );

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {

    }
}
