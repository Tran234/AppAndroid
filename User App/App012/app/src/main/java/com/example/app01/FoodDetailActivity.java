package com.example.app01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app01.Common.Common;
import com.example.app01.Database.Database;
import com.example.app01.Model.Food;
import com.example.app01.Model.Order;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FoodDetailActivity extends AppCompatActivity {

    private TextView foodName, foodPrice, foodDescription, quantity, foodDiscount;
    private ImageView foodImage, addItem, removeItem;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton btnCart;
    private String foodId = "";
    private FirebaseDatabase database;
    private DatabaseReference foods ;
    private int totalQuantity;
    private Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        initUi();
        initListener();

    }

    private void initUi() {
        //Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Food");

        btnCart = findViewById(R.id.add_cart);

        addItem = findViewById(R.id.add_item);
        quantity = findViewById(R.id.quantity);
        removeItem = findViewById(R.id.remove_item);

        foodName = findViewById(R.id.food_name_detail);
        foodDescription = findViewById(R.id.food_des);
        foodPrice = findViewById(R.id.food_price);
        foodImage = findViewById(R.id.img_food);
        foodDiscount = findViewById(R.id.food_discount);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
    }

    private void initListener() {
        //elegant number button
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalQuantity < 100) {
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });

        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalQuantity > 0) {
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });

        //===========activity_food_detail.xml======================
        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty()) {
            //Toast.makeText(this, "" + foodId, Toast.LENGTH_SHORT).show();
            if(Common.ConnectedToInter(getBaseContext())){
                getDetailFood(foodId);
            }
            else {
                Toast.makeText(FoodDetailActivity.this,"Kiểm tra lại kết nối internet!!!",
                        Toast.LENGTH_SHORT).show();
                return;
            }


        }

        //===========Button Cart==========================
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        (String) quantity.getText(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()

                ));

                Toast.makeText(FoodDetailActivity.this, "Thêm vào giỏ hàng",
                        Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void getDetailFood(String foodId) {
        //Firebase

        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Food");
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                currentFood = snapshot.getValue(Food.class);

                Picasso.get().load(currentFood.getImage()).into(foodImage);
                foodName.setText(currentFood.getName());
                foodDescription.setText(currentFood.getDescription());
                foodPrice.setText(currentFood.getPrice());
                foodDiscount.setText(currentFood.getDiscount());
                collapsingToolbarLayout.setTitle(currentFood.getName());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*private void addToCart() {
        String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());


    }*/


}