package com.example.photogallery.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.photogallery.R;
public class AboutFragment extends Fragment {

    private Button btnLinkedin;
    private Button btnGithub;
    private Button btnYoutube;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_about, container, false);

        btnLinkedin = view.findViewById(R.id.btn_linkedin);
        btnGithub = view.findViewById(R.id.btn_github);
        btnYoutube = view.findViewById(R.id.btn_youtube);

        btnLinkedin.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/merve-bilgin-203b16213/"));
                startActivity(browserIntent);
            }
        });

        btnGithub.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mrvblgn?tab=repositories"));
                startActivity(browserIntent);
            }
        });

        btnYoutube.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCYiLL8py58PX3ShVd6aDw3Q"));
                startActivity(browserIntent);
            }
        });


        return view;
    }
}















