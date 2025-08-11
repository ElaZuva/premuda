package com.premuda;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.time.MonthDay;
import java.time.Year;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.time.LocalTime;
import java.time.LocalDate;
import javax.persistence.Persistence;
import java.util.List;
import java.time.DayOfWeek;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;


public class Service {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("BazaPU");

    //stvaranje novog jela u jelovniku
    public void dodajJelo(Jelovnik jelo) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(jelo);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    //stvaranje novog sastojka u jelovniku
    public void dodajSastojak(Sastojak sastojak) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(sastojak);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    public void dodajRecept(Recept recept) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(recept);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    //dodaje jelo u narudbu. Prvo provjerava postoji li to jelo već u narudžbi, ako postoji, dodaje kolicini, inace radi novi zapis
    public void dodajNarudzba(Narudzba narudzba) {
        Long jeloId             = narudzba.getJelo().getId();
        List<Recept> recepti    = findReceptByJeloId(jeloId);
        double kolicinaNarudzbe  = narudzba.getKolicina();

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            TypedQuery<Narudzba> query = em.createQuery(
                "SELECT n FROM Narudzba n WHERE n.rezervacija = :rez AND n.jelo = :jelo",
                Narudzba.class
            );
            query.setParameter("rez", narudzba.getRezervacija());
            query.setParameter("jelo", narudzba.getJelo());

            List<Narudzba> existing = query.getResultList();
            if (!existing.isEmpty()) {
                Narudzba postojeca = existing.get(0);
                postojeca.setKolicina(postojeca.getKolicina() + narudzba.getKolicina());
                em.merge(postojeca);
            } else {
                em.persist(narudzba);
            }

            for (Recept recept : recepti) {
                double kolicinaRecept = recept.getKolicina();
                Sastojak original     = recept.getSastojak();

                double novoStanje = original.getStanje()
                                    - (kolicinaNarudzbe * kolicinaRecept);

                Sastojak managed = em.find(Sastojak.class, original.getId());
                managed.setStanje(novoStanje);
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }



    public void updateRecept(Recept recept) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            TypedQuery<Recept> query = em.createQuery(
                    "SELECT r FROM Recept r WHERE r.jelo = :j AND r.sastojak = :s", 
                    Recept.class
            );
            query.setParameter("j", recept.getJelo());
            query.setParameter("s", recept.getSastojak());

            List<Recept> lista = query.getResultList();
            if (!lista.isEmpty()) {
                Recept postojeca = lista.get(0);
                postojeca.setKolicina(recept.getKolicina());
                em.merge(postojeca);
            } else {
                em.persist(recept);
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void updateSastojak(Sastojak sastojak) {
    EntityManager em = emf.createEntityManager();
    try {
        em.getTransaction().begin();

        Sastojak postojeca = em.find(Sastojak.class, sastojak.getId());
        if (postojeca != null) {
            postojeca.setStanje(sastojak.getStanje()+postojeca.getStanje());
            em.merge(postojeca);
        } else {
            em.persist(sastojak);
        }

        em.getTransaction().commit();
    } finally {
        em.close();
    }
}


    public void dodajStol(Stol stol){
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(stol);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void updateNarudzba(Narudzba n, int brojac) {
    Long jeloId        = n.getJelo().getId();
    double kolicinaJelo = n.getKolicina();  

    List<Recept> recepti = findReceptByJeloId(jeloId);

    EntityManager em = emf.createEntityManager();
    try {
        em.getTransaction().begin();

        TypedQuery<Narudzba> q = em.createQuery(
            "SELECT x FROM Narudzba x WHERE x.jelo = :j AND x.rezervacija = :r",
            Narudzba.class
        );
        q.setParameter("j", n.getJelo());
        q.setParameter("r", n.getRezervacija());
        List<Narudzba> existing = q.getResultList();

        if (!existing.isEmpty()) {
            Narudzba postojeca = existing.get(0);
            postojeca.setKolicina(n.getKolicina());
            em.merge(postojeca);
        } else {
            em.persist(n);
        }

        for (Recept recept : recepti) {
            double kolicinaRecept = recept.getKolicina();
            Sastojak original     = recept.getSastojak();

            double novoStanje = original.getStanje()
                + (brojac * kolicinaRecept);
            

            Sastojak managed = em.find(Sastojak.class, original.getId());
            managed.setStanje(novoStanje);
        }

        em.getTransaction().commit();
    } finally {
        em.close();
    }
}


    //dodaje novu rezervaciju
    public void dodajRezervaciju(Rezervacija rez){
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(rez);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    //Vraća sva jela iz jelovnika koja su određene kategorije
    public List<Jelovnik> getStavkeKategorije(String kategorija) {
        EntityManager em = emf.createEntityManager();
        List<Jelovnik> stavkeKategorije = new ArrayList<Jelovnik>();
        try {
            List<Jelovnik> jelovnik = em.createQuery("SELECT j FROM Jelovnik j WHERE j.kategorija = :kategorija and j.sakriven=false ORDER BY j.imeArtikla ASC", Jelovnik.class)
                     .setParameter("kategorija", kategorija)
                     .getResultList();
            for (Jelovnik jelo : jelovnik){
                if (dostupnoJela(jelo) > 0){
                    stavkeKategorije.add(jelo);
                }
            }
            return stavkeKategorije;
        } finally {
            em.close();
        }
    }

    public Rezervacija findRezervacijaById(Long id) {
        EntityManager em = emf.createEntityManager();
        return em.find(Rezervacija.class, id);
    }

    public Jelovnik findJeloById(Long id) {
        EntityManager em = emf.createEntityManager();
        return em.find(Jelovnik.class, id);
    }

    //pronalazi listu jela (narudzbu) koju je neka rezervacija narucila
    public List<Narudzba> findNarudzbeByRezervacijaId(Long rezId) {
        EntityManager em = emf.createEntityManager();
        return em.createQuery(
                "SELECT n FROM Narudzba n WHERE n.rezervacija.id = :rezId", Narudzba.class)
                .setParameter("rezId", rezId)
                .getResultList();
    }

    public List<Recept> findReceptByJeloId(Long jeloId) {
        EntityManager em = emf.createEntityManager();
        return em.createQuery(
                "SELECT r FROM Recept r WHERE r.jelo.id = :jeloId", Recept.class)
                .setParameter("jeloId", jeloId)
                .getResultList();
    }

    public Recept findReceptByJelo(Long jeloId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT r FROM Recept r WHERE r.jelo.id = :jeloId", 
                    Recept.class)
                .setParameter("jeloId", jeloId)
                .setMaxResults(1)            
                .getSingleResult();          
        
        } finally {
            em.close();
        }
    }


    public void updateJelo(Long idJelo, String novoIme, String novaKat, String novaPotkat, double novaCijena, String noviUnit) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Jelovnik jelo = em.find(Jelovnik.class, idJelo);
            if (jelo != null) {
                jelo.setImeArtikla(novoIme);
                jelo.setKategorija(novaKat);
                jelo.setPotkategorija(novaPotkat);
                jelo.setCijena(novaCijena);
                jelo.setUnit(noviUnit);

                em.merge(jelo);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    

    public List<Object[]> getRezervacijaCountByDate() {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT r.datum, COUNT(r) FROM Rezervacija r GROUP BY r.datum";
            
            return em.createQuery(jpql, Object[].class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public int getBrojRezervacijaPoDanu(LocalDate datum) {
        EntityManager em = emf.createEntityManager();
        try {

            String jpql = "SELECT COUNT(r) FROM Rezervacija r WHERE r.datum = :datum";
            
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("datum", datum);

            Long rezultat = query.getSingleResult();

            return (rezultat != null) ? rezultat.intValue() : 0;
        } finally {
            em.close();
        }
    }

    public int getBrojLjudiPoDanu(LocalDate datum) {
        EntityManager em = emf.createEntityManager();
        try {

            String jpql = "SELECT SUM(r.brojLjudi) FROM Rezervacija r WHERE r.datum = :datum";

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("datum", datum);

            Long rezultat = query.getSingleResult();
            return (rezultat != null) ? rezultat.intValue() : 0;
        } finally {
            em.close();
        }
    }

    public List<LocalDate> getDatumeSRezervacijom() {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT DISTINCT r.datum FROM Rezervacija r ORDER BY r.datum";

            TypedQuery<LocalDate> query = em.createQuery(jpql, LocalDate.class);

            return query.getResultList();
        } finally {
            em.close();
        }
    }


    public double ukupnaCijenaRezervacije(Long rezId) {
        EntityManager em = emf.createEntityManager();
        try {
            double suma=0;
            List<Narudzba> narudzba = em.createQuery("SELECT n FROM Narudzba n WHERE n.rezervacija.id=:rezId", Narudzba.class)
                    .setParameter("rezId", rezId)
                    .getResultList();
            for(Narudzba stavka : narudzba){
                Jelovnik jelo = stavka.getJelo();
                double cijena = jelo.getCijena();
                int kolicina = stavka.getKolicina();
                suma = suma + cijena*kolicina;
                }
            return suma;
        } finally {
            em.close();
        }
    }

    public List<Object[]> getRezervacijaCountByDayOfWeek() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Rezervacija> sveRezervacije = em.createQuery("SELECT r FROM Rezervacija r", Rezervacija.class)
                                                .getResultList();

            Map<DayOfWeek, Long> dayOfWeekCountMap = sveRezervacije.stream()
                .collect(Collectors.groupingBy(
                    r -> r.getDatum().getDayOfWeek(), 
                    Collectors.counting()                         
                ));

            List<Object[]> results = new ArrayList<>();
            for (Map.Entry<DayOfWeek, Long> entry : dayOfWeekCountMap.entrySet()) {
                results.add(new Object[]{ entry.getKey(), entry.getValue() });
            }


            results.sort(Comparator.comparing(o -> ((DayOfWeek) o[0]).getValue()));

            return results;
        } finally {
            em.close();
        }
    }
    
    public List<Object[]> getDnevnaZarada() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Narudzba> narudzbe = em.createQuery(
                "SELECT n FROM Narudzba n", Narudzba.class
            ).getResultList();

            List<Object[]> rezultat = new ArrayList<>();
            Map<LocalDate, Double> ukupnaCijenaDan = new HashMap<>();

            for (Narudzba n : narudzbe) {
                LocalDate datum = n.getRezervacija().getDatum();
                double cijena = n.getJelo().getCijena();
                int kolicina = n.getKolicina();
                double ukupnaCijena = cijena * kolicina;
                ukupnaCijenaDan.merge(datum, ukupnaCijena, Double::sum);
            }

            for (Map.Entry<LocalDate, Double> entry : ukupnaCijenaDan.entrySet()) {
                LocalDate dan = entry.getKey();
                double c = entry.getValue();
                rezultat.add(new Object[]{dan, c});
            }

            return rezultat;
        } finally {
            em.close();
        }
    }

