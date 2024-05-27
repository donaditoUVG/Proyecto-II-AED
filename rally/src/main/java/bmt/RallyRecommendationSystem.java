package bmt;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

public class RallyRecommendationSystem implements AutoCloseable {

    private final Driver driver;

    public RallyRecommendationSystem(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    // Método para agregar un equipo
    public void addTeam(String name, String country, int founded, String director, double budget) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (t:Team {name: $name, country: $country, founded: $founded, director: $director, budget: $budget})",
                        Values.parameters("name", name, "country", country, "founded", founded, "director", director, "budget", budget));
                return null;
            });
        }
    }

    // Método para actualizar el presupuesto de un equipo
    public void updateTeamBudget(String name, double newBudget) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (t:Team {name: $name}) SET t.budget = $newBudget",
                        Values.parameters("name", name, "newBudget", newBudget));
                return null;
            });
        }
    }

    // Método para eliminar un equipo
    public void deleteTeam(String name) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (t:Team {name: $name}) DETACH DELETE t",
                        Values.parameters("name", name));
                return null;
            });
        }
    }

    // Método para recomendar equipos basados en un país
    public void recommendTeams(String country) {
        try (Session session = driver.session()) {
            Result result = session.run("MATCH (t:Team {country: $country}) RETURN t.name AS name, t.budget AS budget, t.director AS director",
                    Values.parameters("country", country));
            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next();
                System.out.println("Team: " + record.get("name").asString() +
                        ", Budget: " + record.get("budget").asDouble() +
                        ", Director: " + record.get("director").asString());
            }
        }
    }

    // Método principal para pruebas
    public static void main(String[] args) {
        try (RallyRecommendationSystem app = new RallyRecommendationSystem("bolt://localhost:7687", "neo4j", "password")) {
            // Agregar equipos
            app.addTeam("Team A", "USA", 1999, "John Doe", 5000000);
            app.addTeam("Team B", "UK", 2005, "Jane Smith", 3000000);

            // Actualizar presupuesto del equipo
            app.updateTeamBudget("Team A", 5500000);

            // Recomendar equipos basados en el país
            app.recommendTeams("USA");

            // Eliminar equipo
            app.deleteTeam("Team B");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
