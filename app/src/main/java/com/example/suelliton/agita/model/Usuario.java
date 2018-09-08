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
    private String email;
    private List<Evento> meusEventos;
    private List<String> eventosInteresse;

    public Usuario() {
    }

    public Usuario(String nome, String email, String contato, String cpf_cnpj, String login, String password, boolean admin) {
        this.nome = nome;
        this.email = email;
        this.contato = contato;
        this.login = login;
        this.password = password;
        this.cpf_cnpj = cpf_cnpj;
        this.admin = admin;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
                Objects.equals(getCpf_cnpj(), usuario.getCpf_cnpj()) ;
    }

    @Override
    public int hashCode() {

        return Objects.hash(getNome(), getContato(), getLogin(), getPassword(), getCpf_cnpj(), isAdmin());
    }
}