    public Map<LocalDate, Integer> getBrojLjudiPoDanu() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Rezervacija> rezervacije = em.createQuery(
                "SELECT r FROM Rezervacija r", Rezervacija.class
            ).getResultList();

            Map<LocalDate, Integer> ukupnoLjudiDan = new HashMap<>();

            for (Rezervacija r : rezervacije) {
                LocalDate datum = r.getDatum();
                Integer brojLjudi = r.getBrojLjudi();
                ukupnoLjudiDan.merge(datum, brojLjudi, Integer::sum);
            }

            return ukupnoLjudiDan;
        } finally {
            em.close();
        }
    } 

    public Double PredvidanjeZarade(LocalDate d){
        List<Object[]> zarade = new ArrayList<>();
        Map<LocalDate, Integer> ljudi = new HashMap<>();
        
        zarade = getDnevnaZarada();
        ljudi=getBrojLjudiPoDanu();

        double xSum = 0.0, ySum=0.0, xxSum = 0.0, xySum=0.0, yySum=0.0;
        int n=0;

        for (Object[] row : zarade){
            
            LocalDate datum = (LocalDate) row[0];
            Number yNum = (Number) row[1];
            double cijena = yNum.doubleValue();
            Integer xNum = ljudi.get(datum);
            double brojLjudi = xNum.doubleValue();
            xSum= xSum + brojLjudi;
            ySum = ySum + cijena;
            xxSum = xxSum + brojLjudi*brojLjudi;
            yySum = yySum + cijena*cijena;
            xySum = xySum + cijena*brojLjudi;
            n=n+1;

            
        }

        if (n<2) return null;
        double denom = n * xxSum - xSum * xSum;
        if (denom == 0.0) return null; 

        double b1 = (n * xySum - xSum * ySum) / denom;
        double b0 = (ySum - b1 * xSum) / n;

        int brojLjudiNaDan = ljudi.get(d);
        Double rezultat = b0+b1*brojLjudiNaDan;
        return rezultat;
    }
    public List<Object[]> PredvidanjeZaradeUBuducnosti(){
        EntityManager em = emf.createEntityManager();
        
        LocalDate od = LocalDate.of(2022, 1,1);
        try{
            List<LocalDate> datumi = em.createQuery(
                "SELECT DISTINCT r.datum FROM Rezervacija r WHERE r.datum >= :od ORDER BY r.datum", LocalDate.class)
                .setParameter("od", od)
                .getResultList();
            Map<LocalDate, Integer> ljudiPoDanu = getBrojLjudiPoDanu();
            List<Object[]> rezultat = new ArrayList<>(datumi.size());
            for (LocalDate datum : datumi){
                Double predvidanje = PredvidanjeZarade(datum);
                Integer brojLjudi = ljudiPoDanu.get(datum);
                rezultat.add(new Object[] {datum, predvidanje, brojLjudi});
            }
            return rezultat;
            
        }
        finally{
            em.close();
        }
    }
    public List<Object[]> getProsjekDnevneZarade() {
        List<Object[]> rezultat = new ArrayList<>();
        List<Object[]> sveGodine = getDnevnaZarada(); 

        Map<MonthDay, Double> zbrojProslih = new HashMap<>();
        Map<MonthDay, Integer> ukupnoProslih = new HashMap<>();
        Map<MonthDay, Double> iznosOve = new HashMap<>();
        Map<MonthDay, Double> prosjekProslih = new HashMap<>();

        int tekucaGodina = Year.now().getValue();

        for (Object[] row : sveGodine) {
            LocalDate datum = (LocalDate) row[0];
            double iznos = ((Number) row[1]).doubleValue();
            MonthDay dan = MonthDay.from(datum);

            if (datum.getYear() == tekucaGodina) {
                iznosOve.merge(dan, iznos, Double::sum);
            } else {
                zbrojProslih.merge(dan, iznos, Double::sum);
                ukupnoProslih.merge(dan, 1, Integer::sum);
            }
        }

        for (Map.Entry<MonthDay, Integer> e : ukupnoProslih.entrySet()) {
            MonthDay dan = e.getKey();
            double sum = zbrojProslih.getOrDefault(dan, 0.0);
            int cnt = e.getValue();
            if (cnt > 0) prosjekProslih.put(dan, sum / cnt);
        }

        for (Map.Entry<MonthDay, Double> e : iznosOve.entrySet()) {
            MonthDay md = e.getKey();
            double ove = e.getValue();
            double prosli = prosjekProslih.getOrDefault(md, 0.0);
            double razlika = ove - prosli;
            
            LocalDate datum = md.atYear(tekucaGodina);
            Double predvidanje= PredvidanjeZarade(datum);
            rezultat.add(new Object[]{datum, prosli, ove, predvidanje, razlika});
        }

        rezultat.sort(Comparator.comparing(o -> (LocalDate) o[0]));

        return rezultat;
    }


    public List<Object[]> prosjecnaPotrosnjaSastojka(LocalDate datum, int brojDana) {
        Map<Sastojak, Double> ukupnaPotrosnja = new HashMap<>();
        Map<Sastojak, Integer> brojGodinaPoSastojku = new HashMap<>();
        int trenutnaGodina = LocalDate.now().getYear();
        EntityManager em = emf.createEntityManager();

        try {
            for (int godina = trenutnaGodina - 1; godina > trenutnaGodina - 5; godina--) {
                LocalDate pocetni = datum.withYear(godina);
                LocalDate kraj = pocetni.plusDays(brojDana - 1);

                List<Narudzba> narudzbe = em.createQuery(
                        "SELECT n FROM Narudzba n WHERE n.rezervacija.datum BETWEEN :pocetni AND :kraj", Narudzba.class)
                        .setParameter("pocetni", pocetni)
                        .setParameter("kraj", kraj)
                        .getResultList();

                if (narudzbe.isEmpty()) continue;

                Map<Sastojak, Double> potrosnjaUGodini = new HashMap<>();

                for (Narudzba n : narudzbe) {
                    Jelovnik jelo = n.getJelo();
                    int kolicinaUNarudzbi = n.getKolicina();

                    List<Recept> recepti = em.createQuery(
                            "SELECT r FROM Recept r WHERE r.jelo = :jelo", Recept.class)
                            .setParameter("jelo", jelo)
                            .getResultList();

                    for (Recept r : recepti) {
                        Sastojak s = r.getSastojak();
                        double kolicinaSastojka = r.getKolicina() * kolicinaUNarudzbi;

                        potrosnjaUGodini.merge(s, kolicinaSastojka, Double::sum);
                    }
                }

                for (Map.Entry<Sastojak, Double> entry : potrosnjaUGodini.entrySet()) {
                    Sastojak s = entry.getKey();
                    double kolicina = entry.getValue();
                    ukupnaPotrosnja.merge(s, kolicina, Double::sum);
                    brojGodinaPoSastojku.merge(s, 1, Integer::sum);
                }
            }

            List<Object[]> rezultat = new ArrayList<>();
            for (Map.Entry<Sastojak, Double> entry : ukupnaPotrosnja.entrySet()) {
                Sastojak s = entry.getKey();
                double ukupno = entry.getValue();
                int brojGodina = brojGodinaPoSastojku.getOrDefault(s, 1);
                double prosjek = ukupno / brojGodina;
                double razlika = prosjek - s.getStanje();
                if(razlika>0){
                    rezultat.add(new Object[]{s.getImeSastojka(), prosjek, s.getStanje(), razlika});
                }

            }

            return rezultat;
        } finally {
            em.close();
        }
    }


    public List<Object[]> getProsjecanBrojLjudiPoDanuUTjednu() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Rezervacija> sveRezervacije = em.createQuery(
                "SELECT r FROM Rezervacija r", Rezervacija.class
            ).getResultList();

     
            Map<DayOfWeek, Double> prosjekPoDanu = sveRezervacije.stream()
                .collect(Collectors.groupingBy(
                    r -> r.getDatum().getDayOfWeek(),        
                    Collectors.averagingInt(Rezervacija::getBrojLjudi) 
                ));

            List<Object[]> rezultati = new ArrayList<>();
            for (Map.Entry<DayOfWeek, Double> entry : prosjekPoDanu.entrySet()) {
                rezultati.add(new Object[] { entry.getKey(), entry.getValue() });
            }

            rezultati.sort(Comparator.comparing(o -> ((DayOfWeek) o[0]).getValue()));

            return rezultati;
        } finally {
            em.close();
        }
    }

    public List<Object[]> getProsjecnaRezervacijaByDayOfWeek() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Rezervacija> sveRezervacije = em.createQuery(
                    "SELECT r FROM Rezervacija r", Rezervacija.class
            ).getResultList();

            Map<DayOfWeek, Map<LocalDate, Long>> dayOfWeekToDateCounts = sveRezervacije.stream()
                    .collect(Collectors.groupingBy(
                            r -> r.getDatum().getDayOfWeek(), 
                            Collectors.groupingBy(
                                    r -> r.getDatum(),          
                                    Collectors.counting()       
                            )
                    ));

            List<Object[]> results = new ArrayList<>();

            for (Map.Entry<DayOfWeek, Map<LocalDate, Long>> entry : dayOfWeekToDateCounts.entrySet()) {
                DayOfWeek day = entry.getKey();
                Map<LocalDate, Long> dateCountMap = entry.getValue();

                long totalReservations = dateCountMap.values()
                                                    .stream()
                                                    .mapToLong(Long::longValue)
                                                    .sum();
                int numberOfDates = dateCountMap.size();

                double avg = (double) totalReservations / numberOfDates;

                results.add(new Object[]{ day, avg });
            }

            results.sort(Comparator.comparing(o -> ((DayOfWeek) o[0]).getValue()));

            return results;
        } finally {
            em.close();
        }
    }


    public List<Stol> getAllStolovi() {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT s FROM Stol s where s.sakriven=false", Stol.class).getResultList();
    }
    public List<Sastojak> getAllSastojci() {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT s FROM Sastojak s ORDER BY s.imeSastojka", Sastojak.class).getResultList();
    }


    public List<Stol> getSlobodneStolove(Rezervacija rezervacija) {
        EntityManager em = emf.createEntityManager();

        LocalDate datum = rezervacija.getDatum();
        LocalTime vrijeme = rezervacija.getVrijeme();

        LocalTime dvaSataPrije = vrijeme.minusHours(2);
        LocalTime dvaSataPoslije = vrijeme.plusHours(2);

        return em.createQuery("SELECT s FROM Stol s WHERE s.brojLjudi >= :brLjudi " +
                "AND s.id NOT IN (" +
                "SELECT r.stol.id FROM Rezervacija r " +
                "WHERE r.datum = :datum " +
                "AND (r.vrijeme BETWEEN :vrijemePrije AND :vrijemePoslije)) " +
                "ORDER BY s.brojLjudi ASC", Stol.class)
                .setParameter("brLjudi", rezervacija.getBrojLjudi())
                .setParameter("datum", datum)
                .setParameter("vrijemePrije", dvaSataPrije)
                .setParameter("vrijemePoslije", dvaSataPoslije)
                .getResultList();
    }

    public List<Rezervacija> getRezervacijeZaStol(Long stolId, LocalDate datum){
        EntityManager em = emf.createEntityManager();

        return em.createQuery("SELECT r FROM Rezervacija r WHERE r.stol.id = :stolId AND r.datum = :datum", Rezervacija.class)
                .setParameter("stolId", stolId)
                .setParameter("datum", datum)
                .getResultList();
        
    }

    public double getDecimalnoVrijeme(LocalTime vrijeme){
        int sati = vrijeme.getHour();
        int minute = vrijeme.getMinute();

        double decimalniSat = sati + (minute / 60.0);
        return decimalniSat;
    }
    public boolean jeDjeljivoSaDva(double broj) {
        double ostatak = broj % 2.0;
        return Math.abs(ostatak) < 0.0001 || Math.abs(ostatak - 2.0) < 0.0001;
    }

    public Stol nadiPrviStol(List<Stol> slobodni, Rezervacija rezervacija) {
        Map<Stol, Double> funkcijaGubitka = new HashMap<>();
        Map<Stol, Double> dodatakPrije = new HashMap<>();
        Map<Stol, Double> dodatakPoslije = new HashMap<>();

        double vrijemePocetka = getDecimalnoVrijeme(rezervacija.getVrijeme());
        double vrijemeKraja = vrijemePocetka + 2;

        for (Stol stol : slobodni) {
            List<Rezervacija> rezervacije = getRezervacijeZaStol(stol.getId(), rezervacija.getDatum());

            double minPrije = Double.MAX_VALUE;
            double minPoslije = Double.MAX_VALUE;
            boolean postojiPrije = false;
            boolean postojiPoslije = false;

            for (Rezervacija r : rezervacije) {
                double vrijemeR = getDecimalnoVrijeme(r.getVrijeme());

                if (vrijemeR < vrijemePocetka) {
                    postojiPrije = true;
                    double razmak = vrijemePocetka - (vrijemeR + 2); 
                    minPrije = Math.min(minPrije, razmak);
                } else {
                    postojiPoslije = true;
                    double razmak = vrijemeR - vrijemeKraja; 
                    minPoslije = Math.min(minPoslije, razmak);
                }
            }

            if (!postojiPrije) {
                minPrije = vrijemePocetka;
            }
            if (!postojiPoslije) {
                minPoslije = 24 - vrijemeKraja;
            }

            double dodatak1;
            if (jeDjeljivoSaDva(minPrije)) {
                dodatak1 = 0.0;
            } else if (minPrije < 1.0) {
                dodatak1 = 0.49;
            } else {
                dodatak1 = 1.0 / (2 * minPrije);
            }
            dodatakPrije.put(stol, dodatak1);

            double dodatak2;
            if (jeDjeljivoSaDva(minPoslije)) {
                dodatak2 = 0.0;
            } else if (minPoslije < 1.0) {
                dodatak2 = 0.49;
            } else {
                dodatak2 = 1.0 / (2 * minPoslije);
            }
            dodatakPoslije.put(stol, dodatak2);

            double gubitak = stol.getBrojLjudi() - rezervacija.getBrojLjudi() + dodatak1 + dodatak2;
            funkcijaGubitka.put(stol, gubitak);
        }

        return funkcijaGubitka.entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public int dostupnoJela(Jelovnik jelo) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Recept> recepti = em.createQuery(
                "SELECT r FROM Recept r WHERE r.jelo.id = :jeloId", Recept.class)
                .setParameter("jeloId", jelo.getId())
                .getResultList();

            int minimum = Integer.MAX_VALUE;

            for (Recept recept : recepti) {
                Sastojak sastojak = recept.getSastojak();
                double stanje = sastojak.getStanje();
                double potrebnaKolicina = recept.getKolicina();

                if (potrebnaKolicina == 0) continue; 

                int dostupno = (int) Math.floor(stanje / potrebnaKolicina);
                if (dostupno < minimum) {
                    minimum = dostupno;
                }
            }

            return recepti.isEmpty() ? 0 : minimum;
        } finally {
            em.close();
        }
    }


    //prodano jela na određeni dan
    public int prodanoJelaUDanu(Long jeloId, LocalDate datum) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT COALESCE(SUM(n.kolicina), 0) " +
                        "FROM Narudzba n JOIN n.rezervacija r " +
                        "WHERE r.datum = :datum AND n.jelo.id = :jeloId";

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("datum", datum);
            query.setParameter("jeloId", jeloId);

            Long ukupno = query.getSingleResult();
            return ukupno.intValue(); 
        } finally {
            em.close();
        }
    }

    public double prosjekProdajeJelaUDanu(Long jeloId, LocalDate datum) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT COALESCE(AVG(n.kolicina), 0) " +
                        "FROM Narudzba n JOIN n.rezervacija r " +
                        "WHERE FUNCTION('YEAR', r.datum) BETWEEN :startYear AND :currentYear " +
                        "AND FUNCTION('MONTH', r.datum) = :month " +
                        "AND FUNCTION('DAY', r.datum) = :day " +
                        "AND n.jelo.id = :jeloId";

            int currentYear = datum.getYear();
            int startYear = currentYear - 5;

            TypedQuery<Double> query = em.createQuery(jpql, Double.class);
            query.setParameter("startYear", startYear);
            query.setParameter("currentYear", currentYear);
            query.setParameter("month", datum.getMonthValue());
            query.setParameter("day", datum.getDayOfMonth());
            query.setParameter("jeloId", jeloId);

            Double prosjek = query.getSingleResult();
            return prosjek;
        } finally {
            em.close();
        }
    }


    public List<Rezervacija> getAllRezervacije() {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT r FROM Rezervacija r ORDER BY datum, vrijeme ASC", Rezervacija.class).getResultList();
    }
    public List<Rezervacija> getRezervacijePoDatumu(LocalDate datum) {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT r FROM Rezervacija r WHERE datum=:datum ORDER BY datum, vrijeme ASC", Rezervacija.class)
        .setParameter("datum", datum)
        .getResultList();
    }
    public List<Rezervacija> getRezervacijePoslijeDatuma(LocalDate datum) {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT r FROM Rezervacija r WHERE datum>=:datum ORDER BY datum, vrijeme ASC", Rezervacija.class)
        .setParameter("datum", datum)
        .getResultList();
    }
    public List<Rezervacija> getRezervacijePrijeDatuma(LocalDate datum) {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("SELECT r FROM Rezervacija r WHERE datum<:datum ORDER BY datum, vrijeme ASC", Rezervacija.class)
        .setParameter("datum", datum)
        .getResultList();
    }

    public void ukloniSveRezervacije() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.createQuery("DELETE FROM Rezervacija").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    public void ukloniSvaJela() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.createQuery("DELETE FROM Jelovnik").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    public void ukloniSveNarudzbe() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.createQuery("DELETE FROM Narudzba").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    public void ukloniSveStolove() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.createQuery("DELETE FROM Stol").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    public void deleteRezervacija(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            int removed = em.createQuery(
                    "DELETE FROM Narudzba n WHERE n.rezervacija.id = :rezId")
                .setParameter("rezId", id)
                .executeUpdate();

            Rezervacija rez = em.find(Rezervacija.class, id);
            if (rez != null) {
                em.remove(rez);
            }

            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteJelo(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            int removed_narudzba = em.createQuery(
                    "DELETE FROM Narudzba n WHERE n.jelo.id = :jeloId")
                .setParameter("jeloId", id)
                .executeUpdate();
            int removed_recept = em.createQuery(
                    "DELETE FROM Recept r WHERE r.jelo.id = :jeloId")
                .setParameter("jeloId", id)
                .executeUpdate();

            Jelovnik jelo = em.find(Jelovnik.class, id);
            if (jelo != null) {
                em.remove(jelo);
            }

            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteNarudzba(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Narudzba narudzba = em.find(Narudzba.class, id);
            if (narudzba != null) {
                em.remove(narudzba);
            }

            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteRecept(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Recept recept = em.find(Recept.class, id);
            if (recept != null) {
                em.remove(recept);
            }

            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    public void deleteSastojak(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            int removed = em.createQuery(
                    "DELETE FROM Recept n WHERE n.sastojak.id = :rezId")
                .setParameter("rezId", id)
                .executeUpdate();

            Sastojak rez = em.find(Sastojak.class, id);
            if (rez != null) {
                em.remove(rez);
            }

            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

}
