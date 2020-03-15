package com.lebogang.zapperdisplay.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lebogang.zapperdisplay.database.AppDatabase;
import com.lebogang.zapperdisplay.database.DataEntry;
import com.lebogang.zapperdisplay.executors.AppExecutors;

import org.json.JSONArray;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PeriodicalFetches extends JobService {

    private static String TAG = PeriodicalFetches.class.getSimpleName();

    //URL Constant
    private String URL = "http://demo9790103.mockable.io/persons";

    private AppDatabase mDb;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i(TAG, "Job started....");

        //get an instance of the AppDatabase object and initialize a context within its parameter.
        mDb = AppDatabase.getInstance(getApplicationContext());

        //Offload work to another thread of execution.
        sendAndRequestResponse();

        //notify user that there's new data if only the app is running on the background.
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG, "Job stopped....");
        return false;
    }

    private void sendAndRequestResponse() {
        //creating a string request to send request to the url
        Log.i(TAG, "fetching data from the API End Point...");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        //progressBar.setVisibility(View.INVISIBLE);

                        try {
                            //initialize JSONObject from string
                            JSONObject obj = new JSONObject(response);

                            //get the name array name from the JSONObject
                            JSONArray heroArray = obj.getJSONArray("persons");

                            //loop through all the elements of the json array
                            for (int i = 0; i < heroArray.length(); i++) {

                                //get the JSONObject of the particular index inside the array
                                JSONObject heroObject = heroArray.getJSONObject(i);

                                //get value of 'name'
                                String name = heroObject.getString("name");
                                Log.i(TAG, name);
                                //get value of 'id'
                                int id = heroObject.getInt("id");

                                //Create DataEntry object and pass 'name' and 'id' parameters
                                final DataEntry dataEntry = new DataEntry(id, name);

                                //Spawn a thread to write or update.
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                            /*
                                             *fetch the data from the end point provided by the API and
                                             * then store it locally
                                             */
                                            Log.i(TAG, "storing the data locally...");
                                            mDb.taskDao().insertEntry(dataEntry);
                                    }
                                });

                            }

                        } catch (Exception je) {
                            je.printStackTrace();
                        }
                    }
                    //show when querying is successful
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Log.i(TAG,error.getLocalizedMessage());
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    private void showNotification(){
        Intent i = new Intent(this, NotificationService.class);
        startService(i);
    }
}
