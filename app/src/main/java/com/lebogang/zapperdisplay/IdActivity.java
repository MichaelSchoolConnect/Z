package com.lebogang.zapperdisplay;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lebogang.zapperdisplay.adapter.EntriesAdapter;
import com.lebogang.zapperdisplay.database.AppDatabase;
import com.lebogang.zapperdisplay.database.DataEntry;
import com.lebogang.zapperdisplay.executors.AppExecutors;
import com.lebogang.zapperdisplay.viewmodel.MainViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class IdActivity extends AppCompatActivity implements EntriesAdapter.ItemClickListener {

    // Constant for logging
    private static final String TAG = IdActivity.class.getSimpleName();

    //URL Constant
    private String URL = "http://demo9790103.mockable.io/persons";

    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private EntriesAdapter mAdapter;

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.l_detail_recyclerview);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //get an instance of the AppDatabase object and initialize a context within its parameter.
        mDb = AppDatabase.getInstance(this);

        // Initialize the adapter...
        mAdapter = new EntriesAdapter(this, this);


        //request a response from the URL object via Volley
        sendAndRequestResponse();

        //get the viewmodel and observe any changes we've done in our database and update the ui.
        setupMainViewModel();
    }

    private void setupMainViewModel(){
        MainViewModel mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getEntries().observe(this, new Observer<List<DataEntry>>() {
            @Override
            public void onChanged(@Nullable List<DataEntry> taskEntries) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setTasks(taskEntries);
                //...and attach it to the RecyclerView
                mRecyclerView.setAdapter(mAdapter);
            }
        });
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

                                //get value of 'id'
                                int id = heroObject.getInt("id");
                                Log.i(TAG, String.valueOf(id));

                                //Create DataEntry object and pass 'name' and 'id' parameters
                                final DataEntry dataEntry = new DataEntry(id, name);

                                //Spawn a thread to write or update.
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        /*
                                         *checking if there is data already stored in the
                                         * database first, and if there is display it while
                                         * fetching the new data then store it locally
                                         */
                                        if(!mAdapter.getTasks().isEmpty()){
                                            Log.i(TAG, "data exists, just update it..");
                                            //mDb.taskDao().updateEntry(dataEntry);
                                        }else{
                                            /*
                                             *fetch the data from the end point provided by the API and
                                             * then store it locally
                                             */
                                            Log.i(TAG, "storing the data locally...");
                                            mDb.taskDao().insertEntry(dataEntry);
                                        }
                                    }
                                });


                            }
                        } catch (Exception je) {
                            je.printStackTrace();
                        }
                    }
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

    @Override
    public void onItemClickListener(int itemId) {

    }
}
