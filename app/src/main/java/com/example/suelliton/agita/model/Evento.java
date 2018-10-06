package com.example.suelliton.agita.model;

import java.io.Serializable;

public class Evento implements Serializable {
    private String key;
    private String nome;
    private String data;
    private String hora;
    private String endereco;
    private String estilo;
    private double latitude;
    private double longitude;
    private String bandas;
    private double entrada;
    private String descricao;
    private String urlBanner;
    private String casashow;
    private String dono;
    private boolean verificado = false;
    private int qtdCurtidas;
    private int qtdIrao;
    private int qtdTalvez;

    //private List<String> participantes = new ArrayList<>();

    public Evento() {
    }

    public Evento(String nome, String data, String hora, String endereco, String estilo, double latitude, double longitude, String bandas, double entrada, String descricao, String urlBanner,String casashow, String dono) {
        this.nome = nome;
        this.data = data;
        this.hora = hora;
        this.endereco = endereco;
        this.estilo = estilo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bandas = bandas;
        this.entrada = entrada;
        this.descricao = descricao;
        this.urlBanner = urlBanner;
        this.casashow = casashow;
        this.dono = dono;
        this.key = "";
        this.qtdCurtidas = 0;
        this.qtdIrao = 0;
        this.qtdTalvez = 0;
//        this.participantes = new ArrayList<>();
    }

    public int getQtdTalvez() {
        return qtdTalvez;
    }

    public void setQtdTalvez(int qtdTalvez) {
        this.qtdTalvez = qtdTalvez;
    }

    public int getQtdIrao() {
        return qtdIrao;
    }

    public void setQtdIrao(int qtdIrao) {
        this.qtdIrao = qtdIrao;
    }

    public int getQtdCurtidas() {
        return qtdCurtidas;
    }

    public void setQtdCurtidas(int qtdCurtidas) {
        this.qtdCurtidas = qtdCurtidas;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
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


    public double getLongitude() {
        return longitude;
    }


    public String getBandas() {
        return bandas;
    }

    public void setBandas(String bandas) {
        this.bandas = bandas;
    }

    public double getEntrada() {
        return entrada;
    }

    public void setEntrada(double valor) {
        this.entrada = entrada;
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

    public String getCasashow() {
        return casashow;
    }

    public void setCasashow(String casashow) {
        this.casashow = casashow;
    }

    public String getDono() {
        return dono;
    }

    public void setDono(String dono) {
        this.dono = dono;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    @Override
    public String toString() {
        return "Evento{" +
                "key='" + key + '\'' +
                ", nomeDetalhe='" + nome + '\'' +
                ", dataDetalhe='" + data + '\'' +
                ", horaDetalhe='" + hora + '\'' +
                ", localDetalhe='" + endereco + '\'' +
                ", estiloDetalhe='" + estilo + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", bandasDetalhe='" + bandas + '\'' +
                ", descricaoDetalhe='" + descricao + '\'' +
                ", urlBanner='" + urlBanner + '\'' +
                ", casashow='" + casashow + '\'' +
                ", donoDetalhe='" + dono + '\'' +
                ", verificado=" + verificado +
                ", qtdParticipantes=" + qtdCurtidas +
                '}';
    }
}
