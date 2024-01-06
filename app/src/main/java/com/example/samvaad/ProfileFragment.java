package com.example.samvaad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.samvaad.model.UserModel;
import com.example.samvaad.utils.AndroidUtil;
import com.example.samvaad.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.A;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class ProfileFragment extends Fragment {


    ImageView profilepic;
    EditText usernameInput;
    EditText phoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logouttext;
    UserModel currentUserModel;
    AlertDialog.Builder builder;

    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result->{
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(),selectedImageUri,profilepic);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        profilepic =  view.findViewById(R.id.profile_imageview);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.profile_phone);
        updateProfileBtn = view.findViewById(R.id.profile_update_btn);
        logouttext = view.findViewById(R.id.logout_text);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        builder = new AlertDialog.Builder(getContext());

        getUserData();

        updateProfileBtn.setOnClickListener(view1 -> {
            updateBtnClick();
        });

        logouttext.setOnClickListener(view1 -> {
            builder.setTitle("Alert!!").setMessage("You really want to Logout").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FirebaseMessaging.getInstance().deleteToken();
                    FirebaseUtil.logout();
                    Intent intent = new Intent(getContext(),SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).show();


        });

        profilepic.setOnClickListener(view12 -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512).createIntent(new Function1<Intent, Unit>() {
                @Override
                public Unit invoke(Intent intent) {
                    imagePickLauncher.launch(intent);
                    return null;
                }
            });
        });

        return view;


    }
    void updateBtnClick(){
        String newusername = usernameInput.getText().toString();
        if(newusername.isEmpty() || newusername.length()<3){
            usernameInput.setError("Username should be at least be 3 chars");
            return;
        }
        currentUserModel.setUsername(newusername);
        setInProgress(true);
        if(selectedImageUri!=null){
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    updateToFirestore();
                }
            });
        }
        else{
            updateToFirestore();
        }


    }
    void updateToFirestore(){
        FirebaseUtil.currentUserDetails().set(currentUserModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    setInProgress(false);
                    AndroidUtil.showToast(getContext(),"Updated Successfully");
                }else{
                    AndroidUtil.showToast(getContext(),"Updated Failed");
                }
            }
        });
    }
    void getUserData(){
        setInProgress(true);
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri uri = task.getResult();
                    AndroidUtil.setProfilePic(getContext(),uri,profilepic);
                }
            }
        });
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                currentUserModel = task.getResult().toObject(UserModel.class);
                usernameInput.setText(currentUserModel.getUsername());
                phoneInput.setText(currentUserModel.getPhone());

            }
        });
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }
}