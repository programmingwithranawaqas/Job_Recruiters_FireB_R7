package com.example.job_recruiters_fireb_r7;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class Profile extends AppCompatActivity {

    // hooks
    TextView tvVerifyEmail, tvName, tvProfession, tvRate;
    Button btnSendEmail, btnLogout, btnResetPassword, btnEditProfile;
    ImageView ivProfilePic;
    FirebaseAuth auth;
    FirebaseUser user;

    final int IMAGE_CODE = 1;

    DatabaseReference reference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        loadInformation();

        if(!user.isEmailVerified()) {
            tvVerifyEmail.setVisibility(View.VISIBLE);
            btnSendEmail.setVisibility(View.VISIBLE);
        }
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog processing = new ProgressDialog(Profile.this);
                processing.setMessage("sending email...");
                processing.show();
                user.sendEmailVerification()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                processing.dismiss();
                                Toast.makeText(Profile.this, "Email sent", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                processing.dismiss();
                                Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                moveToLoginActivity();
            }
        });
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickImage = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(pickImage, IMAGE_CODE);
            }
        });
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.sendPasswordResetEmail(user.getEmail())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Profile.this, "Email sent", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(user.isEmailVerified())
                    editProfile();
                else
                    Toast.makeText(Profile.this, "Verify your email id", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadInformation() {
        reference.child("Users")
                .child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            tvName.setText(snapshot.child("name").getValue().toString());
                            tvProfession.setText(snapshot.child("profession").getValue().toString());
                            tvRate.setText(snapshot.child("rate").getValue().toString());

                            tvProfession.setVisibility(View.VISIBLE);
                            tvRate.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void editProfile() {
        View view = LayoutInflater.from(Profile.this)
                .inflate(R.layout.edit_profile, null, false);
        AlertDialog.Builder edit = new AlertDialog.Builder(this);
        edit.setTitle("Edit Profile");
        edit.setView(view);
        EditText etName = view.findViewById(R.id.etName);
        EditText etProf = view.findViewById(R.id.etProf);
        EditText etRate = view.findViewById(R.id.etRate);

        edit.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = etName.getText().toString().trim();
                String prof = etProf.getText().toString().trim();
                String rate = etRate.getText().toString();

                if(name.isEmpty())
                {
                    etName.setError("Enter the name");
                    return;
                }
                if(prof.isEmpty())
                {
                    etProf.setError("Enter the profession");
                    return;
                }
                if(rate.isEmpty())
                {
                    etRate.setError("Enter the rate");
                    return;
                }




                HashMap<String, Object> data = new HashMap<>();
                data.put("name", name);
                data.put("profession", prof);
                data.put("rate", rate);

                reference.child("Users")
                        .child(user.getUid())
                        .setValue(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                tvName.setText(name);
                                tvProfession.setText(prof);
                                tvRate.setText(rate);
                                tvProfession.setVisibility(View.VISIBLE);
                                tvRate.setVisibility(View.VISIBLE);

                                Toast.makeText(Profile.this, "Profile updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        edit.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_CODE)
        {
            if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show();
            }
            else if(resultCode == RESULT_OK)
            {
                Uri image = data.getData();
                storageReference.child("images/"+user.getUid()+"/profile.jpg");

                ivProfilePic.setImageURI(image);
            }
        }

    }

    private void moveToLoginActivity()
    {
        startActivity(new Intent(Profile.this, Login.class));
        finish();
    }

    private void init()
    {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        tvVerifyEmail = findViewById(R.id.tvVerifyYourAccount);
        btnSendEmail = findViewById(R.id.btnSendEmail);
        tvVerifyEmail.setVisibility(View.GONE);
        btnSendEmail.setVisibility(View.GONE);
        btnLogout = findViewById(R.id.btnLogout);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvName = findViewById(R.id.tvName);
        tvProfession = findViewById(R.id.tvProfession);
        tvRate = findViewById(R.id.tvHourlyRate);
        tvName.setText("Update your profile");
        tvProfession.setVisibility(View.GONE);
        tvRate.setVisibility(View.GONE);
    }
}