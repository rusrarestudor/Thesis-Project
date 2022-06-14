package com.example.phonesensorreader;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Last7days extends AppCompatActivity {
    ArrayList<String> dates;

    Button goToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days_graph);

        goToMain = findViewById(R.id.goToMain1);
        goToMain.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Last7days.this,HistoryFragment.class);
                        startActivity(i);
                    }
                }
        );

        BarChart barChart = findViewById(R.id.bargraph1);

        ArrayList <BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0f, 44f));
        barEntries.add(new BarEntry(1f, 88f));
        barEntries.add(new BarEntry(2f, 41f));
        barEntries.add(new BarEntry(3f, 85f));
        barEntries.add(new BarEntry(4f, 96f));
        barEntries.add(new BarEntry(5f, 25f));
        barEntries.add(new BarEntry(6f, 10f));

        BarDataSet barDataSet = new BarDataSet(barEntries, "dates");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("bar");
    }
}
