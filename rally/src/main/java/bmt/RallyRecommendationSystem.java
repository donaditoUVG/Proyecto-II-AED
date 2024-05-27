package bmt;

import java.util.Scanner;

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
            Scanner scanner = new Scanner(System.in);

            // Menú de opciones
            System.out.println("Seleccione una opción:");
            System.out.println("1. Agregar información de un equipo");
            System.out.println("2. Actualizar presupuesto de un equipo");
            System.out.println("3. Eliminar información de un equipo");
            System.out.print("Ingrese el número de la opción: ");
            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    // Agregar información de un equipo
                    System.out.println("Ingrese el nombre del equipo:");
                    String nombreEquipo = scanner.next();
                    System.out.println("Ingrese el país del equipo:");
                    String paisEquipo = scanner.next();
                    System.out.println("Ingrese el año de fundación del equipo:");
                    int añoFundacion = scanner.nextInt();
                    System.out.println("Ingrese el director del equipo:");
                    String directorEquipo = scanner.next();
                    System.out.println("Ingrese el presupuesto del equipo:");
                    double presupuestoEquipo = scanner.nextDouble();
                    app.addTeam(nombreEquipo, paisEquipo, añoFundacion, directorEquipo, presupuestoEquipo);
                    break;
                case 2:
                    // Actualizar presupuesto de un equipo
                    System.out.println("Ingrese el nombre del equipo a actualizar:");
                    String nombreEquipoActualizar = scanner.next();
                    System.out.println("Ingrese el nuevo presupuesto:");
                    double nuevoPresupuesto = scanner.nextDouble();
                    app.updateTeamBudget(nombreEquipoActualizar, nuevoPresupuesto);
                    break;
                case 3:
                    // Eliminar información de un equipo
                    System.out.println("Ingrese el nombre del equipo a eliminar:");
                    String nombreEquipoEliminar = scanner.next();
                    app.deleteTeam(nombreEquipoEliminar);
                    break;
                default:
                    System.out.println("Opción no válida.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
     * 
     */
}
