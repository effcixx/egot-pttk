package gotpttk.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "trasy", schema = "got_pttk", catalog = "")
public class Route {
    private int id;
    private int pointsStartToEnd;
    private int pointsEndToStart;
    private Point startingPoint;
    private Point endPoint;
    private Tourist routeOwner;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Route() {
    }

    public Route(int pointsStartToEnd, int pointsEndToStart, Point startingPoint, Point endPoint, Tourist routeOwner) {
        this.pointsStartToEnd = pointsStartToEnd;
        this.pointsEndToStart = pointsEndToStart;
        this.startingPoint = startingPoint;
        this.endPoint = endPoint;
        this.routeOwner = routeOwner;
    }

    @Basic
    @Column(name = "pun_pocz_kon")
    public int getPointsStartToEnd() {
        return pointsStartToEnd;
    }

    public void setPointsStartToEnd(int pointsStartToEnd) {
        this.pointsStartToEnd = pointsStartToEnd;
    }

    @Basic
    @Column(name = "pun_kon_pocz")
    public int getPointsEndToStart() {
        return pointsEndToStart;
    }

    public void setPointsEndToStart(int pointsEndToStart) {
        this.pointsEndToStart = pointsEndToStart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return startingPoint.equals(route.startingPoint) &&
                endPoint.equals(route.endPoint) &&
                pointsStartToEnd == route.pointsStartToEnd &&
                pointsEndToStart == route.pointsEndToStart;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pointsStartToEnd, pointsEndToStart);
    }

    @ManyToOne
    @JoinColumn(name = "id_punkt_pocz", referencedColumnName = "id", nullable = false)
    public Point getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(Point startingPoint) {
        this.startingPoint = startingPoint;
    }

    @ManyToOne
    @JoinColumn(name = "id_punkt_kon", referencedColumnName = "id", nullable = false)
    public Point getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

    @ManyToOne
    @JoinColumn(name = "id_turysty", referencedColumnName = "id")
    public Tourist getRouteOwner() {
        return routeOwner;
    }

    public void setRouteOwner(Tourist routeOwner) {
        this.routeOwner = routeOwner;
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", pointsStartToEnd=" + pointsStartToEnd +
                ", pointsEndToStart=" + pointsEndToStart +
                ", startingPoint=" + startingPoint +
                ", endPoint=" + endPoint +
                ", routeOwner=" + routeOwner +
                '}';
    }

    public String showRoute(){
        return startingPoint.getName() + " - " + endPoint.getName();
    }


}
