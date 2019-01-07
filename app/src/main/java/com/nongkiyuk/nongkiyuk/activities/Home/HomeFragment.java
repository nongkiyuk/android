package com.nongkiyuk.nongkiyuk.activities.Home;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nongkiyuk.nongkiyuk.R;
import com.nongkiyuk.nongkiyuk.activities.Main.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_home, container, false);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        GridView gridView = (GridView)rootView.findViewById(R.id.tv_home);
        gridView.setAdapter(new MyAdapter(getContext()));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent intent = new Intent(getContext(), PlaceDetail.class);
                // Show the item position using toast
               /* Toast.makeText(MainActivity.this, "Position " + i,
                        Toast.LENGTH_SHORT).show();*/

                // Send captured position to ViewImage.java
                intent.putExtra("id", i);
                startActivity(intent);
            }
        });

        return rootView;
    }


    public static class MyAdapter extends BaseAdapter
    {
        public static List<Item> items = new ArrayList<Item>();
        public LayoutInflater inflater;


        public MyAdapter(Context context)
        {
            inflater = LayoutInflater.from(context);

            items.add(new Item("Cafe Kimcil", R.drawable.epic3, "Ini deskripsi lho.."));
            items.add(new Item("Image 2", R.drawable.epic, "Ini deskripsi lagi lho.."));
            items.add(new Item("Image 3", R.drawable.epic2, "Ini deskripsi lho.."));
            items.add(new Item("Image 4", R.drawable.epic, "Ini deskripsi lho.."));
            items.add(new Item("Image 5", R.drawable.epic3, "Ini deskripsi lho.."));
            items.add(new Item("Image 1", R.drawable.epic3, "Ini deskripsi lho.."));
            items.add(new Item("Image 2", R.drawable.epic, "Ini deskripsi lho.."));
            items.add(new Item("Image 3", R.drawable.epic2, "Ini deskripsi lho.."));
            items.add(new Item("Image 4", R.drawable.epic, "Ini deskripsi lho.."));
            items.add(new Item("Image 5", R.drawable.epic3,"Ini deskripsi lho.."));
        }

        @Override
        public int getCount() {

            return items.size();
        }

        @Override
        public Object getItem(int i)
        {

            return items.get(i);
        }

        @Override
        public long getItemId(int i)
        {

            return items.get(i).drawableId;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            View v = view;
            ImageView picture;
            TextView name, desc;

            if(v == null)
            {
                v = inflater.inflate(R.layout.item_place, viewGroup, false);
                v.setTag(R.id.coverImg, v.findViewById(R.id.coverImg));
                v.setTag(R.id.text, v.findViewById(R.id.name));
                v.setTag(R.id.desc, v.findViewById(R.id.desc));
            }

            picture = (ImageView)v.getTag(R.id.coverImg);
            name = (TextView)v.getTag(R.id.text);


            Item item = (Item)getItem(i);

            picture.setImageResource(item.drawableId);
            name.setText(item.name);


            return v;
        }

        public class Item
        {
            final String name;
            final String desc;
            final int drawableId;

            Item(String name, int drawableId, String desc)
            {
                this.name = name;
                this.drawableId = drawableId;
                this.desc = desc;
            }
        }
    }

}

