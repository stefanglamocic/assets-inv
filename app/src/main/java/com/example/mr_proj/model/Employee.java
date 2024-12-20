package com.example.mr_proj.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Employee implements DbEntity{
    private static final String IMG = "ic_user";

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String firstName;
    public String lastName;

    public Employee() {}

    public Employee(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String getRowText() {
        return firstName + " " + lastName;
    }

    @Override
    public String getRowImage() {
        return "ic_user";
    }
}
