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

    public boolean recommendPilots(String drivingStyle, int ageMin, int wins, int experience, double salary, String specialSkills, String eventParticipation, String country, String teamName) {
        try {
            createRelationships();
            return queryPilots(drivingStyle, ageMin, wins, experience, salary, specialSkills, eventParticipation, country, teamName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void createRelationships() {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (t:Team), (p:Pilot) WHERE t.driving_style = p.driving_style CREATE (t)-[:PREFERS_DRIVING_STYLE]->(p)");
                tx.run("MATCH (t:Team), (p:Pilot) WHERE toInteger(split(t.preferred_age_range, '-')[0]) <= p.age AND p.age <= toInteger(split(t.preferred_age_range, '-')[1]) CREATE (t)-[:PREFERS_AGE_RANGE]->(p)");
                tx.run("MATCH (t:Team), (p:Pilot) WHERE p.wins >= t.preferred_wins CREATE (t)-[:PREFERS_WINNER]->(p)");
                tx.run("MATCH (t:Team), (p:Pilot) WHERE p.salary_expectation <= t.budget CREATE (t)-[:CAN_AFFORD]->(p)");
                tx.run("MATCH (t:Team), (p:Pilot) WHERE p.years_of_experience <= t.team_experience CREATE (t)-[:PREFERS_EXPERIENCE]->(p)");
                tx.run("MATCH (t:Team), (p:Pilot)-[:HAS_SKILL]->(sk:Skill) WHERE sk.name IN split(t.special_skills, ',') CREATE (t)-[:PREFERS_SKILL]->(p)");
                tx.run("MATCH (t:Team), (p:Pilot) WHERE p.salary_expectation <= t.average_pilot_salary CREATE (t)-[:MATCHES_SALARY]->(p)");
                return null;
            });
        }
    }

    private boolean queryPilots(String drivingStyle, int ageMin, int wins, int experience, double salary, String specialSkills, String eventParticipation, String country, String teamName) {
        try (Session session = driver.session()) {
            String finalQuery = "MATCH (t:Team {name: $teamName}) " +
                                "OPTIONAL MATCH (t)-[:PREFERS_DRIVING_STYLE]->(p1:Pilot) " +
                                "OPTIONAL MATCH (t)-[:PREFERS_AGE_RANGE]->(p2:Pilot) " +
                                "OPTIONAL MATCH (t)-[:PREFERS_WINNER]->(p3:Pilot) " +
                                "OPTIONAL MATCH (t)-[:CAN_AFFORD]->(p4:Pilot) " +
                                "OPTIONAL MATCH (t)-[:PREFERS_EXPERIENCE]->(p5:Pilot) " +
                                "OPTIONAL MATCH (t)-[:PREFERS_SKILL]->(p6:Pilot) " +
                                "OPTIONAL MATCH (t)-[:MATCHES_SALARY]->(p7:Pilot) " +
                                "WITH t, COLLECT(DISTINCT p1) + COLLECT(DISTINCT p2) + COLLECT(DISTINCT p3) + COLLECT(DISTINCT p4) + COLLECT(DISTINCT p5) + COLLECT(DISTINCT p6) + COLLECT(DISTINCT p7) AS allPilots " +
                                "UNWIND allPilots AS p " +
                                "WITH p, COUNT(p) AS matchScore " +
                                "WHERE p.driving_style = $drivingStyle " +
                                "AND p.age >= $ageMin " +
                                "AND p.wins >= $wins " +
                                "AND p.salary_expectation <= $salary " +
                                "AND p.years_of_experience >= $experience " +
                                "AND ANY(skill IN split(p.special_skills, ',') WHERE skill IN split($specialSkills, ',')) " +
                                "AND ANY(event IN split(p.eventos_participados, ',') WHERE event IN split($eventParticipation, ',')) " +
                                "AND p.country = $country " +
                                "RETURN p.name AS name, p.rating AS rating, p.wins AS wins, p.age AS age, p.salary_expectation AS salary, p.years_of_experience AS experience, matchScore " +
                                "ORDER BY matchScore DESC LIMIT 10";
    
            List<Record> result = session.readTransaction(tx -> tx.run(finalQuery, Values.parameters(
                "drivingStyle", drivingStyle,
                "ageMin", ageMin,
                "wins", wins,
                "salary", salary,
                "experience", experience,
                "specialSkills", specialSkills,
                "eventParticipation", eventParticipation,
                "country", country,
                "teamName", teamName
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
