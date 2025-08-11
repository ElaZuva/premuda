package com.premuda;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(
    name = "recept",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_recept_sastojak_jelo",
        columnNames = { "id_sastojka", "id_jela" }
    )
)
public class Recept {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)   
    @JoinColumn(name = "id_sastojka", nullable = false)
    private Sastojak sastojak;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_jela", nullable = false)
    private Jelovnik jelo;

    @Column(name = "kolicina", precision = 10, scale = 4, nullable = false)
    private double kolicina;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sastojak getSastojak() {
        return sastojak;
    }

    public void setSastojak(Sastojak sastojak) {
        this.sastojak = sastojak;
    }

    public Jelovnik getJelo() {
        return jelo;
    }

    public void setJelo(Jelovnik jelo) {
        this.jelo = jelo;
    }

    public double getKolicina() {
        return kolicina;
    }

    public void setKolicina(double kolicina) {
        this.kolicina = kolicina;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recept)) return false;
        Recept other = (Recept) o;
        return Objects.equals(sastojak, other.sastojak) &&
               Objects.equals(jelo,    other.jelo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sastojak, jelo);
    }
}
