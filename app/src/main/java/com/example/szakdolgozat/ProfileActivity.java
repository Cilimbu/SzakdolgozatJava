package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szakdolgozat.CurrentOnline.CurrentUsers;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;
import io.paperdb.Paper;

public class ProfileActivity extends AppCompatActivity {

    private ChatRoomDetails chatRoomDetails = new ChatRoomDetails();
    private UserListDetails listDetails = new UserListDetails();
    private PrivateChatDetails privateChatDetails = new PrivateChatDetails();

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
