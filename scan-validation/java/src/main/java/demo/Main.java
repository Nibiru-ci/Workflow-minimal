// ECHANTILLON VOLONTAIREMENT VULNERABLE : sert uniquement à vérifier que les scans CI détectent bien les problèmes.
package demo;

import java.io.ObjectInputStream;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOG = LogManager.getLogger(Main.class);

    // SAST / secret : identifiants en dur (valeurs factices)
    private static final String DB_PASSWORD = "SuperSecretPassw0rd!";
    private static final String API_KEY = "k8Jd93hFs02LmZp7QxVn4TrWy61BcAe5";

    public static void main(String[] args) throws Exception {
        String name = args.length > 0 ? args[0] : "";

        // SAST : injection SQL
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/app", "root", DB_PASSWORD);
        Statement stmt = conn.createStatement();
        stmt.executeQuery("SELECT * FROM users WHERE name = '" + name + "'");

        // SAST : injection de commande
        Runtime.getRuntime().exec("ping -c 1 " + name);

        // SAST : hachage faible
        MessageDigest.getInstance("MD5").digest(DB_PASSWORD.getBytes());

        // SAST : XXE (parseur XML non durci)
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(name);

        // SAST : désérialisation non sûre
        new ObjectInputStream(System.in).readObject();

        // Entrée utilisateur journalisée avec Log4j vulnérable (Log4Shell)
        LOG.error("Login failed for {}", name);
    }
}
