package com.example.mr_proj.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Employee extends DbEntity{

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
