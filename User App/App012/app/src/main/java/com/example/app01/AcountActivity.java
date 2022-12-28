package com.example.app01;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.app01.Common.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AcountActivity extends AppCompatActivity {

    private EditText edt_update_name, edt_update_phone;
    private Button btn_update_user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount);

        edt_update_name = findViewById(R.id.edt_update_name);
        edt_update_phone = findViewById(R.id.edt_update_phone);
        btn_update_user = findViewById(R.id.btn_update_user);

        edt_update_name.setText(Common.currentUser.getName());
        edt_update_phone.setText(Common.currentUser.getPhone());

        btn_update_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProgressDialog dialog = new ProgressDialog(AcountActivity.this);
                dialog.setMessage("Đang cập nhật...");
                dialog.show();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference acount = database.getReference("User");

                Common.currentUser.setName(edt_update_name.getText().toString());
                Common.currentUser.setPhone(edt_update_phone.getText().toString());

                acount.child(user.getUid()).setValue(Common.currentUser);

                startActivity(new Intent(AcountActivity.this, MainActivity.class));


            }
        });
    }
}