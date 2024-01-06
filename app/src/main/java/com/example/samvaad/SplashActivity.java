package com.example.samvaad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.samvaad.model.UserModel;
import com.example.samvaad.utils.AndroidUtil;
import com.example.samvaad.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class SplashActivity extends AppCompatActivity {

    ImageView applogo;
    TextView appname;
    Animation topAnim,bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        applogo = findViewById(R.id.logo);
        appname = findViewById(R.id.app_name);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        applogo.setAnimation(topAnim);
        appname.setAnimation(bottomAnim);

        if(FirebaseUtil.isLoggedIn() && getIntent().getExtras()!=null){
            // from notification
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtil.allUserCollectionreference().document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        UserModel model = task.getResult().toObject(UserModel.class);

                        Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mainIntent);
                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        AndroidUtil.passUserModelAsIntent(intent,model);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });

        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(FirebaseUtil.isLoggedIn()){
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    }
                    else{
                        startActivity(new Intent(SplashActivity.this,LoginwithPhone.class));
                    }
                    finish();
                }
            },2000);
        }


    }
}