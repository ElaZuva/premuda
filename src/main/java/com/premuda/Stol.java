package com.premuda;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="stolovi")

public class Stol{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="imeStola", nullable = false)
    private String imeStola;

    @Column(name="brojLjudi", nullable = false)
    private int brojLjudi;

    @Column(name="pozicija", nullable = true)
    private boolean pozicija;
   
    @Column(name="sakriven")
    private Boolean sakriven;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getImeStola(){
        return imeStola;
    }

    public void setImeStola(String imeStola){
        this.imeStola = imeStola;
    }

    public int getBrojLjudi(){
        return brojLjudi;
    }

    public void setBrojLjudi(int brojLjudi){
        this.brojLjudi = brojLjudi;
    }

    public boolean getPozicija(){
        return pozicija;
    }

    public void setPozicija(boolean pozicija){
        this.pozicija = pozicija;
    }

    public Boolean getSakriven(){
        return sakriven;
    }

    public void setSakriven(Boolean sakriven){
        this.sakriven = sakriven;
    }


}