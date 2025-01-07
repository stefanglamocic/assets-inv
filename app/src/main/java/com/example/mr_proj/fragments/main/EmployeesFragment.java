package com.example.mr_proj.fragments.main;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mr_proj.R;
import com.example.mr_proj.adapter.ListAdapter;
import com.example.mr_proj.dao.EmployeeDAO;
import com.example.mr_proj.fragments.dialog.AddEntityDialog;
import com.example.mr_proj.model.Employee;
import com.example.mr_proj.service.DAOService;
import com.example.mr_proj.util.DatabaseUtil;
import com.example.mr_proj.util.DialogUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;


public class EmployeesFragment extends Fragment implements AddEntityDialog.DialogListener {
    private final List<Disposable> disposables = new ArrayList<>();
    private ListAdapter<Employee> listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_employees, container, false);

        List<Employee> employees = new ArrayList<>();
        EmployeeDAO dao = DatabaseUtil.getDbInstance(root.getContext()).employeeDAO();
        listAdapter = new ListAdapter<>(employees, dao);
        Disposable d = DAOService.getEntities(listAdapter);
        disposables.add(d);

        RecyclerView employeesRecyclerView = root.findViewById(R.id.employees_list);
        employeesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        employeesRecyclerView.setAdapter(listAdapter);

        root.findViewById(R.id.add_employee_btn).setOnClickListener(this::onAddEmployee);
        return root;
    }

    private void onAddEmployee(View view) {
        DialogFragment dialog = new AddEntityDialog<>();
        dialog.show(getChildFragmentManager(), "addEmployee");
    }

    @Override
    public void onDestroy() {
        for (Disposable d : disposables)
            d.dispose();
        super.onDestroy();
    }

    @Override
    public void onAddPositiveClick(DialogFragment dialog) {
        String firstName = DialogUtil.getFieldValue(dialog, R.id.firstName);
        String lastName = DialogUtil.getFieldValue(dialog, R.id.lastName);

        if (firstName == null || lastName == null)
            return;

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(getContext(), R.string.form_notice, Toast.LENGTH_SHORT).show();
            return;
        }

        Employee employee = new Employee(firstName, lastName);
        Disposable d = DAOService.insertEntity(employee, listAdapter);
        disposables.add(d);
        dialog.dismiss();
    }
}