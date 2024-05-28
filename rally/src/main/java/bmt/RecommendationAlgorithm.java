package bmt;

import java.util.List;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

public class RecommendationAlgorithm implements AutoCloseable {
    private final Driver driver;

    public RecommendationAlgorithm(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    public boolean recommendPilotsByTeam(String teamName, int teamId) {
        try (Session session = driver.session()) {
            String finalQuery = "MATCH (t:Team {name: $teamName, team_id: $teamId})-[:HAS_PILOT]->(p:Pilot) " +
                                "RETURN p.name AS name, p.rating AS rating, p.wins AS wins, p.age AS age, p.salary_expectation AS salary, p.years_of_experience AS experience " +
                                "ORDER BY p.rating DESC LIMIT 10";

            System.out.println("Ejecutando consulta Cypher: " + finalQuery);
            System.out.println("Parámetros: " + Values.parameters("teamName", teamName, "teamId", teamId));

            List<Record> result = session.readTransaction(tx -> tx.run(finalQuery, Values.parameters("teamName", teamName, "teamId", teamId)).list());

            if (result.isEmpty()) {
                System.out.println("No se encontraron pilotos recomendados.");
                return false;
            }

            System.out.println("Pilotos recomendados:");
            for (Record record : result) {
                System.out.println("Nombre: " + record.get("name").asString());
                System.out.println("Calificación: " + record.get("rating").asDouble());
                System.out.println("Victorias: " + record.get("wins").asInt());
                System.out.println("Edad: " + record.get("age").asInt());
                System.out.println("Salario: " + record.get("salary").asDouble());
                System.out.println("Experiencia: " + record.get("experience").asInt());
                System.out.println();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean recommendPilotsBySpecific(String specificType, String specificValue) {
        try (Session session = driver.session()) {
            final String[] finalQuery = {""}; // Using an array to hold the query string

            switch (specificType) {
                case "eventos":
                    finalQuery[0] = "MATCH (p:Pilot) WHERE ANY(event IN split(p.eventos_participados, ',') WHERE event = $specificValue) " +
                                    "RETURN p.name AS name, p.rating AS rating, p.wins AS wins, p.age AS age, p.salary_expectation AS salary, p.years_of_experience AS experience " +
                                    "ORDER BY p.rating DESC LIMIT 10";
                    break;
                case "pais":
                    finalQuery[0] = "MATCH (p:Pilot {country: $specificValue}) " +
                                    "RETURN p.name AS name, p.rating AS rating, p.wins AS wins, p.age AS age, p.salary_expectation AS salary, p.years_of_experience AS experience " +
                                    "ORDER BY p.rating DESC LIMIT 10";
                    break;
                case "victorias":
                    finalQuery[0] = "MATCH (p:Pilot) WHERE p.wins >= toInteger($specificValue) " +
                                    "RETURN p.name AS name, p.rating AS rating, p.wins AS wins, p.age AS age, p.salary_expectation AS salary, p.years_of_experience AS experience " +
                                    "ORDER BY p.rating DESC LIMIT 10";
                    break;
                case "edad":
                    finalQuery[0] = "MATCH (p:Pilot) WHERE p.age <= toInteger($specificValue) " +
                                    "RETURN p.name AS name, p.rating AS rating, p.wins AS wins, p.age AS age, p.salary_expectation AS salary, p.years_of_experience AS experience " +
                                    "ORDER BY p.rating DESC LIMIT 10";
                    break;
                case "salario":
                    finalQuery[0] = "MATCH (p:Pilot) WHERE p.salary_expectation <= toFloat($specificValue) " +
                                    "RETURN p.name AS name, p.rating AS rating, p.wins AS wins, p.age AS age, p.salary_expectation AS salary, p.years_of_experience AS experience " +
                                    "ORDER BY p.rating DESC LIMIT 10";
                    break;
                default:
                    System.out.println("Tipo específico no válido.");
                    return false;
            }

            System.out.println("Ejecutando consulta Cypher: " + finalQuery[0]);
            System.out.println("Parámetros: " + Values.parameters("specificValue", specificValue));

            List<Record> result = session.readTransaction(tx -> tx.run(finalQuery[0], Values.parameters("specificValue", specificValue)).list());

            if (result.isEmpty()) {
                System.out.println("No se encontraron pilotos recomendados.");
                return false;
            }

            System.out.println("Pilotos recomendados:");
            for (Record record : result) {
                System.out.println("Nombre: " + record.get("name").asString());
                System.out.println("Calificación: " + record.get("rating").asDouble());
                System.out.println("Victorias: " + record.get("wins").asInt());
                System.out.println("Edad: " + record.get("age").asInt());
                System.out.println("Salario: " + record.get("salary").asDouble());
                System.out.println("Experiencia: " + record.get("experience").asInt());
                System.out.println();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
