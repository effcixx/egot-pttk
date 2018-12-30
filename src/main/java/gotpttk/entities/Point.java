package gotpttk.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "punkty", schema = "got_pttk", catalog = "")
public class Point {
    private int id;
    private String name;
    private String height;
    private Region region;
    private Tourist pointOwner;

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

    @Basic
    @Column(name = "wysokosc")
    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return id == point.id &&
                Objects.equals(name, point.name) &&
                Objects.equals(height, point.height);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, height);
    }

    @ManyToOne
    @JoinColumn(name = "id_regionu", referencedColumnName = "id", nullable = false)
    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @ManyToOne
    @JoinColumn(name = "id_turysty", referencedColumnName = "id")
    public Tourist getPointOwner() {
        return pointOwner;
    }

    public void setPointOwner(Tourist pointOwner) {
        this.pointOwner = pointOwner;
    }
}
