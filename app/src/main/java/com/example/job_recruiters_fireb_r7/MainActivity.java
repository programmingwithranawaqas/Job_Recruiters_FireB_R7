package com.example.job_recruiters_fireb_r7;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.FirebaseDatabaseKtxRegistrar;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabAdd;
    RecyclerView rvApplicants;
    // TextView tvRecords;
    Button btnAddJob;

    ArrayList<String> jobs;
    ArrayAdapter<String> adapter;

    DatabaseReference reference;

    ApplicantAdapter applicantAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        loadJobs();
        //loadApplicants();
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewApplicantDialog();
            }
        });
        btnAddJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewJob();
            }
        });

    }

    private void addNewJob()
    {
        AlertDialog.Builder addJob = new AlertDialog.Builder(this);
        addJob.setTitle("New Job");
        View v = LayoutInflater.from(this)
                        .inflate(R.layout.add_job, null, false);
        addJob.setView(v);
        EditText etJobTitle = v.findViewById(R.id.etJobtitle);

        addJob.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String, Object> job = new HashMap<>();
                job.put("jobtitle", etJobTitle.getText().toString().trim());

                reference.child("Jobs")
                        .push()
                        .setValue(job)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MainActivity.this, "New Job Created", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });

        addJob.show();
    }

    private void loadApplicants()
    {
        //withoutRefresh();
        //withRefresh();
    }

    private void withoutRefresh()
    {
        reference.child("Applicants")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String text = "";
                        for (DataSnapshot ds: snapshot.getChildren())
                        {
                            text += ds.child("name").getValue().toString()+"\n";
                            text += ds.child("phone").getValue().toString()+"\n";
                            text += ds.child("email").getValue().toString()+"\n";
                            text += ds.child("address").getValue().toString()+"\n";
                            text += ds.child("qualification").getValue().toString()+"\n";
                            text += ds.child("jobtitle").getValue().toString()+"\n\n";

                        }

                        //tvRecords.setText(text);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void withRefresh()
    {
        reference.child("Applicants")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String text = "";
                        for (DataSnapshot ds: snapshot.getChildren())
                        {
                            text += ds.child("name").getValue().toString()+"\n";
                            text += ds.child("phone").getValue().toString()+"\n";
                            text += ds.child("email").getValue().toString()+"\n";
                            text += ds.child("address").getValue().toString()+"\n";
                            text += ds.child("qualification").getValue().toString()+"\n";
                            text += ds.child("jobtitle").getValue().toString()+"\n\n";

                        }

                        //tvRecords.setText(text);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void addNewApplicantDialog()
    {
        AlertDialog.Builder add = new AlertDialog.Builder(this);
        add.setTitle("New Applicant");
        View v = LayoutInflater.from(this)
                        .inflate(R.layout.applicant_form, null, false);
        add.setView(v);

        // hooks of form
        EditText etName = v.findViewById(R.id.etName);
        EditText etPhone = v.findViewById(R.id.etPhone);
        EditText etEmail = v.findViewById(R.id.etEmailAddress);
        EditText etAddress = v.findViewById(R.id.etAddress);
        EditText etQualification = v.findViewById(R.id.etQualification);
        AutoCompleteTextView actvJobTitle = v.findViewById(R.id.actvJobTitle);
        actvJobTitle.setAdapter(adapter);

        // positive button -> add
        add.setPositiveButton("Add", new DialogInterface.OnClickListener() {
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

                reference
                        .child("Applicants")
                        .push()
                        .setValue(record)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MainActivity.this, "Record added", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

            }
        }); // end of positive button code
        add.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder confirmation = new AlertDialog.Builder(MainActivity.this);
                confirmation.setTitle("Confirmation");
                confirmation.setMessage("Are you sure to leave?");

                confirmation.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                confirmation.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                confirmation.show();

            }
        });
        add.show();

    }

    private void init()
    {
        reference = FirebaseDatabase.getInstance().getReference();
        fabAdd = findViewById(R.id.fabAdd);
        //tvRecords = findViewById(R.id.tvRecords);
        //tvRecords.setText("Loading...");
        btnAddJob = findViewById(R.id.btnAddJob);

        jobs = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, jobs);

        rvApplicants = findViewById(R.id.rvApplicants);
        rvApplicants.setHasFixedSize(true);
        rvApplicants.setLayoutManager(new LinearLayoutManager(this));

        Query query = reference.child("Applicants");
        FirebaseRecyclerOptions<Applicant> options
                = new FirebaseRecyclerOptions.Builder<Applicant>()
                .setQuery(query, Applicant.class)
                .build();

        applicantAdapter = new ApplicantAdapter(options);
        rvApplicants.setAdapter(applicantAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        applicantAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        applicantAdapter.stopListening();
    }

    private void loadJobs()
    {
        reference.child("Jobs")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren())
                        {
                            String job = ds.child("jobtitle").getValue().toString();
                            jobs.add(job);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}