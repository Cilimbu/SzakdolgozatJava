package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.InputMismatchException;

import javax.security.auth.login.LoginException;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountBtn;
    private EditText InputEmail, InputName, InputPass;
    private ProgressDialog loadingBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountBtn = (Button) findViewById(R.id.reg_btn);
        InputEmail = (EditText) findViewById(R.id.reg_email_input);
        InputName = (EditText) findViewById(R.id.reg_user_input);
        InputPass = (EditText) findViewById(R.id.reg_pass_input);
        loadingBar = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();



        CreateAccountBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(task.isSuccessful())
                        {
                            String token = task.getResult().getToken();
                            CreateAccount(token);
                        }
                    }
                });
            }
        });
    }
    private void CreateAccount(String token)
    {
       try {
           String name = InputName.getText().toString();
           String email = InputEmail.getText().toString();
           String pass = InputPass.getText().toString();

           if(TextUtils.isEmpty(name))
           {
               Toast.makeText(this, "Kérlek írd be a felhasználó neved!", Toast.LENGTH_SHORT).show();
           }
           else if(TextUtils.isEmpty(pass))
           {
               Toast.makeText(this, "Kérlek írd be a jelszavad!", Toast.LENGTH_SHORT).show();
           }
           else if(pass.length()<6)
           {
               Toast.makeText(this, "A jelszónak minimum 7 karakter hosszúnak kell lennie!", Toast.LENGTH_SHORT).show();
           }
           else if(TextUtils.isEmpty(email))
           {
               Toast.makeText(this, "Kérlek írd be az email címed!", Toast.LENGTH_SHORT).show();
           }
           else {
               loadingBar.setTitle("Felírunk a listára!");
               loadingBar.setMessage("Kérlek várj...");
               loadingBar.setCanceledOnTouchOutside(false);
               loadingBar.show();
               ValidateName(name, email, pass, token);
           }
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }


    }

    private void ValidateName(final String name,final String email,final String pass, final String token)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String temp = email.replace(".",  ",");
                DataSnapshot mail = snapshot.child("Users").child(temp);
                if (!(mail.exists()))
                {
                    firebaseAuth.createUserWithEmailAndPassword(email,pass);
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("email", email);
                    userdataMap.put("name", name);
                    userdataMap.put("pass", pass);
                    userdataMap.put("token", token);
                    RootRef.child("Users").child(temp).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(RegisterActivity.this, "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(RegisterActivity.this, loginActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this, "Hiba lépett fel a regisztráció közben, próbálja meg később", Toast.LENGTH_SHORT).show();
                            }
                        }
                        
                    }
                    );
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Ez az email cím: " + email + " már foglalt", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Próbálja másik email címmel!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}