package com.codesdel.dailyfruit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codesdel.dailyfruit.Model.Users;
import com.codesdel.dailyfruit.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity
{
    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton, CreateNewAccountBtn;
    private ProgressDialog loadingBar;
    private TextView AdminLink, NotAdminLink;
    private String parentDbName = "Users";

    private com.rey.material.widget.CheckBox chkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_btn);
        CreateNewAccountBtn = (Button)findViewById(R.id.login_join_now_btn);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);

        //AdminPanel FindViewID
        AdminLink = (TextView)findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView)findViewById(R.id.not_admin_panel_link);


        //LoadingBar casting
        loadingBar = new ProgressDialog(this);

        //Declaring and initialize
        chkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        //LoginButton OnClickListener Set
        LoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LoginUser();
            }
        });
        //AdminText onClickListener Set
        AdminLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //When User Click I'm an Admin than "LoginButton" Text Change in "Login Admin"
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);//When LoginAdmin text Show at this time I'm an admin Text will'be hide or invisible
                NotAdminLink.setVisibility(View.VISIBLE);//When LoginAdmin text Show at this time NotAdmin Text will'be visible

                parentDbName = "Admins";//Here now change user account to Switch Admins account
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Now all function working as login info
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });

        CreateNewAccountBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();//When Click BackPressButton than he back redirectTo MainActivity
            }
        });


    }

    //Login User
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
            loadingBar.setIcon(R.drawable.sync_icon);
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            //Create a method for Checking Authentication & Access
            AllowAccessToAccount(phone, password);

        }
    }



    private void AllowAccessToAccount(final String phone, final String password)
    {
        if (chkBoxRememberMe.isChecked())
        {
            //When userCheckedBox than paper library store Phone and password in phone Memory than autoLogin in his account
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }


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
                           if (parentDbName.equals("Admins"))
                           {
                               Toast.makeText(LoginActivity.this, "Welcome Admin Your're, Logged in Successfully....", Toast.LENGTH_SHORT).show();
                               loadingBar.dismiss();

                               startActivity(new Intent(LoginActivity.this, AdminAddNewProductActivity.class));
                           }
                           else if (parentDbName.equals("Users"))
                               {
                                   Toast.makeText(LoginActivity.this, "Logged in Successfully....", Toast.LENGTH_SHORT).show();
                                   loadingBar.dismiss();

                                   startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                               }
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
