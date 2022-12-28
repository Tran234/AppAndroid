package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Common.Common;
import com.example.myapplication.Interface.ItemClickListener;
import com.example.myapplication.Model.Category;
import com.example.myapplication.Model.Token;
import com.example.myapplication.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    private TextView txtName;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    private RecyclerView recycler_menu;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseStorage storage;

    private StorageReference storageReference;
    private EditText edtNameCategory;
    private Button btnChoose, btnUpload;

    private Category newCategory;
    private Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toobar);
        toolbar.setTitle("QUẢN LÝ THỰC ĐƠN");
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        //Firebase realtimedatabase
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Categoty");
        //Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference =storage.getReference();
        //Init View
        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);
        loadMenu();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMenuDialog();
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new FCM registration token
                        String tokenRefresh = task.getResult();
                        updateTokenToFirebase(tokenRefresh);
                    }
                });

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_home){

        }else if(id == R.id.nav_cart){
            startActivity(new Intent(MainActivity.this,BannerActivity.class));
        }else if(id == R.id.nav_order) {
            startActivity(new Intent(MainActivity.this,OrderStatusActivity.class));
        }else if(id == R.id.nav_log_out){
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAffinity();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
        super.onBackPressed();
    }

    private void loadMenu() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Category");
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(databaseReference,Category.class).build();
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull  Category model) {
                holder.txtMenuName.setText(model.getName());
                //Glide.with(MainActivity.this).load(model.getImage()).into(holder.imageView);
                Picasso.get().load(model.getImage()).into(holder.imageView);
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodList = new Intent(MainActivity.this,FoodListActivity.class);
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,parent,false);
                return new MenuViewHolder(view);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recycler_menu.setAdapter(adapter);
    }

    //Add new category
    private void addMenuDialog() {

        AlertDialog.Builder altertDialog = new AlertDialog.Builder(MainActivity.this);
        altertDialog.setTitle("Thêm trường món ăn");
        altertDialog.setMessage("Vui lòng nhập đầy đủ thông tin");
        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.layout_dialog_add_menu, null);
        edtNameCategory = add_menu_layout.findViewById(R.id.edt_add_menu);
        btnChoose = add_menu_layout.findViewById(R.id.btn_choose_menu);
        btnUpload = add_menu_layout.findViewById(R.id.btn_upload_menu);
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

                if (newCategory != null){
                    databaseReference.push().setValue(newCategory);
                    Snackbar.make(drawerLayout, "Thêm "+newCategory.getName()+" thành công!!!",
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
                            Toast.makeText(MainActivity.this, "Cập nhật thành công !!!",
                                    Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newCategory = new Category(edtNameCategory.getText().toString(), uri.toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(MainActivity.this, "" + e.getMessage(),
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

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)){
            updateMenuDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)){
            deleteMenuDialog(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void updateMenuDialog(String key, Category item) {
        AlertDialog.Builder altertDialog = new AlertDialog.Builder(MainActivity.this);
        altertDialog.setTitle("Cập nhật trường món");
        altertDialog.setMessage("Vui lòng nhập đầy đủ thông tin");
        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.layout_dialog_add_menu, null);
        edtNameCategory = add_menu_layout.findViewById(R.id.edt_add_menu);
        btnChoose = add_menu_layout.findViewById(R.id.btn_choose_menu);
        btnUpload = add_menu_layout.findViewById(R.id.btn_upload_menu);
        //Set default name
        edtNameCategory.setText(item.getName());

        altertDialog.setView(add_menu_layout);
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

                item.setName(edtNameCategory.getText().toString());
                databaseReference.child(key).setValue(item);
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

    private void changeImage(Category item) {
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
                            Toast.makeText(MainActivity.this, "Cập nhật thành công !!!",
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
                            Toast.makeText(MainActivity.this, "" + e.getMessage(),
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

    private void deleteMenuDialog(String key) {

        DatabaseReference food = FirebaseDatabase.getInstance().getReference("Food");
        Query foodCategory = food.orderByChild("menuId").equalTo(key);
        foodCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot:snapshot.getChildren()){
                    postSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child(key).removeValue();
        Toast.makeText(this, "Xóa thành công!!!", Toast.LENGTH_SHORT).show();
    }


    private void updateTokenToFirebase(String tokenRefresh) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens").child(Common.currentAdmin.getName());
        //String phone = Common.currentUser.getPhone();
        //DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens").child(phone);
        Token datatoken = new Token(tokenRefresh,true);
        databaseReference.setValue(datatoken);
    }
}