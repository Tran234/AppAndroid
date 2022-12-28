package com.example.app01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private TextView txtChangePassword;
    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private Button btnChangePassword;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        txtChangePassword = findViewById(R.id.txt_change_password);
        Typeface face = Typeface.createFromAsset(getAssets(), "font/NABILA.TTF");
        txtChangePassword.setTypeface(face);

        initUi();
        initListener();
    }
    private void initUi() {
        edtOldPassword = findViewById(R.id.edt_old_password);
        edtNewPassword = findViewById(R.id.edt_new_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        btnChangePassword = findViewById(R.id.btnChange);
    }

    private void initListener() {
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChangePassword();
            }
        });
    }

    private void onClickChangePassword() {
        String oldPss = edtOldPassword.getText().toString().trim();
        String newPss = edtNewPassword.getText().toString().trim();
        String confirmPss = edtConfirmPassword.getText().toString().trim();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if(oldPss.isEmpty() || newPss.isEmpty() || confirmPss.isEmpty()){
            Toast.makeText(ChangePasswordActivity.this, "Không để trống ô nào!!!",
                    Toast.LENGTH_SHORT).show();
        }else if (! confirmPss.equals(newPss)){
            Toast.makeText(ChangePasswordActivity.this, "Xác nhận mật khẩu không khớp với mật khẩu mới!!!",
                    Toast.LENGTH_SHORT).show();
        } else {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(firebaseUser.getEmail(),oldPss);
            firebaseUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                firebaseUser.updatePassword(newPss).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            auth.signOut();
                                            Intent intent = new Intent(ChangePasswordActivity.this, SplashActivity.class);
                                            startActivity(intent);
                                        }else {
                                            Toast.makeText(ChangePasswordActivity.this, task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
        }

    }
}