package com.kenzohenri.chatapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenzohenri.chatapp.Model.GroupChat;
import com.kenzohenri.chatapp.R;

import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<GroupChat> mChat;

    FirebaseUser fuser;
    DatabaseReference reference;
    String currentUserID;
    String currentUserName;

    public GroupChatAdapter(Context mContext, List<GroupChat> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @NonNull
    @Override
    public GroupChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.groupchat_item_right,parent,false);
            return new GroupChatAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.groupchat_item_left,parent,false);
            return new GroupChatAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatAdapter.ViewHolder holder, int position) {

        GroupChat chat = mChat.get(position);
        GetUserInfo();
        if(mChat.get(position).getName().equals(currentUserName)){
            holder.show_message.setText(chat.getMessage());
            holder.show_datetime.setText(chat.getDate());
        }else{
            holder.show_message.setText(chat.getMessage());
            holder.show_datetime.setText(chat.getDate());
            holder.show_username.setText(chat.getName());
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public TextView show_username;
        public TextView show_datetime;

        public ViewHolder(View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_groupmessage);
            show_username = itemView.findViewById(R.id.show_name);
            show_datetime = itemView.findViewById(R.id.txt_datetime);
        }
    }

    private void GetUserInfo()
    {
        reference.child(currentUserID).addValueEventListener(new ValueEventListener() {
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

    @Override
    public int getItemViewType(int position) {
        GetUserInfo();
        if(mChat.get(position).getName().equals(currentUserName)){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }

}
