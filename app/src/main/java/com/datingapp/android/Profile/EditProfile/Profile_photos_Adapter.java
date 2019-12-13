package com.datingapp.android.Profile.EditProfile;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.datingapp.android.R;
import com.squareup.picasso.Picasso;
import com.wonshinhyo.dragrecyclerview.DragAdapter;
import com.wonshinhyo.dragrecyclerview.DragHolder;
import com.wonshinhyo.dragrecyclerview.DragRecyclerView;

import java.util.ArrayList;

/**
 * Created by AQEEL on 7/16/2018.
 */

public class Profile_photos_Adapter
        extends DragAdapter {
    Context context;

    ArrayList<String> photos;

    private Profile_photos_Adapter.OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(String item, int postion, View view);
    }


    public Profile_photos_Adapter(Context context, ArrayList<String> arrayList, Profile_photos_Adapter.OnItemClickListener listener)  {
        super(context,arrayList);
        this.context=context;
        photos=arrayList;
        this.listener=listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {

        return new HistoryviewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_edit_profile_layout, viewGroup, false));


    }



    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public void onBindViewHolder(final DragRecyclerView.ViewHolder hol, final int position) {
        super.onBindViewHolder(hol, position);
        HistoryviewHolder holder = (HistoryviewHolder) hol;
        holder.bind(photos.get(position),position,listener);
        if(position==0){
            holder.crossbtn.setVisibility(View.GONE);
        }else {
            holder.crossbtn.setVisibility(View.VISIBLE);
        }
        if(photos.get(position).equals("")){
            holder.crossbtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_round_add_btn));
            Picasso.with(context).load("null").placeholder(R.drawable.image_placeholder).centerCrop().resize(200,300).into(holder.image);

        }else {
            holder.crossbtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cross));
            Picasso.with(context).load(photos.get(position)).placeholder(R.drawable.image_placeholder).centerCrop().resize(200,300).into(holder.image);
        }
     }
    /**
     * Inner Class for a recycler view
     */
    class HistoryviewHolder extends DragHolder {
        View view;
        ImageView image;
        ImageButton crossbtn;
        public HistoryviewHolder(View itemView) {
            super(itemView);
            view = itemView;
            image=view.findViewById(R.id.image);
            crossbtn=view.findViewById(R.id.cross_btn);
        }


        public void bind(final String item, final int position , final Profile_photos_Adapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item,position,v);
                }
            });

            crossbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item,position,v);
                }
            });
        }


    }

}

