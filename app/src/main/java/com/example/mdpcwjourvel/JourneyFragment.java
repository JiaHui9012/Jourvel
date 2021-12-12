package com.example.mdpcwjourvel;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mdpcwjourvel.adapters.AdapterJourney;
import com.example.mdpcwjourvel.models.ModelJourney;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class JourneyFragment extends Fragment {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceUsers;

    //user info
    String name, username, bio, image, cover;

    RecyclerView recyclerView;
    List<ModelJourney> journeyList;
    AdapterJourney adapterJourney;

    public JourneyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_journey, container, false);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceUsers = firebaseDatabase.getReference("Users");

        //recycler view and itd properties
        recyclerView = view.findViewById(R.id.myJourneyRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //show newest journey first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set layout to recycler view
        recyclerView.setLayoutManager(layoutManager);

        //init journey list
        journeyList = new ArrayList<>();

        //get current user info from database
        Query query =  databaseReferenceUsers.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check until required data get
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    name = "" + ds.child("name").getValue();
                    bio = "" + ds.child("bio").getValue();
                    image = "" + ds.child("image").getValue();
                    cover = "" + ds.child("cover").getValue();
                    username = "" + ds.child("username").getValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        loadJourneys();

        return view;

    }

    private void loadJourneys() {
        //path of all journeys
        DatabaseReference databaseReferencePosts = FirebaseDatabase.getInstance().getReference("Journeys");
        //get all data from this reference
        databaseReferencePosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                journeyList.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    ModelJourney modelJourney = ds.getValue(ModelJourney.class);
                    if(modelJourney.getUid().equals(user.getUid())){
                        journeyList.add(modelJourney);
                        //adapter
                        adapterJourney = new AdapterJourney(getActivity(), journeyList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                        //set adapter to recycler view
                        recyclerView.setAdapter(adapterJourney);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //if error
            }
        });
    }
}