package com.example.suelliton.agita.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Evento implements Serializable {
    private String key;
    private String nome;
    private String data;
    private String hora;
    private String local;
    private String estilo;
    private double latitude;
    private double longitude;
    private String bandas;
    private double valor;
    private String descricao;
    private String urlBanner;
    private boolean liberado;
    private String casashow;
    private boolean cover;
    private String dono;
    //private List<String> participantes = new ArrayList<>();

    public Evento() {
    }

    public Evento(String nome, String data, String hora, String local, String estilo, double latitude, double longitude, String bandas, double valor, String descricao, String urlBanner, boolean liberado, String casashow, boolean cover, String dono) {
        this.nome = nome;
        this.data = data;
        this.hora = hora;
        this.local = local;
        this.estilo = estilo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bandas = bandas;
        this.valor = valor;
        this.descricao = descricao;
        this.urlBanner = urlBanner;
        this.liberado = liberado;
        this.casashow = casashow;
        this.cover = cover;
        this.dono = dono;
        this.key = "";
//        this.participantes = new ArrayList<>();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getEstilo() {
        return estilo;
    }

    public void setEstilo(String estilo) {
        this.estilo = estilo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getBandas() {
        return bandas;
    }

    public void setBandas(String bandas) {
        this.bandas = bandas;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUrlBanner() {
        return urlBanner;
    }

    public void setUrlBanner(String urlBanner) {
        this.urlBanner = urlBanner;
    }

    public boolean isLiberado() {
        return liberado;
    }

    public void setLiberado(boolean liberado) {
        this.liberado = liberado;
    }

    public String getCasashow() {
        return casashow;
    }

    public void setCasashow(String casashow) {
        this.casashow = casashow;
    }

    public boolean isCover() {
        return cover;
    }

    public void setCover(boolean cover) {
        this.cover = cover;
    }

    public String getDono() {
        return dono;
    }

    public void setDono(String dono) {
        this.dono = dono;
    }




}
