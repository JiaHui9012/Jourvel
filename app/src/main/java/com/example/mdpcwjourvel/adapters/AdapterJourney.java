package com.example.mdpcwjourvel.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mdpcwjourvel.R;
import com.example.mdpcwjourvel.models.ModelJourney;
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

import java.util.List;

public class AdapterJourney extends RecyclerView.Adapter<AdapterJourney.MyHolder> {

    Context context;
    List<ModelJourney> journeyList;

    String myUid;

    public AdapterJourney(Context context, List<ModelJourney> journeyList) {
        this.context = context;
        this.journeyList = journeyList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_journeys, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String uid = journeyList.get(position).getUid();
        String email = journeyList.get(position).getEmail();
        String name = journeyList.get(position).getName();
        String username = journeyList.get(position).getUsername();
        String journeyID = journeyList.get(position).getJourneyID();
        String journey = journeyList.get(position).getJourney();
        String uploadTime = journeyList.get(position).getUploadTime();

        try{
            Glide.with(context).load(journey).into(holder.journey);
            //Picasso.get().load(journey).into(holder.journey);
        }
        catch (Exception e) {
        }

        //handle button
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                showMoreOptions(holder.moreBtn, uid, myUid, journeyID, journey);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showMoreOptions(ImageView moreBtn, String uid, String myUid, String journeyID, String journey) {
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
                    beginDelete(journeyID, journey);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void beginDelete(String journeyID, String journey) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(journey);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Query query = FirebaseDatabase.getInstance().getReference("Journeys").orderByChild("journeyID").equalTo(journeyID);
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

    @Override
    public int getItemCount() {
        return journeyList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {
        ImageView journey, moreBtn;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            //init view
            journey = itemView.findViewById(R.id.journeyContent);
            moreBtn = itemView.findViewById(R.id.moreButton);

        }

    }

}
