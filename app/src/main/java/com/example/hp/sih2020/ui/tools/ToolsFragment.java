package com.example.hp.sih2020.ui.tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import Prevalent.Prevalent;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import de.hdodenhof.circleimageview.CircleImageView;

import com.example.hp.sih2020.MainActivity;
import com.example.hp.sih2020.R;
import com.firebase.ui.database.paging.FirebaseDataSource;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;
    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, vehicleEditText;  //addressEditText on Github
    private TextView profileChangeTextBtn,  closeTextBtn, saveTextButton;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String checker = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);
        final TextView textView = root.findViewById(R.id.text_tools);
        toolsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView = (CircleImageView) view.findViewById(R.id.settings_profile_image);
        fullNameEditText = (EditText) view.findViewById(R.id.settings_full_name);
        userPhoneEditText = (EditText) view.findViewById(R.id.settings_phone_number);
        vehicleEditText = (EditText) view.findViewById(R.id.settings_vehicle_number);
        profileChangeTextBtn = (TextView) view.findViewById(R.id.profile_image_change_btn);
        saveTextButton = (TextView) view.findViewById(R.id.update_account_settings_btn);


        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, vehicleEditText);


        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });


        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start((Activity) getContext());
            }
        });
    }

    private void updateOnlyUserInfo()
    {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Resident");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("name", fullNameEditText.getText().toString());
        userMap. put("address", vehicleEditText.getText().toString());
        userMap. put("phoneOrder", userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(getActivity(), MainActivity.class));
        Toast.makeText(getActivity(), "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(getActivity(), "Error, Try Again.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(getActivity(),getActivity().getClass()));
            getActivity().finish();
        }
    }

    private void userInfoSaved()
    {
        if (TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Toast.makeText(getActivity(), "Name is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(vehicleEditText.getText().toString()))
        {
            Toast.makeText(getActivity(), "Name is address.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString()))
        {
            Toast.makeText(getActivity(), "Name is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }
    }



    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(Prevalent.currentUser.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            if (task.isSuccessful())
                            {
                                Uri downloadUrl = task.getResult();
                                myUrl = downloadUrl.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Resident");

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap. put("name", fullNameEditText.getText().toString());
                                userMap. put("address", vehicleEditText.getText().toString());
                                userMap. put("phoneOrder", userPhoneEditText.getText().toString());
                                userMap. put("image", myUrl);
                                ref.child(Prevalent.currentUser.getPhone()).updateChildren(userMap);

                                progressDialog.dismiss();

                                startActivity(new Intent(getActivity(), MainActivity.class));
                                Toast.makeText(getActivity(), "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Error.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(getActivity(), "image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }


    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Resident").child(Prevalent.currentUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}