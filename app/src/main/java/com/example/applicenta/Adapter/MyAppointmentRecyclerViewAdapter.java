package com.example.applicenta.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.applicenta.Interface.OnAppointmentListInteractionListener;
import com.example.applicenta.R;
import com.example.applicenta.general.Appointment;

import org.threeten.bp.LocalDateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MyAppointmentRecyclerViewAdapter extends RecyclerView.Adapter<MyAppointmentRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "MyAppointmentRecyclerVi";
    
    private List<Appointment> appointments;
    private final OnAppointmentListInteractionListener mListener;

    public MyAppointmentRecyclerViewAdapter(List<Appointment> appointments, OnAppointmentListInteractionListener listener) {
        this.appointments = appointments;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.appointment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        String doctorName = appointments.get(position).getDoctorName();
        String doctorSpecialty = appointments.get(position).getDoctorSpecialty();
        String date = appointments.get(position).getDate().replaceAll(".*\\{|\\}.*", "");
        String hour = appointments.get(position).getHour();


        holder.appointmentDoctorName.setText("Dr. " + doctorName);
        holder.appointmentDoctorSpecialty.setText(doctorSpecialty);
        holder.appointmentDate.setText(formatDate(date));
        holder.appointmentHour.setText(hour);

        holder.appointment = appointments.get(position);

        holder.itemView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onAppointmentListFragmentInteraction(holder.appointment);
            }
        });
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }


    public void loadNewData(List<Appointment> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
    }

    public void addNewItem(Appointment appointment) {
        if(!appointments.contains(appointment)) {
            this.appointments.add(appointment);
            notifyDataSetChanged();
        }
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

    public void deleteItems() {
        appointments.removeAll(appointments);
        notifyItemRangeRemoved(0, appointments.size());
    }

    public void checkHour() {
        for(int i = 0; i < appointments.size(); i++) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            try {
                Date date = format.parse(appointments.get(i).getDate() +  appointments.get(i).getHour());
                Log.d(TAG, "checkHour: " + date);
                Calendar calendar = Calendar.getInstance();
                LocalDateTime time = LocalDateTime.now();
                Date dateNow = format.parse(String.valueOf(time));
                Log.d(TAG, "checkHour: " + dateNow);
                if(dateNow.equals(date)) {
                    Log.d(TAG, "checkHour: sunt egale");
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView appointmentDoctorName;
        private TextView appointmentDoctorSpecialty;
        private TextView appointmentDate;
        private TextView appointmentHour;
        private Appointment appointment = null;

        ViewHolder(View view) {
            super(view);
            appointmentDoctorName = view.findViewById(R.id.appointmentDoctorNameTextView);
            appointmentDoctorSpecialty = view.findViewById(R.id.appointmentDoctorSpecialtyTextView);
            appointmentDate = view.findViewById(R.id.appointmentDateTextView);
            appointmentHour = view.findViewById(R.id.appointmentHourTextView);
        }
    }
}
