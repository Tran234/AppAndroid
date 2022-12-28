package com.example.app01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app01.Common.Common;
import com.example.app01.Common.Config;
import com.example.app01.Database.Database;
import com.example.app01.Model.MyResponse;
import com.example.app01.Model.Notification;
import com.example.app01.Model.Sender;
import com.example.app01.Model.Token;
import com.example.app01.Remote.APIService;
import com.example.app01.ViewHolder.CartAdapter;
import com.example.app01.Model.Order;
import com.example.app01.Model.Request;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import com.paypal.android.sdk.da;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private static final int PAYPAL_REQUEST_CODE = 999;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase database;
    private DatabaseReference request;
    private TextView txtTotal;

    private TextView txtTotal_Paypal;

    private Button btnPlace;
    private List<Order> cart = new ArrayList<>();
    private CartAdapter cartAdapter;

    private String saveCurrentDate, saveCurrentTime;

    private APIService mService;

    //Paypal
    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);
    String address,addspinner, addCommment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        database = FirebaseDatabase.getInstance();
        request = database.getReference("Request");

        initUi();
        initListener();
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        // Init Service
        mService = Common.getFCMService();

        // Init Paypal
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);
    }

    private void initUi() {
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        txtTotal = findViewById(R.id.total);
        btnPlace = findViewById(R.id.btnPlace);

        txtTotal_Paypal = findViewById(R.id.total_paypal);

    }

    private void initListener() {
        if(Common.ConnectedToInter(getBaseContext())){
            loadListFood();
        }
        else {
            Toast.makeText(CartActivity.this,"Kiểm tra lại kết nối internet!!!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        loadListFood();
        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cart.size() >0)
                    showAlertDialog();
                else
                    Toast.makeText(CartActivity.this, "Trống!!!",
                            Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAlertDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("Thanh toán...");
        //alertDialog.setMessage("Thêm địa chỉ: ");

        /*final EditText edtAddress = new EditText(CartActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        edtAddress.setLayoutParams(layoutParams);
        alertDialog.setView(edtAddress);*/
        ////////////////////////////////////////////////////////////////////////////////////////////
        LayoutInflater inflater = this.getLayoutInflater();
        View add_cart_layout = inflater.inflate(R.layout.layout_dialog_add_cart, null);
        EditText edtAddress = add_cart_layout.findViewById(R.id.edt_add_address);
        MaterialSpinner spinner = add_cart_layout.findViewById(R.id.edt_add_spinner);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        EditText edt_add_commment = add_cart_layout.findViewById(R.id.edt_add_commment);
        RadioButton rdb_paypal = add_cart_layout.findViewById(R.id.rdb_paypal);
        RadioButton rdb_cod = add_cart_layout.findViewById(R.id.rdb_cod);
        spinner.setItems("Tại cửa hàng","Shipped");
        alertDialog.setView(add_cart_layout);
        ////////////////////////////////////////////////////////////////////////////////////////////
        alertDialog.setIcon(R.drawable.ic_cart);
        alertDialog.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Show Paypal

                address = edtAddress.getText().toString();

                if (TextUtils.isEmpty(address)){
                    Toast.makeText(CartActivity.this, "Vui lòng nhập địa chỉ !!!",
                            Toast.LENGTH_SHORT).show();
                    showAlertDialog();
                    return;
                }

                addspinner = spinner.getText().toString();

                addCommment = edt_add_commment.getText().toString();


                if (!rdb_cod.isChecked() && !rdb_paypal.isChecked()){
                    Toast.makeText(CartActivity.this, "Vui lòng chọn phương thức thanh toán !!!",
                            Toast.LENGTH_SHORT).show();
                    showAlertDialog();
                    return;
                }

                else if (rdb_paypal.isChecked()) {

                    String formatAmount = txtTotal_Paypal.getText().toString()
                            .replace("$", "")
                            .replace(",", "");

                    PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(formatAmount))
                            , "USD", "Foods App",
                            PayPalPayment.PAYMENT_INTENT_SALE);
                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                    startActivityForResult(intent, PAYPAL_REQUEST_CODE);
/*/////////////////////////////////////////////////////////////////////////////////////
                Request requestCart = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotal.getText().toString(),
                        cart,
                        "tiền mặt",
                        spinner.getText().toString(),
                        saveCurrentDate,
                        saveCurrentTime
                        );

                String order_number = String.valueOf(System.currentTimeMillis());
                request.child(order_number)
                        .setValue(requestCart);
                //Delete cart after palce
                new Database(getBaseContext()).cleanCart();
                //=====Notification=================================================================
                sendNotificationOrder(order_number);
                //==================================================================================
                //Toast.makeText(CartActivity.this, "Cảm bạn đã đặt hàng !!!",
                        //Toast.LENGTH_SHORT).show();
                //finish();*/
                }

                else if (rdb_cod.isChecked()){
                    Request requestCart = new Request(
                            Common.currentUser.getPhone(),
                            Common.currentUser.getName(),
                            edtAddress.getText().toString(),
                            txtTotal.getText().toString(),
                            cart,
                            "Tiền mặt",
                            "Chưa thanh toán",
                            spinner.getText().toString(),
                            edt_add_commment.getText().toString(),
                            saveCurrentDate,
                            saveCurrentTime
                    );

                    String order_number = String.valueOf(System.currentTimeMillis());
                    request.child(order_number)
                            .setValue(requestCart);
                    //Delete cart after palce
                    new Database(getBaseContext()).cleanCart();
                    //=====Notification=================================================================
                    sendNotificationOrder(order_number);
                    //==================================================================================
                }
            }
        });

        alertDialog.setNegativeButton("Không, cảm ơn", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);

                        //==========================================================================

                        Request requestCart = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                txtTotal.getText().toString(),
                                cart,
                                "Paypal",
                                jsonObject.getJSONObject("response").getString("state"),
                                addspinner,
                                addCommment,
                                saveCurrentDate,
                                saveCurrentTime
                        );

                        String order_number = String.valueOf(System.currentTimeMillis());
                        request.child(order_number)
                                .setValue(requestCart);

                        //Delete cart after palce
                        new Database(getBaseContext()).cleanCart();

                        sendNotificationOrder(order_number);

                        Toast.makeText(CartActivity.this, "Cảm bạn đã đặt hàng !!!",
                                Toast.LENGTH_SHORT).show();
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Hủy thanh toán", Toast.LENGTH_SHORT).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, "Thanh toán không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendNotificationOrder(String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot:snapshot.getChildren()){
                    Token serverToken = postSnapshot.getValue(Token.class);
                    Notification notification = new Notification(""+order_number,"Bạn có đơn hàng mới");
                    Sender content = new Sender(serverToken.getToken(),notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.body().success == 1){
                                        Toast.makeText(CartActivity.this, "Cảm bạn đã đặt hàng !!!",
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else {
                                        Toast.makeText(CartActivity.this, "Thất bại !!!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("Thất bại!!!",t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadListFood() {

        cart = new Database(this).getCarts();
        cartAdapter = new CartAdapter(cart,this);
        cartAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(cartAdapter);

        float total = 0;
        for (Order order:cart){
            total+= (Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));

       }

        Locale locale = new Locale("vi", "VN");
        //NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        //txtTotal_Paypal.setText(String.valueOf(total));

        DecimalFormat fmt = (DecimalFormat) DecimalFormat.getCurrencyInstance(locale);
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setCurrencySymbol("VND");
        fmt.setDecimalFormatSymbols(formatSymbols);

        txtTotal.setText(fmt.format(total));
        //DecimalFormat fmtUs = (DecimalFormat) DecimalFormat.getCurrencyInstance(new Locale("en","US"));

        txtTotal_Paypal.setText(DecimalFormat.getCurrencyInstance(new Locale("en","US")).format(total/23840));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }
    private void deleteCart(int position) {
        cart.remove(position);
        new Database(this).cleanCart();
        for (Order item : cart)
            new Database(this).addToCart(item);
        loadListFood();
    }
}