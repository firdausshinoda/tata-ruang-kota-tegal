package firdausns.id.smarttataruangtegalkota.config;

import java.util.ArrayList;

public class ItemKategori {
    public String  warna, nama_kategori, nama_polygon, keterangan;
    public ArrayList<String> list_id_poly;
    public ArrayList<Double> list_lt;
    public ArrayList<Double> list_lg;
    public int id_polygon;
    public boolean pilih;


    public ItemKategori(int id_polygon, String warna, String nama_kategori, String nama_polygon, String keterangan, ArrayList<String> list_id_poly, ArrayList<Double> list_lt,
                        ArrayList<Double> list_lg, boolean pilih){
        this.id_polygon = id_polygon;
        this.warna = warna;
        this.nama_kategori = nama_kategori;
        this.nama_polygon = nama_polygon;
        this.keterangan = keterangan;
        this.list_id_poly = list_id_poly;
        this.list_lt = list_lt;
        this.list_lg = list_lg;
        this.pilih = pilih;
    }

    public int getId_polygon() {
        return id_polygon;
    }

    public String getWarna() {
        return warna;
    }

    public String getNama_kategori() {
        return nama_kategori;
    }

    public String getNama_polygon() {
        return nama_polygon;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public ArrayList<String> getList_id_poly() {
        return list_id_poly;
    }

    public ArrayList<Double> getList_lg() {
        return list_lg;
    }

    public ArrayList<Double> getList_lt() {
        return list_lt;
    }

    public boolean isPilih() {
        return pilih;
    }

    public void setPilih(boolean pilih) {
        this.pilih = pilih;
    }
}
