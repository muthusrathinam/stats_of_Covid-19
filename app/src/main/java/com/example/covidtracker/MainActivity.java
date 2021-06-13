package com.example.covidtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CountryCodePicker countryCodePicker;
    TextView mTodayTotal, mTotal, mTodayRecovered, mRecovered,mTodayDeaths, mDeaths, mActive, mTodayActive;

    String country;
    TextView mFilter;
    Spinner spinner;
    String[] types = {"cases", "deaths", "recovered", "active"};
    private List<ModelClass> modelClassList1;
    private List<ModelClass> modelClassList2;

    PieChart mPieChart;
    private RecyclerView recyclerView;
    com.example.covidtracker.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        countryCodePicker = findViewById(R.id.ccp);
        mTodayActive = findViewById(R.id.todayActive);
        mActive = findViewById(R.id.activeCase);
        mTodayActive = findViewById(R.id.todayActive);
        mTotal = findViewById(R.id.totalCase);
        mDeaths = findViewById(R.id.totalDeath);
        mTodayDeaths = findViewById(R.id.todayDeath);
        mRecovered = findViewById(R.id.recoveredCase);
        mTodayRecovered = findViewById(R.id.todayRecovered);
        mPieChart = findViewById(R.id.piechart);
        spinner = findViewById(R.id.spinner);
        mFilter = findViewById(R.id.filter);
        recyclerView = findViewById(R.id.recyclerView);
        modelClassList1 = new ArrayList<>();
        modelClassList2 = new ArrayList<>();

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        ApiUtilities.getApiInterface().getcoutrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList2.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

        adapter = new Adapter(getApplicationContext(), modelClassList2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        countryCodePicker.setAutoDetectedCountry(true);
        country = countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country = countryCodePicker.getSelectedCountryName();
                fetchdata();
            }
        });
        fetchdata();




    }

    private void fetchdata() {
        ApiUtilities.getApiInterface().getcoutrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList1.addAll(response.body());
                for(int i=0; i<modelClassList1.size(); i++){
                    if(modelClassList1.get(i).getCountry().equals(country)){
                        mActive.setText(modelClassList1.get(i).getActive());
                        mTodayDeaths.setText(modelClassList1.get(i).getDeaths());
                        mTodayRecovered.setText(modelClassList1.get(i).getTodayRecovered());
                        //mTodayTotal.setText(modelClassList1.get(i).getTodayCases());
                        mTotal.setText(modelClassList1.get(i).getCases());
                        mDeaths.setText(modelClassList1.get(i).getDeaths());
                        mRecovered.setText(modelClassList1.get(i).getRecovered());


                        int active, total, recovered, deaths;

                        active = Integer.parseInt(modelClassList1.get(i).getActive());
                        total = Integer.parseInt(modelClassList1.get(i).getCases());
                        recovered = Integer.parseInt(modelClassList1.get(i).getRecovered());
                        deaths = Integer.parseInt(modelClassList1.get(i).getDeaths());

                        updateGraph(active, total, recovered, deaths);


                    }
                }
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });
    }

    private void updateGraph(int active, int total, int recovered, int deaths) {

        mPieChart.clearChart();
        mPieChart.addPieSlice(new PieModel("Confirm", total, Color.parseColor("#F4B400")));
        mPieChart.addPieSlice(new PieModel("Active", active, Color.parseColor("#0F9D58")));
        mPieChart.addPieSlice(new PieModel("Recovered", recovered, Color.parseColor("#4285F4")));
        mPieChart.addPieSlice(new PieModel("Deaths", deaths, Color.parseColor("#DB4437")));
        mPieChart.startAnimation();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String item = types[i]; //i = position (here)
        mFilter.setText(item);
        adapter.filter(item);


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}