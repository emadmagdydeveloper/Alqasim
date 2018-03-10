package com.Alatheer.m.schooles.Activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.Alatheer.m.schooles.Adapters.All_News_Adapter;
import com.Alatheer.m.schooles.Models.News_Model;
import com.Alatheer.m.schooles.Models.School_Fees_Model;
import com.Alatheer.m.schooles.R;
import com.Alatheer.m.schooles.Services.ServicesApi;
import com.Alatheer.m.schooles.Services.Service;

import java.util.ArrayList;
import java.util.List;

import me.anwarshahriar.calligrapher.Calligrapher;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    All_News_Adapter allNewsAdapter;
    ArrayList<News_Model> newsList ;
    private ProgressBar progBar;
    private LinearLayout nodata_container;
    private SwipeRefreshLayout sr;
    String school_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        getDataFromIntent();
        initView();
        getDataFromServer();

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
        recyclerView = findViewById(R.id.recyc);
        newsList = new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(NewsActivity.this, 1));
        recyclerView.setHasFixedSize(true);
        allNewsAdapter = new All_News_Adapter(NewsActivity.this,newsList);
        recyclerView.setAdapter(allNewsAdapter);

    }

    private void getDataFromServer() {
        progBar.setVisibility(View.VISIBLE);

        Service service = ServicesApi.CreateApiClient().create(Service.class);
        Call<List<News_Model>> call = service.getNewsData("4");

        call.enqueue(new Callback<List<News_Model>>() {
            @Override
            public void onResponse(Call<List<News_Model>> call, Response<List<News_Model>> response) {

                newsList.clear();
                newsList.addAll(response.body());
                allNewsAdapter.notifyDataSetChanged();
                if (newsList.size()>0)
                {
                    progBar.setVisibility(View.GONE);
                    sr.setRefreshing(false);

                }else
                {
                    progBar.setVisibility(View.GONE);
                    nodata_container.setVisibility(View.VISIBLE);
                    sr.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<News_Model>> call, Throwable t) {
                nodata_container.setVisibility(View.GONE);
                sr.setRefreshing(false);


            }
        });
    }


    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent !=null)
        {
            if(intent.hasExtra("school_id"))
            {
                school_id = intent.getStringExtra("school_id");
            }
        }
    }




}
