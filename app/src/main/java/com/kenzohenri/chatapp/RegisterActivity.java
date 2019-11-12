package com.kenzohenri.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText username, email, password;
    Button btn_register;

    FirebaseAuth auth;
    DatabaseReference reference;
    CheckBox checkBoxView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set the checkBox

        checkBoxView = findViewById(R.id.checkBoxView);
        String checkBoxText = "I agree to all the <a href='com.kenzohenri.chatapp.Fragments.TermsFragment://Kode' > Terms and Conditions</a>";
        checkBoxView.setText(Html.fromHtml(checkBoxText));
        checkBoxView.setMovementMethod(LinkMovementMethod.getInstance());
        checkBoxView.setVisibility(View.INVISIBLE);


        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }else if (txt_password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }else {
                    register(txt_username,txt_email,txt_password);
                }
            }
        });
    }

    private void register(final String username, String email, String password){

        // Check if admin is logged in


        FirebaseUser user = auth.getCurrentUser();
        if(user!= null && user.getEmail().equals("admin@chatapp.com")){
            registerDoctor(username,email,password);
        }else{
            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                final FirebaseUser firebaseUser = auth.getCurrentUser();
                                assert firebaseUser != null;
                                firebaseUser.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(RegisterActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                                                    auth.getCurrentUser().reload();
                                                    String userid = firebaseUser.getUid();

                                                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                                                    HashMap<String, String> hashMap = new HashMap<>();
                                                    hashMap.put("id",userid);
                                                    hashMap.put("username", username);
                                                    hashMap.put("imageURL", "default");
                                                    hashMap.put("status", "offline");
                                                    hashMap.put("search", username.toLowerCase());
                                                    hashMap.put("type","patient");
                                                    hashMap.put("description","");

                                                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }
                                                    });
                                                }else{
                                                    Log.e("info",task.getException().getMessage());
                                                    Toast.makeText(RegisterActivity.this, "Failed to verify email", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }else{
                                Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void registerDoctor(final String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            final FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            firebaseUser.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                                                auth.getCurrentUser().reload();
                                                String userid= firebaseUser.getUid();

                                                reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                                                HashMap<String, String> hashMap = new HashMap<>();
                                                hashMap.put("id",userid);
                                                hashMap.put("username", username);
                                                hashMap.put("imageURL", "default");
                                                hashMap.put("status", "offline");
                                                hashMap.put("search", username.toLowerCase());
                                                hashMap.put("type","doctor");
                                                hashMap.put("description","");

                                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
