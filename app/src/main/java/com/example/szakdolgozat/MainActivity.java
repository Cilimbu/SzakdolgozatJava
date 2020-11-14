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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.szakdolgozat.CurrentOnline.CurrentUsers;
import com.example.szakdolgozat.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity
{
    private Button regNowButton, loginNowButton;
    private ProgressDialog loadingBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        regNowButton = (Button) findViewById(R.id.main_reg_btn);
        loginNowButton = (Button) findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

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

        if (UserEmailKey != null && UserPassKey != null)
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
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final String temp = email.replace(".",",");
        firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful())
                {
                    if (pass.length() < 6) {
                        Toast.makeText(MainActivity.this, "A jelszónak legalább 7 karakterből kell állnia", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Nem sikerült belépni", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                }
                else{
                    rootRef.child("Users").child(temp).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String name = snapshot.getValue(String.class);
                            CurrentUsers.currentOnlineUser.setName(name);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    CurrentUsers.currentOnlineUser.setEmail(email);
                    loadingBar.dismiss();
                    refreshToken(temp);
                    Toast.makeText(MainActivity.this, "Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoggedInActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void refreshToken(final String email)
    {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful())
                {
                    String token = task.getResult().getToken();
                    Map<String, Object> map = new HashMap<>();
                    map.put("token", token);
                    rootRef.child("Users").child(email).updateChildren(map);
                    Toast.makeText(MainActivity.this, "Token sikeresen frissítve", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}