package com.example.mdpcwjourvel;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceUsers;
    //storage
    StorageReference storageReference;
    //path where images of user profile and cover will be stored
    String storagePath = "Users_Profile_Cover_Images/";

    //user info
    String name, username, bio, image, cover;

    //views from xml
    ImageView avatarIV, coverIV, worldMapTab, journeyTab, planTab, saveTab;
    TextView nameTV, bioTV;
    TabLayout postArticleAboutTab;
    //ScrollView scrollView;

    //progress dialog
    ProgressDialog progressDialog;

    /*RecyclerView recyclerView;
    List<ModelPost> postList;
    AdapterPost adapterPost;*/

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //arrays of permission to be requested
    String cameraPermissions[];
    String storagePermissions[];

    //uri of picked image
    Uri image_uri;

    //for checking profile or cover photo
    String photoType;

    //action bar
    //ActionBar actionBar;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceUsers = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        //init arrays of permission
        cameraPermissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init views
        avatarIV = view.findViewById(R.id.avatarImageView);
        coverIV = view.findViewById(R.id.coverImageView);
        nameTV =  view.findViewById(R.id.nameTextView);
        bioTV = view.findViewById(R.id.bioTextView);
        worldMapTab = view.findViewById(R.id.world_map_tab);
        journeyTab = view.findViewById(R.id.journey_tab);
        planTab = view.findViewById(R.id.plan_tab);
        saveTab = view.findViewById(R.id.save_tab);
        postArticleAboutTab = view.findViewById(R.id.post_article_about_tab);

        progressDialog = new ProgressDialog(getActivity());

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

                    //set data
                    nameTV.setText(name);
                    bioTV.setText(bio);
                    try {
                        Picasso.get().load(image).into(avatarIV);
                        Picasso.get().load(cover).into(coverIV);
                    }
                    catch (Exception e) {
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //default showing the post tab fragment
        PostFragment fragmentPost =  new PostFragment();
        FragmentTransaction fragmentTransactionPost = getParentFragmentManager().beginTransaction();
        fragmentTransactionPost.replace(R.id.post_article_about_content, fragmentPost, "");
        fragmentTransactionPost.commit();

        //handle the journal world map tab clicks
        worldMapTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                worldMapTab.setImageResource(R.drawable.worldmaptab1);
                journeyTab.setImageResource(R.drawable.journeytab);
                planTab.setImageResource(R.drawable.plantab);
                saveTab.setImageResource(R.drawable.savetab);
                WorldMapTabFragment fragmentWorldMap =  new WorldMapTabFragment();
                FragmentTransaction fragmentTransactionWorldMap = getParentFragmentManager().beginTransaction();
                fragmentTransactionWorldMap.replace(R.id.journal_content, fragmentWorldMap, "");
                fragmentTransactionWorldMap.commit();
            }
        });
        //journal journey tab clicks
        journeyTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                worldMapTab.setImageResource(R.drawable.worldmaptab);
                journeyTab.setImageResource(R.drawable.journeytab1);
                planTab.setImageResource(R.drawable.plantab);
                saveTab.setImageResource(R.drawable.savetab);
                JourneyFragment fragmentJourney =  new JourneyFragment();
                FragmentTransaction fragmentTransactionJourney = getParentFragmentManager().beginTransaction();
                fragmentTransactionJourney.replace(R.id.journal_content, fragmentJourney, "");
                fragmentTransactionJourney.commit();
            }
        });
        //journal plan tab clicks
        planTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                worldMapTab.setImageResource(R.drawable.worldmaptab);
                journeyTab.setImageResource(R.drawable.journeytab);
                planTab.setImageResource(R.drawable.plantab1);
                saveTab.setImageResource(R.drawable.savetab);
                PlanFragment fragmentPlan =  new PlanFragment();
                FragmentTransaction fragmentTransactionPlan = getParentFragmentManager().beginTransaction();
                fragmentTransactionPlan.replace(R.id.journal_content, fragmentPlan, "");
                fragmentTransactionPlan.commit();
            }
        });
        //journal save tab clicks
        saveTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                worldMapTab.setImageResource(R.drawable.worldmaptab);
                journeyTab.setImageResource(R.drawable.journeytab);
                planTab.setImageResource(R.drawable.plantab);
                saveTab.setImageResource(R.drawable.savetab1);
                SaveFragment fragmentSave =  new SaveFragment();
                FragmentTransaction fragmentTransactionSave = getParentFragmentManager().beginTransaction();
                fragmentTransactionSave.replace(R.id.journal_content, fragmentSave, "");
                fragmentTransactionSave.commit();
            }
        });

        postArticleAboutTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        //loadPosts();
                        PostFragment fragmentPost =  new PostFragment();
                        FragmentTransaction fragmentTransactionPost = getParentFragmentManager().beginTransaction();
                        fragmentTransactionPost.replace(R.id.post_article_about_content, fragmentPost, "");
                        fragmentTransactionPost.commit();
                        break;
                    case 1:
                        ArticleFragment fragmentArticle =  new ArticleFragment();
                        FragmentTransaction fragmentTransactionArticle = getParentFragmentManager().beginTransaction();
                        fragmentTransactionArticle.replace(R.id.post_article_about_content, fragmentArticle, "");
                        fragmentTransactionArticle.commit();
                        break;
                    case 2:
                        AboutFragment fragmentAbout =  new AboutFragment();
                        FragmentTransaction fragmentTransactionAbout = getParentFragmentManager().beginTransaction();
                        fragmentTransactionAbout.replace(R.id.post_article_about_content, fragmentAbout, "");
                        fragmentTransactionAbout.commit();
                        break;
                    default:

                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return view;
    }

    private boolean checkStoragePermission() {
        //check if storage permission is enabled or not
        //return true if enable
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        //request runtime storage permission
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        //check if camera permission is enabled or not
        //return true if enable
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        //request runtime storage permission
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.menu_main, menu);
        //hide item
        menu.findItem(R.id.submit).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        switch(id) {
            case R.id.action_logout:
                firebaseAuth.signOut();
                checkUserStatus();
                return true;
            case R.id.add_post:
                progressDialog.setMessage("Add Post");
                startActivity(new Intent(getActivity(), AddPostActivity.class));
                return true;
            case R.id.add_journey:
                progressDialog.setMessage("Add Journey");
                startActivity(new Intent(getActivity(), AddJourneyActivity.class));
                return true;
            case R.id.edit_name:
                progressDialog.setMessage("Edit Name");
                showEditTextDialog("name");
                return true;
            case R.id.edit_avatar:
                progressDialog.setMessage("Edit Profile Picture");
                photoType = "image";
                showImageSelectionDialog();
                return true;
            case R.id.edit_cover:
                progressDialog.setMessage("Edit Cover Photo");
                photoType = "cover";
                showImageSelectionDialog();
                return true;
            case R.id.edit_bio:
                progressDialog.setMessage("Edit Bio");
                showEditTextDialog("bio");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showEditTextDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit " + key);
        //set layout of dialog
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //add edit text
        EditText editText = new EditText(getActivity());
        editText.setHint("Enter " + key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //add confirm button in dialog
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text from edit text
                String value = editText.getText().toString().trim();
                //validate if user has entered something or not
                if(!TextUtils.isEmpty(value)) {
                    progressDialog.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);
                    databaseReferenceUsers.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //updated, dismiss progress
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Updated...", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed, dismiss progress and show error
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(getActivity(), "Please Enter " + key, Toast.LENGTH_SHORT).show();
                }
            }
        });
        //add cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //create and show dialog
        builder.create().show();
    }

    //show dialog containing options Camera & Gallery to pick the image
    private void showImageSelectionDialog() {
        String options[] = {"Camera", "Gallery"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle("Select Image From");
        //set options to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                if(which == 0) {
                    //Camera clicked
                    if(!checkCameraPermission()) {
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                else if(which == 1) {
                    //Gallery clicked
                    if(!checkStoragePermission()) {
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }
            }
        });
        //create and show dialog
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this method called when user press Allow or Deny from permission request dialog
        //handle permission cases
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                //pick from camera, first check if camera and storage permission allowed or not
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted) {
                        //permission enabled
                        pickFromCamera();
                    }
                    else {
                        //permission denied
                        Toast.makeText(getActivity(), "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                //pick from gallery, first check if camera and storage permission allowed or not
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted) {
                        //permission enabled
                        pickFromGallery();
                    }
                    else {
                        //permission denied
                        Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //this method called after picking image from camera or gallery
        if (resultCode == RESULT_OK) {
            if(requestCode == IMAGE_PICK_GALLERY_CODE) {
                // image is picked from gallery, get uri
                image_uri = data.getData();
                uploadPhoto(image_uri);
            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE) {
                // image is picked from camera, get uri
                uploadPhoto(image_uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadPhoto(Uri image_uri) {
        //show progress
        progressDialog.show();
        //path and name of image to be stored in firebase storage, eg Users_Profile_Cover_Images/image_xxxxxxxxxx.jpg
        String filePathAndName = storagePath + "" + photoType + "_" + user.getUid();
        StorageReference storageReference1 = storageReference.child(filePathAndName);
        storageReference1.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // image is uploaded to storage, now get it's url and store in user's database
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        //check if image is uploaded or not and url is received
                        if (uriTask.isSuccessful()) {
                            //image uploaded
                            //add or update url in user's database
                            HashMap<String, Object> results = new HashMap<>();
                            results.put(photoType, downloadUri.toString());
                            databaseReferenceUsers.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //url in database of user is added successfully
                                            //dismiss progress bar
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), "Image Updated...", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //error adding url in database of user
                                    //dismiss progress bar
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Error Updating Image...", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            //error
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //if error, show msg and dismiss dialog
                progressDialog.dismiss();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickFromCamera() {
        //Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
            //user is signed in stay here
        }
        else {
            //user not signed in, go to main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

}