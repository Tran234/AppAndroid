package com.example.app01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView txtForgotPassword,txtReset;
    private EditText edtEmailSend;
    private Button btnSend;
    @SuppressLint("MissingInflatedId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        txtForgotPassword = findViewById(R.id.txt_forgot_password);
        Typeface face = Typeface.createFromAsset(getAssets(), "font/NABILA.TTF");
        txtForgotPassword.setTypeface(face);

        initUi();
        initListener();
    }

    private void initUi() {
        edtEmailSend = findViewById(R.id.edtEmailSend);
        btnSend = findViewById(R.id.btnSend);
        txtReset = findViewById(R.id.txtreset);
    }

    private void initListener() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSend();
            }
        });

    }

    private void onClickSend() {
        String email = edtEmailSend.getText().toString().trim();
        if (TextUtils.isEmpty(email)){
            edtEmailSend.setError("Kiểm tra lại Email!!!");
            return;
        }
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.getResult().getSignInMethods().isEmpty()) {
                            txtReset.setText("Email không tồn tại, bạn có thể tạo tài khoản mới");
                        } else {
                            auth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                txtReset.setText("Một email để thiết lập lại mật khẩu đã được gửi đến địa chỉ email của bạn");
                                            }else {
                                                txtReset.setText(task.getException().getMessage());
                                            }
                                        }
                                    });

                        }
                    }
                });

    }
}