package com.premuda;

import javax.persistence.*;
import java.util.Date; // Ovo je ispravna klasa za datum
import java.sql.Time; 
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "narudzbe")

public class Narudzba{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="id_rezervacije", nullable = true)
    private Rezervacija rezervacija;

    @ManyToOne
    @JoinColumn(name="id_jela", nullable = true)
    private Jelovnik jelo;

    @Column(name="kolicina", nullable = false)
    private int kolicina;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    public int getKolicina(){
        return kolicina;
    }

    public void setKolicina(int kolicina){
        this.kolicina = kolicina;
    }

    public Rezervacija getRezervacija(){
        return rezervacija;
    }

    public void setRezervacija(Rezervacija rezervacija){
        this.rezervacija = rezervacija;
    }

    public Jelovnik getJelo(){
        return jelo;
    }

    public void setJelo(Jelovnik jelo){
        this.jelo=jelo;
    }


}