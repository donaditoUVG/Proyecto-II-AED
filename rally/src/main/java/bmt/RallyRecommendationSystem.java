package bmt;

import java.util.Scanner;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

public class RallyRecommendationSystem implements AutoCloseable {

    private Driver driver;

    public RallyRecommendationSystem() {
        // Constructor vacío
    }

    public void connect(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() throws Exception {
        if (driver != null) {
            driver.close();
        }
    }

    // Método para agregar un equipo
    public boolean addTeam(int team_id, String name, String driving_style, String preferred_age_range, int preferred_wins, double budget, int team_experience, String sponsor_name, double team_rating, double average_pilot_salary, String country) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (t:Team {team_id: $team_id, name: $name, driving_style: $driving_style, preferred_age_range: $preferred_age_range, preferred_wins: $preferred_wins, budget: $budget, team_experience: $team_experience, sponsor_name: $sponsor_name, team_rating: $team_rating, average_pilot_salary: $average_pilot_salary, country: $country})",
                        Values.parameters("team_id", team_id, "name", name, "driving_style", driving_style, "preferred_age_range", preferred_age_range, "preferred_wins", preferred_wins, "budget", budget, "team_experience", team_experience, "sponsor_name", sponsor_name, "team_rating", team_rating, "average_pilot_salary", average_pilot_salary, "country", country));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para actualizar el presupuesto de un equipo
    public boolean updateTeamBudget(int team_id, double newBudget) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (t:Team {team_id: $team_id}) SET t.budget = $newBudget",
                        Values.parameters("team_id", team_id, "newBudget", newBudget));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para eliminar un equipo
    public boolean deleteTeam(int team_id) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (t:Team {team_id: $team_id}) DETACH DELETE t",
                        Values.parameters("team_id", team_id));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para verificar si un equipo existe
    public boolean teamExists(int team_id) {
        try (Session session = driver.session()) {
            Result result = session.run("MATCH (t:Team {team_id: $team_id}) RETURN t",
                    Values.parameters("team_id", team_id));
            return result.hasNext();
        }
    }

    // Método para verificar si un patrocinador existe
    public boolean sponsorExists(String sponsor_name) {
        try (Session session = driver.session()) {
            Result result = session.run("MATCH (s:Sponsor {name: $name}) RETURN s",
                    Values.parameters("name", sponsor_name));
            return result.hasNext();
        }
    }

    // Método para agregar un país si no existe
    public void addCountry(String name) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MERGE (c:Country {name: $name})",
                        Values.parameters("name", name));
                return null;
            });
        }
    }

    // Métodos adicionales para manejar otros nodos y relaciones

    // Método para agregar un piloto
    public boolean addPilot(int pilot_id, String name, int age, String driving_style, int wins, int salary_expectation, int years_of_experience, double rating, String special_skills, String country, int team_id) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (p:Pilot {pilot_id: $pilot_id, name: $name, age: $age, driving_style: $driving_style, wins: $wins, salary_expectation: $salary_expectation, years_of_experience: $years_of_experience, rating: $rating, special_skills: $special_skills, country: $country, team_id: $team_id})",
                        Values.parameters("pilot_id", pilot_id, "name", name, "age", age, "driving_style", driving_style, "wins", wins, "salary_expectation", salary_expectation, "years_of_experience", years_of_experience, "rating", rating, "special_skills", special_skills, "country", country, "team_id", team_id));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para agregar un copiloto
    public boolean addCopilot(String name, int age, String driving_style, int salary_expectation, int years_of_experience, double rating, String special_skills, String country, String team, String pilot) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (c:Copilot {name: $name, age: $age, driving_style: $driving_style, salary_expectation: $salary_expectation, years_of_experience: $years_of_experience, rating: $rating, special_skills: $special_skills, country: $country, team: $team, pilot: $pilot})",
                        Values.parameters("name", name, "age", age, "driving_style", driving_style, "salary_expectation", salary_expectation, "years_of_experience", years_of_experience, "rating", rating, "special_skills", special_skills, "country", country, "team", team, "pilot", pilot));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método principal para pruebas
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            RallyRecommendationSystem app = new RallyRecommendationSystem();

            // Solicitar información de conexión a la base de datos
            System.out.println("Ingrese la URL de conexión de la base de datos:");
            String uri = scanner.nextLine();
            System.out.println("Ingrese el usuario de la base de datos:");
            String user = scanner.nextLine();
            System.out.println("Ingrese la contraseña de la base de datos:");
            String password = scanner.nextLine();

            app.connect(uri, user, password);
            System.out.println("Conexión exitosa a la base de datos.");

            // Menú de opciones
            boolean continuar = true;
            while (continuar) {
                System.out.println("Seleccione una opción:");
                System.out.println("1. Agregar información de un equipo");
                System.out.println("2. Actualizar presupuesto de un equipo");
                System.out.println("3. Eliminar información de un equipo");
                System.out.println("4. Agregar información de un piloto");
                System.out.println("5. Agregar información de un copiloto");
                System.out.println("6. Salir");
                System.out.print("Ingrese el número de la opción: ");
                int opcion = scanner.nextInt();

                switch (opcion) {
                    case 1:
                        handleAddTeam(app, scanner);
                        break;
                    case 2:
                        handleUpdateBudget(app, scanner);
                        break;
                    case 3:
                        handleDeleteTeam(app, scanner);
                        break;
                    case 4:
                        handleAddPilot(app, scanner);
                        break;
                    case 5:
                        handleAddCopilot(app, scanner);
                        break;
                    case 6:
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            }

            app.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleAddTeam(RallyRecommendationSystem app, Scanner scanner) {
        System.out.println("Ingrese el ID del equipo:");
        int team_id = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.println("Ingrese el nombre del equipo:");
        String nombreEquipo = scanner.nextLine();
        System.out.println("Ingrese el estilo de conducción del equipo:");
        String estiloConduccion = scanner.nextLine();
        System.out.println("Ingrese el rango de edad preferido:");
        String rangoEdadPreferido = scanner.nextLine();
        System.out.println("Ingrese el número de victorias preferidas:");
        int victoriasPreferidas = scanner.nextInt();
        System.out.println("Ingrese el presupuesto del equipo:");
        double presupuestoEquipo = scanner.nextDouble();
        System.out.println("Ingrese la experiencia del equipo en años:");
        int experienciaEquipo = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.println("Ingrese el nombre del patrocinador:");
        String sponsor_name = scanner.nextLine();
        System.out.println("Ingrese la calificación del equipo:");
        double calificacionEquipo = scanner.nextDouble();
        System.out.println("Ingrese el salario promedio de los pilotos:");
        double salarioPromedioPilotos = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        System.out.println("Ingrese el país del equipo:");
        String paisEquipo = scanner.nextLine();

        if (!app.sponsorExists(sponsor_name)) {
            System.out.println("El patrocinador " + sponsor_name + " no existe.");
            return;
        }

        app.addCountry(paisEquipo);

        if (app.addTeam(team_id, nombreEquipo, estiloConduccion, rangoEdadPreferido, victoriasPreferidas, presupuestoEquipo, experienciaEquipo, sponsor_name, calificacionEquipo, salarioPromedioPilotos, paisEquipo)) {
            System.out.println("Equipo agregado exitosamente.");
        } else {
            System.out.println("Error al agregar el equipo.");
        }
    }

    private static void handleUpdateBudget(RallyRecommendationSystem app, Scanner scanner) {
        System.out.println("Ingrese el ID del equipo a actualizar:");
        int team_idActualizar = scanner.nextInt();
        System.out.println("Ingrese el nuevo presupuesto:");
        double nuevoPresupuesto = scanner.nextDouble();

        if (!app.teamExists(team_idActualizar)) {
            System.out.println("El equipo con ID " + team_idActualizar + " no existe.");
            return;
        }

        if (app.updateTeamBudget(team_idActualizar, nuevoPresupuesto)) {
            System.out.println("Presupuesto actualizado exitosamente.");
        } else {
            System.out.println("Error al actualizar el presupuesto.");
        }
    }

    private static void handleDeleteTeam(RallyRecommendationSystem app, Scanner scanner) {
        System.out.println("Ingrese el ID del equipo a eliminar:");
        int team_idEliminar = scanner.nextInt();

        if (!app.teamExists(team_idEliminar)) {
            System.out.println("El equipo con ID " + team_idEliminar + " no existe.");
            return;
        }

        if (app.deleteTeam(team_idEliminar)) {
            System.out.println("Equipo eliminado exitosamente.");
        } else {
            System.out.println("Error al eliminar el equipo.");
        }
    }

    private static void handleAddPilot(RallyRecommendationSystem app, Scanner scanner) {
        System.out.println("Ingrese el ID del piloto:");
        int pilot_id = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.println("Ingrese el nombre del piloto:");
        String nombrePiloto = scanner.nextLine();
        System.out.println("Ingrese la edad del piloto:");
        int edadPiloto = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.println("Ingrese el estilo de conducción del piloto:");
        String estiloConduccionPiloto = scanner.nextLine();
        System.out.println("Ingrese el número de victorias del piloto:");
        int victoriasPiloto = scanner.nextInt();
        System.out.println("Ingrese la expectativa salarial del piloto:");
        int salarioPiloto = scanner.nextInt();
        System.out.println("Ingrese los años de experiencia del piloto:");
        int experienciaPiloto = scanner.nextInt();
        System.out.println("Ingrese la calificación del piloto:");
        double calificacionPiloto = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        System.out.println("Ingrese las habilidades especiales del piloto (separadas por comas):");
        String habilidadesPiloto = scanner.nextLine();
        System.out.println("Ingrese el país del piloto:");
        String paisPiloto = scanner.nextLine();
        System.out.println("Ingrese el ID del equipo del piloto:");
        int team_id = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (app.addPilot(pilot_id, nombrePiloto, edadPiloto, estiloConduccionPiloto, victoriasPiloto, salarioPiloto, experienciaPiloto, calificacionPiloto, habilidadesPiloto, paisPiloto, team_id)) {
            System.out.println("Piloto agregado exitosamente.");
        } else {
            System.out.println("Error al agregar el piloto.");
        }
    }

    private static void handleAddCopilot(RallyRecommendationSystem app, Scanner scanner) {
        System.out.println("Ingrese el nombre del copiloto:");
        String nombreCopiloto = scanner.nextLine();
        System.out.println("Ingrese la edad del copiloto:");
        int edadCopiloto = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.println("Ingrese el estilo de conducción del copiloto:");
        String estiloConduccionCopiloto = scanner.nextLine();
        System.out.println("Ingrese la expectativa salarial del copiloto:");
        int salarioCopiloto = scanner.nextInt();
        System.out.println("Ingrese los años de experiencia del copiloto:");
        int experienciaCopiloto = scanner.nextInt();
        System.out.println("Ingrese la calificación del copiloto:");
        double calificacionCopiloto = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        System.out.println("Ingrese las habilidades especiales del copiloto (separadas por comas):");
        String habilidadesCopiloto = scanner.nextLine();
        System.out.println("Ingrese el país del copiloto:");
        String paisCopiloto = scanner.nextLine();
        System.out.println("Ingrese el equipo del copiloto:");
        String equipoCopiloto = scanner.nextLine();
        System.out.println("Ingrese el nombre del piloto asociado:");
        String pilotoAsociado = scanner.nextLine();

        if (app.addCopilot(nombreCopiloto, edadCopiloto, estiloConduccionCopiloto, salarioCopiloto, experienciaCopiloto, calificacionCopiloto, habilidadesCopiloto, paisCopiloto, equipoCopiloto, pilotoAsociado)) {
            System.out.println("Copiloto agregado exitosamente.");
        } else {
            System.out.println("Error al agregar el copiloto.");
        }
    }
}
