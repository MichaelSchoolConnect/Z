package com.lebogang.zapperdisplay.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lebogang.zapperdisplay.IdActivity;
import com.lebogang.zapperdisplay.R;
import com.lebogang.zapperdisplay.adapter.EntriesAdapter;
import com.lebogang.zapperdisplay.database.AppDatabase;
import com.lebogang.zapperdisplay.database.DataEntry;
import com.lebogang.zapperdisplay.executors.AppExecutors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DetailFragment extends Fragment implements EntriesAdapter.ItemClickListener{

    // Constant for logging
    private static final String TAG = DetailFragment.class.getSimpleName();

    //Fragment context
    private Context context = getContext();

    //URL Constant
    private String URL = "http://demo9790103.mockable.io/persons";

    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private EntriesAdapter mAdapter;

    private AppDatabase mAppDatabase;

    private OnDetailFragmentInteractionListener mListener;

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
            //Inflate the layout
        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);

            // Set the RecyclerView to its corresponding view
            mRecyclerView = rootView.findViewById(R.id.detail_recyclerView);

            // Set the layout for the RecyclerView to be a linear layout, which measures and
            // positions items within a RecyclerView into a linear list
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

            //get an instance of the AppDatabase object and initialize a context within its parameter.
            mAppDatabase = AppDatabase.getInstance(getContext());

            // Initialize the adapter...
            mAdapter = new EntriesAdapter(context, this);

            if(getActivity() != null){
                // Initialize the adapter...
                mAdapter = new EntriesAdapter(getActivity(), this);
            }
        //request a response from the URL object via Volley
        sendAndRequestResponse();
            //get the viewmodel and observe any changes we've done in our database and update the ui.
        setupMainViewModel();

    }

    private void setupMainViewModel(){
        DetailViewModel mViewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        mViewModel.getDetailEntries().observe(getViewLifecycleOwner(), new Observer<List<DataEntry>>() {
            @Override
            public void onChanged(@Nullable List<DataEntry> taskEntries) {
                Log.i(TAG, "Updating list of tasks from LiveData in ViewModel");
                //...and attach it to the RecyclerView
                mAdapter.setTasks(taskEntries);
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
                        parseJSON(response);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error if it occurs
                        error.getMessage();
                        Log.i(TAG,error.getLocalizedMessage());
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    private void parseJSON(String response){
        JSONObject obj;
        JSONArray heroArray;

        try {
            //initialize JSONObject from string
            obj = new JSONObject(response);
            //get the name array name from the JSONObject
            heroArray = obj.getJSONArray("persons");

            String name;
            int id;

            //loop through all the elements of the json array
            for (int i = 0; i < heroArray.length(); i++) {

                //get the JSONObject of the particular index inside the array
                JSONObject heroObject = heroArray.getJSONObject(i);

                //get value of 'name'
                name = heroObject.getString("name");

                //get value of 'id'
                id = heroObject.getInt("id");

                DataEntry dataEntry = new DataEntry(id, name);

                //Log data received.
                Log.i(TAG, id + ": " + name);
                insertOrUpdateDB(mAdapter, dataEntry);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void insertOrUpdateDB(final EntriesAdapter adapter, final DataEntry dataEntry1){
        //Spawn a thread to write or update.
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                /*
                 *checking if there is data already stored in the
                 * database first, and if there is display it while
                 * fetching the new data then store it locally
                 */
                if(dataEntry1.getName() != null){
                    Log.i(TAG, "Database is not empty.");
                }else{
                    /*
                     *fetch the data from the end point provided by the API and
                     * then store it locally
                     */
                    Log.i(TAG, "Inserting data into database...");
                    mAppDatabase.taskDao().insertEntry(dataEntry1);
                }
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        //Launch IdActivity
        startActivity(new Intent(getContext(), IdActivity.class));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDetailFragmentInteractionListener) {
            mListener = (OnDetailFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnDetailFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
