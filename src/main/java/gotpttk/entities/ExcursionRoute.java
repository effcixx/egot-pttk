package gotpttk.entities;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "trasy_wycieczki", schema = "got_pttk", catalog = "")
public class ExcursionRoute {
    private int id;
    private byte isFromStartToEnd;
    private Date dateOfCompletion;
    private Route trasyByIdTrasy;
    private Excursion wycieczkiByIdWycieczki;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "czy_pocz_kon")
    public byte getIsFromStartToEnd() {
        return isFromStartToEnd;
    }

    public void setIsFromStartToEnd(byte isFromStartToEnd) {
        this.isFromStartToEnd = isFromStartToEnd;
    }

    @Basic
    @Column(name = "data_przejscia")
    public Date getDateOfCompletion() {
        return dateOfCompletion;
    }

    public void setDateOfCompletion(Date dateOfCompletion) {
        this.dateOfCompletion = dateOfCompletion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExcursionRoute that = (ExcursionRoute) o;
        return id == that.id &&
                isFromStartToEnd == that.isFromStartToEnd &&
                Objects.equals(dateOfCompletion, that.dateOfCompletion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isFromStartToEnd, dateOfCompletion);
    }

    @ManyToOne
    @JoinColumn(name = "id_trasy", referencedColumnName = "id", nullable = false)
    public Route getTrasyByIdTrasy() {
        return trasyByIdTrasy;
    }

    public void setTrasyByIdTrasy(Route trasyByIdTrasy) {
        this.trasyByIdTrasy = trasyByIdTrasy;
    }

    @ManyToOne
    @JoinColumn(name = "id_wycieczki", referencedColumnName = "id", nullable = false)
    public Excursion getWycieczkiByIdWycieczki() {
        return wycieczkiByIdWycieczki;
    }

    public void setWycieczkiByIdWycieczki(Excursion wycieczkiByIdWycieczki) {
        this.wycieczkiByIdWycieczki = wycieczkiByIdWycieczki;
    }
}
