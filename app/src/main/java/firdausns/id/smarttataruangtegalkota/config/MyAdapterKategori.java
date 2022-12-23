package firdausns.id.smarttataruangtegalkota.config;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import firdausns.id.smarttataruangtegalkota.R;

public class MyAdapterKategori extends RecyclerView.Adapter<MyAdapterKategori.ViewHolder> {
    private ArrayList<ItemKategori> itemKategoris;
    private Context context;
    OnItemClickListener mItemClickListener;

    public MyAdapterKategori(final ArrayList<ItemKategori> itemKategoris, Context context){
        this.itemKategoris  = itemKategoris;
        this.context        = context;
    }

    @Override
    public MyAdapterKategori.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_kategori,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapterKategori.ViewHolder holder, int position) {
        holder.tv_nama.setText(itemKategoris.get(position).getNama_polygon());
        holder.tv_keterangan.setText(itemKategoris.get(position).getKeterangan());
        holder.tv_warna.setBackgroundColor(Color.parseColor(itemKategoris.get(position).getWarna()));
        if (itemKategoris.get(position).isPilih()){
            holder.btn_pilih.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
        } else {
            holder.btn_pilih.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        return itemKategoris.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_nama) TextView tv_nama;
        @BindView(R.id.tv_keterangan) TextView tv_keterangan;
        @BindView(R.id.tv_warna) TextView tv_warna;
        Button btn_pilih;
        public ViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);

            btn_pilih = view.findViewById(R.id.btn_pilih);
            btn_pilih.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == btn_pilih){
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, getAdapterPosition());
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
