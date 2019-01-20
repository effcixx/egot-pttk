package gotpttk.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "kategorie", schema = "got_pttk", catalog = "")
public class Category {
    private int id;
    private int hierarchyLevel;
    private String name;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "poziom")
    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(int hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
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
        Category category = (Category) o;
        return id == category.id &&
                hierarchyLevel == category.hierarchyLevel &&
                Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hierarchyLevel, name);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", hierarchyLevel=" + hierarchyLevel +
                ", name='" + name + '\'' +
                '}';
    }
}
