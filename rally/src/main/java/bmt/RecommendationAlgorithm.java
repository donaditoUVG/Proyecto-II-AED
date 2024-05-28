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

    public boolean recommendPilots(String drivingStyle, String ageRange, int wins, int experience, double budget, double salary, String specialSkills, String eventParticipation, String country, String sponsor) {
        try (Session session = driver.session()) {
            String[] ageRangeSplit = ageRange.split("-");
            int ageRangeStart = Integer.parseInt(ageRangeSplit[0]);
            int ageRangeEnd = Integer.parseInt(ageRangeSplit[1]);
    
            String finalQuery = "MATCH (p:Pilot)-[:BELONGS_TO]->(t:Team) " +
                                "WHERE p.driving_style = $drivingStyle " +
                                "AND p.age >= $ageRangeStart AND p.age <= $ageRangeEnd " +
                                "AND p.wins >= $wins " +
                                "AND p.salary_expectation <= $salary " +
                                "AND p.years_of_experience >= $experience " +
                                "AND ANY(skill IN split(p.special_skills, ',') WHERE skill IN split($specialSkills, ',')) " +
                                "AND ANY(event IN split(p.eventos_participados, ',') WHERE event IN split($eventParticipation, ',')) " +
                                "AND p.country = $country " +
                                "AND t.sponsor = $sponsor " +
                                "RETURN p.name AS name, p.rating AS rating, p.wins AS wins, p.age AS age, p.salary_expectation AS salary, p.years_of_experience AS experience " +
                                "ORDER BY p.rating DESC LIMIT 10";
    
            List<Record> result = session.readTransaction(tx -> tx.run(finalQuery, Values.parameters(
                    "drivingStyle", drivingStyle,
                    "ageRangeStart", ageRangeStart,
                    "ageRangeEnd", ageRangeEnd,
                    "wins", wins,
                    "salary", salary,
                    "experience", experience,
                    "specialSkills", specialSkills,
                    "eventParticipation", eventParticipation,
                    "country", country,
                    "sponsor", sponsor
            )).list());
    
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