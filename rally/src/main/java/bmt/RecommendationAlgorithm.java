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

    public boolean recommendPilots(String teamName) {
        try (Session session = driver.session()) {
            String teamQuery = "MATCH (t:Team {name: $teamName}) " +
                               "RETURN t.driving_style AS drivingStyle, t.preferred_age_range AS ageRange, " +
                               "t.preferred_wins AS wins, t.budget AS budget, t.team_experience AS experience, " +
                               "t.average_pilot_salary AS salary";
            
            Record teamResult = session.readTransaction(tx -> tx.run(teamQuery, Values.parameters("teamName", teamName)).single());

            if (teamResult == null) {
                System.out.println("No se encontró el equipo: " + teamName);
                return false;
            }

            String drivingStyle = teamResult.get("drivingStyle").asString();
            String ageRange = teamResult.get("ageRange").asString();
            int wins = teamResult.get("wins").asInt();
            double budget = teamResult.get("budget").asDouble();
            int experience = teamResult.get("experience").asInt();
            double salary = teamResult.get("salary").asDouble();

            String[] ageRangeSplit = ageRange.split("-");
            int ageRangeStart = Integer.parseInt(ageRangeSplit[0]);
            int ageRangeEnd = Integer.parseInt(ageRangeSplit[1]);

            String finalQuery = "MATCH (p:Pilot) " +
                                "WHERE p.driving_style = $drivingStyle " +
                                "AND p.age >= $ageRangeStart AND p.age <= $ageRangeEnd " +
                                "AND p.wins >= $wins " +
                                "AND p.salary_expectation <= $salary " +
                                "AND p.years_of_experience >= $experience " +
                                "RETURN p.name AS name, p.rating AS rating, p.wins AS wins " +
                                "ORDER BY p.rating DESC LIMIT 10";

            List<Record> result = session.readTransaction(tx -> tx.run(finalQuery, Values.parameters(
                    "drivingStyle", drivingStyle,
                    "ageRangeStart", ageRangeStart,
                    "ageRangeEnd", ageRangeEnd,
                    "wins", wins,
                    "salary", salary,
                    "experience", experience
            )).list());

            if (result.isEmpty()) {
                System.out.println("No se encontraron pilotos recomendados.");
                return false;
            }

            System.out.println("Pilotos recomendados para el equipo " + teamName + ":");
            for (Record record : result) {
                System.out.println("Nombre: " + record.get("name").asString());
                System.out.println("Calificación: " + record.get("rating").asDouble());
                System.out.println("Victorias: " + record.get("wins").asInt());
                System.out.println();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
