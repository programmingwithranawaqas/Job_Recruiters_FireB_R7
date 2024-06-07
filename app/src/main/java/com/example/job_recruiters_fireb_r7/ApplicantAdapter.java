package com.example.job_recruiters_fireb_r7;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ApplicantAdapter extends FirebaseRecyclerAdapter<Applicant, ApplicantAdapter.ApplicantViewHolder> {

    public ApplicantAdapter(@NonNull FirebaseRecyclerOptions<Applicant> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ApplicantViewHolder holder,
                                    int i, @NonNull Applicant applicant) {
        String key = getRef(i).getKey();
        holder.tvName.setText(applicant.getName());
        holder.tvEmail.setText(applicant.getEmail());
        holder.tvAddress.setText(applicant.getAddress());
        holder.tvQualification.setText(applicant.getQualification());
        holder.tvJobTitle.setText(applicant.getJobtitle());
        holder.tvPhone.setText(applicant.getPhone());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder edit = new AlertDialog.Builder(v.getContext());
                edit.setTitle(applicant.getName()+" Record");
                View view
                         = LayoutInflater.from(v.getContext())
                                .inflate(R.layout.applicant_form, null, false);
                edit.setView(view);

                EditText etName = view.findViewById(R.id.etName);
                EditText etPhone = view.findViewById(R.id.etPhone);
                EditText etEmail = view.findViewById(R.id.etEmailAddress);
                EditText etAddress = view.findViewById(R.id.etAddress);
                EditText etQualification = view.findViewById(R.id.etQualification);
                AutoCompleteTextView actvJobTitle = view.findViewById(R.id.actvJobTitle);
                etName.setText(applicant.getName());
                etPhone.setText(applicant.getPhone());
                etEmail.setText(applicant.getEmail());
                etAddress.setText(applicant.getAddress());
                etQualification.setText(applicant.getQualification());
                actvJobTitle.setText(applicant.getJobtitle());

                edit.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = etName.getText().toString().trim();
                        String phone = etPhone.getText().toString();
                        String email = etEmail.getText().toString().trim();
                        String address = etAddress.getText().toString().trim();
                        String qualification = etQualification.getText().toString().trim();
                        String jobtitle = actvJobTitle.getText().toString().trim();

                        HashMap<String, Object> record
                                = new HashMap<>();
                        record.put("name", name);
                        record.put("phone", phone);
                        record.put("email", email);
                        record.put("address", address);
                        record.put("qualification", qualification);
                        record.put("jobtitle", jobtitle);

                        assert key != null;
                        FirebaseDatabase.getInstance().getReference("Applicants")
                                .child(key)
                                .setValue(record)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(view.getContext(), "Record updated", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                edit.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        assert key != null;
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("Applicants")
                                .child(key)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(view.getContext(), "Deleted...", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });


                edit.show();


                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @NonNull
    @Override
    public ApplicantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_applicant_item_design, parent, false);
        return new ApplicantViewHolder(v);
    }

    public class ApplicantViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvName, tvEmail, tvAddress, tvJobTitle, tvQualification, tvPhone;
        public ApplicantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvQualification = itemView.findViewById(R.id.tvQualification);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
        }
    }
}
