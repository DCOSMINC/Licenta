package com.example.applicenta.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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


public class MyDoctorsRecyclerViewAdapter extends RecyclerView.Adapter<MyDoctorsRecyclerViewAdapter.DoctorViewHolder> {

    private static final String TAG = "MyDoctorsRecyclerViewAd";

    private List<Doctor> doctorList;
    private final DoctorListFragmentInteractionListener mListener;
    private Context context;

    public MyDoctorsRecyclerViewAdapter(List<Doctor> items, DoctorListFragmentInteractionListener listener, Context context) {
        doctorList = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public DoctorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doctor_item, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DoctorViewHolder holder, int position) {
        String doctorFirstName = doctorList.get(position).getFirstName();
        String doctorLastName = doctorList.get(position).getLastName();
        String specialty = doctorList.get(position).getSpecialty();
        String photoPath = doctorList.get(position).getPhotoPath();

        Uri uri = Uri.parse(photoPath);

        String fullName = doctorFirstName.concat(" ").concat(doctorLastName);

        holder.fullName.setText(fullName);
        holder.specialty.setText(specialty);
        holder.doctorItem = doctorList.get(position);
        Glide.with(context).load(uri).into(holder.profileImageView);


        holder.callNowTv.setOnClickListener(v -> mListener.callDoctorViewOnClick(v, position, doctorList.get(position).getTelephone()));

        holder.itemView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(holder.doctorItem);
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

    class DoctorViewHolder extends RecyclerView.ViewHolder {

        private TextView fullName;
        private TextView specialty;
        private ImageView profileImageView;
        private Doctor doctorItem = null;
        private TextView bookNowTV;
        private TextView callNowTv;

        DoctorViewHolder(View view) {
            super(view);
            fullName = view.findViewById(R.id.doctorItemNameTextView);
            specialty = view.findViewById(R.id.doctorItemSpecialtyTextView);
            profileImageView = view.findViewById(R.id.doctorProfileImageView);
            bookNowTV = view.findViewById(R.id.bookAppointmentTV);
            callNowTv = view.findViewById(R.id.callDoctorTv);
        }
    }


}
