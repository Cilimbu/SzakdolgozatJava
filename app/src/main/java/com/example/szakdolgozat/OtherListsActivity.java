package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.szakdolgozat.CurrentOnline.CurrentUsers;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OtherListsActivity extends AppCompatActivity {

    UserListDetails ListDetails;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_lists);
        listView = (ListView) findViewById(R.id.list_view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                UserListDetails ListDetails = (UserListDetails) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(OtherListsActivity.this,SomeonesListActivity.class);
                intent.putExtra("ListDetails",ListDetails);
                startActivity(intent);
            }
        });
        RefreshList();
    }
    public void RefreshList()
    {
        DatabaseReference reference;
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        reference= FirebaseDatabase.getInstance().getReference().child("Lists");
        final ArrayList<UserListDetails> arrayList = new ArrayList<UserListDetails>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter<UserListDetails>(this,R.layout.list_items, R.id.textt, arrayList);
        reference.orderByChild("shared").equalTo("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    String name = ds.child("name").getValue(String.class);
                    String ID = ds.child("ID").getValue(String.class);
                    final String email = ds.child("email").getValue(String.class);
                    final String listitems = ds.child("listitems").getValue(String.class);
                    String date = ds.child("date").getValue(String.class);
                    String shared = ds.child("shared").getValue(String.class);
                    final UserListDetails listDetails = new UserListDetails(ID, email, name, listitems, shared, date);
                    if (!email.equals(CurrentUsers.currentOnlineUser.getEmail())) {
                        RootRef.child("UserListConnect").orderByChild("userID").equalTo(CurrentUsers.currentOnlineUser.getEmail().replace(".",",")).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot datas : snapshot.getChildren()) {
                                    if (datas.child("listID").getValue().equals(listDetails.getID())) {
                                        arrayList.add(listDetails);
                                    }
                                }
                                listView.setAdapter(arrayAdapter);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error)
                            {
                                Toast.makeText(OtherListsActivity.this, "Nem sikerült a művelet, próbálja meg később!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OtherListsActivity.this, "Nem sikerült a művelet, próbálja meg később!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}