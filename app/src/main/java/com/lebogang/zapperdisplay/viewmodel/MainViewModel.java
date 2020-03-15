package com.lebogang.zapperdisplay.viewmodel;

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

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<DataEntry>> tasks;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.i(TAG, "Actively retrieving the tasks from the DataBase");
        tasks = database.taskDao().loadAllEntries();
    }

    //getter
    public LiveData<List<DataEntry>> getEntries() {
        return tasks;
    }
}
