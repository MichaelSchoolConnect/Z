/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.lebogang.zapperdisplay.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lebogang.zapperdisplay.R;
import com.lebogang.zapperdisplay.database.DataEntry;

import java.util.List;

/**
 * This EntriesAdapter creates and binds ViewHolders, that hold the description and priority of an entry,
 * to a RecyclerView to efficiently display data.
 */
public class EntriesAdapter extends RecyclerView.Adapter<EntriesAdapter.TaskViewHolder> {

    private static String TAG = EntriesAdapter.class.getSimpleName();

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds task data and the Context
    private List<DataEntry> mTaskEntries;
    private Context mContext;

    /**
     * Constructor for the EntriesAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public EntriesAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Log.i(TAG, "inflating content_item to a view.");
        View view = null;
        try{
            // Inflate the content_item to a view
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.content_item, parent, false);

        }catch (Exception e){
            e.printStackTrace();
        }

        return new TaskViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(final TaskViewHolder holder, int position) {
        //Log.i(TAG, "binding data to views.");
        // Determine the values of the wanted data
        final DataEntry dataEntry = mTaskEntries.get(position);
        final String name = dataEntry.getName();

        //Set values
        holder.name.setText(name);
    }


    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mTaskEntries == null) {
            return 0;
        }
        return mTaskEntries.size();
    }


    public List<DataEntry> getTasks() {
        return mTaskEntries;
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setTasks(List<DataEntry> taskEntries) {
        Log.i(TAG, "data changed, notifying adapter");
        mTaskEntries = taskEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        TaskViewHolder(View itemView) {
            super(itemView);
            try{
                name = itemView.findViewById(R.id.tv_name);
                itemView.setOnClickListener(this);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void onClick(View view) {
            int elementId = mTaskEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}