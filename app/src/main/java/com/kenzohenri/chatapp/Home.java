package com.kenzohenri.chatapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenzohenri.chatapp.Fragments.BlogFragment;
import com.kenzohenri.chatapp.Fragments.ChatsFragment;
import com.kenzohenri.chatapp.Fragments.DoctorsFragment;
import com.kenzohenri.chatapp.Fragments.GroupFragment;
import com.kenzohenri.chatapp.Fragments.HomeFragment;
import com.kenzohenri.chatapp.Fragments.ProfileFragment;
import com.kenzohenri.chatapp.Fragments.TermsFragment;
import com.kenzohenri.chatapp.Fragments.UsersFragment;
import com.kenzohenri.chatapp.Model.Post;
import com.kenzohenri.chatapp.Model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference reference;
    Dialog popAddPost;
    ImageView popupAddBtn;
    CircleImageView popupUserImage;
    TextView popupTitle, popupDescription;
    ProgressBar popupClickProgress;
    FloatingActionButton fab;

    //Current user info
    String img, id, username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //initialize

        mAuth = FirebaseAuth.getInstance();

        //inipopup
        iniPopup();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();
        //set the home fragment as the default one

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();

    }


    public void showFloatingActionButton(){
        fab.show();
    }

    public void hideFloatingActionButton(){
        fab.hide();
    }

    private void iniPopup() {

        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        //inipopup widgets
        popupUserImage = popAddPost.findViewById(R.id.popup_user_image);
        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupDescription = popAddPost.findViewById(R.id.popup_description);
        popupClickProgress = popAddPost.findViewById(R.id.popup_progressBar);
        popupAddBtn = popAddPost.findViewById(R.id.popup_add);

        //Load Current User profile photo

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        id = currentUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                img = user.getImageURL();
                username = user.getUsername();
                if(user.getImageURL() !=null){
                    //Load userPhoto
                    if(user.getImageURL().equals("default")){
                        popupUserImage.setImageResource(R.mipmap.ic_launcher);
                    }else {
                        Glide.with(getApplicationContext()).load(user.getImageURL()).into(popupUserImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Add post click Listener

        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                //Validate Text fields in description
                if(!popupTitle.getText().toString().isEmpty()
                && !popupDescription.getText().toString().isEmpty()){
                    //TODO create Post Object and save it to Firebase Database

                    currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            /*Post post = new Post(popupTitle.getText().toString(),
                            popupDescription.getText().toString(),user.getImageURL(),user.getId());*/

                            Post post = new Post(popupTitle.getText().toString(),
                                    popupDescription.getText().toString(),img,id, username,popupTitle.getText().toString().toLowerCase());
                            addPost(post);
                            popupTitle.setText("");
                            popupDescription.setText("");
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    showMessage("Please Verify all input fields");
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);
                }


            }

        });

    }

    private void addPost(Post post) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        //get your unique Id and update post key

        String key  = myRef.getKey();
        post.setPostKey(key);

        //add post data to firebase
        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post added Successfully");
                popupClickProgress.setVisibility(View.INVISIBLE);
                popupAddBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
            }
        });

    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Home.this,StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();

        } else if (id == R.id.nav_blog) {
            getSupportActionBar().setTitle("My Blog");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new BlogFragment()).commit();

        } else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("About Me");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new ProfileFragment()).commit();

        } else if (id == R.id.nav_chatRoom) {
            getSupportActionBar().setTitle("Chats");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new ChatsFragment()).commit();

        } else if (id == R.id.nav_users) {

            getSupportActionBar().setTitle("Users");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new UsersFragment()).commit();

        } else if (id == R.id.nav_doctors) {

            getSupportActionBar().setTitle("Doctors");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new DoctorsFragment()).commit();

        } else if (id == R.id.nav_terms) {

            getSupportActionBar().setTitle("Terms and Conditions of Use");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new TermsFragment()).commit();

        } else if (id == R.id.nav_group) {

            getSupportActionBar().setTitle("Group Chat");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new GroupFragment()).commit();

        } else if (id == R.id.nav_logout) {

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader(){

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        final TextView navUsername = headerView.findViewById(R.id.nav_username);
        final TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        final CircleImageView navUserPhoto = headerView.findViewById(R.id.nav_user_photo);


        //Get Database User Info
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getImageURL() !=null){
                    navUsername.setText(user.getUsername());
                    navUserMail.setText(currentUser.getEmail());
                    //Load userPhoto
                    if(user.getImageURL().equals("default")){
                        navUserPhoto.setImageResource(R.mipmap.ic_launcher);
                    }else {
                        Glide.with(getApplicationContext()).load(user.getImageURL()).into(navUserPhoto);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }





}
