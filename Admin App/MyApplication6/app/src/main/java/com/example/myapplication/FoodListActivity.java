package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.Common.Common;
import com.example.myapplication.Interface.ItemClickListener;
import com.example.myapplication.Model.Category;
import com.example.myapplication.Model.Food;
import com.example.myapplication.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class FoodListActivity extends AppCompatActivity {

    private FloatingActionButton fab_food;
    private FirebaseDatabase database;
    private DatabaseReference food;
    private RecyclerView recycler_food;
    private RecyclerView.LayoutManager layoutManager;
    private String categoryId="";
    private FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;

    private FirebaseStorage storage;

    private StorageReference storageReference;
    private EditText edtUpdateNameFood, edtUpdateDescriptionFood, edtUpdateDiscountFood, edtUpdatePriceFood;
    private Button btnChoose, btnUpload;

    private CoordinatorLayout coordinator_layout;
    private Food newFood;
    private Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        database = FirebaseDatabase.getInstance();
        food = database.getReference("Food");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        recycler_food = findViewById(R.id.recycler_food);
        recycler_food.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_food.setLayoutManager(layoutManager);

        coordinator_layout = findViewById(R.id.coordinator_layout);
        //swipe_layout = findViewById(R.id.swipe_layout);

        fab_food = findViewById(R.id.fab_food);
        fab_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFoodDialog();
            }
        });
        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty() && categoryId != null){
                loadFoods(categoryId);
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

    @SuppressLint("MissingInflatedId")
    private void addFoodDialog() {
        AlertDialog.Builder altertDialog = new AlertDialog.Builder(FoodListActivity.this);
        altertDialog.setTitle("Thêm món ăn");
        altertDialog.setMessage("Vui lòng nhập đầy đủ thông tin");
        LayoutInflater inflater = this.getLayoutInflater();
        View add_food_layout = inflater.inflate(R.layout.layout_dialog_add_food, null);
        edtUpdateNameFood = add_food_layout.findViewById(R.id.edt_add_food);
        edtUpdateDescriptionFood = add_food_layout.findViewById(R.id.edt_add_description);
        edtUpdateDiscountFood = add_food_layout.findViewById(R.id.edt_add_discount);
        edtUpdatePriceFood = add_food_layout.findViewById(R.id.edt_add_price);

        btnChoose = add_food_layout.findViewById(R.id.btn_choose_food);
        btnUpload = add_food_layout.findViewById(R.id.btn_upload_food);

        altertDialog.setView(add_food_layout);
        altertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);
        //


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        //========================================
        altertDialog.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (newFood != null){
                    food.push().setValue(newFood);
                    Snackbar.make(coordinator_layout, "Thêm "+newFood.getName()+" thành công!!!",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });

        altertDialog.setNegativeButton("Không, Cảm ơn", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        altertDialog.show();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), PICK_IMAGE_REQUEST);
    }


    //Update and Delete Food
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
            btnChoose.setText("Đã chọn hình ảnh!");
        }
    }

    private void uploadImage() {
        if (saveUri != null) {
            ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Cập nhật...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(FoodListActivity.this, "Cập nhật thành công !!!",
                                    Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newFood = new Food(edtUpdateNameFood.getText().toString(), uri.toString(),
                                            edtUpdateDescriptionFood.getText().toString(), edtUpdatePriceFood.getText().toString(),
                                            edtUpdateDiscountFood.getText().toString(),categoryId);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(FoodListActivity.this, "" + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            mDialog.setMessage("Đang cập nhật" + progress + "%...");
                        }
                    });
        }

    }

    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)){
            updateFoodDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));} else
        if (item.getTitle().equals(Common.DELETE)){
            deleteFoodDialog(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    @SuppressLint("MissingInflatedId")
    private void updateFoodDialog(String key, Food item) {
        AlertDialog.Builder altertDialog = new AlertDialog.Builder(FoodListActivity.this);
        altertDialog.setTitle("Cập nhật trường món");
        altertDialog.setMessage("Vui lòng nhập đầy đủ thông tin");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_food_layout = inflater.inflate(R.layout.layout_dialog_add_food, null);
        edtUpdateNameFood = add_food_layout.findViewById(R.id.edt_add_food);
        edtUpdateDescriptionFood = add_food_layout.findViewById(R.id.edt_add_description);
        edtUpdateDiscountFood = add_food_layout.findViewById(R.id.edt_add_discount);
        edtUpdatePriceFood = add_food_layout.findViewById(R.id.edt_add_price);

        btnChoose = add_food_layout.findViewById(R.id.btn_choose_food);
        btnUpload = add_food_layout.findViewById(R.id.btn_upload_food);
        //Set default name
        edtUpdateNameFood.setText(item.getName());
        edtUpdateDescriptionFood.setText(item.getDescription());
        edtUpdateDiscountFood.setText(item.getDiscount());
        edtUpdatePriceFood.setText(item.getPrice());


        altertDialog.setView(add_food_layout);
        altertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);
        //


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage(item);
            }
        });

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        //========================================
        altertDialog.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                item.setName(edtUpdateNameFood.getText().toString());
                item.setDescription(edtUpdateDescriptionFood.getText().toString());
                item.setPrice(edtUpdatePriceFood.getText().toString());
                item.setDiscount(edtUpdateDiscountFood.getText().toString());
                food.child(key).setValue(item);
            }
        });

        altertDialog.setNegativeButton("Không, Cảm ơn", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        altertDialog.show();
    }

    private void changeImage(Food item) {
        if (saveUri != null) {
            ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Cập nhật...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(FoodListActivity.this, "Cập nhật thành công !!!",
                                    Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage(uri.toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(FoodListActivity.this, "" + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            mDialog.setMessage("Đang cập nhật" + progress + "%...");
                        }
                    });
        }

    }

    private void deleteFoodDialog(String key) {
        food.child(key).removeValue();
        Toast.makeText(this, "Xóa thành công!!!", Toast.LENGTH_SHORT).show();
    }


}