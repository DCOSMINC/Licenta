package com.example.applicenta.Interface;

import android.view.View;

import com.example.applicenta.general.Doctor;

public interface DoctorListFragmentInteractionListener {
    void onListFragmentInteraction(Doctor item);
    void callDoctorViewOnClick(View v, int position, String phoneNumber);
}
