package com.example.applicenta.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.applicenta.Interface.DoctorListFragmentInteractionListener;
import com.example.applicenta.R;
import com.example.applicenta.general.Doctor;

import java.util.List;


public class MyFavoritesRecyclerViewAdapter extends RecyclerView.Adapter<MyFavoritesRecyclerViewAdapter.FavoriteDoctorViewHolder> {

    private List<Doctor> doctorList;
    private final DoctorListFragmentInteractionListener mListener;
    private Context context;

    public MyFavoritesRecyclerViewAdapter(List<Doctor> items, DoctorListFragmentInteractionListener listener, Context context) {
        doctorList = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public FavoriteDoctorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doctor_item, parent, false);
        return new FavoriteDoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteDoctorViewHolder favoriteDoctorViewHolder, int position) {
        String doctorFirstName = doctorList.get(position).getFirstName();
        String doctorLastName = doctorList.get(position).getLastName();
        String specialty = doctorList.get(position).getSpecialty();
        String photoPath = doctorList.get(position).getPhotoPath();

        Uri uri = Uri.parse(photoPath);

        String fullName = doctorFirstName.concat(doctorLastName);

        favoriteDoctorViewHolder.fullName.setText(fullName);
        favoriteDoctorViewHolder.specialty.setText(specialty);
        favoriteDoctorViewHolder.doctorItem = doctorList.get(position);
        Glide.with(context).load(uri).into(favoriteDoctorViewHolder.profileImageView);

        favoriteDoctorViewHolder.itemView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(favoriteDoctorViewHolder.doctorItem);
            }
        });
    }


    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public void loadNewData(List<Doctor> doctorList) {
        this.doctorList = doctorList;
        notifyDataSetChanged();
    }

    public void addNewDoctor(Doctor doctor) {
        if(!doctorList.contains(doctor)) {
            this.doctorList.add(doctor);
            notifyDataSetChanged();
        }
    }

    class FavoriteDoctorViewHolder extends RecyclerView.ViewHolder {

        private TextView fullName;
        private TextView specialty;
        private Doctor doctorItem = null;
        private ImageView profileImageView;


        FavoriteDoctorViewHolder(View view) {
            super(view);
            fullName = view.findViewById(R.id.doctorItemNameTextView);
            specialty = view.findViewById(R.id.doctorItemSpecialtyTextView);
            profileImageView = view.findViewById(R.id.doctorProfileImageView);
        }
    }
}
