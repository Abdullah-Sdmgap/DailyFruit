package com.codesdel.dailyfruit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codesdel.dailyfruit.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity
{
    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton, CreateNewAccountBtn;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_btn);
        CreateNewAccountBtn = (Button)findViewById(R.id.login_join_now_btn);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        loadingBar = new ProgressDialog(this);

        LoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LoginUser();
            }
        });
        CreateNewAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });


    }

    private void LoginUser()
    {
        //Get Data text To Convert String
        
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

    if (TextUtils.isEmpty(phone))
    {
        Toast.makeText(this, "Please enter your phone number...", Toast.LENGTH_SHORT).show();
    }
    else if (TextUtils.isEmpty(password))
    {
        Toast.makeText(this, "Please enter your password...", Toast.LENGTH_SHORT).show();
    }
    else
        {
            //For showing Progress LoginDialog
            loadingBar.setTitle("Account Login");
            loadingBar.setMessage("Please wait, we are checking your credentials...");
            loadingBar.setIcon(R.drawable.custom_toast_icon);
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            //Create a method for Checking Authentication & Access
            AllowAccessToAccount(phone, password);

        }
    }

    private void AllowAccessToAccount(final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //set if condition For Checking phoneNumber for unique id
                if (dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    //Check DataBase parentDbName
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            Toast.makeText(LoginActivity.this, "Logged in Successfully....", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password is incorrect...", Toast.LENGTH_SHORT).show();
                        }
                    }


                }
                else
                    {
                        Toast.makeText(LoginActivity.this, "Account with this "+phone+" number do not exits...", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                           }
        });
    }
}
