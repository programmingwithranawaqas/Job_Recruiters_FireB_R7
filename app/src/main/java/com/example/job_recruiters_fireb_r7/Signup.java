package com.example.job_recruiters_fireb_r7;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signup extends AppCompatActivity {

    // hooks
    EditText etEmail;
    TextInputEditText etPassword, etCPassword;
    Button btnSignup;
    TextView tvLogin;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLoginActivity();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString().trim();
                String pass = etPassword.getText().toString();
                String cpass = etCPassword.getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    etEmail.setError("Email can't be empty");
                    return;
                }

                if(TextUtils.isEmpty(pass))
                {
                    etPassword.setError("Password can't be empty");
                    return;
                }

                if(TextUtils.isEmpty(cpass))
                {
                    etCPassword.setError("Confirm password can't be empty");
                    return;
                }

                if(!TextUtils.equals(pass, cpass))
                {
                    etCPassword.setError("password mis-matched");
                    return;
                }

                ProgressDialog processing = new ProgressDialog(Signup.this);
                processing.setMessage("Registration in process...");
                processing.show();

                auth.createUserWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                processing.dismiss();
                                moveToProfileActivity();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                processing.dismiss();
                                Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }

        });

    }

    private void moveToLoginActivity()
    {
        startActivity(new Intent(Signup.this, Login.class));
        finish();
    }

    private void moveToProfileActivity()
    {
        startActivity(new Intent(Signup.this, Profile.class));
        finish();
    }

    private void init()
    {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etCPassword = findViewById(R.id.etCPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }
}