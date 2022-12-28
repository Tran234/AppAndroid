package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.Common.Common;
import com.example.myapplication.Model.Admin;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    private EditText edtId, edtPassword;
    private Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initUi();
        initListener();
    }

    private void initUi() {
        btnSignIn = findViewById(R.id.btn_sign_in);
        edtId = findViewById(R.id.edt_id);
        edtPassword = findViewById(R.id.edt_password);
    }

    private void initListener() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Admin");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProgressDialog dialog = new ProgressDialog(SignInActivity.this);
                dialog.setMessage("Đợi trong giây lát...");
                dialog.show();

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        dialog.dismiss();

                        Admin admin = snapshot.child(edtId.getText().toString()).getValue(Admin.class);
                        Common.currentAdmin = admin;
                        if(admin.getPassword().equals(edtPassword.getText().toString())){
                            Toast.makeText(SignInActivity.this, "Đăng nhập thành công",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            finishAffinity();
                        }else {
                            Toast.makeText(SignInActivity.this, "Đăng nhập thất bại!!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}