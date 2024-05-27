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

    // Método para crear un nuevo equipo
    public boolean createTeam(int team_id, String name, String driving_style, String preferred_age_range, int preferred_wins, double budget, int team_experience, String sponsor_name, double team_rating, double average_pilot_salary, String country, String eventos_participados) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (t:Team {team_id: $team_id, name: $name, driving_style: $driving_style, preferred_age_range: $preferred_age_range, preferred_wins: $preferred_wins, budget: $budget, team_experience: $team_experience, sponsor: $sponsor_name, team_rating: $team_rating, average_pilot_salary: $average_pilot_salary, country: $country, eventos_participados: $eventos_participados})",
                        Values.parameters("team_id", team_id, "name", name, "driving_style", driving_style, "preferred_age_range", preferred_age_range, "preferred_wins", preferred_wins, "budget", budget, "team_experience", team_experience, "sponsor_name", sponsor_name, "team_rating", team_rating, "average_pilot_salary", average_pilot_salary, "country", country, "eventos_participados", eventos_participados));
                return null;
            });
            createTeamRelations(team_id, eventos_participados);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para crear un nuevo piloto
    public boolean createPilot(int pilot_id, String name, int age, String driving_style, int wins, int salary_expectation, int years_of_experience, double rating, String special_skills, String country, int team_id, String eventos_participados) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (p:Pilot {pilot_id: $pilot_id, name: $name, age: $age, driving_style: $driving_style, wins: $wins, salary_expectation: $salary_expectation, years_of_experience: $years_of_experience, rating: $rating, special_skills: $special_skills, country: $country, team_id: $team_id, eventos_participados: $eventos_participados})",
                        Values.parameters("pilot_id", pilot_id, "name", name, "age", age, "driving_style", driving_style, "wins", wins, "salary_expectation", salary_expectation, "years_of_experience", years_of_experience, "rating", rating, "special_skills", special_skills, "country", country, "team_id", team_id, "eventos_participados", eventos_participados));
                return null;
            });
            createPilotRelations(pilot_id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para crear un nuevo copiloto
    public boolean createCopilot(String name, int age, String driving_style, int salary_expectation, int years_of_experience, double rating, String special_skills, String country, String team, String pilot, String eventos_participados) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (c:Copilot {name: $name, age: $age, driving_style: $driving_style, salary_expectation: $salary_expectation, years_of_experience: $years_of_experience, rating: $rating, special_skills: $special_skills, country: $country, team: $team, pilot: $pilot, eventos_participados: $eventos_participados})",
                        Values.parameters("name", name, "age", age, "driving_style", driving_style, "salary_expectation", salary_expectation, "years_of_experience", years_of_experience, "rating", rating, "special_skills", special_skills, "country", country, "team", team, "pilot", pilot, "eventos_participados", eventos_participados));
                return null;
            });
            createCopilotRelations(name);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para crear un nuevo vehículo
    public boolean createVehicle(String model, int year, String engine_type, double performance_rating, String sponsor, String team) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (v:Vehicle {model: $model, year: $year, engine_type: $engine_type, performance_rating: $performance_rating, sponsor: $sponsor, team: $team})",
                        Values.parameters("model", model, "year", year, "engine_type", engine_type, "performance_rating", performance_rating, "sponsor", sponsor, "team", team));
                return null;
            });
            createVehicleRelations(model);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para crear un nuevo patrocinador
    public boolean createSponsor(String name, int importance_level, int sponsorship_amount) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (s:Sponsor {name: $name, importance_level: $importance_level, sponsorship_amount: $sponsorship_amount})",
                        Values.parameters("name", name, "importance_level", importance_level, "sponsorship_amount", sponsorship_amount));
                return null;
            });
            createSponsorRelations(name);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para crear una nueva habilidad
    public boolean createSkill(int skill_id, String name, int importance_level, String skill_type) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (s:Skill {skill_id: $skill_id, name: $name, importance_level: $importance_level, skill_type: $skill_type})",
                        Values.parameters("skill_id", skill_id, "name", name, "importance_level", importance_level, "skill_type", skill_type));
                return null;
            });
            createSkillRelations(skill_id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para crear un nuevo evento
    public boolean createEvent(String name, int year, String location, String surface, String escuderias_que_participaron) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (e:Event {name: $name, year: $year, location: $location, surface: $surface, escuderias_que_participaron: $escuderias_que_participaron})",
                        Values.parameters("name", name, "year", year, "location", location, "surface", surface, "escuderias_que_participaron", escuderias_que_participaron));
                return null;
            });
            createEventRelations(name);
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

    // Métodos adicionales para crear relaciones
    public boolean createTeamRelations(int team_id, String eventos_participados) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (t:Team {team_id: $team_id}), (s:Sponsor {name: t.sponsor}) CREATE (t)-[:SPONSORED_BY]->(s)", Values.parameters("team_id", team_id));
                tx.run("MATCH (t:Team {team_id: $team_id}), (c:Country {name: t.country}) CREATE (t)-[:BASED_IN]->(c)", Values.parameters("team_id", team_id));
    
                String[] eventos = eventos_participados.split(",");
                for (String evento : eventos) {
                    evento = evento.trim();  // Eliminar espacios en blanco antes y después del nombre del evento
                    System.out.println("Relacionando equipo con evento: " + evento);  // Depuración
                    tx.run("MATCH (t:Team {team_id: $team_id}), (e:Event {name: $evento}) CREATE (t)-[:COMPETED_IN]->(e)",
                            Values.parameters("team_id", team_id, "evento", evento));
                }
    
                tx.run("MATCH (t:Team {team_id: $team_id}), (p:Pilot) WHERE p.team_id = t.team_id CREATE (t)-[:HAS_PILOT]->(p)", Values.parameters("team_id", team_id));
                tx.run("MATCH (t:Team {team_id: $team_id}), (c:Copilot) WHERE c.team = t.name CREATE (t)-[:HAS_COPILOT]->(c)", Values.parameters("team_id", team_id));
                tx.run("MATCH (t:Team {team_id: $team_id}), (v:Vehicle) WHERE v.team = t.name CREATE (t)-[:USES_VEHICLE]->(v)", Values.parameters("team_id", team_id));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public boolean createPilotRelations(int pilot_id) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (p:Pilot {pilot_id: $pilot_id}), (t:Team {team_id: p.team_id}) CREATE (p)-[:BELONGS_TO]->(t)", Values.parameters("pilot_id", pilot_id));
                tx.run("MATCH (p:Pilot {pilot_id: $pilot_id}), (c:Country {name: p.country}) CREATE (p)-[:FROM]->(c)", Values.parameters("pilot_id", pilot_id));
                tx.run("MATCH (p:Pilot {pilot_id: $pilot_id}), (sk:Skill) WHERE p.special_skills CONTAINS sk.name CREATE (p)-[:HAS_SKILL]->(sk)", Values.parameters("pilot_id", pilot_id));
                tx.run("MATCH (p:Pilot {pilot_id: $pilot_id}), (e:Event) WHERE ANY(event IN split(p.eventos_participados, ',') WHERE event = e.name) CREATE (p)-[:PARTICIPATED_IN]->(e)", Values.parameters("pilot_id", pilot_id));
                tx.run("MATCH (p:Pilot {pilot_id: $pilot_id}), (c:Copilot {name: p.pilot}) CREATE (p)-[:HAS_COPILOT]->(c)", Values.parameters("pilot_id", pilot_id));
                tx.run("MATCH (p:Pilot {pilot_id: $pilot_id}), (v:Vehicle) WHERE p.team_id = v.team CREATE (p)-[:DRIVES]->(v)", Values.parameters("pilot_id", pilot_id));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createCopilotRelations(String name) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (c:Copilot {name: $name}), (t:Team {name: c.team}) CREATE (c)-[:BELONGS_TO]->(t)", Values.parameters("name", name));
                tx.run("MATCH (c:Copilot {name: $name}), (p:Pilot {name: c.pilot}) CREATE (c)-[:HAS_PILOT]->(p)", Values.parameters("name", name));
                tx.run("MATCH (c:Copilot {name: $name}), (c2:Country {name: c.country}) CREATE (c)-[:FROM]->(c2)", Values.parameters("name", name));
                tx.run("MATCH (c:Copilot {name: $name}), (sk:Skill) WHERE c.special_skills CONTAINS sk.name CREATE (c)-[:HAS_SKILL]->(sk)", Values.parameters("name", name));
                tx.run("MATCH (c:Copilot {name: $name}), (e:Event) WHERE ANY(event IN split(c.eventos_participados, ',') WHERE event = e.name) CREATE (c)-[:PARTICIPATED_IN]->(e)", Values.parameters("name", name));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createVehicleRelations(String model) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (v:Vehicle {model: $model}), (t:Team {name: v.team}) CREATE (v)-[:USED_BY]->(t)", Values.parameters("model", model));
                tx.run("MATCH (v:Vehicle {model: $model}), (s:Sponsor {name: v.sponsor}) CREATE (v)-[:SPONSORED_BY]->(s)", Values.parameters("model", model));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createEventRelations(String eventName) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (e:Event {name: $eventName}), (t:Team) WHERE ANY(team IN split(e.escuderias_que_participaron, ',') WHERE team = t.name) CREATE (t)-[:COMPETED_IN]->(e)", Values.parameters("eventName", eventName));
                tx.run("MATCH (e:Event {name: $eventName}), (p:Pilot) WHERE ANY(event IN split(p.eventos_participados, ',') WHERE event = e.name) CREATE (p)-[:PARTICIPATED_IN]->(e)", Values.parameters("eventName", eventName));
                tx.run("MATCH (e:Event {name: $eventName}), (c:Copilot) WHERE ANY(event IN split(c.eventos_participados, ',') WHERE event = e.name) CREATE (c)-[:PARTICIPATED_IN]->(e)", Values.parameters("eventName", eventName));
                // Relaciones adicionales sugeridas
                tx.run("MATCH (sk:Skill), (e:Event {name: $eventName}) WHERE e.surface CONTAINS sk.skill_type CREATE (sk)-[:RELEVANT_FOR_EVENT]->(e)", Values.parameters("eventName", eventName));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createSponsorRelations(String name) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (t:Team), (s:Sponsor {name: $name}) WHERE t.sponsor = s.name CREATE (t)-[:SPONSORED_BY]->(s)", Values.parameters("name", name));
                tx.run("MATCH (p:Pilot), (s:Sponsor {name: $name}) WHERE p.team_id = s.sponsor_id CREATE (p)-[:SPONSORED_BY]->(s)", Values.parameters("name", name));
                tx.run("MATCH (c:Copilot), (s:Sponsor {name: $name}) WHERE c.team = s.name CREATE (c)-[:SPONSORED_BY]->(s)", Values.parameters("name", name));
                tx.run("MATCH (v:Vehicle), (s:Sponsor {name: $name}) WHERE v.sponsor = s.name CREATE (v)-[:SPONSORED_BY]->(s)", Values.parameters("name", name));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createSkillRelations(int skill_id) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (p:Pilot), (sk:Skill {skill_id: $skill_id}) WHERE p.special_skills CONTAINS sk.name CREATE (p)-[:HAS_SKILL]->(sk)", Values.parameters("skill_id", skill_id));
                tx.run("MATCH (c:Copilot), (sk:Skill {skill_id: $skill_id}) WHERE c.special_skills CONTAINS sk.name CREATE (c)-[:HAS_SKILL]->(sk)", Values.parameters("skill_id", skill_id));
                return null;
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        try (RallyRecommendationSystem app = new RallyRecommendationSystem(); Scanner scanner = new Scanner(System.in)) {
            System.out.println("Ingrese la URL de conexión de la base de datos:");
            String uri = scanner.nextLine();
            System.out.println("Ingrese el usuario de la base de datos:");
            String user = scanner.nextLine();
            System.out.println("Ingrese la contraseña de la base de datos:");
            String password = scanner.nextLine();

            app.connect(uri, user, password);
            System.out.println("Conexión exitosa a la base de datos.");

            while (true) {
                System.out.println("Seleccione una opción:");
                System.out.println("1. Crear un equipo");
                System.out.println("2. Eliminar un equipo");
                System.out.println("3. Crear un piloto");
                System.out.println("4. Crear un copiloto");
                System.out.println("5. Crear un vehículo");
                System.out.println("6. Crear un patrocinador");
                System.out.println("7. Crear una habilidad");
                System.out.println("8. Crear un evento");
                System.out.println("9. Salir");
                System.out.print("Ingrese el número de la opción: ");
                int option = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (option) {
                    case 1:
                        handleCreateTeam(app, scanner);
                        break;
                    case 2:
                        handleDeleteTeam(app, scanner);
                        break;
                    case 3:
                        handleCreatePilot(app, scanner);
                        break;
                    case 4:
                        handleCreateCopilot(app, scanner);
                        break;
                    case 5:
                        handleCreateVehicle(app, scanner);
                        break;
                    case 6:
                        handleCreateSponsor(app, scanner);
                        break;
                    case 7:
                        handleCreateSkill(app, scanner);
                        break;
                    case 8:
                        handleCreateEvent(app, scanner);
                        break;
                    case 9:
                        System.out.println("Saliendo del programa.");
                        return;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleCreateTeam(RallyRecommendationSystem app, Scanner scanner) {
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
        System.out.println("Ingrese los eventos en los que ha participado el equipo (separados por comas):");
        String eventosParticipados = scanner.nextLine();

        if (!app.sponsorExists(sponsor_name)) {
            System.out.println("El patrocinador " + sponsor_name + " no existe.");
            return;
        }

        app.addCountry(paisEquipo);

        if (app.createTeam(team_id, nombreEquipo, estiloConduccion, rangoEdadPreferido, victoriasPreferidas, presupuestoEquipo, experienciaEquipo, sponsor_name, calificacionEquipo, salarioPromedioPilotos, paisEquipo, eventosParticipados)) {
            System.out.println("Equipo creado exitosamente.");
        } else {
            System.out.println("Error al crear el equipo.");
        }
    }

    private static void handleCreatePilot(RallyRecommendationSystem app, Scanner scanner) {
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
        System.out.println("Ingrese los eventos en los que ha participado el piloto (separados por comas):");
        String eventosParticipados = scanner.nextLine();

        if (app.createPilot(pilot_id, nombrePiloto, edadPiloto, estiloConduccionPiloto, victoriasPiloto, salarioPiloto, experienciaPiloto, calificacionPiloto, habilidadesPiloto, paisPiloto, team_id, eventosParticipados)) {
            System.out.println("Piloto creado exitosamente.");
        } else {
            System.out.println("Error al crear el piloto.");
        }
    }

    private static void handleCreateCopilot(RallyRecommendationSystem app, Scanner scanner) {
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
        System.out.println("Ingrese el nombre del equipo del copiloto:");
        String nombreEquipo = scanner.nextLine();
        System.out.println("Ingrese el nombre del piloto asociado:");
        String pilotoAsociado = scanner.nextLine();
        System.out.println("Ingrese los eventos en los que ha participado el copiloto (separados por comas):");
        String eventosParticipados = scanner.nextLine();

        if (app.createCopilot(nombreCopiloto, edadCopiloto, estiloConduccionCopiloto, salarioCopiloto, experienciaCopiloto, calificacionCopiloto, habilidadesCopiloto, paisCopiloto, nombreEquipo, pilotoAsociado, eventosParticipados)) {
            System.out.println("Copiloto creado exitosamente.");
        } else {
            System.out.println("Error al crear el copiloto.");
        }
    }

    private static void handleCreateVehicle(RallyRecommendationSystem app, Scanner scanner) {
        System.out.println("Ingrese el modelo del vehículo:");
        String modelo = scanner.nextLine();
        System.out.println("Ingrese el año del vehículo:");
        int year = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.println("Ingrese el tipo de motor del vehículo:");
        String tipoMotor = scanner.nextLine();
        System.out.println("Ingrese la calificación de rendimiento del vehículo:");
        double calificacionRendimiento = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        System.out.println("Ingrese el patrocinador del vehículo:");
        String patrocinador = scanner.nextLine();
        System.out.println("Ingrese el nombre del equipo del vehículo:");
        String equipo = scanner.nextLine();

        if (app.createVehicle(modelo, year, tipoMotor, calificacionRendimiento, patrocinador, equipo)) {
            System.out.println("Vehículo creado exitosamente.");
        } else {
            System.out.println("Error al crear el vehículo.");
        }
    }

    private static void handleCreateSponsor(RallyRecommendationSystem app, Scanner scanner) {
        System.out.println("Ingrese el nombre del patrocinador:");
        String nombre = scanner.nextLine();
        System.out.println("Ingrese el nivel de importancia del patrocinador:");
        int nivelImportancia = scanner.nextInt();
        System.out.println("Ingrese la cantidad de patrocinio del patrocinador:");
        int cantidadPatrocinio = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (app.createSponsor(nombre, nivelImportancia, cantidadPatrocinio)) {
            System.out.println("Patrocinador creado exitosamente.");
        } else {
            System.out.println("Error al crear el patrocinador.");
        }
    }

    private static void handleCreateSkill(RallyRecommendationSystem app, Scanner scanner) {
        System.out.println("Ingrese el ID de la habilidad:");
        int skill_id = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.println("Ingrese el nombre de la habilidad:");
        String nombre = scanner.nextLine();
        System.out.println("Ingrese el nivel de importancia de la habilidad:");
        int nivelImportancia = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.println("Ingrese el tipo de habilidad:");
        String tipoHabilidad = scanner.nextLine();

        if (app.createSkill(skill_id, nombre, nivelImportancia, tipoHabilidad)) {
            System.out.println("Habilidad creada exitosamente.");
        } else {
            System.out.println("Error al crear la habilidad.");
        }
    }

    private static void handleCreateEvent(RallyRecommendationSystem app, Scanner scanner) {
        System.out.println("Ingrese el nombre del evento:");
        String nombreEvento = scanner.nextLine();
        System.out.println("Ingrese el año del evento:");
        int yearEvento = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.println("Ingrese la ubicación del evento:");
        String ubicacionEvento = scanner.nextLine();
        System.out.println("Ingrese el tipo de superficie del evento:");
        String superficieEvento = scanner.nextLine();
        System.out.println("Ingrese las escuderías que participaron en el evento (separadas por comas):");
        String escuderiasParticipantes = scanner.nextLine();

        if (app.createEvent(nombreEvento, yearEvento, ubicacionEvento, superficieEvento, escuderiasParticipantes)) {
            System.out.println("Evento creado exitosamente.");
        } else {
            System.out.println("Error al crear el evento.");
        }
    }

    private static void handleDeleteTeam(RallyRecommendationSystem app, Scanner scanner) {
        System.out.println("Ingrese el ID del equipo a eliminar:");
        int team_id = scanner.nextInt();

        if (!app.teamExists(team_id)) {
            System.out.println("El equipo con ID " + team_id + " no existe.");
            return;
        }

        if (app.deleteTeam(team_id)) {
            System.out.println("Equipo eliminado exitosamente.");
        } else {
            System.out.println("Error al eliminar el equipo.");
        }
    }
}
