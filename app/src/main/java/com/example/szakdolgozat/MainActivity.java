package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.szakdolgozat.CurrentOnline.CurrentUsers;
import com.example.szakdolgozat.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity
{
    private Button regNowButton, loginNowButton;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        regNowButton = (Button) findViewById(R.id.main_reg_btn);
        loginNowButton = (Button) findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);
        NotificationHelper.createNotificationChannel(this);
        loginNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });
        regNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        String UserEmailKey = Paper.book().read(CurrentUsers.UserEmailKey);
        String UserPassKey = Paper.book().read(CurrentUsers.UserPassKey);

        if (UserEmailKey != "" && UserPassKey != "")
        {
            if(!TextUtils.isEmpty(UserEmailKey) && !TextUtils.isEmpty(UserPassKey))
            {
                AllowAccess(UserEmailKey, UserPassKey);

                loadingBar.setTitle("Belépés folyamatban!");
                loadingBar.setMessage("Kérlek várj...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }

    }

    private void AllowAccess(final String email, final String pass) {
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
                    CurrentUsers.currentOnlineUser = user;
                    if (user.getPass().equals(pass))
                    {
                        Toast.makeText(MainActivity.this, "Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        Intent intent = new Intent(MainActivity.this, LoggedInActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Rossz email vagy jelszó!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Ez az email " + email + " nem létezik.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}