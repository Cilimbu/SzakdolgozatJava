package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.HashMap;
import java.util.Map;

public class SomeonesListActivity extends AppCompatActivity {

    ListView listView;
    TextView textView1, textView2, textView3;
    UserListDetails ListDetails;
    ListItemDetails listItemDetails;
    final ArrayList<ListItemDetails> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_someones_list);
        ListDetails = (UserListDetails)getIntent().getSerializableExtra("ListDetails");
        listItemDetails = new ListItemDetails();
        final ArrayAdapter arrayAdapter = new ArrayAdapter<ListItemDetails>(this, R.layout.list_items_checkbox, R.id.textt, arrayList);
        listView = (ListView) findViewById(R.id.someonelist_list_view);
        textView1 = (TextView) findViewById(R.id.nametextView);
        textView2 = (TextView) findViewById(R.id.emailtextView);
        textView3 = (TextView) findViewById(R.id.datetextView);
        RefreshList(arrayAdapter);
        textView1.setText(ListDetails.getName());
        textView2.setText(ListDetails.getEmail());
        textView3.setText(ListDetails.getDate());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListItemDetails listItemDetails = (ListItemDetails) arrayAdapter.getItem(i);
                toggleChecked(i);
            }
        });
    }

    public void RefreshList(final ArrayAdapter<ListItemDetails> arrayAdapter) {
        DatabaseReference reference;
        reference=FirebaseDatabase.getInstance().getReference().child("Lists");
        reference.child(ListDetails.getID()).child("listitems").orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    ListItemDetails temp = new ListItemDetails();
                    String name = ds.child("name").getValue(String.class);
                    temp.setName(name);
                    String ID = ds.child("ID").getValue(String.class);
                    temp.setID(ID);
                    String checked = ds.child("checked").getValue(String.class);
                    temp.setChecked(checked);
                    arrayList.add(temp);
                }
                listView.setAdapter(arrayAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void toggleChecked(final Integer i)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.child("Lists").child(ListDetails.getID()).child("listitems").child(arrayList.get(i).getID()).child("checked").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String checked = snapshot.getValue(String.class);
                Map<String, Object> map = new HashMap<>();
                map.put("checked",(checked.equals("") ? "Kész" : ""));

                RootRef.child("Lists").child(ListDetails.getID()).child("listitems").child(arrayList.get(i).getID()).updateChildren(map);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}