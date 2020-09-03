package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szakdolgozat.CurrentOnline.CurrentUsers;
import com.example.szakdolgozat.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class loginActivity extends AppCompatActivity {

    private EditText InputEmail, InputPass;
    private Button LoginButton, forgotPassButton;
    private ProgressDialog loadingBar;
    private CheckBox ckBoxRememberMe;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_btn);
        InputPass = (EditText) findViewById(R.id.login_pass_input);
        InputEmail = (EditText) findViewById(R.id.login_email_input);
        loadingBar = new ProgressDialog(this);
        ckBoxRememberMe = (CheckBox)findViewById(R.id.remember_me);
        forgotPassButton = (Button) findViewById(R.id.forgot_pass);
        firebaseAuth = FirebaseAuth.getInstance();

        forgotPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loginActivity.this, ForgotPassActivity.class);
                startActivity(intent);
            }
        });

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
        if(ckBoxRememberMe.isChecked())
        {
            Paper.book().write(CurrentUsers.UserEmailKey, email);
            Paper.book().write(CurrentUsers.UserPassKey, pass);
        }

        final String temp = email.replace(".",",");
        firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful())
                {
                    if (pass.length() < 6) {
                        Toast.makeText(loginActivity.this, "A jelszónak legalább 7 karakterből kell állnia", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(loginActivity.this, "Nem sikerült belépni", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                }
                else{
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    rootRef.child("Users").child(temp).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String name = snapshot.getValue(String.class);
                            Log.i("name",name);
                            CurrentUsers.currentOnlineUser.setEmail(email);
                            CurrentUsers.currentOnlineUser.setName(name);
                            Toast.makeText(loginActivity.this, ""+CurrentUsers.currentOnlineUser.getEmail(), Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            refreshToken(temp);
                            Toast.makeText(loginActivity.this, "Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(loginActivity.this, LoggedInActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });



        /*try
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
                    final String temp = email.replace(".",  ",");
                    final DataSnapshot mail = snapshot.child("Users").child(temp);
                    if(mail.exists())
                    {
                        Users user = mail.getValue(Users.class);
                        if (user.getPass().equals(pass))
                        {
                            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Users user = mail.getValue(Users.class);
                                    CurrentUsers.currentOnlineUser = user;
                                    refreshToken(temp);
                                    Toast.makeText(loginActivity.this, "Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                    Intent intent = new Intent(loginActivity.this, LoggedInActivity.class);
                                    startActivity(intent);
                                }
                            });
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
        }*/
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
                    Log.i("asdasdasdasda",token);
                    Map<String, Object> map = new HashMap<>();
                    map.put("token", token);
                    rootRef.child("Users").child(email).updateChildren(map);
                    Toast.makeText(loginActivity.this, "Token sikeresen frissítve", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}