package fr.millenium_blitz.projectpicars.util.sql;

import java.io.Serializable;

public class Connexion implements Serializable{

    private int id;

    private String adresse;

    public int getId() {
        return id;
    }

    public void setId(int pId) {
        this.id = pId;
    }

    void setAdresse(String pAdresse) {
        this.adresse = pAdresse;
    }

    @Override
    public String toString() {
        return adresse;
    }
}
