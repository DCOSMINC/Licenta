package com.example.applicenta.general;

import android.util.Log;

import com.example.applicenta.Interface.FavoriteCallBack;
import com.example.applicenta.Interface.MyCallBack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FavoriteCheck {

    private static final String TAG = "FavoriteCheck";

    public static void checkIfFavorite(FavoriteCallBack favoriteCallBack, String idDoctor) {
        FirebaseFirestore.getInstance().collection(Constants.FIREBASE_USERS).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(documentSnapshot.get(Constants.FIREBASE_ID))) {
                    for(String string : (List<String>)documentSnapshot.get(Constants.FIREBASE_FAVORITES)) {
                        if(string.equals(idDoctor)) {
                            Log.d(TAG, "checkIfFavorite: a intrat la favorite " + idDoctor);
                            favoriteCallBack.onFavoriteCallBack(true);
                            return;
                        }
                    }
                    Log.d(TAG, "checkIfFavorite: a intrat la fals " + idDoctor);
                    favoriteCallBack.onFavoriteCallBack(false);
                }
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "checkIfFavorite: ", e);
        });
    }

}
