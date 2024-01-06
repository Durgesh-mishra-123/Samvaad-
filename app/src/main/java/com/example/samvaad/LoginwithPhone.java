package com.example.samvaad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class LoginwithPhone extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOtpBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginwith_phone);
        countryCodePicker = findViewById(R.id.login_countrycode);
        phoneInput = findViewById(R.id.login_phonenumber);
        sendOtpBtn = findViewById(R.id.send_otpbtn);
        progressBar = findViewById(R.id.login_progressbar);

        progressBar.setVisibility(View.GONE);

        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendOtpBtn.setOnClickListener(view -> {
           if(!countryCodePicker.isValidFullNumber()){
               phoneInput.setError("Mobile number not valid");
               return;
           }
           Intent intent = new Intent(LoginwithPhone.this,LoginOtpActivity.class);
           intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
           startActivity(intent);
        });
    }
}