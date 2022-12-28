package com.example.app01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.app01.Common.Common;
import com.example.app01.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private EditText edtEmail,edtPassword, edtName, edtPhone;
    private Button btnSignUp;
    private LinearLayout layoutSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initUi();
        initListener();
    }

    private void initUi(){
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        layoutSignIn= findViewById(R.id.layout_sign_in);
    }

    private void initListener(){
        layoutSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.ConnectedToInter(getBaseContext())){
                    onClickSignUp();
                } else {
                    Toast.makeText(SignUpActivity.this,"Kiểm tra lại kết nối internet!!!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void onClickSignUp() {

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)){
            edtName.setError("Kiểm tra lại Họ và tên");
            return;
        }
        if (TextUtils.isEmpty(name)){
            edtPhone.setError("Kiểm tra lại Số điện thoại");
            return;
        }
        if (TextUtils.isEmpty(email)){
            edtEmail.setError("Kiểm tra lại Email");
            return;
        }
        if (TextUtils.isEmpty(password)){
            edtPassword.setError("Kiểm tra lại Mật khẩu");
            return;
        }else {
            SignUp(name, email, password, phone);
        }


    }

    private void SignUp(String name, String email, String password, String phone) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser rUser = auth.getCurrentUser();
                            String userId = rUser.getUid();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userId);
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("userId",userId);
                            hashMap.put("name",name);
                            hashMap.put("email",email);
                            hashMap.put("phone",phone);
                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        auth.getCurrentUser().sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finishAffinity();
                                                        }else {
                                                            Toast.makeText(SignUpActivity.this, "Xác thực thất bại",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "Xác thực thất bại",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(SignUpActivity.this, "Xác thực thất bại",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}