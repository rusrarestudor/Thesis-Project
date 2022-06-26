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

public class LastYear extends AppCompatActivity {

    BarChart barChart;

    ArrayList<String> dates;

    Random random;

    ArrayList<BarEntry> barEntries;

    Button goToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year_graph);



          barChart = (BarChart) findViewById(R.id.bargraph3);

        goToMain = findViewById(R.id.goToMain3);
        goToMain.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(LastYear.this, HistoryFragment.class);
                        startActivity(i);
                    }
                }
        );

        BarChart barChart = findViewById(R.id.bargraph2);

        ArrayList <BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0f, 151f));
        barEntries.add(new BarEntry(1f, 162f));
        barEntries.add(new BarEntry(2f, 164f));
        barEntries.add(new BarEntry(3f, 170f));
        barEntries.add(new BarEntry(4f, 85f));
        barEntries.add(new BarEntry(5f, 98f));
        barEntries.add(new BarEntry(6f, 140f));
        barEntries.add(new BarEntry(7f, 160f));
        barEntries.add(new BarEntry(8f, 190f));
        barEntries.add(new BarEntry(9f, 100f));

        BarDataSet barDataSet = new BarDataSet(barEntries, "dates");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("bar");

    }

    public void createMyBarGraph(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date aux = cal.getTime();

        String sevenDaysAgo = formatter.format(aux);

        String today = formatter.format(date);

        try {
            Date date1 = simpleDateFormat.parse(sevenDaysAgo);
            Date date2 = simpleDateFormat.parse(today);

            Calendar mDate1 = Calendar.getInstance();
            Calendar mDate2 = Calendar.getInstance();
            mDate1.clear();
            mDate2.clear();

            mDate1.setTime(date1);
            mDate2.setTime(date2);

            dates = new ArrayList<>();
            dates = getList(mDate1,mDate2);

            barEntries = new ArrayList<>();

            float max = 0f;
            float value = 0f;

            random = new Random();
            for(int j = 0; j< dates.size();j++){
                max = 100f;
                value = random.nextFloat()*max;
                barEntries.add(new BarEntry(value,j));
            }

        }catch(ParseException e){
            e.printStackTrace();
        }

        BarDataSet barDataSet = new BarDataSet(barEntries,"Dates");
        BarData barData = new BarData((IBarDataSet) dates,barDataSet);
        barChart.setData(barData);
        //barChart.setDescription();

    }

    public ArrayList<String> getList(Calendar startDate, Calendar endDate){
        ArrayList<String> list = new ArrayList<String>();
        while(startDate.compareTo(endDate)<=0){
            list.add(getDate(startDate));
            startDate.add(Calendar.DAY_OF_MONTH,1);
        }
        return list;
    }

    public String getDate(Calendar cld){
        String curDate = cld.get(Calendar.YEAR) + "/" + (cld.get(Calendar.MONTH) + 1) + "/"
                +cld.get(Calendar.DAY_OF_MONTH);
        try{
            Date date = new SimpleDateFormat("yyyy/MM/dd").parse(curDate);
            curDate =  new SimpleDateFormat("yyy/MM/dd").format(date);
        }catch(ParseException e){
            e.printStackTrace();
        }
        return curDate;
    }

}