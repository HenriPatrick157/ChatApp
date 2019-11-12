package com.kenzohenri.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenzohenri.chatapp.Adapter.CommentAdapter;
import com.kenzohenri.chatapp.Model.Comment;
import com.kenzohenri.chatapp.Model.Post;
import com.kenzohenri.chatapp.Model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailsActivity extends AppCompatActivity {

    CircleImageView imgUserPost, imgCurrentUser;
    TextView txtPostDesc, textPostDateName,textPostTitle;
    EditText editTextComment;
    Button btnAddComment;

    FirebaseUser firebaseUser;
    DatabaseReference reference, commentReference;

    String postKey;
    Comment comment;
    User user;

    RecyclerView RvComment;
    CommentAdapter commentAdapter;
    List<Comment> listComments;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        //Status bar transparent
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //getSupportActionBar().hide();

        //Check if User is logged in

        /*firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            showMessage("Please log in or register first");
            startActivity(new Intent(PostDetailsActivity.this,StartActivity.class));
            finish();
        }*/


        //ini views

        imgUserPost = findViewById(R.id.post_detail_user_img);
        imgCurrentUser = findViewById(R.id.post_detail_currentuser_img);
        txtPostDesc = findViewById(R.id.post_detail_description);
        textPostDateName = findViewById(R.id.post_detail_date_name);
        textPostTitle = findViewById(R.id.post_detail_title);
        editTextComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_add_comment);

        RvComment = findViewById(R.id.rv_comment);

        postKey = getIntent().getExtras().getString("postKey");
        //add Comment button click Listener

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddComment.setVisibility(View.INVISIBLE);
                reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                commentReference = FirebaseDatabase.getInstance().getReference("Comment").child(postKey).push();
                final String comment_content = editTextComment.getText().toString();
                final String uid = firebaseUser.getUid();
                //get Username and Comment user image
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                        String uname = user.getUsername();
                        String uimg = user.getImageURL().toString();
                        comment = new Comment(comment_content,uid,uimg,uname);
                        if(!comment_content.equals("")){
                            commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    showMessage("Comment added");
                                    editTextComment.setText("");
                                    btnAddComment.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showMessage("failed to add comment "+e.getMessage());
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        //We need to bind all data into these Views
        //first we need to get post Data
        //We need to send post detail data to this activity first
        //then we can get post data


        String postTitle = getIntent().getExtras().getString("title");
        textPostTitle.setText(postTitle);

        final String userPostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userPostImage).into(imgUserPost);

        String postDescription = getIntent().getExtras().getString("description");
        txtPostDesc.setText(postDescription);

        //Get post username

        String postname = getIntent().getExtras().getString("username");
        String date = timeStampToString(getIntent().getExtras().getLong("postDate"));
        textPostDateName.setText(date + " by " + postname);


        //set Comment user image
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!= null){
            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user.getImageURL().equals("default")){
                        imgCurrentUser.setImageResource(R.mipmap.ic_launcher_round);
                    } else {
                        Glide.with(getApplicationContext()).load(user.getImageURL()).into(imgCurrentUser);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //ini RecyclerView Comment
            iniRvComment();
        }else{
            showMessage("Please log in or register first to comment");
            imgCurrentUser.setVisibility(View.INVISIBLE);
            editTextComment.setVisibility(View.INVISIBLE);
            btnAddComment.setVisibility(View.INVISIBLE);
            RvComment.setVisibility(View.INVISIBLE);

        }

    }

    private void iniRvComment() {

        RvComment.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comment").child(postKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComments = new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Comment comment = snapshot.getValue(Comment.class);
                    listComments.add(comment);
                }

                commentAdapter = new CommentAdapter(getApplicationContext(),listComments);
                RvComment.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String timeStampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;
    }

}
