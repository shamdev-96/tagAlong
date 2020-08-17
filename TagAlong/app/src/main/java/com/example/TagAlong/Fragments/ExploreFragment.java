package com.example.TagAlong.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ListView;


import com.example.TagAlong.FindMatch;
import com.example.TagAlong.ListAdapter.PlaceAdapter;
import com.example.TagAlong.Models.MatchedUsers;
import com.example.TagAlong.OperationHelper.DbOperation;
import com.example.TagAlong.Models.Place;
import com.example.TagAlong.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FusedLocationProviderClient fusedLocationClient;
    private ListView placeList;
    private ArrayList<Place> places = new ArrayList<>();
    private FirebaseAuth mAuth;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private  PlaceAdapter placeAdapter;

    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExploreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExploreFragment newInstance(String param1, String param2) {

        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FirebaseFirestore db = DbOperation.getInstance().db;
        mAuth = FirebaseAuth.getInstance();
        getAllPlace(db);
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        placeList = rootView.findViewById(R.id.simpleListView);
        return  rootView;
    }

    public void getAllPlace( FirebaseFirestore db) {
             db.collection("places")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                            for (DocumentSnapshot document :  myListOfDocuments) {
                                Map<String, Object> placeMap = document.getData();
                                Place place = new Place(placeMap);
                                Log.d("data", "Cached document data: " + place.PlaceDescription);
                                places.add(place);
                            }

                            placeAdapter = new PlaceAdapter(getActivity() , places);
                            placeList.setAdapter(placeAdapter);

                            placeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    PlaceAdapter adapter = (PlaceAdapter) parent.getAdapter();
                                    Place selectedPlace = adapter.placeList.get(position);

                                    SavedVisitedUserIntoFirestore(selectedPlace);

                                    Intent intent = new Intent(getActivity() , FindMatch.class);
                                    intent.putExtra("placeName" , selectedPlace.PlaceName);
                                    startActivity(intent);
//
                                }
                            });
                        }
                    }
                });
    }

    public void SavedVisitedUserIntoFirestore(final Place selectedPlace)
    {
        DocumentReference docRef =  DbOperation.getInstance().db.collection("matchedUsers").document(selectedPlace.PlaceName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            FirebaseFirestore db = DbOperation.getInstance().db;

            final ArrayList<String> matchedUsers = new ArrayList<>();

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> map = document.getData();
                        MatchedUsers user = new MatchedUsers(map);

                        for ( String perUser : user.Users) {
                            //if currentUser already in the list, no need to add
                            String currentUserId = mAuth.getUid();
                            if(!(""+perUser+"").equals(currentUserId))
                            {
                                matchedUsers.add(perUser);
                            }

                        }
                    }

                    matchedUsers.add(mAuth.getUid());

                    Map<String, Object> docData = new HashMap<>();
                    docData.put("users" , matchedUsers);

                    db.collection("matchedUsers").document(selectedPlace.PlaceName).set(docData);
                }
            }
        });

    }
}