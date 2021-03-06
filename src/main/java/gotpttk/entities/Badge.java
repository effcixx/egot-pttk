package gotpttk.entities;

import org.joda.time.DateTimeComparator;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "odznaki", schema = "got_pttk", catalog = "")
public class Badge {
    private int id;
    private Date achievingDate;
    private Category category;
    private Tourist owner;

    public Badge() {
    }

    public Badge(Date achievingDate, Category category, Tourist owner) {
        this.achievingDate = achievingDate;
        this.category = category;
        this.owner = owner;
    }

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "data_zdobycia")
    public Date getAchievingDate() {
        return achievingDate;
    }

    public void setAchievingDate(Date achievingDate) {
        this.achievingDate = achievingDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Badge badge = (Badge) o;
        return id == badge.id &&
                DateTimeComparator.getDateOnlyInstance().compare(achievingDate, badge.achievingDate) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, achievingDate);
    }

    @ManyToOne
    @JoinColumn(name = "id_kategorii", referencedColumnName = "id", nullable = false)
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @ManyToOne
    @JoinColumn(name = "id_turysty", referencedColumnName = "id", nullable = false)
    public Tourist getOwner() {
        return owner;
    }

    public void setOwner(Tourist owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Badge{" +
                "id=" + id +
                ", achievingDate=" + achievingDate +
                ", category=" + category +
                ", owner=" + owner +
                '}';
    }
}
