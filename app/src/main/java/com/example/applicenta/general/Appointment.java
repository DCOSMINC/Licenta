package com.example.applicenta.general;

import com.example.applicenta.Model.DoctorAddress;

public class Appointment {
    private String doctorID;
    private String doctorName;
    private String doctorSpecialty;
    private DoctorAddress address;
    private String date;
    private String hour;

    public Appointment(String doctorID, String doctorName, String doctorSpecialty, DoctorAddress address, String date, String hour) {
        this.doctorID = doctorID;
        this.doctorName = doctorName;
        this.doctorSpecialty = doctorSpecialty;
        this.address = address;
        this.date = date;
        this.hour = hour;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpecialty() {
        return doctorSpecialty;
    }

    public void setDoctorSpecialty(String doctorSpecialty) {
        this.doctorSpecialty = doctorSpecialty;
    }

    public DoctorAddress getAddress() {
        return address;
    }

    public void setAddress(DoctorAddress address) {
        this.address = address;
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
