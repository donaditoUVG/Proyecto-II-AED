package bmt;

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

    // Método para eliminar un nodo
    public boolean deleteNode(String label, String identifier, Object value) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (n:" + label + " {" + identifier + ": $value}) DETACH DELETE n",
                        Values.parameters("value", value));
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
}
