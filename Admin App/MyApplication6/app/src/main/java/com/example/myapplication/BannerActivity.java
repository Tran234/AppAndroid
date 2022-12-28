package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.myapplication.Model.Banner;
import com.example.myapplication.Model.Category;
import com.example.myapplication.Model.Food;
import com.example.myapplication.ViewHolder.BannerViewHolder;
import com.example.myapplication.ViewHolder.FoodViewHolder;
import com.example.myapplication.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class BannerActivity extends AppCompatActivity {

    private FloatingActionButton fab_banner;
    private FirebaseDatabase database;
    private DatabaseReference banner;
    private RecyclerView recycler_banner;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseRecyclerAdapter<Banner, BannerViewHolder> adapter;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private EditText edtNameFoodBanner;
    private Button btnChoose, btnUpload;
    private Banner newBanner;
    private Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        database = FirebaseDatabase.getInstance();
        banner = database.getReference("Banner");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        recycler_banner = findViewById(R.id.recycler_banner);
        recycler_banner.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_banner.setLayoutManager(layoutManager);

        loadBanner();

        FloatingActionButton fab = findViewById(R.id.fab_banner);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBannerDialog();
            }
        });
    }

    private void loadBanner() {
        banner = FirebaseDatabase.getInstance().getReference().child("Banner");
        FirebaseRecyclerOptions<Banner> options = new FirebaseRecyclerOptions.Builder<Banner>()
                .setQuery(banner,Banner.class).build();
        adapter = new FirebaseRecyclerAdapter<Banner, BannerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BannerViewHolder holder, int position, @NonNull Banner model) {
                holder.txtBannerName.setText(model.getName());
                //Glide.with(MainActivity.this).load(model.getImage()).into(holder.imageView);
                Picasso.get().load(model.getImage()).into(holder.imageView);
                /*holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodList = new Intent(MainActivity.this,FoodListActivity.class);
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });*/
            }

            @NonNull
            @Override
            public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item,parent,false);
                return new BannerViewHolder(view);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recycler_banner.setAdapter(adapter);
    }

    @SuppressLint("MissingInflatedId")
    private void addBannerDialog() {
        AlertDialog.Builder altertDialog = new AlertDialog.Builder(BannerActivity.this);
        altertDialog.setTitle("Thêm món ăn vào ảnh bìa");
        altertDialog.setMessage("Vui lòng nhập đầy đủ thông tin");
        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.layout_dialog_add_banner, null);
        edtNameFoodBanner = add_menu_layout.findViewById(R.id.edt_add_banner);
        btnChoose = add_menu_layout.findViewById(R.id.btn_choose_banner);
        btnUpload = add_menu_layout.findViewById(R.id.btn_upload_banner);
        altertDialog.setView(add_menu_layout);
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

                banner.push().setValue(newBanner);
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
                            Toast.makeText(BannerActivity.this, "Cập nhật thành công !!!",
                                    Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newBanner = new Banner(edtNameFoodBanner.getText().toString(), uri.toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(BannerActivity.this, "" + e.getMessage(),
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

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
            btnChoose.setText("Đã chọn hình ảnh!");
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
         if (item.getTitle().equals(Common.DELETE)){
            deleteBannerDialog(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteBannerDialog(String key) {
        banner.child(key).removeValue();
        Toast.makeText(this, "Xóa thành công!!!", Toast.LENGTH_SHORT).show();
    }
}