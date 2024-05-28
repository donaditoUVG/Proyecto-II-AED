package bmt;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (RallyRecommendationSystem app = new RallyRecommendationSystem();
             Scanner scanner = new Scanner(System.in)) {
            System.out.println("Ingrese la URL de conexión de la base de datos:");
            String uri = scanner.nextLine();
            System.out.println("Ingrese el usuario de la base de datos:");
            String user = scanner.nextLine();
            System.out.println("Ingrese la contraseña de la base de datos:");
            String password = scanner.nextLine();

            app.connect(uri, user, password);
            RecommendationAlgorithm recommendationAlgorithm = new RecommendationAlgorithm(uri, user, password);
            System.out.println("Conexión exitosa a la base de datos.");

            while (true) {
                System.out.println("Seleccione el tipo de usuario:");
                System.out.println("1. ADMIN");
                System.out.println("2. USER");
                System.out.println("3. Salir");
                System.out.print("Ingrese el número de la opción: ");
                int userType = scanner.nextInt();
                scanner.nextLine(); // consume newline

                if (userType == 1) {
                    adminMenu(app, scanner);
                } else if (userType == 2) {
                    userMenu(recommendationAlgorithm, scanner);
                } else if (userType == 3) {
                    System.out.println("Saliendo del programa.");
                    recommendationAlgorithm.close();
                    return;
                } else {
                    System.out.println("Opción no válida. Intente de nuevo.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void adminMenu(RallyRecommendationSystem app, Scanner scanner) {
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
            System.out.println("9. Añadir un patrocinador");
            System.out.println("10. Eliminar un equipo/patrocinador/piloto/copiloto/vehículo/habilidad/evento");
            System.out.println("11. Salir");
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
                    handleAddSponsor(app, scanner);
                    break;
                case 10:
                    handleDeleteNode(app, scanner);
                    break;
                case 11:
                    return;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private static void userMenu(RecommendationAlgorithm recommendationAlgorithm, Scanner scanner) {
        while (true) {
            System.out.println("Seleccione una opción:");
            System.out.println("1. Recomendar pilotos");
            System.out.println("2. Salir");
            System.out.print("Ingrese el número de la opción: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (option) {
                case 1:
                    handleRecommendPilots(recommendationAlgorithm, scanner);
                    break;
                case 2:
                    return;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
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
        String nombrePatrocinador = scanner.nextLine();
        System.out.println("Ingrese el nivel de importancia del patrocinador:");
        int nivelImportancia = scanner.nextInt();
        System.out.println("Ingrese el monto de patrocinio del patrocinador:");
        int montoPatrocinio = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (app.createSponsor(nombrePatrocinador, nivelImportancia, montoPatrocinio)) {
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
        String nombreHabilidad = scanner.nextLine();
        System.out.println("Ingrese el nivel de importancia de la habilidad:");
        int nivelImportancia = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.println("Ingrese el tipo de habilidad:");
        String tipoHabilidad = scanner.nextLine();

        if (app.createSkill(skill_id, nombreHabilidad, nivelImportancia, tipoHabilidad)) {
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
        System.out.println("Ingrese los equipos que participaron en el evento (separados por comas):");
        String equiposParticipantes = scanner.nextLine();

        if (app.createEvent(nombreEvento, yearEvento, ubicacionEvento, superficieEvento, equiposParticipantes)) {
            System.out.println("Evento creado exitosamente.");
        } else {
            System.out.println("Error al crear el evento.");
        }
    }

    private static void handleAddSponsor(RallyRecommendationSystem app, Scanner scanner) {
        System.out.println("Ingrese el nombre del equipo:");
        String nombreEquipo = scanner.nextLine();
        System.out.println("Ingrese el nombre del patrocinador:");
        String nombrePatrocinador = scanner.nextLine();

        if (app.addSponsorToTeam(nombreEquipo, nombrePatrocinador)) {
            System.out.println("Patrocinador añadido exitosamente al equipo.");
        } else {
            System.out.println("Error al añadir el patrocinador al equipo.");
        }
    }

    private static void handleDeleteNode(RallyRecommendationSystem app, Scanner scanner) {
        System.out.println("Seleccione el tipo de nodo a eliminar:");
        System.out.println("1. Equipo");
        System.out.println("2. Patrocinador");
        System.out.println("3. Piloto");
        System.out.println("4. Copiloto");
        System.out.println("5. Vehículo");
        System.out.println("6. Habilidad");
        System.out.println("7. Evento");
        int nodeType = scanner.nextInt();
        scanner.nextLine(); // consume newline

        String label = "";
        String identifier = "";
        Object value = null;

        switch (nodeType) {
            case 1:
                label = "Team";
                System.out.println("Ingrese el ID del equipo a eliminar:");
                identifier = "team_id";
                value = scanner.nextInt();
                scanner.nextLine(); // consume newline
                break;
            case 2:
                label = "Sponsor";
                System.out.println("Ingrese el nombre del patrocinador a eliminar:");
                identifier = "name";
                value = scanner.nextLine();
                break;
            case 3:
                label = "Pilot";
                System.out.println("Ingrese el ID del piloto a eliminar:");
                identifier = "pilot_id";
                value = scanner.nextInt();
                scanner.nextLine(); // consume newline
                break;
            case 4:
                label = "Copilot";
                System.out.println("Ingrese el nombre del copiloto a eliminar:");
                identifier = "name";
                value = scanner.nextLine();
                break;
            case 5:
                label = "Vehicle";
                System.out.println("Ingrese el modelo del vehículo a eliminar:");
                identifier = "model";
                value = scanner.nextLine();
                break;
            case 6:
                label = "Skill";
                System.out.println("Ingrese el ID de la habilidad a eliminar:");
                identifier = "skill_id";
                value = scanner.nextInt();
                scanner.nextLine(); // consume newline
                break;
            case 7:
                label = "Event";
                System.out.println("Ingrese el nombre del evento a eliminar:");
                identifier = "name";
                value = scanner.nextLine();
                break;
            default:
                System.out.println("Tipo de nodo no válido.");
                return;
        }

        if (app.deleteNode(label, identifier, value)) {
            System.out.println("Nodo eliminado exitosamente.");
        } else {
            System.out.println("Error al eliminar el nodo.");
        }
    }

    private static void handleDeleteTeam(RallyRecommendationSystem app, Scanner scanner) {
        System.out.println("Ingrese el ID del equipo a eliminar:");
        int team_id = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (app.deleteTeam(team_id)) {
            System.out.println("Equipo eliminado exitosamente.");
        } else {
            System.out.println("Error al eliminar el equipo.");
        }
    }

    private static void handleRecommendPilots(RecommendationAlgorithm recommendationAlgorithm, Scanner scanner) {
        System.out.println("Para encontrar el piloto ideal para su equipo de rally, por favor responda las siguientes preguntas:");
        System.out.println("1. ¿Cuál es el estilo de conducción preferido de su equipo? (Opciones: agresivo, defensivo, balanceado)");
        String drivingStyle = scanner.nextLine();
        System.out.println("2. ¿Cuál es el rango de edad preferido para el piloto? (Por favor ingrese en el formato: min-max, e.g., 25-35)");
        String ageRange = scanner.nextLine();
        System.out.println("3. ¿Cuál es el mínimo de victorias que desea que tenga el piloto?");
        int wins = scanner.nextInt();
        System.out.println("4. ¿Cuántos años de experiencia debería tener el piloto como mínimo?");
        int experience = scanner.nextInt();
        System.out.println("5. ¿Cuál es el presupuesto máximo que su equipo está dispuesto a gastar en un piloto?");
        double budget = scanner.nextDouble();
        System.out.println("6. ¿Cuál es la expectativa salarial promedio que su equipo puede ofrecer al piloto?");
        double salary = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        System.out.println("7. ¿Existen habilidades especiales o cualificaciones adicionales que son importantes para su equipo? (Por favor liste cualquier habilidad específica)");
        String specialSkills = scanner.nextLine();
        System.out.println("8. ¿En qué eventos debería haber participado el piloto? (Ingrese los nombres de los eventos separados por comas)");
        String eventParticipation = scanner.nextLine();
        System.out.println("9. ¿De qué país debería ser el piloto?");
        String country = scanner.nextLine();
        System.out.println("10. ¿Cuál es el patrocinador asociado con su equipo?");
        String sponsor = scanner.nextLine();

        recommendationAlgorithm.recommendPilots(drivingStyle, ageRange, wins, experience, budget, salary, specialSkills, eventParticipation, country, sponsor);
    }
}
