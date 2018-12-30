package gotpttk.entities;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "trasy_ksiazeczki", schema = "got_pttk", catalog = "")
public class BookRoute {
    private int id;
    private byte isFromStartToEnd;
    private Date dateOfCompletion;
    private Category currentBadgeCategory;
    private Route route;
    private Book book;

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
        BookRoute bookRoute = (BookRoute) o;
        return id == bookRoute.id &&
                isFromStartToEnd == bookRoute.isFromStartToEnd &&
                Objects.equals(dateOfCompletion, bookRoute.dateOfCompletion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isFromStartToEnd, dateOfCompletion);
    }

    @ManyToOne
    @JoinColumn(name = "w_ramach_odznaki", referencedColumnName = "id", nullable = false)
    public Category getCurrentBadgeCategory() {
        return currentBadgeCategory;
    }

    public void setCurrentBadgeCategory(Category currentBadgeCategory) {
        this.currentBadgeCategory = currentBadgeCategory;
    }

    @ManyToOne
    @JoinColumn(name = "id_trasy", referencedColumnName = "id", nullable = false)
    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    @ManyToOne
    @JoinColumn(name = "id_ksiazeczki", referencedColumnName = "id", nullable = false)
    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
