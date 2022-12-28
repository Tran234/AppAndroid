package com.example.app01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app01.Common.Common;
import com.example.app01.Interface.ItemClickListener;
import com.example.app01.Model.Food;
import com.example.app01.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {


    private EditText edtSearch;
    private Button btnSearch;
    private RecyclerView recycler_search;
    private String SearchInput;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Food,FoodViewHolder> adapterSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initUi();
        initListener();
    }

    private void initUi() {

        edtSearch = findViewById(R.id.edt_search);
        btnSearch = findViewById(R.id.btn_search);
        recycler_search = findViewById(R.id.recycler_search);
        recycler_search.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_search.setLayoutManager(layoutManager);

    }

    private void initListener() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchInput = edtSearch.getText().toString();
                if(Common.ConnectedToInter(getBaseContext())){
                    onStart();                }
                else {
                    Toast.makeText(SearchActivity.this,"Kiểm tra lại kết nối internet!!!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Food");
       FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
               .setQuery(databaseReference.orderByChild("name").startAt(SearchInput).endAt(SearchInput+"\uf8ff"),Food.class)
               .build();

        adapterSearch = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                holder.txtFoodName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.imageFoodView);
                //final Food local =model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick)  {
                        Intent foodDetail = new Intent(SearchActivity.this,FoodDetailActivity.class);
                        //Toast.makeText(FoodListActivity.this, ""+local.getName(),Toast.LENGTH_SHORT).show();
                        foodDetail.putExtra("FoodId",adapterSearch.getRef(position).getKey());
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
        adapterSearch.startListening();
        recycler_search.setAdapter(adapterSearch);
    }
}
