package com.nongkiyuk.nongkiyuk.activities.Favorite.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nongkiyuk.nongkiyuk.R;
import com.nongkiyuk.nongkiyuk.activities.Favorite.Models.Place;
import com.nongkiyuk.nongkiyuk.activities.Home.PlaceDetail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Place> places;

    public FavoriteAdapter(Context context, ArrayList<Place> places) {
        this.places = places;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_place, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        TextView textView = viewHolder.textView;
        ImageView coverImg = viewHolder.coverImg;
        RelativeLayout itemPlace = viewHolder.itemPlace;

        textView.setText(places.get(i).getName());
        Picasso.get().load(places.get(i).getCoverUrl()).into(coverImg);

        itemPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlaceDetail.class);
                intent.putExtra("favorite", true);
                intent.putExtra("place", places.get(i));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView coverImg;
        RelativeLayout itemPlace;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.textView = (TextView)itemView.findViewById(R.id.name);
            this.coverImg = (ImageView)itemView.findViewById(R.id.coverImg);
            this.itemPlace = (RelativeLayout)itemView.findViewById(R.id.item_place);
        }
    }
}
