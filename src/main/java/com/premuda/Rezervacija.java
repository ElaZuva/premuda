package com.premuda;

import javax.persistence.*;
import java.util.Date; 
import java.sql.Time; 
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "rezervacije")

public class Rezervacija{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="imeRezervacije", nullable=false)
    private String imeRezervacije;

    @Column(name="brojLjudi", nullable=false)
    private int brojLjudi;

    @Column(name="datum", nullable = false)
    private LocalDate datum;

    @Column(name="vrijeme", nullable = false)
    private LocalTime vrijeme;


    @ManyToOne
    @JoinColumn(name="id_stola", nullable = true)
    private Stol stol;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    public String getImeRezervacije(){
        return imeRezervacije;
    }

    public void setImeRezervacije(String imeRezervacije){
        this.imeRezervacije = imeRezervacije;
    }

    public int getBrojLjudi(){
        return brojLjudi;
    }

    public void setBrojLjudi(int brojLjudi){
        this.brojLjudi = brojLjudi;
    }

    public LocalDate getDatum(){
        return datum;
    }

    public void setDatum(LocalDate datum){
        this.datum=datum;
    }

    public LocalTime getVrijeme(){
        return vrijeme;
    }

    public void setVrijeme(LocalTime vrijeme){
        this.vrijeme = vrijeme;
    }
    public Stol getStol(){
        return stol;
    }

    public void setStol(Stol stol){
        this.stol= stol;
    }


}