package com.example.suelliton.agita.model;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String nome;
    private String contato;
    private String login;
    private String password;
    private String cpf_cnpj;
    private boolean admin;
    private String email;
    private List<String> curtidos =  new ArrayList<>();;
    private List<String> irei = new ArrayList<>();
    private List<String> talvez = new ArrayList<>();
    public Usuario() {
    }

    public Usuario(String nome, String contato, String login, String password, String cpf_cnpj, boolean admin, String email) {
        this.nome = nome;
        this.contato = contato;
        this.login = login;
        this.password = password;
        this.cpf_cnpj = cpf_cnpj;
        this.admin = admin;
        this.email = email;
//        this.participarei =  new ArrayList<>();
    }

    public List<String> getTalvez() {
        return talvez;
    }

    public void setTalvez(List<String> talvez) {
        this.talvez = talvez;
    }

    public List<String> getIrei() {
        return irei;
    }

    public void setIrei(List<String> irei) {
        this.irei = irei;
    }

    public List<String> getCurtidos() {
        return curtidos;
    }

    public void setCurtidos(List<String> curtidos) {
        this.curtidos = curtidos;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
