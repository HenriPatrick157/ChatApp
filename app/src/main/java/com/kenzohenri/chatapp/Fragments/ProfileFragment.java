package com.kenzohenri.chatapp.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kenzohenri.chatapp.Home;
import com.kenzohenri.chatapp.MainActivity;
import com.kenzohenri.chatapp.Model.User;
import com.kenzohenri.chatapp.R;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

        CircleImageView image_profile;
        TextView username, out_description;
        EditText input_description;
        Button add_description;

        String desc;

        DatabaseReference reference;
        FirebaseUser fuser;

        StorageReference storageReference;
        private static final int IMAGE_REQUEST = 1;
        private Uri imageUri;
        private StorageTask uploadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container,false);

        image_profile = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);
        input_description = view.findViewById(R.id.input_description);
        out_description = view.findViewById(R.id.out_description);
        add_description = view.findViewById(R.id.add_description);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        storageReference= FirebaseStorage.getInstance().getReference("Uploads");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isAdded()){
                    User user =  dataSnapshot.getValue(User.class);
                    username.setText(user.getUsername());
                    if(user.getImageURL().equals("default")){
                        image_profile.setImageResource(R.mipmap.ic_launcher);
                    }else {
                        Glide.with(getContext()).load(user.getImageURL()).into(image_profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        //Load about me info
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(!user.getDescription().equals("")){
                    loadDescription();
                }else {
                    out_description.setVisibility(View.INVISIBLE);
                    //Save description
                    add_description.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            desc = input_description.getText().toString();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("description",desc );
                            reference.updateChildren(map);
                            Toast.makeText(getContext(), "Well saved", Toast.LENGTH_SHORT).show();
                            loadDescription();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((Home) getActivity()).hideFloatingActionButton();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((Home) getActivity()).showFloatingActionButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((Home) getActivity()).hideFloatingActionButton();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((Home) getActivity()).showFloatingActionButton();
    }

    private void loadDescription() {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                out_description.setText(user.getDescription());
                input_description.setVisibility(View.INVISIBLE);
                add_description.setVisibility(View.INVISIBLE);
                out_description.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if(imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    }else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(), "No image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            }else {
                uploadImage();
            }
        }
    }
}
