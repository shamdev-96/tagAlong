package com.example.TagAlong.ListAdapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.TagAlong.Models.Profile;
import com.example.TagAlong.OperationHelper.DistanceOperation;
import com.example.TagAlong.Models.Place;
import com.example.TagAlong.R;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends BaseAdapter {
    Context context;
    public List<Place> placeList;
    double distance[];
    LayoutInflater inflater;

    public PlaceAdapter(Context applicationContext, List<Place> placeList) {
        this.context = context;
        this.placeList = placeList;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return placeList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.place_list, null);

        TextView name = (TextView) view.findViewById(R.id.place_Name);
        TextView description = (TextView) view.findViewById(R.id.place_Description);
        TextView distance =  (TextView) view.findViewById(R.id.place_Distance);
        TextView fee = (TextView) view.findViewById(R.id.place_Fee);
        ImageView image = (ImageView) view.findViewById(R.id.place_Image);

        name.setText(placeList.get(i).PlaceName);
        description.setText( placeList.get(i).PlaceDescription);
//        description.setSingleLine(true);
//        description.setTop(90);
//        description.setEllipsize(TextUtils.TruncateAt.END);
        Location placeLocation  = new Location("");
        placeLocation.setLatitude(placeList.get(i).PlaceLocation.getLatitude());
        placeLocation.setLongitude(placeList.get(i).PlaceLocation.getLongitude());

        float placeDistance = DistanceOperation.getInstance().CalculateDistance(placeLocation);
        String formattedString = String.format("%.1f", placeDistance);
        distance.setText(formattedString + "km");
        fee.setText(placeList.get(i).PlaceFee);

        try {
           URL url = new URL(placeList.get(i).PlaceImageUrl);
            Picasso.get().load(String.valueOf(url)).resize(150,120).into(image);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //distance.setText(distance.toString());
        return view;
    }
}
