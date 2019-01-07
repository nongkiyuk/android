package com.nongkiyuk.nongkiyuk.activities.Home.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageAdapter extends PagerAdapter {
    private Context mContext;
    private String imageUrls[];

    public ImageAdapter(Context mContext, String[] imageUrls) {
        this.mContext = mContext;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return imageUrls.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        Picasso.get()
                .load(imageUrls[position])
//                .fit()
//                .centerCrop()
                .into(imageView);
        container.addView(imageView);

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
