package gotpttk.entities;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "wycieczki", schema = "got_pttk", catalog = "")
public class Excursion {
    private int id;
    private String name;
    private Collection<ExcursionRoute> excursionRoutes;
    private Tourist owner;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "nazwa")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Excursion excursion = (Excursion) o;
        return id == excursion.id &&
                Objects.equals(name, excursion.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @OneToMany(mappedBy = "wycieczkiByIdWycieczki")
    public Collection<ExcursionRoute> getExcursionRoutes() {
        return excursionRoutes;
    }

    public void setExcursionRoutes(Collection<ExcursionRoute> excursionRoutes) {
        this.excursionRoutes = excursionRoutes;
    }

    @ManyToOne
    @JoinColumn(name = "id_turysty", referencedColumnName = "id", nullable = false)
    public Tourist getOwner() {
        return owner;
    }

    public void setOwner(Tourist owner) {
        this.owner = owner;
    }
}
