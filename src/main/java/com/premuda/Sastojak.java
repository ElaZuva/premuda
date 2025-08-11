package com.premuda;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="sastojak")

public class Sastojak{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="imeSastojka", nullable = false)
    private String imeSastojka;

    @Column(name="unit")
    private String unit;

    @Column(name="kolicina")
    private Integer kolicina;

    @Column(name="stanje")
    private double stanje;

    @Column(name="sakriven")
    private Boolean sakriven;

    public Long getId() {
        return id;
    }

    public String getImeSastojka(){
        return imeSastojka;
    }

    public void setImeSastojka(String imeSastojka){
        this.imeSastojka = imeSastojka;
    }

    public String getUnit(){
        return unit;
    }

    public void setUnit(String unit){
        this.unit = unit;
    }

    public Integer getKolicina(){
        return kolicina;
    }

    public void setKolicina(Integer kolicina){
        this.kolicina = kolicina;
    }
    public double getStanje(){
        return stanje;
    }

    public void setStanje(double stanje){
        this.stanje = stanje;
    }

    public Boolean getSakriven(){
        return sakriven;
    }

    public void setSakriven(Boolean sakriven){
        this.sakriven = sakriven;
    }

}