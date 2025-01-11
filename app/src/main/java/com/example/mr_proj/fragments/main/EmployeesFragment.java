package com.example.mr_proj.fragments.main;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mr_proj.R;
import com.example.mr_proj.adapter.ListAdapter;
import com.example.mr_proj.dao.EmployeeDAO;
import com.example.mr_proj.fragments.dialog.AddEntityDialog;
import com.example.mr_proj.fragments.dialog.EditEntityDialog;
import com.example.mr_proj.fragments.dialog.RemoveEntityDialog;
import com.example.mr_proj.model.DbEntity;
import com.example.mr_proj.model.Employee;
import com.example.mr_proj.service.DAOService;
import com.example.mr_proj.util.DatabaseUtil;
import com.example.mr_proj.util.DialogUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;


public class EmployeesFragment extends Fragment implements AddEntityDialog.DialogListener, RemoveEntityDialog.RemoveDialogListener {
    private final List<Disposable> disposables = new ArrayList<>();
    private ListAdapter<Employee> listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_employees, container, false);

        EmployeeDAO dao = DatabaseUtil.getDbInstance(root.getContext()).employeeDAO();
        listAdapter = new ListAdapter<>(dao, this);
        Disposable d = DAOService.getEntities(listAdapter);
        disposables.add(d);

        RecyclerView employeesRecyclerView = root.findViewById(R.id.employees_list);
        employeesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        employeesRecyclerView.setAdapter(listAdapter);

        root.findViewById(R.id.add_employee_btn).setOnClickListener(this::onAddEmployee);
        return root;
    }

    private void onAddEmployee(View view) {
        DialogFragment dialog = new AddEntityDialog();
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
        Employee employee = extractFromFields(dialog);
        if (employee == null)
            return;
        Disposable d = DAOService.insertEntity(employee, listAdapter);
        disposables.add(d);
        dialog.dismiss();
    }

    @Override
    public void onEditPositiveClick(DialogFragment dialog) {
        Employee employee = extractFromFields(dialog);
        if (employee == null)
            return;

        EditEntityDialog<? extends DbEntity> editDialog = (EditEntityDialog<? extends DbEntity>) dialog;
        employee.id = editDialog.getEntityId();

        Disposable d = DAOService.updateEntity(employee, listAdapter);
        disposables.add(d);
        dialog.dismiss();
    }

    @Override
    public void onPositiveClick(DialogFragment dialog) {
        RemoveEntityDialog<? extends DbEntity> removeDialog = (RemoveEntityDialog<? extends DbEntity>) dialog;
        Employee entity = (Employee) removeDialog.getEntity();
        Disposable d = DAOService.deleteEntity(entity, listAdapter);
        disposables.add(d);
    }

    private Employee extractFromFields(DialogFragment dialog) {
        String firstName = DialogUtil.getFieldValue(dialog, R.id.firstName);
        String lastName = DialogUtil.getFieldValue(dialog, R.id.lastName);

        if (firstName == null || lastName == null)
            return null;

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(getContext(), R.string.form_notice, Toast.LENGTH_SHORT).show();
            return null;
        }

        return new Employee(firstName, lastName);
    }
}