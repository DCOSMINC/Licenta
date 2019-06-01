package com.example.applicenta.Model;

import java.util.ArrayList;

public class UserModel {
    private String ID;
    private ArrayList<AppointmentModel> bookings;
    private String email;
    private ArrayList<String> favorites;
    private String firstName;
    private String lastName;
    private int isDoctor;
    private String photoPath;
    private String telephoneNumber;
    private String username;

    public UserModel() {};

    public UserModel(String ID, ArrayList<AppointmentModel> bookings, String email, ArrayList<String> favorites, String firstName, String lastName, int isDoctor, String photoPath, String telephoneNumber, String username) {
        this.ID = ID;
        this.bookings = bookings;
        this.email = email;
        this.favorites = favorites;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isDoctor = isDoctor;
        this.photoPath = photoPath;
        this.telephoneNumber = telephoneNumber;
        this.username = username;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public ArrayList<AppointmentModel> getBookings() {
        return bookings;
    }

    public void setBookings(ArrayList<AppointmentModel> bookings) {
        this.bookings = bookings;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayList<String> favorites) {
        this.favorites = favorites;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getIsDoctor() {
        return isDoctor;
    }

    public void setIsDoctor(int isDoctor) {
        this.isDoctor = isDoctor;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
