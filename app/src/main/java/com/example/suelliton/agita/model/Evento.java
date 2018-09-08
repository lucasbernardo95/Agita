package com.example.suelliton.agita.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Evento {
    private String nome;
    private String data;
    private String hora;
    private String local;
    private String estilo;
    private Integer latitude;
    private Integer longitude;
    private String bandas;
    private double valor;
    private String descricao;
    private String baner;
    private boolean liberado;
    private String casashow;

    public Evento() {
    }

    public Evento(String nome, String data, String hora, String local, String estilo, Integer latitude, Integer longitude, String bandas, double valor, String descricao, String baner, boolean liberado, String casashow) {
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
        this.baner = baner;
        this.liberado = liberado;
        this.casashow = casashow;
    }

    @Override
    public String toString() {
        return "Evento{" +
                "nome='" + nome + '\'' +
                ", data=" + data +
                ", hora='" + hora + '\'' +
                ", local='" + local + '\'' +
                ", estilo='" + estilo + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", bandas=" + bandas +
                ", valor=" + valor +
                ", descricao='" + descricao + '\'' +
                ", baner='" + baner + '\'' +
                ", liberado=" + liberado +
                ", casashow='" + casashow + '\'' +
                '}';
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

    public Integer getLatitude() {
        return latitude;
    }

    public void setLatitude(Integer latitude) {
        this.latitude = latitude;
    }

    public Integer getLongitude() {
        return longitude;
    }

    public void setLongitude(Integer longitude) {
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

    public String getBaner() {
        return baner;
    }

    public void setBaner(String baner) {
        this.baner = baner;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evento)) return false;
        Evento evento = (Evento) o;
        return Double.compare(evento.valor, valor) == 0 &&
                liberado == evento.liberado &&
                Objects.equals(nome, evento.nome) &&
                Objects.equals(data, evento.data) &&
                Objects.equals(hora, evento.hora) &&
                Objects.equals(local, evento.local) &&
                Objects.equals(estilo, evento.estilo) &&
                Objects.equals(latitude, evento.latitude) &&
                Objects.equals(longitude, evento.longitude) &&
                Objects.equals(bandas, evento.bandas) &&
                Objects.equals(descricao, evento.descricao) &&
                Objects.equals(baner, evento.baner) &&
                Objects.equals(casashow, evento.casashow);
    }

    @Override
    public int hashCode() {

        return Objects.hash(nome, data, hora, local, estilo, latitude, longitude, bandas, valor, descricao, baner, liberado, casashow);
    }
}
