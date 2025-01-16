package com.example.mr_proj.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mr_proj.model.Employee;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface EmployeeDAO extends IDAO<Employee>{
    @Insert
    Completable insert(Employee employee);

    @Query("SELECT * FROM employee")
    Flowable<List<Employee>> getAll();

    @Update
    Completable update(Employee employee);

    @Delete
    Completable delete(Employee employee);
    
    @Query("SELECT * FROM employee WHERE firstName LIKE :query OR lastName LIKE :query")
    Flowable<List<Employee>> search(String query);
}
