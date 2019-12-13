package com.datingapp.android.Chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.datingapp.android.R;

import java.util.ArrayList;

public class PhotoAdapter extends BaseAdapter {
    private Context ctx;
    private final ArrayList<String> filesPaths;

    public PhotoAdapter(Context ctx, ArrayList<String> filesPaths) {
        this.ctx = ctx;
        this.filesPaths = filesPaths;
    }

    @Override
    public int getCount() {
        return filesPaths.size();
    }

    @Override
    public Object getItem(int pos) {
        return pos;
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int p, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = inflater.inflate(R.layout.gridview_item, null);

            ImageView imageView = (ImageView)grid.findViewById(R.id.gridview_image);

            Bitmap bmp = BitmapFactory.decodeFile(filesPaths.get(p));
            imageView.setImageBitmap(bmp);
        } else {
            grid = (View) convertView;
        }
        return grid;
    }
}