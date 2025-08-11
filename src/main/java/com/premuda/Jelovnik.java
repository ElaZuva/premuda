package com.premuda;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="jelovnik")

public class Jelovnik{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="imeArtikla", nullable = false)
    private String imeArtikla;

    @Column(name="kategorija", nullable = false)
    private String kategorija;

    @Column(name="potkategorija", nullable = false)
    private String potkategorija;

    @Column(name="unit")
    private String unit;

    @Column(name="cijena", nullable = false)
    private double cijena;

    @Column(name="kolicina")
    private int kolicina;

    @Column(name="sakriven")
    private Boolean sakriven;

    public Long getId() {
        return id;
    }

    public String getImeArtikla(){
        return imeArtikla;
    }

    public void setImeArtikla(String imeArtikla){
        this.imeArtikla = imeArtikla;
    }

    public String getKategorija(){
        return kategorija;
    }

    public void setKategorija(String kategorija){
        this.kategorija = kategorija;
    }

    public String getPotkategorija(){
        return potkategorija;
    }

    public void setPotkategorija(String potkategorija){
        this.potkategorija = potkategorija;
    }

    public String getUnit(){
        return unit;
    }

    public void setUnit(String unit){
        this.unit = unit;
    }

    public double getCijena(){
        return cijena;
    }

    public void setCijena(double cijena){
        this.cijena = cijena;
    }

    public int getKolicina(){
        return kolicina;
    }

    public void setKolicina(int kolicina){
        this.kolicina = kolicina;
    }

    public Boolean getSakriven(){
        return sakriven;
    }

    public void setSakriven(Boolean sakriven){
        this.sakriven = sakriven;
    }

}