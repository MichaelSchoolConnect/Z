package com.lebogang.zapperdisplay.fragment;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lebogang.zapperdisplay.database.AppDatabase;
import com.lebogang.zapperdisplay.database.DataEntry;

import java.util.List;

/*
 *We are going to use this ViewModel to cache our list of DataEntry objects
 * wrapped in a LiveData object.
 */
public class DetailViewModel extends AndroidViewModel {
    // Constant for logging
    private static final String TAG = DetailViewModel.class.getSimpleName();

    public LiveData<List<DataEntry>> tasks;

    public DetailViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.i(TAG, "Actively retrieving the tasks from the DataBase for DetailView");
        tasks = database.taskDao().loadAllEntries();
    }

    public LiveData<List<DataEntry>> getDetailEntries() {
        return tasks;
    }
}

