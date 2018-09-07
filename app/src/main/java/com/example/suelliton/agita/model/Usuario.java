package com.example.suelliton.agita.model;

import java.util.List;
import java.util.Objects;

public class Usuario {
    private String nome;
    private String contato;
    private String login;
    private String password;
    private String cpf_cnpj;
    private boolean admin;
    private List<Evento> meusEventos;
    private List<String> eventosInteresse;

    public Usuario() {
    }

    public Usuario(String nome, String contato, String login, String password, String cpf_cnpj, boolean admin) {
        this.nome = nome;
        this.contato = contato;
        this.login = login;
        this.password = password;
        this.cpf_cnpj = cpf_cnpj;
        this.admin = admin;
    }

    public Usuario(String nome, String contato, String login, String password, String cpf_cnpj, boolean admin, List<Evento> meusEventos, List<String> eventosInteresse) {
        this.nome = nome;
        this.contato = contato;
        this.login = login;
        this.password = password;
        this.cpf_cnpj = cpf_cnpj;
        this.admin = admin;
        this.meusEventos = meusEventos;
        this.eventosInteresse = eventosInteresse;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nome='" + nome + '\'' +
                ", contato='" + contato + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", cpf_cnpj='" + cpf_cnpj + '\'' +
                ", admin=" + admin +
                ", meusEventos=" + meusEventos +
                ", eventosInteresse=" + eventosInteresse +
                '}';
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCpf_cnpj() {
        return cpf_cnpj;
    }

    public void setCpf_cnpj(String cpf_cnpj) {
        this.cpf_cnpj = cpf_cnpj;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public List<Evento> getMeusEventos() {
        return meusEventos;
    }

    public void setMeusEventos(List<Evento> meusEventos) {
        this.meusEventos = meusEventos;
    }

    public List<String> getEventosInteresse() {
        return eventosInteresse;
    }

    public void setEventosInteresse(List<String> eventosInteresse) {
        this.eventosInteresse = eventosInteresse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return isAdmin() == usuario.isAdmin() &&
                Objects.equals(getNome(), usuario.getNome()) &&
                Objects.equals(getContato(), usuario.getContato()) &&
                Objects.equals(getLogin(), usuario.getLogin()) &&
                Objects.equals(getPassword(), usuario.getPassword()) &&
                Objects.equals(getCpf_cnpj(), usuario.getCpf_cnpj()) &&
                Objects.equals(getMeusEventos(), usuario.getMeusEventos()) &&
                Objects.equals(getEventosInteresse(), usuario.getEventosInteresse());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getNome(), getContato(), getLogin(), getPassword(), getCpf_cnpj(), isAdmin(), getMeusEventos(), getEventosInteresse());
    }
}
