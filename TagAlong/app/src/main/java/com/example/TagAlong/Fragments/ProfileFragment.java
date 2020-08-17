package com.example.TagAlong.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.TagAlong.EditProfile;
import com.example.TagAlong.Login_Signup_Page;
import com.example.TagAlong.MyFriendListPage;
import com.example.TagAlong.NoFriendList;
import com.example.TagAlong.OperationHelper.DbOperation;
import com.example.TagAlong.OperationHelper.FriendListOperation;
import com.example.TagAlong.OperationHelper.ProfileOperation;
import com.example.TagAlong.Models.Profile;
import com.example.TagAlong.ProfileDataListener;
import com.example.TagAlong.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    private int friendCount;

    private Button logoutButton;
    private  Button EditButton;
    private ImageView friendList;

    private TextView my_username;
    private TextView my_age;
    private TextView my_location;
    private TextView my_gender;
    private TextView my_about;
    private  TextView my_friend;
    private ImageView my_profileImage;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        logoutButton = rootView.findViewById(R.id._logoutBtn);
        EditButton = rootView.findViewById(R.id.editButton);

        my_username = rootView.findViewById(R.id._username) ;
        my_age = rootView.findViewById(R.id._age) ;
        my_location = rootView.findViewById(R.id._location) ;
        my_gender = rootView.findViewById(R.id._gender) ;
        my_about = rootView.findViewById(R.id._about) ;
        my_profileImage = rootView.findViewById(R.id.myProfileImage);
        my_friend = rootView.findViewById(R.id.friendNumber);
        friendList = rootView.findViewById(R.id.myFriendList);

        friendCount = FriendListOperation.Instance().GetCountMyFriendList();

        if(friendCount > 1)
        {
            my_friend.setText( friendCount + " friends");
        }

        else
        {
            my_friend.setText( friendCount + " friend");
        }

        FirebaseFirestore db = DbOperation.getInstance().db;


        getProfileDataFromFirestore(db);

        logoutButton.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {

                                             progressDialog.setMessage("Logging out");
                                             progressDialog.show();
                                             mAuth.getUid();
                                             mAuth.signOut();
                                             progressDialog.dismiss();

                                             if(FriendListOperation.Instance().matchUsersId.size() > 0)
                                             {
                                                 FriendListOperation.Instance().matchUsersId.clear();
                                             }

                                             if(FriendListOperation.Instance().matchProfileImage.size() > 0)
                                             {
                                                 FriendListOperation.Instance().matchProfileImage.clear();
                                             }

                                             ProfileOperation.getInstance().myProfile = null;
                                             Intent intent = new Intent(getActivity() , Login_Signup_Page.class);
                                             startActivity(intent);
                                             getActivity().finish();
                                         }
                                     });

        EditButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Intent intent=new Intent(getActivity(), EditProfile.class);
                                                startActivity(intent);
                                            }
                                        }

        );

        friendList.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              int friendCount = FriendListOperation.Instance().GetCountMyFriendList();
                                              if(friendCount == 0)
                                              {
                                                  Intent intent=new Intent(getActivity(), NoFriendList.class);
                                                  startActivity(intent);
                                              }
                                              else
                                              {
                                                  Intent intent=new Intent(getActivity(), MyFriendListPage.class);
                                                  startActivity(intent);
                                              }
                                          }
                                      }

        );

        return rootView;
    }


    public void getProfileDataFromFirestore(FirebaseFirestore db) {
        String userId = mAuth.getUid();
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> placeMap = document.getData();
                        Profile matchProfile = new Profile(placeMap);
                        UpdateViewWithData(matchProfile);
                    }
                }
            }
        });
    }

    public void UpdateViewWithData(Profile matchProfile) {

        my_username.setHint(matchProfile.Name);
        my_gender.setHint(matchProfile.Gender);
        my_age.setHint(String.valueOf(matchProfile.Age));
        my_location.setHint(matchProfile.LocationName);
        my_about.setHint(matchProfile.About);
        Picasso.get().load(matchProfile.ImageUri).resize(110,100).into(my_profileImage);

        ProfileOperation.getInstance().SaveAllProfileData(matchProfile);
    }

}