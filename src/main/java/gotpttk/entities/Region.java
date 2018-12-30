package gotpttk.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "regiony", schema = "got_pttk", catalog = "")
public class Region {
    private int id;
    private String name;
    private Country country;
    private Range range;

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
        Region region = (Region) o;
        return id == region.id &&
                Objects.equals(name, region.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @ManyToOne
    @JoinColumn(name = "id_panstwa", referencedColumnName = "id", nullable = false)
    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @ManyToOne
    @JoinColumn(name = "id_pasma", referencedColumnName = "id", nullable = false)
    public Range getRange() {
        return range;
    }

    public void setRange(Range mountainRange) {
        this.range = mountainRange;
    }

    @Override
    public String toString() {
        return "Region{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country=" + country +
                ", mountainRange=" + range +
                '}';
    }
}
