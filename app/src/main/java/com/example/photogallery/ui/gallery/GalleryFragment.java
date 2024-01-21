package com.example.photogallery.ui.gallery;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.photogallery.R;
import com.example.photogallery.databinding.FragmentGalleryBinding;
import com.example.photogallery.ui.adapter.MyAdapter;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.photogallery.utils.VerticalSpaceItemDecoration;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private MyAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        GalleryViewModel galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseDatabase = FirebaseDatabase.getInstance();
        getDataFromFirebase();

        adapter = new MyAdapter(requireContext(), new ArrayList<>());
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        int verticalSpaceHeight = 1;
        binding.recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(verticalSpaceHeight));

        return root;
    }

    private void getDataFromFirebase() {
        DatabaseReference photosReference = FirebaseDatabase.getInstance().getReference().child("photos");
        photosReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseReference photosReference = FirebaseDatabase.getInstance().getReference().child("photos");

                photosReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            ArrayList<String> dataList = new ArrayList<>();

                            for(DataSnapshot photoSnapshot : dataSnapshot.getChildren()){
                                String imageUrl = photoSnapshot.child("photoUrl").getValue(String.class);
                                String userEmail = photoSnapshot.child("userEmail").getValue(String.class);

                                ArrayList<String> labelList = new ArrayList<>();

                                for(DataSnapshot labelSnapshot : photoSnapshot.child("labels").getChildren()) {
                                    String label = labelSnapshot.getValue(String.class);
                                    labelList.add(label);
                                }
                                String formattedLabels = formatLabels(labelList);
                                String data = "Image Url: " + (imageUrl != null ? imageUrl : "") +
                                        "\nLabels: " + formattedLabels +
                                        "\n User Email: " + (userEmail != null ? userEmail : "");

                                dataList.add(data);
                            }
                            adapter.setData(dataList);
                        }
                        else {
                            Toast.makeText(requireContext(), "Veriler Alınamadı!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Veri çekme işlemi iptal edildi.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Veri çekme işlemi iptal edildi: ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatLabels(ArrayList<String> labelList){
        if(labelList == null || labelList.isEmpty()){
            return "";
        }
        StringBuilder formatedLabels = new StringBuilder();
        for(String label: labelList) {
            formatedLabels.append(label).append(", ");
        }
        return formatedLabels.substring(0, formatedLabels.length() - 2);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}
























