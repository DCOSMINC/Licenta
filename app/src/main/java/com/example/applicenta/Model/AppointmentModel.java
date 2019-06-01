package com.example.applicenta.Model;

public class AppointmentModel {
    private String doctorId;
    private String date;
    private String hour;

    public AppointmentModel() {}

    public AppointmentModel(String doctorId, String date, String hour) {
        this.doctorId = doctorId;
        this.date = date;
        this.hour = hour;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
