package com.example.mdpcwjourvel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class AddPlanActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    ActionBar actionBar;
    RelativeLayout planLayout;

    boolean isBold = false;
    boolean isItalic = false;
    int textSize = 18;
    Typeface typeface;

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //arrays of permission to be requested
    String cameraPermissions[];
    String storagePermissions[];

    //user info
    String name, email, uid, username;

    //uri of picked image
    Uri image_uri = null;

    //progress dialog
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);

        firebaseAuth =  FirebaseAuth.getInstance();
        checkUserStatus();

        //tools navigation
        BottomNavigationView navigationView = findViewById(R.id.tools_menu);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //init arrays of permission
        cameraPermissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        actionBar = getSupportActionBar();
        actionBar.setTitle("Add Plan");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        planLayout = findViewById(R.id.plan_content);

        //get some info of current user to include in plan
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = databaseReference.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    name = "" + dataSnapshot.child("name").getValue();
                    email = "" + dataSnapshot.child("email").getValue();
                    username = "" + dataSnapshot.child("username").getValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =  new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //handle item clicks
            switch(item.getItemId()) {
                case R.id.add_picture_icon:
                    //show image pick dialog
                    showImageSelectionDialog();
                    return true;
                case R.id.add_text_icon:
                    //show add text dialog
                    showAddTextDialog();
                    return true;
            }
            return false;
        }
    };

    private void showAddTextDialog() {
        isBold = false;
        isItalic = false;
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set title
        builder.setTitle("Add Text");
        //set layout of dialog
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //add edit text
        EditText editText = new EditText(this);
        editText.setHint("Enter Text here");
        linearLayout.addView(editText);
        //children of parent linear layout
        LinearLayout linearLayout1 = new LinearLayout(this);
        linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout1.setPadding(10,10,10,10);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        linearLayout.addView(linearLayout1, layoutParams);
        //add bold button
        ImageButton boldButton = new ImageButton(this);
        boldButton.setImageResource(R.drawable.ic_bold_grey);
        boldButton.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(70,70);
        layoutParams1.setMargins(10,30,10,10);
        //boldButton.setPadding(10,10,10,10);
        linearLayout1.addView(boldButton, layoutParams1);
        //add italic button
        ImageButton italicButton = new ImageButton(this);
        italicButton.setImageResource(R.drawable.ic_italic_grey);
        italicButton.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        //italicButton.setPadding(10,10,10,10);
        linearLayout1.addView(italicButton, layoutParams1);
        //add text size
        EditText textSizeET = new EditText(this);
        textSizeET.setInputType(InputType.TYPE_CLASS_NUMBER);
        textSizeET.setTextSize(21);
        textSizeET.setText("18");
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(100,80);
        layoutParams2.setMargins(10,20,10,10);
        textSizeET.setPadding(10,10,10,10);
        linearLayout1.addView(textSizeET, layoutParams2);
        //add spinner font
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.spinnerItems));
        spinner.setAdapter(spinnerArrayAdapter);
        linearLayout1.addView(spinner);

        //bold button clicks
        boldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isBold) {
                    isBold = true;
                    boldButton.setBackgroundColor(Color.parseColor("#77808080"));
                    if(isItalic) {
                        editText.setTypeface(typeface, Typeface.BOLD_ITALIC);
                    }
                    else {
                        editText.setTypeface(typeface, Typeface.BOLD);
                    }
                }
                else {
                    isBold = false;
                    boldButton.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                    if(isItalic) {
                        editText.setTypeface(typeface, Typeface.ITALIC);
                    }
                    else {
                        editText.setTypeface(typeface, Typeface.NORMAL);
                    }
                }
            }
        });
        //italic button clicks
        italicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isItalic) {
                    isItalic = true;
                    italicButton.setBackgroundColor(Color.parseColor("#77808080"));
                    if(isBold) {
                        editText.setTypeface(typeface, Typeface.BOLD_ITALIC);
                    }
                    else {
                        editText.setTypeface(typeface, Typeface.ITALIC);
                    }
                }
                else {
                    isItalic = false;
                    italicButton.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                    if(isBold) {
                        editText.setTypeface(typeface, Typeface.BOLD);
                    }
                    else {
                        editText.setTypeface(typeface, Typeface.NORMAL);
                    }
                }
            }
        });
        //handle text size edit text
        textSizeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if(textSizeET.getText().toString().matches("")) {
                        textSizeET.setText("18");
                        Toast.makeText(AddPlanActivity.this,
                                "Please enter a number", Toast.LENGTH_SHORT)
                                .show();
                    }
                    else {
                        Toast.makeText(AddPlanActivity.this,
                                "Remember to click enter to change the font size", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });
        textSizeET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN)&&
                        (keyCode==KeyEvent.KEYCODE_ENTER)){
                    if(textSizeET.getText().toString().matches("")){
                        textSizeET.setText("18");
                        Toast.makeText(AddPlanActivity.this,
                                "Please enter a number", Toast.LENGTH_SHORT)
                                .show();
                    }
                    textSize = Integer.parseInt(textSizeET.getText().toString().trim());
                    editText.setTextSize(textSize);
                    //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(textSizeET.getWindowToken(),0);
                    return true;
                }
                return false;
            }
        });
        //handle spinner selection
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    typeface = Typeface.SANS_SERIF;
                }
                else if (position == 1) {
                    typeface = getResources().getFont(R.font.aguafina_script);
                }
                else if (position == 2) {
                    typeface = getResources().getFont(R.font.aladin);
                }
                else if (position == 3) {
                    typeface = getResources().getFont(R.font.amatica_sc);
                }
                else if (position == 4) {
                    typeface = getResources().getFont(R.font.bilbo_swash_caps);
                }
                else if (position == 5) {
                    typeface = getResources().getFont(R.font.caveat);
                }
                else if (position == 6) {
                    typeface = getResources().getFont(R.font.cedarville_cursive);
                }
                else if (position == 7) {
                    typeface = getResources().getFont(R.font.fredericka_the_great);
                }
                else if (position == 8) {
                    typeface = getResources().getFont(R.font.homemade_apple);
                }
                else if (position == 9) {
                    typeface = getResources().getFont(R.font.luckiest_guy);
                }
                else if (position == 10) {
                    typeface = getResources().getFont(R.font.petit_formal_script);
                }
                else if (position == 11) {
                    typeface = getResources().getFont(R.font.rock_salt);
                }
                else if (position == 12) {
                    typeface = getResources().getFont(R.font.trade_winds);
                }
                else {
                    typeface = Typeface.SANS_SERIF;
                }
                //set text style
                if(isBold&&isItalic) {
                    editText.setTypeface(typeface, Typeface.BOLD_ITALIC);
                }
                else if (isBold&&!isItalic) {
                    editText.setTypeface(typeface, Typeface.BOLD);
                }
                else if (!isBold&&isItalic) {
                    editText.setTypeface(typeface, Typeface.ITALIC);
                }
                else {
                    editText.setTypeface(typeface, Typeface.NORMAL);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        builder.setView(linearLayout);

        //add confirm button in dialog
        builder.setPositiveButton("Add Text", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text from edit text
                String value = editText.getText().toString().trim();
                //validate if user has entered something or not
                if(!TextUtils.isEmpty(value)) {
                    //progressDialog.show();
                    TextView textView = new TextView(AddPlanActivity.this);
                    textView.setText(value);
                    textView.setTextSize(textSize);
                    if(isBold&&isItalic) {
                        textView.setTypeface(typeface, Typeface.BOLD_ITALIC);
                    }
                    else if (isBold&&!isItalic) {
                        textView.setTypeface(typeface, Typeface.BOLD);
                    }
                    else if (!isBold&&isItalic) {
                        textView.setTypeface(typeface, Typeface.ITALIC);
                    }
                    else {
                        textView.setTypeface(typeface, Typeface.NORMAL);
                    }
                    planLayout.addView(textView);
                    textView.setOnTouchListener(onTouchListener());
                }
                else {
                    Toast.makeText(AddPlanActivity.this, "No text is added", Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private void pickFromCamera() {
        //Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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

    private boolean checkStoragePermission() {
        //check if storage permission is enabled or not
        //return true if enable
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        //request runtime storage permission
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        //check if camera permission is enabled or not
        //return true if enable
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        //request runtime storage permission
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this method called when user press Allow or Deny from permission request dialog
        //handle permission cases
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                //pick from camera, first check if camera and storage permission allowed or not
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        //permission enabled
                        pickFromCamera();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                //pick from gallery, first check if camera and storage permission allowed or not
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        //permission enabled
                        pickFromGallery();
                    }
                    else {
                        //permission denied
                        Toast.makeText(this, "Please enable storage permission", Toast.LENGTH_SHORT).show();
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
                //add image view
                ImageView imageView = new ImageView(this);
                imageView.setImageURI(image_uri);
                planLayout.addView(imageView);
                imageView.setOnTouchListener(onTouchListener());
            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE) {
                // image is picked from camera, get uri
                ImageView imageView = new ImageView(this);
                imageView.setImageURI(image_uri);
                planLayout.addView(imageView);
                imageView.setOnTouchListener(onTouchListener());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {

            private static final int NONE = 0;
            private static final int DRAG = 1;
            private static final int ZOOM = 2;
            private int mode = NONE;
            private float oldDist = 1f;
            private float d = 0f;
            private float newRot = 0f;
            float scalediff;
            RelativeLayout.LayoutParams parms;
            int startwidth;
            int startheight;
            float dx = 0, dy = 0, x = 0, y = 0;
            float angle = 0;

            @SuppressLint("ClickableViewAccessibility")
            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return (float) Math.sqrt(x * x + y * y);
            }

            private float rotation(MotionEvent event) {
                double delta_x = (event.getX(0) - event.getX(1));
                double delta_y = (event.getY(0) - event.getY(1));
                double radians = Math.atan2(delta_y, delta_x);
                return (float) Math.toDegrees(radians);
            }

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        parms = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        startwidth = parms.width;
                        startheight = parms.height;
                        dx = event.getRawX() - parms.leftMargin;
                        dy = event.getRawY() - parms.topMargin;
                        mode = DRAG;
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            mode = ZOOM;
                        }

                        d = rotation(event);

                        break;
                    case MotionEvent.ACTION_UP:

                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;

                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {

                            x = event.getRawX();
                            y = event.getRawY();

                            parms.leftMargin = (int) (x - dx);
                            parms.topMargin = (int) (y - dy);

                            parms.rightMargin = (int) (dx - x);
                            parms.bottomMargin = (int) (dy - y);
                            //parms.rightMargin = parms.leftMargin + (5 * parms.width);
                            //parms.bottomMargin = parms.topMargin + (10 * parms.height);

                            view.setLayoutParams(parms);

                        } else if (mode == ZOOM) {

                            if (event.getPointerCount() == 2) {

                                newRot = rotation(event);
                                float r = newRot - d;
                                angle = r;

                                x = event.getRawX();
                                y = event.getRawY();

                                float newDist = spacing(event);
                                if (newDist > 10f) {
                                    float scale = newDist / oldDist * view.getScaleX();
                                    if (scale > 0.6) {
                                        scalediff = scale;
                                        view.setScaleX(scale);
                                        view.setScaleY(scale);

                                    }
                                }

                                view.animate().rotationBy(angle).setDuration(0).setInterpolator(new LinearInterpolator()).start();

                                x = event.getRawX();
                                y = event.getRawY();

                                parms.leftMargin = (int) ((x - dx) + scalediff);
                                parms.topMargin = (int) ((y - dy) + scalediff);

                                parms.rightMargin = 0;
                                parms.bottomMargin = 0;
                                parms.rightMargin = parms.leftMargin + (5 * parms.width);
                                parms.bottomMargin = parms.topMargin + (10 * parms.height);

                                view.setLayoutParams(parms);

                            }
                        }
                        break;
                }

                return true;
            }
        };
    }

    //inflate options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflating menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //hide item
        menu.findItem(R.id.action_logout).setVisible(false);
        menu.findItem(R.id.edit_icon).setVisible(false);
        menu.findItem(R.id.add_icon).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    //handle menu item click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        if(id == R.id.submit) {
            Bitmap bitmap = Bitmap.createBitmap(planLayout.getWidth(), planLayout.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            planLayout.draw(canvas);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            //progress dialog
            progressDialog.setMessage("Adding your plan...");
            progressDialog.show();
            //for plan content, id, add time
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String filePathAndName = "Plans/" + "plan_" + uid + timeStamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            storageReference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            String downloadUri = uriTask.getResult().toString();
                            if(uriTask.isSuccessful()) {
                                //uri is received upload plan to firebase Database
                                HashMap<Object, String> hashMap = new HashMap<>();
                                //put plan info
                                hashMap.put("uid", uid);
                                hashMap.put("name", name);
                                hashMap.put("email", email);
                                hashMap.put("username", username);
                                hashMap.put("planID", uid+timeStamp);
                                hashMap.put("plan", downloadUri);
                                hashMap.put("uploadTime", timeStamp);
                                //path to store plan data
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Plans");
                                //put data in the reference
                                databaseReference.child(uid+timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //added successfully
                                                progressDialog.dismiss();
                                                Toast.makeText(AddPlanActivity.this, "Plan Added", Toast.LENGTH_SHORT).show();
                                                //back to profile fragment
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failed adding data
                                        progressDialog.dismiss();
                                        Toast.makeText(AddPlanActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //failed uploading image
                    progressDialog.dismiss();
                    Toast.makeText(AddPlanActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
            //user is signed in stay here
            email = user.getEmail();
            uid = user.getUid();
        }
        else {
            //user not signed in, go to main activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

}