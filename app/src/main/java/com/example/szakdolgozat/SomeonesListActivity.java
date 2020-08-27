package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szakdolgozat.CurrentOnline.CurrentUsers;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SomeonesListActivity extends AppCompatActivity {

    ListView listView;
    TextView textView1, textView2, textView3;
    UserListDetails ListDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_someones_list);
        ListDetails = (UserListDetails)getIntent().getSerializableExtra("ListDetails");
        listView = (ListView) findViewById(R.id.someonelist_list_view);
        textView1 = (TextView) findViewById(R.id.nametextView);
        textView2 = (TextView) findViewById(R.id.emailtextView);
        textView3 = (TextView) findViewById(R.id.datetextView);
        RefreshList();
        textView1.setText(ListDetails.getName());
        textView2.setText(ListDetails.getEmail());
        textView3.setText(ListDetails.getDate());
    }

    public void RefreshList() {
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference().child("Lists");
        final ArrayList<String> arrayList = new ArrayList<>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_items, R.id.textt, arrayList);
        reference.orderByChild("ID").equalTo(ListDetails.getID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String listitems = ds.child("listitems").getValue(String.class);
                    if (!(listitems.length() == 0)) {
                        for (String item : listitems.split(",")) {
                            arrayList.add(item);
                        }
                    }
                    listView.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SomeonesListActivity.this, "Hiba lépett fel a művelet során! Próbálja újra később!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}