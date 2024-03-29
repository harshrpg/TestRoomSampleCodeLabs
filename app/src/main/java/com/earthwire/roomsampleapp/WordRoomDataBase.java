package com.earthwire.roomsampleapp;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {
                Word.class
        },
        version = 1,
        exportSchema = false
)
public abstract class WordRoomDataBase extends RoomDatabase {
    public abstract WordDao wordDao();
    private static WordRoomDataBase INSTANCE;

    public static WordRoomDataBase getDatabase(Context context){
        if (INSTANCE == null){
            synchronized (WordRoomDataBase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(
                                        context.getApplicationContext(),
                                        WordRoomDataBase.class,
                                        "word_database"
                                )
                                .fallbackToDestructiveMigration()
                                .addCallback(sRoomDatabaseCallback)
                                .build();

                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    /**
     * Populate the database in the background.
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final WordDao mDao;
        String[] words = {"dolphin", "crocodile", "cobra"};

        PopulateDbAsync(WordRoomDataBase db) {
            mDao = db.wordDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // Start the app with a clean database every time.
            // Not needed if you only populate the database
            // when it is first created
            mDao.deleteAll();

            for (int i = 0; i <= words.length - 1; i++) {
                Word word = new Word(words[i]);
                mDao.insert(word);
            }
            return null;
        }
    }
}
