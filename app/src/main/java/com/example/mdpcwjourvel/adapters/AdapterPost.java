package com.example.mdpcwjourvel.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdpcwjourvel.R;
import com.example.mdpcwjourvel.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder> {

    Context context;
    List<ModelPost> postList;

    String myUid;

    public AdapterPost (Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String uid = postList.get(position).getUid();
        String email = postList.get(position).getEmail();
        String name = postList.get(position).getName();
        String username = postList.get(position).getUsername();
        String profilePic = postList.get(position).getProfilePic();
        String postID = postList.get(position).getPostID();
        String postText = postList.get(position).getPostText();
        String postImage = postList.get(position).getPostImage();
        String postTime = postList.get(position).getPostTime();

        //convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(postTime));
        String pTime = DateFormat.format("dd/mm/yyyy hh:mm aa", calendar).toString();

        //set data
        holder.username.setText(username);
        holder.postTime.setText(pTime);
        holder.postText.setText(postText);

        //set user profile pic
        try{
            Picasso.get().load(profilePic).placeholder(R.drawable.ic_user_black).into(holder.profilePic);
        }
        catch (Exception e) {

        }

        //set post image
        //if there is no image, then hide image view
        if(postImage.equals("noImage")) {
            //hide image view
            holder.postImage.setVisibility(View.GONE);
        }
        else {
            holder.postImage.setVisibility(View.VISIBLE);
            try{
                Picasso.get().load(postImage).into(holder.postImage);
            }
            catch (Exception e) {
            }
        }

        //handle button
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                showMoreOptions(holder.moreBtn, uid, myUid, postID, postImage);
            }
        });
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
            }
        });
        holder.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show();
            }
        });
        holder.addToPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Add to plan", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showMoreOptions(ImageView moreBtn, String uid, String myUid, String postID, String postImage) {
        //creating popup menu
        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);
        if(uid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id==0) {
                    //delete
                    beginDelete(postID, postImage);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void beginDelete(String postID, String postImage) {
        if(postImage.equals("noImage")){
            deleteWithoutImage(postID);
        }
        else {
            deleteWithImage(postID, postImage);
        }
    }

    private void deleteWithImage(String postID, String postImage) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(postImage);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("postID").equalTo(postID);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds:snapshot.getChildren()) {
                                    ds.getRef().removeValue();
                                }
                                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteWithoutImage(String postID) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");
        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("postID").equalTo(postID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {
        //views from row_post.xml
        //uid, name, email, profilePic, username, postID, postText, postImage, postTime;
        ImageView profilePic, postImage, moreBtn, likeBtn, commentBtn, saveBtn, addToPlanBtn;
        TextView username, postTime, postText;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            //init view
            profilePic = itemView.findViewById(R.id.user_pic_post);
            postImage = itemView.findViewById(R.id.post_image);
            moreBtn = itemView.findViewById(R.id.moreButton);
            likeBtn = itemView.findViewById(R.id.likeButton);
            commentBtn = itemView.findViewById(R.id.commentButton);
            saveBtn = itemView.findViewById(R.id.saveButton);
            addToPlanBtn = itemView.findViewById(R.id.addToPlanButton);
            username = itemView.findViewById(R.id.username_post_TextView);
            postTime = itemView.findViewById(R.id.time_post);
            postText = itemView.findViewById(R.id.post_content);

        }

    }

}
