package com.example.applicenta.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.applicenta.Model.AppointmentModel;
import com.example.applicenta.R;
import com.example.applicenta.general.Appointment;
import com.example.applicenta.general.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AppointmentDetailFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = "AppointmentDetailFragme";

    private MapView mapView;

    private GoogleMap map;

    private String location;

    private TextView doctorName, dateHour, address, doctorSpecialty;
    private Button cancelAppointment;

    private Appointment appointment;


    public AppointmentDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment_detail, container, false);

        doctorName = view.findViewById(R.id.appointmentDoctorName);
        dateHour = view.findViewById(R.id.appointmentDateHour);
        address = view.findViewById(R.id.appointmentAddress);
        doctorSpecialty = view.findViewById(R.id.appointmentDoctorSpecialty);
        cancelAppointment = view.findViewById(R.id.cancelAppointmentBtn);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        cancelAppointment.setOnClickListener(this);

        mapView.getMapAsync(this);
        Log.d(TAG, "onCreateView: " + mapView);

        String appointmentJsonString = getArguments().getString(Constants.APPOINTMENT_KEY);
        appointment = new GsonBuilder().create().fromJson(appointmentJsonString, Appointment.class);

        String date = appointment.getDate().replaceAll(".*\\{|\\}.*", "");
        String hour = appointment.getHour();

        location = String.format("%s %s %s", appointment.getAddress().getStreet(), appointment.getAddress().getCity(), appointment.getAddress().getCounty());

        doctorName.setText(appointment.getDoctorName());
        dateHour.setText(String.format("%s între orele %s", formatDate(date), hour));
        address.setText(String.format("%s %s", appointment.getAddress().getCity(), appointment.getAddress().getStreet()));
        doctorSpecialty.setText(appointment.getDoctorSpecialty());


        return view;
    }

    public GeoPoint getLocationFromAddress(){

        Geocoder coder = new Geocoder(getContext());
        List<Address> address;
        GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(location,1);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            Log.d(TAG, "getLocationFromAddress: " + location);
            location.getLatitude();
            location.getLongitude();

            p1 = new GeoPoint( location.getLatitude() , location.getLongitude());

            return p1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private String formatDate(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date data = format.parse(date);
            return new SimpleDateFormat("dd-MMMM-yyyy").format(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.MY_PERMISIONS_REQUEST_MAP_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Log.d(TAG, "onRequestPermissionsResult: " + "smecherie");
                    map.setMyLocationEnabled(true);
                    GeoPoint point = getLocationFromAddress();
                    map.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude())).title("Nebunie"));
                    map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(point.getLatitude(), point.getLongitude())));
                }
                return;
            default:
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        Log.d(TAG, "onMapReady: " + map);

        map.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.MY_PERMISIONS_REQUEST_MAP_ACCESS_COARSE_LOCATION);
            return;
        }
        map.setMyLocationEnabled(true);
        GeoPoint point = getLocationFromAddress();
        map.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude())).title("Nebunie"));
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(point.getLatitude(), point.getLongitude())));

    }

    private void cancelAppointment() {
        FirebaseFirestore.getInstance().collection(Constants.FIREBASE_USERS).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if(documentSnapshot.get(Constants.FIREBASE_ID).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    AppointmentModel appointmentModel = new AppointmentModel(appointment.getDoctorID(), appointment.getDate(), appointment.getHour());
                    documentSnapshot.getReference().update(Constants.FIREBASE_BOOKINGS, FieldValue.arrayRemove(appointmentModel));
                    getFragmentManager().popBackStackImmediate();
                }
            }
        });

        FirebaseFirestore.getInstance().collection(Constants.FIREBASE_DOCTORS).document(appointment.getDoctorID()).get().addOnSuccessListener(documentSnapshot -> {
            documentSnapshot.getReference().update(appointment.getDate(), FieldValue.arrayRemove(appointment.getHour()));
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.cancelAppointmentBtn:
                cancelAppointment();
                break;
            default:
                break;
        }
    }
}
