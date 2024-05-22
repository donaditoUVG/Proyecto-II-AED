import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import static org.neo4j.driver.Values.parameters;

/**
 * @author Jose Donado
 * 
*/


/**
 * Clase que representa la base de datoss
 */
public class RallyDatabase implements AutoCloseable {
    private final Driver driver;

    public RallyDatabase(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    public void agregarEscuderia(String nombre) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run("CREATE (e:Escuderia {nombre: $nombre})", parameters("nombre", nombre)));
        }
    }

    public void agregarPiloto(String nombre, int experiencia, String escuderia) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(
                "MATCH (e:Escuderia {nombre: $escuderia}) " +
                "CREATE (p:Piloto {nombre: $nombre, experiencia: $experiencia}) " +
                "CREATE (e)-[:TIENE_PILOTO]->(p)",
                parameters("nombre", nombre, "experiencia", experiencia, "escuderia", escuderia)));
        }
    }

    public void actualizarPiloto(String nombre, int experiencia) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(
                "MATCH (p:Piloto {nombre: $nombre}) " +
                "SET p.experiencia = $experiencia",
                parameters("nombre", nombre, "experiencia", experiencia)));
        }
    }

    public void eliminarPiloto(String nombre) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(
                "MATCH (p:Piloto {nombre: $nombre}) DETACH DELETE p",
                parameters("nombre", nombre)));
        }
    }

    public static void main(String[] args) {
        try (RallyDatabase db = new RallyDatabase("bolt://localhost:7687", "neo4j", "password")) {
            db.agregarEscuderia("Citroën World Rally Team");
            db.agregarPiloto("Sebastian Loeb", 7, "Citroën World Rally Team");
            db.actualizarPiloto("Sebastian Loeb", 8);
            db.eliminarPiloto("Sebastian Loeb");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
