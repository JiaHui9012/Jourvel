package com.example.mdpcwjourvel;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.mdpcwjourvel.adapters.AdapterPost;
import com.example.mdpcwjourvel.models.ModelPost;
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
public class SearchFragment extends Fragment {

    RelativeLayout content;
    SearchView searchView;
    TextView textView;

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceUsers;

    //user info
    String name, username, bio, image, cover;

    RecyclerView recyclerView;
    List<ModelPost> postList;
    AdapterPost adapterPost;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);

        content = view.findViewById(R.id.searchContent);
        searchView = view.findViewById(R.id.searchBar);

        //init
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceUsers = firebaseDatabase.getReference("Users");

        //recycler view and itd properties
        recyclerView = view.findViewById(R.id.searchRecycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //show newest post first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set layout to recycler view
        recyclerView.setLayoutManager(layoutManager);

        //init post list
        postList = new ArrayList<>();

        //default showing recommended posts/articles/...
        defaultSearchPage();

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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query)) {
                    content.removeView(textView);
                    startSearch(query);
                }
                else {
                    defaultSearchPage();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText)) {
                    content.removeView(textView);
                    startSearch(newText);
                }
                else {
                    defaultSearchPage();
                }
                return false;
            }
        });

        return view;
    }

    private void defaultSearchPage() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT
        );
        textView = new TextView(getActivity());
        textView.setText("Recommended posts/articles/tours...");
        textView.setTextSize(30);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setPadding(0,500,0,0);
        textView.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
        content.addView(textView, layoutParams);
    }

    private void startSearch(String query) {
        //path of all posts
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        //get all data from this reference
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    if(modelPost.getUid().equals(user.getUid())){
                        if(modelPost.getName()!=name){
                            modelPost.setName(name);
                        }
                        if(modelPost.getProfilePic()!=image){
                            modelPost.setProfilePic(image);
                        }
                    }
                    if(modelPost.getPostText().toLowerCase().contains(query.toLowerCase())) {
                        postList.add(modelPost);
                    }
                    //adapter
                    adapterPost = new AdapterPost(getActivity(), postList);
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //if error
            }
        });
    }

    /*private void loadPosts() {
        //path of all posts
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        //get all data from this reference
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    if(modelPost.getUid().equals(user.getUid())){
                        if(modelPost.getName()!=name){
                            modelPost.setName(name);
                        }
                        if(modelPost.getProfilePic()!=image){
                            modelPost.setProfilePic(image);
                        }
                    }
                    postList.add(modelPost);
                    //adapter
                    adapterPost = new AdapterPost(getActivity(), postList);
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //if error
                //Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

}