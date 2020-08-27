package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szakdolgozat.CurrentOnline.CurrentUsers;
import com.example.szakdolgozat.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class loginActivity extends AppCompatActivity {

    private EditText InputEmail, InputPass;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private CheckBox ckBoxRememberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_btn);
        InputPass = (EditText) findViewById(R.id.login_pass_input);
        InputEmail = (EditText) findViewById(R.id.login_email_input);
        loadingBar = new ProgressDialog(this);
        ckBoxRememberMe = (CheckBox)findViewById(R.id.remember_me);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }

        });
    }
    private void LoginUser()
    {
        String email = InputEmail.getText().toString();
        String pass = InputPass.getText().toString();

        if(TextUtils.isEmpty(pass))
        {
            Toast.makeText(this, "Kérlek írd be a jelszavad!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Kérlek írd be az email címed!", Toast.LENGTH_SHORT).show();
        }
        else {
                loadingBar.setTitle("Belépés folyamatban!");
                loadingBar.setMessage("Kérlek várj...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                AllowAccessToAccount(email, pass);
        }
    }

    private void AllowAccessToAccount(final String email,final String pass)
    {
        try
        {
            if(ckBoxRememberMe.isChecked())
            {
                Paper.book().write(CurrentUsers.UserEmailKey, email);
                Paper.book().write(CurrentUsers.UserPassKey, pass);
            }

            final DatabaseReference RootRef;
            RootRef = FirebaseDatabase.getInstance().getReference();

            RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String temp = email.replace(".",  ",");
                    DataSnapshot mail = snapshot.child("Users").child(temp);
                    if(mail.exists())
                    {
                        Users user = mail.getValue(Users.class);
                        if (user.getPass().equals(pass))
                        {
                            Toast.makeText(loginActivity.this, "Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            CurrentUsers.currentOnlineUser = user;
                            Intent intent = new Intent(loginActivity.this, LoggedInActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(loginActivity.this, "Rossz email vagy jelszó!", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                    else {
                        Toast.makeText(loginActivity.this, "Ez az email " + email + " nem létezik.", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}