package com.kenzohenri.chatapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kenzohenri.chatapp.Adapter.GroupChatAdapter;
import com.kenzohenri.chatapp.Adapter.MessageAdapter;
import com.kenzohenri.chatapp.Model.Chat;
import com.kenzohenri.chatapp.Model.GroupChat;
import com.kenzohenri.chatapp.Model.User;
import com.kenzohenri.chatapp.Notifications.Data;
import com.kenzohenri.chatapp.Notifications.MyResponse;
import com.kenzohenri.chatapp.Notifications.Sender;
import com.kenzohenri.chatapp.Notifications.Token;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupChatActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageButton btn_send;
    private EditText text_send;


    private ScrollView mScrollView;
    private TextView displayTextMessages;

    GroupChatAdapter groupChatAdapter;
    List<GroupChat> mChat;

    RecyclerView recyclerView;


    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, GroupRef, GroupMessageKeyRef;

    private String currentGroupName, currentUserID, currentUserName, currentDate, currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);



        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupRef = FirebaseDatabase.getInstance().getReference().child("GroupChat");



        InitializeFields();


        GetUserInfo();


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SaveMessageInfoToDatabase();

                text_send.setText("");

                //mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        DisplayMessages();
    }

    private void InitializeFields()
    {
        mToolbar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        btn_send = (ImageButton) findViewById(R.id.btn_send);
        text_send = (EditText) findViewById(R.id.text_send);
        //displayTextMessages = (TextView) findViewById(R.id.group_chat_text_display);
        //mScrollView = (ScrollView) findViewById(R.id.my_scroll_view);
    }



    private void GetUserInfo()
    {
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    currentUserName = dataSnapshot.child("username").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void SaveMessageInfoToDatabase()
    {
        String message = text_send.getText().toString();
        //String messagekEY = GroupNameRef.push().getKey();

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Please write message first...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());


            /*HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            GroupMessageKeyRef = GroupNameRef.child(messagekEY);*/
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            //GroupMessageKeyRef.updateChildren(messageInfoMap);
            reference.child("GroupChat").push().setValue(messageInfoMap);
        }
    }

    private void DisplayMessages()
    {
        mChat = new ArrayList<>();
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    GroupChat chat = snapshot.getValue(GroupChat.class);
                    mChat.add(chat);
                    groupChatAdapter = new GroupChatAdapter(GroupChatActivity.this, mChat);
                    recyclerView.setAdapter(groupChatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        //displayTextMessages.append(chatName + " :\n" + chatMessage + "\n" + chatTime + "     " + chatDate + "\n\n\n");
        //mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }
}
