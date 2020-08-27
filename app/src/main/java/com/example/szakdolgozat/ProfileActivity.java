package com.example.szakdolgozat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szakdolgozat.CurrentOnline.CurrentUsers;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;
import io.paperdb.Paper;

public class ProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        TextView usernameTextView = findViewById(R.id.profile_user_name);
        ImageView profileImageView = findViewById(R.id.profile_pic);
        TextView emailTextView = findViewById(R.id.profile_email);

        usernameTextView.setText(CurrentUsers.currentOnlineUser.getName());
        String temp = CurrentUsers.currentOnlineUser.getEmail();
        String email = temp.replace(",",".");
        emailTextView.setText(email);


    }
}