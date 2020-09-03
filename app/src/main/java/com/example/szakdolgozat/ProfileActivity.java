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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szakdolgozat.CurrentOnline.CurrentUsers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.persistence.PruneForest;

import org.w3c.dom.Text;
import io.paperdb.Paper;

public class ProfileActivity extends AppCompatActivity {

    private ChatRoomDetails chatRoomDetails = new ChatRoomDetails();
    private UserListDetails listDetails = new UserListDetails();
    private PrivateChatDetails privateChatDetails = new PrivateChatDetails();
    private Button passwordResetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        TextView usernameTextView = findViewById(R.id.profile_user_name);
        ImageView profileImageView = findViewById(R.id.profile_pic);
        TextView emailTextView = findViewById(R.id.profile_email);
        passwordResetButton = (Button)findViewById(R.id.profile_password_update_button);

        usernameTextView.setText(CurrentUsers.currentOnlineUser.getName());
        String temp = CurrentUsers.currentOnlineUser.getEmail();
        String email = temp.replace(",",".");
        emailTextView.setText(email);


        passwordResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passReset();
            }
        });

    }
    private void passReset()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Adja meg az új jelszót!");
        final EditText input = new EditText(ProfileActivity.this);
        input.setHint("Jelszó");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        final EditText input1 = new EditText(ProfileActivity.this);
        input1.setHint("Jelszó újra");
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(input);
        lay.addView(input1);
        builder.setView(lay);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String pass = input.getText().toString();
                String passagain = input1.getText().toString();
                if(pass.equals(passagain))
                {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updatePassword(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ProfileActivity.this, "Jelszó változtatás sikeres!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(ProfileActivity.this, "A két mező nem azonos!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }
}
