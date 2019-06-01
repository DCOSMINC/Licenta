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


public class DoctorProfileMainFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "DoctorProfileMainFragme";

    private static final int GALLERY_INTENT = 234;

    private CircleImageView profileImage;
    private TextView profileName, profileSpecialty;

    private Uri imageUri = null;

    private StorageTask uploadTask;

    public DoctorProfileMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        profileImage = view.findViewById(R.id.doctorProfilePicture);
        profileName = view.findViewById(R.id.doctorProfileName);
        profileSpecialty = view.findViewById(R.id.doctorProfileSpecialty);

        FirebaseFirestore.getInstance().collection(Constants.FIREBASE_DOCTORS).get().addOnSuccessListener(queryDocumentSnapshots ->  {
            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if(documentSnapshot.get(Constants.FIREBASE_ID).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    profileImage.setOnClickListener(this);
                    profileName.setText("Dr. " + documentSnapshot.get(Constants.FIREBASE_FIRST_NAME) + " " + documentSnapshot.get(Constants.FIREBASE_LAST_NAME));
                    profileSpecialty.setText(documentSnapshot.get(Constants.FIREBASE_SPECIALTY).toString());
                    documentSnapshot.getReference().addSnapshotListener((documentSnapshot1, e) -> {
                        if(e != null) {
                            Log.w(TAG, "onCreateView: ", e);
                            return;
                        }
                        if(!documentSnapshot1.get(Constants.FIREBASE_PHOTO_PATH).equals(Constants.FIREBASE_DEFAULT)) {
                            Uri uri = Uri.parse(documentSnapshot1.get(Constants.FIREBASE_PHOTO_PATH).toString());
                            Glide.with(getContext()).load(uri).into(profileImage);
                        } else {
                            profileImage.setImageResource(R.drawable.circle_profile_placeholder);
                        }
                    });
                }
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "onCreateView: ", e);
        });

        return view;
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

                    FirebaseFirestore.getInstance().collection(Constants.FIREBASE_DOCTORS).get().addOnSuccessListener(queryDocumentSnapshots -> {
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

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.doctorProfilePicture:
                openImage();
                break;
            default:
                break;
        }
    }

}
