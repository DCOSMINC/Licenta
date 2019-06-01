package com.example.applicenta.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.applicenta.R;
import com.example.applicenta.general.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PacientProfileFragment extends Fragment {

    private static final String TAG = "PacientProfileFragment";


    private TextView pacientNameTV, pacientEmailTV, pacientTelephoneNumberTV;

    private CircleImageView profileImage;

    private static final int GALLERY_INTENT = 234;

    private Uri imageUri = null;

    private StorageTask uploadTask;

    public PacientProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PacientProfileFragment newInstance() {
        PacientProfileFragment fragment = new PacientProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pacient_profile, container, false);

        pacientNameTV = view.findViewById(R.id.pacientProfileName);
        pacientEmailTV = view.findViewById(R.id.pacientProfileEmail);
        pacientTelephoneNumberTV = view.findViewById(R.id.pacientProfileTelephoneNumber);
        profileImage = view.findViewById(R.id.pacientProfilePicture);

        profileImage.setOnClickListener(v -> {
            openImage();
        });

        FirebaseFirestore.getInstance().collection(Constants.FIREBASE_USERS).get().addOnSuccessListener(queryDocumentSnapshots -> {
           for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
               if(documentSnapshot.get(Constants.FIREBASE_ID).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                   pacientTelephoneNumberTV.setText(documentSnapshot.get(Constants.FIREBASE_TELEPHONE_NUMBER).toString());
                   pacientEmailTV.setText(documentSnapshot.get(Constants.FIREBASE_EMAIL).toString());
                   String name = documentSnapshot.get(Constants.FIREBASE_FIRST_NAME).toString() + " " + documentSnapshot.get(Constants.FIREBASE_LAST_NAME).toString();
                   pacientNameTV.setText(name);

                   documentSnapshot.getReference().addSnapshotListener((documentSnapshot1, e) -> {
                        if(e != null) {
                            Log.w(TAG, "onEvent: ", e);
                            return;
                        }

                        if(!documentSnapshot1.get(Constants.FIREBASE_PHOTO_PATH).equals(Constants.FIREBASE_DEFAULT)) {
                            Uri uri = Uri.parse(documentSnapshot1.get(Constants.FIREBASE_PHOTO_PATH).toString());
                            Log.d(TAG, "onCreateView: TEST" + uri);
                            Glide.with(getContext()).load(uri).into(profileImage);
                        } else {
                            profileImage.setImageResource(R.drawable.circle_profile_placeholder);
                        }
                   });
               }
           }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == Activity.RESULT_OK
            && data != null && data.getData() != null) {
            imageUri = data.getData();

            if(uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_LONG).show();
            } else {
                uploadImage();
            }


        }
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = FirebaseStorage.getInstance().getReference().child(Constants.FIREBASE_PHOTOS).child(FirebaseAuth.getInstance().getUid());

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()) {
                    throw task.getException();
                }

                return fileReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Uri downloadUri = (Uri)task.getResult();
                    String mUri = downloadUri.toString();

                    FirebaseFirestore.getInstance().collection(Constants.FIREBASE_USERS).get().addOnSuccessListener(queryDocumentSnapshots -> {
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.get(Constants.FIREBASE_ID).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                Map<String, String> photoPath = new HashMap<>();
                                photoPath.put(Constants.FIREBASE_PHOTO_PATH, mUri);
                                documentSnapshot.getReference().set(photoPath, SetOptions.merge());
                            }
                        }

                    });
                    pd.dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "uploadImage: ", e);
                pd.dismiss();
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_INTENT );
    }

}
