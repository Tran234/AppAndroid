package com.example.app01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.app01.Common.Common;
import com.example.app01.Interface.ItemClickListener;
import com.example.app01.Model.Category;
import com.example.app01.Model.Food;
import com.example.app01.ViewHolder.FoodViewHolder;
import com.example.app01.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class FoodListActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private FirebaseDatabase database;
    private DatabaseReference food;
    private RecyclerView recycler_food;
    private RecyclerView.LayoutManager layoutManager;
    private String categoryId="";
    private FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        /*database = FirebaseDatabase.getInstance();
        food = database.getReference("Food");*/
        initUi();
        initListener();
}

    private void initUi() {

        fab = findViewById(R.id.fab_list);
        recycler_food=findViewById(R.id.recycler_food);
        recycler_food.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_food.setLayoutManager(layoutManager);
    }

    private void initListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(FoodListActivity.this, CartActivity.class);
                startActivity(cartIntent);
            }
        });

        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty() && categoryId != null){
            if(Common.ConnectedToInter(getBaseContext())){
                loadFoods(categoryId);
            }
            else {
                Toast.makeText(FoodListActivity.this,"Kiểm tra lại kết nối internet!!!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void loadFoods(String categoryId) {
        food = FirebaseDatabase.getInstance().getReference("Food");
        Query keycode = food.orderByChild("menuId").equalTo(categoryId);
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(keycode,Food.class).build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull final Food model) {
                holder.txtFoodName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.imageFoodView);
                //final Food local =model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetail = new Intent(FoodListActivity.this,FoodDetailActivity.class);
                        //Toast.makeText(FoodListActivity.this, ""+local.getName(),Toast.LENGTH_SHORT).show();
                        foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(foodDetail);

                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }
        };
        adapter.startListening();
        recycler_food.setAdapter(adapter);
    }
}