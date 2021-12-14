package com.example.mdpcwjourvel;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mdpcwjourvel.adapters.AdapterPlan;
import com.example.mdpcwjourvel.models.ModelPlan;
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
public class PlanFragment extends Fragment {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceUsers;

    //user info
    String name, username, bio, image, cover;

    RecyclerView recyclerView;
    List<ModelPlan> planList;
    AdapterPlan adapterPlan;

    public PlanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceUsers = firebaseDatabase.getReference("Users");

        //recycler view and itd properties
        recyclerView = view.findViewById(R.id.myPlanRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //show newest plan first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set layout to recycler view
        recyclerView.setLayoutManager(layoutManager);

        //init plan list
        planList = new ArrayList<>();

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

        loadPlans();

        return view;

    }

    private void loadPlans() {
        //path of all plans
        DatabaseReference databaseReferencePosts = FirebaseDatabase.getInstance().getReference("Plans");
        //get all data from this reference
        databaseReferencePosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                planList.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    ModelPlan modelPlan = ds.getValue(ModelPlan.class);
                    if(modelPlan.getUid().equals(user.getUid())){
                        planList.add(modelPlan);
                        //adapter
                        adapterPlan = new AdapterPlan(getActivity(), planList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                        //set adapter to recycler view
                        recyclerView.setAdapter(adapterPlan);
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