package com.example.szakdolgozat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    private ImageButton myListBtn, privateChatBtn, groupChatBtn, sharedListBtn, logOutBtn, otherListBtn, profileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        myListBtn = (ImageButton) findViewById(R.id.home_mylist_ibtn);
        privateChatBtn = (ImageButton) findViewById(R.id.home_priv_chat_ibtn);
        groupChatBtn = (ImageButton) findViewById(R.id.home_group_chat_ibtn);
        sharedListBtn = (ImageButton) findViewById(R.id.home_shared_list_ibtn);
        logOutBtn = (ImageButton) findViewById(R.id.home_logout_ibtn);
        otherListBtn = (ImageButton) findViewById(R.id.home_other_ibtn);
        profileBtn = (ImageButton) findViewById(R.id.home_profile_ibtn);

        myListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MyListActivity.class);
                startActivity(intent);
            }
        });
        privateChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        groupChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        sharedListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MySharedListsActivity.class);
                startActivity(intent);
            }
        });
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().destroy();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        otherListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, OtherListsActivity.class);
                startActivity(intent);
            }
        });
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}