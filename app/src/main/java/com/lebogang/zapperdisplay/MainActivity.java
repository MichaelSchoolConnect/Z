package com.lebogang.zapperdisplay;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.lebogang.zapperdisplay.service.PeriodicalFetches;
import com.lebogang.zapperdisplay.fragment.DetailFragment;
import com.lebogang.zapperdisplay.fragment.MasterFragment;

//This activity is responsible for handling device orientation & displaying fragment
public class MainActivity extends AppCompatActivity {

    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_main);

            Configuration config = getResources().getConfiguration();

            //Helps to add, remove, or replace a fragment.
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            /*
             *Check the device orientation and programmatically add the fragment to an existing ViewGroup
             */
            if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                /** * Landscape mode of the device */
                fragmentTransaction.replace(R.id.flContainer, MasterFragment.newInstance());
                fragmentTransaction.commitAllowingStateLoss();

                fragmentTransaction.add(R.id.details_frame, DetailFragment.newInstance());
                fragmentTransaction.commitNowAllowingStateLoss();
            }else{
                /** * Portrait mode of the device */
                fragmentTransaction.replace(R.id.flContainer, DetailFragment.newInstance());
                fragmentTransaction.commitAllowingStateLoss();
            }

            //do periodical fetches from the API end point to pull the data
            scheduleJob(this);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    // schedule the start of the service every 10 - 30 seconds
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, PeriodicalFetches.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1000); // wait at least
        builder.setOverrideDeadline(3 * 1000); // maximum delay
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());

    }
}
