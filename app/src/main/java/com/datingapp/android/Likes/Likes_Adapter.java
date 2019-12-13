package com.datingapp.android.Likes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.datingapp.android.Main_Menu.MainMenuActivity;
import com.datingapp.android.R;
import com.datingapp.android.Users.Nearby_User_Get_Set;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Likes_Adapter extends RecyclerView.Adapter<Likes_Adapter.MyLikeViewHolder> {
    ArrayList<Nearby_User_Get_Set> userList;
    Context context;
    OnSelectedLikeItemListner listner;
    public Boolean maskable;

    public Likes_Adapter(Context context, ArrayList<Nearby_User_Get_Set> list, OnSelectedLikeItemListner likeItemListner) {
        this.context = context;
        this.userList = list;
        this.listner = likeItemListner;
    }
    @Override
    public MyLikeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.like_item_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyLikeViewHolder vh = new MyLikeViewHolder(v); // pass the view to View Holder
        return vh;
    }

    public void updateList(ArrayList<Nearby_User_Get_Set> updates) {
        this.userList = (ArrayList<Nearby_User_Get_Set>) updates.clone();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MyLikeViewHolder holder, final int position) {
        // set the data in items
        Nearby_User_Get_Set item = userList.get(position);
        holder.nameView.setText(item.getFirst_name());
        holder.ageView.setText(item.getBirthday());
        for (String path : item.getImagesurl()) {
            if (!path.isEmpty()) {
                Picasso.with(context).load(path)
                        .resize(200, 200)
                        .placeholder(R.drawable.profile_image_placeholder)
                        .centerCrop()
                        .into(holder.imageView);
                break;
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listner != null) {
                    listner.OnSelectedLikeItem(userList.get(position));
                }
            }
        });

        if (MainMenuActivity.purduct_purchase)
            holder.maskView.setVisibility(View.GONE);
        else
            holder.maskView.setVisibility(View.VISIBLE);

    }
    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyLikeViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView ageView;
        TextView nameView;
        ImageView imageView;
        RelativeLayout maskView;

        public MyLikeViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            nameView = (TextView) itemView.findViewById(R.id.nameTxt);
            ageView = (TextView) itemView.findViewById(R.id.ageTxt);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            maskView = itemView.findViewById(R.id.maskView);
        }
    }
    public static interface OnSelectedLikeItemListner{
        public void OnSelectedLikeItem(Nearby_User_Get_Set data);
    }

}
