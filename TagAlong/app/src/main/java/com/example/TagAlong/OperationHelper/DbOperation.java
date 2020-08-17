package com.example.TagAlong.OperationHelper;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;



public class DbOperation {

    private static DbOperation dbInstance = null;
    public FirebaseFirestore db;

    public static DbOperation getInstance()  {
        if (dbInstance == null)
            dbInstance = new DbOperation();

        return dbInstance;
    }

    public DbOperation() {
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);

    }
}
