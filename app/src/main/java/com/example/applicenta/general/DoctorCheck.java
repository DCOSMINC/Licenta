package com.example.applicenta.general;


import android.util.Log;

import com.example.applicenta.Interface.MyCallBack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoctorCheck {

    private static final String TAG = "DoctorCheck";

    public static void checkIfDoctor(MyCallBack myCallBack) {
        FirebaseFirestore.getInstance().collection(Constants.FIREBASE_DOCTORS).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if(FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(documentSnapshot.get(Constants.FIREBASE_EMAIL))) {
                    myCallBack.onCallBack(true);
                }
            }
            myCallBack.onCallBack(false);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "checkIfDoctor: ", e);
        });
    }
}
