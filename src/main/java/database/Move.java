package database;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Move {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private int id_u;
    private int field;
    private Date created_at;

   /* @ManyToMany(fetch = FetchType.EAGER, mappedBy = "moves")
    private List<GamesEntity> m = new ArrayList<>();*/

   // public List<GamesEntity> getInMoves() {return m;}

   // public void setInMoves(List<GamesEntity> moves) {this.m = moves;}

    public int getId_u() {
        return id_u;
    }

    public void setId_u(int id_u) {
        this.id_u = id_u;
    }

    public int getField() {
        return field;
    }

    public void setField(int field) {
        this.field = field;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Move{" +
                "id_u='" + getId_u() + '\'' +
                ", field='" + getField() + '\'' +
                ", created at='" + getCreated_at() + '\'' +
                '}';
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
