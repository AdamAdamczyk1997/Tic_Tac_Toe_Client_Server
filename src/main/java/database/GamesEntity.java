package database;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;


@Entity
public class GamesEntity {


    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String id_u1;
    private String id_u2;
    private final String symbol_u1 = "O";
    private final String symbol_u2 = "X";
    private int result;

   /* @ManyToMany(cascade = CascadeType.ALL)
    private Set<Move> moves;

    public Set<Move> getMoves() {
        return moves;
    }

    public void setMoves(Set<Move> moves) {
        this.moves = moves;
    }
*/
    private Date date;

/*    public GamesEntity(){
        moves = new HashSet<>();
    }*/

    public String getId_u1() {
        return id_u1;
    }

    public void setId_u1(String id_u1) {
        this.id_u1 = id_u1;
    }

    public String getId_u2() {
        return id_u2;
    }

    public void setId_u2(String id_u2) {
        this.id_u2 = id_u2;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Game{" +
                " player_1='" + getId_u1() + '\'' +
                "| player_2='" + getId_u2() + '\'' +
                "| date='" + getDate() + '\'' +
                "| win=' player_" + getResult() + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
