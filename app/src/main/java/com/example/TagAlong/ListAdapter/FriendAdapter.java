package com.example.TagAlong.ListAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.TagAlong.Models.Profile;
import com.example.TagAlong.OperationHelper.DistanceOperation;
import com.example.TagAlong.Models.Place;
import com.example.TagAlong.OperationHelper.FriendListOperation;
import com.example.TagAlong.R;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends BaseAdapter {
    Context context;
    public List<Profile> friendList;
    LayoutInflater inflater;

    public FriendAdapter(Context applicationContext, ArrayList<Profile> friendList) {
        this.context = context;
        this.friendList = friendList;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.friend_list, null);

        TextView name = (TextView) view.findViewById(R.id.friendName);
        TextView meetup = (TextView) view.findViewById(R.id.friendMeetup);
        TextView details =  (TextView) view.findViewById(R.id.friendDetails);
        ImageView image = (ImageView) view.findViewById(R.id.friendImage);

        String userId = friendList.get(i).UserId;
        Uri uri = FriendListOperation.Instance().GetImageUri(userId);

        name.setText("" + friendList.get(i).Name + ","  + friendList.get(i).Age);
        meetup.setText( friendList.get(i).LocationName);
        details.setHint(friendList.get(i).About);
        Picasso.get().load(uri).resize(150,130).into(image);

        return view;
    }
}
