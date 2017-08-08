package com.udacity.jevonaverill.udacitypopularmoviesstage2.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.udacity.jevonaverill.udacitypopularmoviesstage2.R;
import com.udacity.jevonaverill.udacitypopularmoviesstage2.model.Trailer;

import java.util.ArrayList;

/**
 * Created by jevon.averill on 8/8/2017.
 */

public class TrailerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Trailer> trailerList;

    public TrailerAdapter(Context context){
        this.context = context;
        this.trailerList = new ArrayList<>();
    }

    private void clear(){
        trailerList.clear();
        notifyDataSetChanged();
    }

    public void setTrailers(ArrayList<Trailer> trailers){
        clear();
        trailerList.addAll(trailers);
        notifyDataSetChanged();
    }

    public Uri getTrailerUri(int position){
        Trailer trailer = getItem(position);
        if (trailer!=null){
            return Uri.parse(trailer.url);
        }
        return null;
    }

    @Override
    public int getCount() {
        return trailerList.size();
    }

    @Override
    public Trailer getItem(int position) {
        if (position>=0 && position < trailerList.size()){
            return trailerList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (getItem(position) == null){
            return -1L;
        }
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View trailerItem = convertView;
        Trailer trailer = getItem(position);
        if (trailerItem == null) {
            try {
                LayoutInflater layoutInflater;
                layoutInflater = LayoutInflater.from(context);
                trailerItem = layoutInflater.inflate(R.layout.trailer_list_item,parent,false);
            } catch (Exception ex){
                Log.e(context.getClass().getSimpleName(), ex.toString());
            }
        }
        ((TextView) trailerItem.findViewById(R.id.tvTrailer)).setText(trailer.title);
        return trailerItem;
    }

}
