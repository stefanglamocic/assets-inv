package com.example.mr_proj.fragments.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mr_proj.R;
import com.example.mr_proj.adapter.ListAdapter;
import com.example.mr_proj.dao.EmployeeDAO;
import com.example.mr_proj.model.Employee;
import com.example.mr_proj.service.DAOService;
import com.example.mr_proj.util.DatabaseUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class EmployeesFragment extends Fragment {
    private EmployeeDAO dao;
    private final List<Disposable> disposables = new ArrayList<>();
    private ListAdapter<Employee> listAdapter;
    private List<Employee> employees;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_employees, container, false);

        employees = new ArrayList<>();
        listAdapter = new ListAdapter<>(employees);
        dao = DatabaseUtil.getDbInstance(root.getContext()).employeeDAO();
        Disposable d = DAOService.getEntities(dao, listAdapter);
        disposables.add(d);

        RecyclerView employeesRecyclerView = root.findViewById(R.id.employees_list);
        employeesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        employeesRecyclerView.setAdapter(listAdapter);

        root.findViewById(R.id.add_employee_btn).setOnClickListener(this::onAddEmployee);
        return root;
    }

    private void onAddEmployee(View view) {}

    @Override
    public void onDestroy() {
        for (Disposable d : disposables)
            d.dispose();
        super.onDestroy();
    }
}