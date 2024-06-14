package com.example.job_recruiters_fireb_r7;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity {

    // hooks
    TextView tvVerifyEmail, tvName, tvProfession, tvRate;
    Button btnSendEmail, btnLogout, btnResetPassword, btnEditProfile;
    ImageView ivProfilePic;
    FirebaseAuth auth;
    FirebaseUser user;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

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

        tvVerifyEmail = findViewById(R.id.tvVerifyYourAccount);
        btnSendEmail = findViewById(R.id.btnSendEmail);
        tvVerifyEmail.setVisibility(View.GONE);
        btnSendEmail.setVisibility(View.GONE);
        btnLogout = findViewById(R.id.btnLogout);

    }
}