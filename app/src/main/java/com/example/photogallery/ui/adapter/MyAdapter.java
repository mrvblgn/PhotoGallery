package com.example.photogallery.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.photogallery.R;

import java.util.List;

// RecyclerView, listedeki öğeleri düzenlemek ve görüntülemek için kullanılan bir Android bileşenidir.
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<String> dataList; // Metinleri liste şeklinde çekeceğimiz veri listesi
    private Context context;

    // yapıcı metot
    public MyAdapter(Context context, List<String> dataList){
        this.context = context;
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        // XML layout dosyasından bir View oluşturuyoruz
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data, parent, false);
        //// ViewHolder sınıfına oluşturulan View geçirilerek yeni bir ViewHolder nesnesi oluşturuyoruz
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        // Belirli bir pozisyondaki veriyi ViewHolder'a bağlar
        String data = dataList.get(position);
        holder.bind(data, position);
    }
    @Override
    // Veri listesinde kaç eleman olduğunu belirtir
    public int getItemCount(){
        return dataList.size();
    }

    // Veri listesini güncellemek için kullanılır
    public void setData(List<String> dataList){
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    // RecyclerView'da her bir öğe için görünümü tutan ve yöneten sınıf
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView textView;
        public ViewHolder(@NonNull View itemView){ // yapıcı metot
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textViewData);
        }

        // Firebase'den gelen veriyi ViewHolder'a bağlama işlemini gerçekleştirir
        public void bind(String data, int position){
            String [] parts = data.split("\n");
            String imageUrl = parts[0].substring(parts[0].indexOf(":") +1).trim();
            String labels = parts[1].substring(parts[1].indexOf(":") +1).trim();
            String userEmail = parts[2].substring(parts[2].indexOf(":") +1).trim();

            Log.d("My Adapter", "position" + position);
            Log.d("My Adapter", "imageUrl" + imageUrl);

            if(!imageUrl.isEmpty()){
                RequestOptions requestOptions = new RequestOptions().centerCrop();

                Glide.with(itemView).load(imageUrl).apply(requestOptions).into(imageView);
            }
            else { // Eğer resim yoksa varsayılan bir resim atanıyor
                imageView.setImageResource(R.drawable.ic_menu_camera);
            } // TextView'a bilgileri yerleştirme
            textView.setText("User email: " + userEmail + "\n\nLabel: \n" + labels);
        }
    }
}















