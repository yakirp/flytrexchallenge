package com.yakirp.flytrexchallenge;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.yakirp.flytrexchallenge.Utils.bytesToHex;
import static com.yakirp.flytrexchallenge.Utils.toHex;

public class MainActivity extends AppCompatActivity {

    public static final String URL = "http://ec2-52-88-173-47.us-west-2.compute.amazonaws.com:8000/moviequotes/";
    private ListView listView;
    private TextView paylod_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.lvMovies);
        paylod_tv = (TextView) findViewById(R.id.payload);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchMoviesFromFlytrex();
            }
        });
    }



    private void fetchMoviesFromFlytrex() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(URL)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    final byte[] res = response.body().bytes();

                    final List<FlytrexMovie> movies = new FlytrexMoviesParser().parse(res);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            paylod_tv.setText(bytesToHex(res));
                            MoviesAdapter adapter = new MoviesAdapter(MainActivity.this, movies);
                            listView.setAdapter(adapter);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    
}
