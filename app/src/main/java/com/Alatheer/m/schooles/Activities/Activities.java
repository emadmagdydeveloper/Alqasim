package com.Alatheer.m.schooles.Activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.Alatheer.m.schooles.Adapters.All_Activities_Adapter;
import com.Alatheer.m.schooles.Models.AllActivities_Model;
import com.Alatheer.m.schooles.Models.News_Model;
import com.Alatheer.m.schooles.R;
import com.Alatheer.m.schooles.Services.Service;
import com.Alatheer.m.schooles.Services.ServicesApi;
import java.util.ArrayList;
import java.util.List;

import me.anwarshahriar.calligrapher.Calligrapher;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activities extends AppCompatActivity {
    ArrayList<AllActivities_Model> model;
    All_Activities_Adapter adapter;
    RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private String class_room_id;
    private ProgressBar progBar;
    private LinearLayout nodata_container;
    private SwipeRefreshLayout sr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);



        getDataFromIntent();
        initView();
        getDataFromServer();
      //  Toast.makeText(this, ""+class_room_id, Toast.LENGTH_SHORT).show();
    }


    private void getDataFromServer() {


        Service service = ServicesApi.CreateApiClient().create(Service.class);
        Call<List<AllActivities_Model>> call = service.AllActivities(class_room_id);
        call.enqueue(new Callback<List<AllActivities_Model>>() {
            @Override
            public void onResponse(Call<List<AllActivities_Model>> call, Response<List<AllActivities_Model> > response) {


                model.clear();
                model.addAll( response.body());
                if (model.get(0).getMessage().equals("no activities")){
                    progBar.setVisibility(View.GONE);
                    nodata_container.setVisibility(View.VISIBLE);
                    sr.setRefreshing(false);
                    Toast.makeText(Activities.this, "no activities", Toast.LENGTH_SHORT).show();
                }else {
                    adapter.notifyDataSetChanged();
                    progBar.setVisibility(View.GONE);
                    sr.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<List<AllActivities_Model>> call, Throwable t) {
                nodata_container.setVisibility(View.GONE);
                sr.setRefreshing(false);


            }
        });
    }

    private void initView() {

        sr = findViewById(R.id.sr);
        sr.setRefreshing(false);

        progBar = findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        nodata_container = findViewById(R.id.nodata_container);
        sr.setColorSchemeResources(R.color.colorPrimary);
        sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromServer();
            }
        });
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "JannaLT-Regular.ttf", true);
        recyclerView = findViewById(R.id.recView_activities);
        model = new ArrayList<>();
        mLayoutManager=new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new All_Activities_Adapter(this, model);
        recyclerView.setAdapter(adapter);
    }

    private void getDataFromIntent() {

        Intent intent = getIntent();
        if (intent!=null)
        {
            if (intent.hasExtra("class_room_id"))
            {
                class_room_id = intent.getStringExtra("class_room_id");

            }
        }

    }
}
