import static org.neo4j.driver.internal.types.InternalTypeSystem.TYPE_SYSTEM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.neo4j.driver.util.Pair;
public class Zoo {
    public static Result createAnimalCaretaker(Transaction transaction, String animalCaretakerName) {
        String command = "CREATE (:Opiekun {nazwisko:$animalCaretakerName})";
        System.out.println("Executing: " + command);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("animalCaretakerName", animalCaretakerName);
        return transaction.run(command, parameters);
    }

    public static Result createAnimal(Transaction transaction, String Animal) {
        String command = "CREATE (:Zwierze {nazwa:$Animal})";
        System.out.println("Executing: " + command);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("Animal", Animal);
        return transaction.run(command, parameters);
    }

    public static Result createRelationship(Transaction transaction, String animalCaretakerName, String Animal) {
        String command =
                "MATCH (s:Opiekun),(g:Zwierze) " +
                        "WHERE s.nazwisko = $animalCaretakerName AND g.nazwa = $Animal "
                        + "CREATE (s)−[r:JEST_OPIEKUNEM]−>(g)" +
                        "RETURN type(r)";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("animalCaretakerName", animalCaretakerName);
        parameters.put("Animal", Animal);
        System.out.println("Executing: " + command);
        return transaction.run(command, parameters);
    }

    public static Result readAllNodes(Transaction transaction) {
        String command =
                "MATCH (n)" +
                        "RETURN n";
        System.out.println("Executing: " + command);
        Result result = transaction.run(command);
        while (result.hasNext()) {
            Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                printField(field);
        }
        return result;
    }

    public static Result readAllRealtionships(Transaction transaction) {
        String command =
                "MATCH ()−[r]−>()" +
                        "RETURN r;";
        System.out.println("Executing: " + command);
        Result result = transaction.run(command);
        while (result.hasNext()) {
            Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                printField(field);
        }
        return result;
    }

    public static Result readAllNodesWithRealationships(Transaction transaction) {
        String command =
                "MATCH (n1)−[r]−(n2) " +
                        "RETURN n1, r, n2 ";
        System.out.println("Executing: " + command);
        Result result = transaction.run(command);
        while (result.hasNext()) {
            Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                printField(field);
        }
        return result;
    }

    public static Result readAllNodesWithLabel(Transaction transaction) {
        String command =
                "MATCH (s:Opiekun)−[r]−(n) " +
                        "RETURN s, r, n";
        System.out.println("Executing: " + command);
        Result result = transaction.run(command);
        while (result.hasNext()) {
            Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                printField(field);
        }
        return result;
    }

    public static void printField(Pair<String, Value> field) {
        System.out.println("field = " + field);
        Value value = field.value();
        if (TYPE_SYSTEM.NODE().isTypeOf(value))
            printNode(field.value().asNode());
        else if (TYPE_SYSTEM.RELATIONSHIP().isTypeOf(value))
            printRelationship(field.value().asRelationship());
        else
            throw new RuntimeException();
    }

    public static void printNode(Node node) {
        System.out.println("id = " + node.id());
        System.out.println("labels = " + " : " + node.labels());
        System.out.println("asMap = " + node.asMap());
    }

    public static void printRelationship(Relationship relationship) {
        System.out.println("id = " + relationship.id());
        System.out.println("type = " + relationship.type());
        System.out.println("startNodeId = " + relationship.startNodeId());
        System.out.println("endNodeId = " + relationship.endNodeId());
        System.out.println("asMap = " + relationship.asMap());
    }

    public static Result deleteEverything(Transaction transaction) {
        String command = "MATCH (n) DETACH DELETE n";
        System.out.println("Executing: " + command);
        return transaction.run(command);
    }


    public static void main(String[] args) throws Exception {
        try (Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "neo4jpassword"));
             Session session = driver.session()) {
            session.writeTransaction(tx -> deleteEverything(tx));



            int n=0;

            Scanner scanner = new Scanner(System.in);

                    do{
                        System.out.println("0.Create data\n1.Show\n2.Edit\n3.Delete\n4.Select\n5.Select where\n6.exit\n");
                        n=scanner.nextInt();
                        switch(n){
                            case 0:
                                session.writeTransaction(tx -> createAnimalCaretaker(tx, "Nowak"));
                                session.writeTransaction(tx -> createAnimalCaretaker(tx, "Polak"));
                                session.writeTransaction(tx -> createAnimalCaretaker(tx, "Kowalski"));
                                session.writeTransaction(tx -> createAnimal(tx, "Lampart"));
                                session.writeTransaction(tx -> createAnimal(tx, "Slon"));
                                session.writeTransaction(tx -> createAnimal(tx, "Orzel"));
                                session.writeTransaction(tx -> createRelationship(tx, "Kowalski", "Orzel"));
                                break;
                            case 1:
                                session.writeTransaction(tx -> readAllNodes(tx));

                                session.writeTransaction(tx -> readAllNodesWithRealationships(tx));
                                session.writeTransaction(tx -> readAllNodesWithLabel(tx));
                                break;
                            case 2:
                                break;
                            case 3:
                                try {
                                    session.writeTransaction(tx -> deleteEverything(tx));
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                break;
                            case 4:
                                session.writeTransaction(tx -> readAllRealtionships(tx));
                                break;
                            case 5:

                                break;


                        }

                    }while(n!=6);
        }
    }

}
