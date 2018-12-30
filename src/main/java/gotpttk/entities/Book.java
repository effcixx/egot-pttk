package gotpttk.entities;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "ksiazeczki", schema = "got_pttk", catalog = "")
public class Book {
    private int id;
    private Tourist owner;
    private Collection<BookRoute> bookRoutes;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @ManyToOne
    @JoinColumn(name = "id_turysty", referencedColumnName = "id", nullable = false)
    public Tourist getOwner() {
        return owner;
    }

    public void setOwner(Tourist owner) {
        this.owner = owner;
    }

    @OneToMany(mappedBy = "book")
    public Collection<BookRoute> getBookRoutes() {
        return bookRoutes;
    }

    public void setBookRoutes(Collection<BookRoute> bookRoutes) {
        this.bookRoutes = bookRoutes;
    }
}
