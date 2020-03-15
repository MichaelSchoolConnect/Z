package com.lebogang.zapperdisplay.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DataEntry.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "content";
    private static AppDatabase sInstance;

    //*Uncomment this method if db is being modified*//
   /* private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE task ADD COLUMN status INTEGER");
        }
    };*/

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.i(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        //this static method goes aling with the above commented out method .addMigrations(MIGRATION_1_2)
                        .build();
            }
        }
        Log.i(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract EntryDao taskDao();
}
