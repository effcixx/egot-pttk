package gotpttk.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "administratorzy", schema = "got_pttk", catalog = "")
public class Admin {
    private int id;
    private String login;
    private String password;
    private String email;

    public Admin(String login, String password, String email) {
        this.login = login;
        this.password = password;
        this.email = email;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return id == admin.id &&
                Objects.equals(login, admin.login) &&
                Objects.equals(password, admin.password) &&
                Objects.equals(email, admin.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password, email);
    }
}
