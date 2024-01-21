package com.example.photogallery.ui.addLabel;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import java.util.List;
import java.util.ArrayList;

import com.example.photogallery.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AddLabelFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }
    private DatabaseReference mDatabase;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    public static List<String> labelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_label, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference("labels");
        listView = view.findViewById(R.id.listView);
        labelList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, labelList);
        listView.setAdapter(adapter);

        Button addButton = view.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText labelEditText = view.findViewById(R.id.label_edit_text);
                String label = labelEditText.getText().toString();
                if (!label.isEmpty()) {
                    // Veriyi Firebase'e ekle
                    mDatabase.push().setValue(label);
                }
            }
        });

        // ValueEventListener ekleyerek Firebase'den verileri al
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                labelList.clear();
                for (DataSnapshot labelSnapshot : dataSnapshot.getChildren()) {
                    String label = labelSnapshot.getValue(String.class);
                    labelList.add(label);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hata oluştuğunda
            }
        });

        return view;
    }
}