package com.codesdel.dailyfruit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{

    private Button CreateAccountButton;
    private EditText InputName, InputPhoneNumber, InputPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountButton = (Button)findViewById(R.id.register_btn);
        InputName = (EditText)findViewById(R.id.register_username_input);
        InputPhoneNumber = (EditText)findViewById(R.id.register_phone_number_input);
        InputPassword = (EditText)findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);



        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CreateAccount();
            }
        });
    }

    private void CreateAccount()
    {
        //Get RegisterActivity text data Convert TO String
        String name = InputName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        //Check isEmpty null
        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Please enter your name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please enter your phone number...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter your password...", Toast.LENGTH_SHORT).show();
        }
        else
            {
                loadingBar.setTitle("Create Account");
                loadingBar.setIcon(R.drawable.check_register);
                loadingBar.setMessage("Please wait, while we are checking your credentials");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                //ValidatePhoneNumber & Password
                ValidatePhoneNumber(name, phone, password);
            }
    }

    private void ValidatePhoneNumber(final String name, final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //This statement means PhoneNumber is not exits Than create new account

                if (!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    //Create new account and data storage in fireBase Database
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone",phone);         //dataStore in FireBase under phoneList category
                    userDataMap.put("password",password);   //dataStore in FireBase under passwordList category
                    userDataMap.put("name",name);           //dataStore in FireBase under nameList category

                    RootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        //Toast.makeText(RegisterActivity.this, "Congrats! Your account has been created successfully...", Toast.LENGTH_SHORT).show();

                                        //Initialize CustomToast
                                        LayoutInflater inflater = getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup) findViewById(R.id.customToast_Layout_ID));
                                        TextView tv = (TextView) layout.findViewById(R.id.toast_text);
                                        tv.setText("Congrats! Your account has been created successfully...");
                                        Toast toast = new Toast(getApplicationContext());
                                        toast.setGravity(Gravity.BOTTOM, 0, 100);
                                        toast.setDuration(Toast.LENGTH_LONG);
                                        toast.setView(layout);
                                        toast.show();

                                        loadingBar.dismiss();


                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                        {
                                            loadingBar.dismiss();
                                            Toast.makeText(RegisterActivity.this, "Network Error: Please try again..", Toast.LENGTH_SHORT).show();
                                        }
                                }
                            });

                }
                else
                    {
                        Toast.makeText(RegisterActivity.this, "This "+phone+" already exits", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                        Toast.makeText(RegisterActivity.this, "Please try again another Phone Number", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

}
