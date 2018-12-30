package gotpttk.entities;

import javax.persistence.*;
import java.sql.Date;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "turysci", schema = "got_pttk", catalog = "")
public class Tourist {
    private int id;
    private String login;
    private String password;
    private String email;
    private Date dateOfBirth;
    private String firstName;
    private String lastName;
    private Collection<Book> book;
    private Collection<Badge> badgesAwarded;
    private Collection<Point> addedPoints;
    private Collection<Route> addedRoutes;
    private Collection<Excursion> addedExcursions;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "login")
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Basic
    @Column(name = "haslo")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "data_urodzenia")
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Basic
    @Column(name = "imie")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Basic
    @Column(name = "nazwisko")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tourist tourist = (Tourist) o;
        return id == tourist.id &&
                Objects.equals(login, tourist.login) &&
                Objects.equals(password, tourist.password) &&
                Objects.equals(email, tourist.email) &&
                Objects.equals(dateOfBirth, tourist.dateOfBirth) &&
                Objects.equals(firstName, tourist.firstName) &&
                Objects.equals(lastName, tourist.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password, email, dateOfBirth, firstName, lastName);
    }

    @OneToMany(mappedBy = "owner")
    public Collection<Book> getBook() {
        return book;
    }

    public void setBook(Collection<Book> book) {
        this.book = book;
    }

    @OneToMany(mappedBy = "owner")
    public Collection<Badge> getBadgesAwarded() {
        return badgesAwarded;
    }

    public void setBadgesAwarded(Collection<Badge> badgesAwarded) {
        this.badgesAwarded = badgesAwarded;
    }

    @OneToMany(mappedBy = "pointOwner")
    public Collection<Point> getAddedPoints() {
        return addedPoints;
    }

    public void setAddedPoints(Collection<Point> addedPoints) {
        this.addedPoints = addedPoints;
    }

    @OneToMany(mappedBy = "routeOwner")
    public Collection<Route> getAddedRoutes() {
        return addedRoutes;
    }

    public void setAddedRoutes(Collection<Route> addedRoutes) {
        this.addedRoutes = addedRoutes;
    }

    @OneToMany(mappedBy = "owner")
    public Collection<Excursion> getAddedExcursions() {
        return addedExcursions;
    }

    public void setAddedExcursions(Collection<Excursion> addedExcursions) {
        this.addedExcursions = addedExcursions;
    }
}
