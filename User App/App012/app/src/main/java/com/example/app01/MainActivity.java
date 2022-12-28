package com.example.app01;

import static com.example.app01.R.layout.menu_item;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.app01.Common.Common;
import com.example.app01.Database.Database;
import com.example.app01.Interface.ItemClickListener;
import com.example.app01.Model.Category;
import com.example.app01.Model.Request;
import com.example.app01.Model.Token;
import com.example.app01.Model.User;
import com.example.app01.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView txtName, txtvEmail, txtPhone;
    private DrawerLayout mdrawerLayout;
    private FirebaseDatabase database;
    private DatabaseReference category;
    private RecyclerView recycler_menu;
    private RecyclerView.LayoutManager layoutManager;
    //Slider Banner
    private HashMap<String,String> imager_list;
    private SliderLayout sliderLayout;

    //private ActionBarDrawerToggle toggle;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Thực đơn");
        setSupportActionBar(toolbar);

        //Load refresh
        swipeRefreshLayout = findViewById(R.id.swip_layout);
        swipeRefreshLayout.setColorSchemeColors(R.color.black,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Check connectinternet================================
                if(Common.ConnectedToInter(getBaseContext())){
                    loadMenu();
                }
                else {
                    Toast.makeText(getBaseContext(),"Kiểm tra lại kết nối internet!!!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        //default load
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //Check connectinternet================================
                if(Common.ConnectedToInter(getBaseContext())){
                    loadMenu();
                }
                else {
                    Toast.makeText(getBaseContext(),"Kiểm tra lại kết nối internet!!!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

 //(2). Init Firebase-----------------(2).
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        //======================================
        // Button Cart==========================
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((view) -> {
            Intent cartIntent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(cartIntent);
        });

        mdrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mdrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mdrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        //(3).Load menu (3)-------------------------
        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        //layoutManager = new LinearLayoutManager(this);
        //recycler_menu.setLayoutManager(layoutManager);

        recycler_menu.setLayoutManager(new GridLayoutManager(this,2));


        //==========================================

        //(1).Init Firebase layout_header_nav.xml.(1)
        initUiheaderNav();
        initListenerheaderNav();
        //===========================================

        //Setup Banner
        //setupSliderBanner();
        ImageSlider imageSlider = findViewById(R.id.image_slider);
        final List<SlideModel> remote = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Banner")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data:snapshot.getChildren()){
                            remote.add(new SlideModel(data.child("image").getValue().toString(),data.child("name").getValue().toString(), ScaleTypes.FIT));
                        }
                        imageSlider.setImageList(remote, ScaleTypes.FIT);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //==========================================================================================
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

        ///==========================================================================================
    }
    private void updateTokenToFirebase(String tokenRefresh) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser rUser = auth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens").child(rUser.getUid());
        //String phone = Common.currentUser.getPhone();
        //DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens").child(phone);
        Token datatoken = new Token(tokenRefresh,false);
        databaseReference.setValue(datatoken);
    }

    /*private void setupSliderBanner() {
        sliderLayout = findViewById(R.id.slider);
        imager_list = new HashMap<>();

        DatabaseReference bannerData = database.getReference("Banner");
        bannerData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot:snapshot.getChildren()){
                    Banner banner = postSnapshot.getValue(Banner.class);

                    imager_list.put(banner.getName()+"_"+banner.getId(),banner.getImage());
                }
                for (String key:imager_list.keySet()){
                    String [] keySplit = key.split("_");
                    String namofFood = keySplit[0];
                    String idofFood = keySplit[1];

                    TextSliderView textSliderView = new TextSliderView(getBaseContext());
                    textSliderView.description(namofFood)
                            .image(imager_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(MainActivity.this,FoodDetailActivity.class);
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);
                                }
                            });
                    //Add extra bundle
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("FoodId",idofFood);

                    sliderLayout.addSlider(textSliderView);

                    //Remove finish
                    bannerData.removeEventListener(this);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);
    }*/

    private void loadMenu() {
        category = FirebaseDatabase.getInstance().getReference().child("Category");
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category,Category.class).build();
                adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull  Category model) {
                holder.txtMenuName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.imageView);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodList = new Intent(MainActivity.this,FoodListActivity.class);
                        //CategoryId la key
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });

            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(menu_item,parent,false);
                return new MenuViewHolder(view);
            }
        };
        adapter.startListening();
        recycler_menu.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_home){

        } else if(id == R.id.nav_cart){
            startActivity(new Intent(MainActivity.this, CartActivity.class));

        } else if(id == R.id.nav_orders){
            startActivity(new Intent(MainActivity.this, OrderActivity.class));

        }
        else  if(id == R.id.nav_change_password){
            startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
        }
        else  if(id == R.id.nav_change_acount){
            startActivity(new Intent(MainActivity.this, AcountActivity.class));
        }
        else if(id == R.id.nav_log_out){
            FirebaseAuth.getInstance().signOut();
            //Delete cart=============================
            new Database(getBaseContext()).cleanCart();
            //==========================================
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAffinity();
        }
        mdrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.search)
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_search, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mdrawerLayout.isDrawerOpen(GravityCompat.START)){
            mdrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //-------------(1)---------------------------------
    private void initUiheaderNav() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        txtName = navigationView.getHeaderView(0).findViewById(R.id.txt_name_user);
        txtvEmail = navigationView.getHeaderView(0).findViewById(R.id.txt_email_user);
        txtPhone = navigationView.getHeaderView(0).findViewById(R.id.txt_phone_user);
    }

    private void initListenerheaderNav() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userdata = snapshot.getValue(User.class);
                Common.currentUser = userdata;
                assert userdata != null;
                txtName.setText(userdata.getName());
                txtvEmail.setText(userdata.getEmail());
                txtPhone.setText(userdata.getPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    //==================================================

}