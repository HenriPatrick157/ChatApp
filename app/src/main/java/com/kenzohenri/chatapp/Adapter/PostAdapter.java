package com.kenzohenri.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kenzohenri.chatapp.Model.Post;
import com.kenzohenri.chatapp.PostDetailsActivity;
import com.kenzohenri.chatapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    Context mContext;
    List<Post> mData;

    public PostAdapter(Context mContext, List<Post> mData){
        this.mContext = mContext;
        this.mData = mData;
    }


    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item,parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.MyViewHolder holder, int position) {
        holder.tvTitle.setText(mData.get(position).getTitle());
        holder.tvDescription.setText(mData.get(position).getDescription());
        Glide.with(mContext).load(mData.get(position).getUserphoto()).into(holder.imgPostProfile);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        TextView tvDescription;
        CircleImageView imgPostProfile;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_post_title);
            tvDescription = itemView.findViewById(R.id.row_post_text);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postDetailActivity = new Intent(mContext, PostDetailsActivity.class);
                    int position = getAdapterPosition();

                    postDetailActivity.putExtra("title",mData.get(position).getTitle());
                    postDetailActivity.putExtra("description",mData.get(position).getDescription());
                    postDetailActivity.putExtra("postKey",mData.get(position).getPostKey());
                    postDetailActivity.putExtra("userPhoto",mData.get(position).getUserphoto());
                    postDetailActivity.putExtra("username",mData.get(position).getUsername());
                    postDetailActivity.putExtra("search",mData.get(position).getSearch());
                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate",timestamp);
                    mContext.startActivity(postDetailActivity);
                }
            });



        }
    }
}
