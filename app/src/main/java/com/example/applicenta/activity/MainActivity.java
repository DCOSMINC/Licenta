package com.example.applicenta.activity;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.applicenta.Adapter.SectionsPageAdapter;
import com.example.applicenta.Fragment.AppointmentDetailFragment;
import com.example.applicenta.Fragment.AppointmentListFragment;
import com.example.applicenta.Fragment.DoctorListFragment;
import com.example.applicenta.Fragment.DoctorProfileFragment;
import com.example.applicenta.Fragment.DoctorProfileMainFragment;
import com.example.applicenta.Fragment.FavoritesListFragment;
import com.example.applicenta.Fragment.MapFragment;
import com.example.applicenta.Fragment.PacientProfileFragment;
import com.example.applicenta.Interface.DoctorListFragmentInteractionListener;
import com.example.applicenta.Interface.OnAppointmentListInteractionListener;
import com.example.applicenta.R;
import com.example.applicenta.general.Appointment;
import com.example.applicenta.general.Constants;
import com.example.applicenta.general.Doctor;
import com.example.applicenta.general.DoctorCheck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.GsonBuilder;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DoctorListFragmentInteractionListener
        , MapFragment.OnFragmentInteractionListener
        , OnAppointmentListInteractionListener
        , SearchView.OnQueryTextListener {

    private static final String TAG = "MainActivity";

    private ViewPager mViewPager;

    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(3);


        DoctorCheck.checkIfDoctor(value -> {
            if(!value) {
                setupPageViewPatient(mViewPager);
            } else {
                setupPageViewDoctor(mViewPager);
            }
        });

        final TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.post(() -> tabLayout.setupWithViewPager(mViewPager));

//        getWindow().setExitTransition(new Explode());
//        getWindow().setEnterTransition(new Explode());

        if(savedInstanceState != null ){
            mViewPager.setCurrentItem(savedInstanceState.getInt(Constants.VIEWPAGER_ITEM_DETAILS));
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupPageViewDoctor(ViewPager viewPager) {
        SectionsPageAdapter pageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(new DoctorListFragment(), "TAB1");
        pageAdapter.addFragment(new FavoritesListFragment(), "TAB2");
        pageAdapter.addFragment(new AppointmentListFragment(), "TAB3");
        pageAdapter.addFragment(new DoctorProfileMainFragment(), "TAB4");
        viewPager.setAdapter(pageAdapter);
    }

    private void setupPageViewPatient(ViewPager viewPager) {
        SectionsPageAdapter pageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(new DoctorListFragment(), "Acasa");
        pageAdapter.addFragment(new FavoritesListFragment(), "Favoriti");
        pageAdapter.addFragment(new AppointmentListFragment(), "Programari");
        pageAdapter.addFragment(new PacientProfileFragment(), "Profil");
        viewPager.setAdapter(pageAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem docRegister = menu.findItem(R.id.add_doctor_menu);
        docRegister.setVisible(checkAdmin());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case (R.id.sign_out_menu):
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            case(R.id.add_doctor_menu):
                startActivity(new Intent(MainActivity.this, AdminDoctorActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //search view listerens


    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "onQueryTextSubmit: keke " + query);
        searchView.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextChange: " + newText);
        if(newText != null) {
            DoctorListFragment fragment = (DoctorListFragment)getSupportFragmentManager().getFragments().get(0);
            Log.d(TAG, "onQueryTextSubmit: " + fragment);
            fragment.search(newText);
        }
        return false;
    }

    // fragment interaction
    // doctors && favorites

    @Override
    public void onListFragmentInteraction(Doctor item) {

        FirebaseFirestore.getInstance().collection(Constants.FIREBASE_DOCTORS).get().addOnSuccessListener(queryDocumentSnapshots -> {

            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if(Objects.equals(documentSnapshot.get(Constants.FIREBASE_EMAIL), item.getEmail())) {

//                    Intent intent = new Intent(MainActivity.this, DoctorProfileActivity.class);
//                    intent.putExtra(Constants.FIREBASE_DOCTOR_ID, documentSnapshot.getId());
//                    startActivity(intent);
                    Log.d(TAG, "onAppointmentListFragmentInteraction: chestie");
                    Fragment fragment = new DoctorProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.FIREBASE_DOCTOR_ID, documentSnapshot.getId());
                    bundle.putString(Constants.FIREBASE_SPECIALTY, documentSnapshot.get(Constants.FIREBASE_SPECIALTY).toString());
                    bundle.putString(Constants.FIREBASE_FIRST_NAME, documentSnapshot.get(Constants.FIREBASE_FIRST_NAME).toString());
                    bundle.putString(Constants.FIREBASE_LAST_NAME, documentSnapshot.get(Constants.FIREBASE_LAST_NAME).toString());
                    bundle.putString(Constants.FIREBASE_PHOTO_PATH, documentSnapshot.get(Constants.FIREBASE_PHOTO_PATH).toString());

                    fragment.setArguments(bundle);

                    FragmentManager fragmentManager = getSupportFragmentManager();

                    fragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            .replace(R.id.containerTest, fragment, fragment.getTag())
                            .addToBackStack("chestie")
                            .commit();
                }
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "onAppointmentListFragmentInteraction: ", e);
        });
    }

    //call action on call now TV

    @Override
    public void callDoctorViewOnClick(View v, int position, String phoneNumber) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    Constants.MY_PERMISSIONS_REQUEST_CALL_PHONE);

            // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } else {
            //You already have permission
            try {
                startPhoneCall(phoneNumber);
            } catch(SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    //asking permission to perform call

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private boolean checkAdmin() {
        if(FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("cosmindogaru93@gmail.com")) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(this.getSupportFragmentManager().getBackStackEntryCount() == 0)
            this.moveTaskToBack(true);
        else
            super.onBackPressed();
    }

    private void startPhoneCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));

        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callIntent);
    }

    @Override
    public void onAppointmentListFragmentInteraction(Appointment appointment) {
        Fragment fragment = new AppointmentDetailFragment();
        Bundle bundle = new Bundle();
        String appointmentJsonString = new GsonBuilder().create().toJson(appointment);
        bundle.putString(Constants.APPOINTMENT_KEY, appointmentJsonString);

        fragment.setArguments(bundle);


        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.containerTest, fragment, fragment.getTag())
                .addToBackStack("chestie")
                .commit();
    }
}
