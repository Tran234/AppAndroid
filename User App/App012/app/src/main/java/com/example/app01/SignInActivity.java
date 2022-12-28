package com.example.app01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.app01.Common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    private EditText edtEmail,edtPassword;
    private Button btnSignIn, btnSignUp;
    private ProgressDialog progressDialog;
    private LinearLayout layoutForgottenPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initUi();
        initListener();
    }

    private void initUi() {
        //progressDialog = new ProgressDialog(this);
        btnSignUp = findViewById(R.id.btnSignUp);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        layoutForgottenPassword= findViewById(R.id.layout_forgotten_password);
    }

    private void initListener(){
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        layoutForgottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.ConnectedToInter(getBaseContext())){
                    onClickSignIn();

                } else {
                    Toast.makeText(SignInActivity.this,"Kiểm tra lại kết nối internet!!!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void onClickSignIn() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)){
            edtEmail.setError("Kiểm tra lại Email");
            return;
        }
        if (TextUtils.isEmpty(password)){
            edtPassword.setError("Kiểm tra lại Mật khẩu");
            return;
        }
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            Toast.makeText(SignInActivity.this, "Xác thực thất bại",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}