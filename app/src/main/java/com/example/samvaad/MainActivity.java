package com.example.samvaad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.example.samvaad.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;
    ChatFragment chatFragment;
    ProfileFragment profileFragment;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatFragment = new ChatFragment();
        profileFragment =  new ProfileFragment();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchButton = findViewById(R.id.search_button);

        searchButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,SearchUserActivity.class));
        });



        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.menu_chat){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,chatFragment).commit();
                }

                if(item.getItemId()==R.id.menu_profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,profileFragment).commit();
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);

        getFCMToken();
    }
    void getFCMToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    String token = task.getResult();
                    FirebaseUtil.currentUserDetails().update("fcmToken",token);
                }
            }
        });
    }
   public void onbackPressed(){
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle(R.string.app_name);
      builder.setIcon(R.mipmap.ic_launcher_round);
      builder.setMessage("Do you want to exit ?")
              .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                      finish();;
                  }
              })
              .setNegativeButton("No", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                      dialogInterface.cancel();
                  }
              });
      builder.create();
      builder.show();

   }

}