package cat.iesesteveterradas;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import org.basex.api.client.ClientSession;
import org.basex.core.*;
import org.basex.core.cmd.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);    

    public static void main(String[] args) throws IOException {
         // Initialize connection details
        String host = "127.0.0.1";
        int port = 1984;
        String username = "admin"; // Default username
        String password = "admin"; // Default password

        // Establish a connection to the BaseX server
        try (ClientSession session = new ClientSession(host, port, username, password)) {
            logger.info("Connected to BaseX server.");

            session.execute(new Open("xml_ddbb")); 
            
            // Example query - adjust as needed
            File dataDir = new File("./data/input/");
            if (dataDir.isDirectory()) {
                File[] dir = dataDir.listFiles();
                for (File f : dir) {
                    String query = Files.readString(f.toPath());
                    String result = session.execute(new XQuery(query));
                    File resultFile = new File("./data/output/result_"+f.getName()+".xml");
                    System.out.println(resultFile.getName());
                    FileWriter writer = new FileWriter(resultFile);
                    writer.write(result);
                    writer.close();
                } 
            }            

        } catch (BaseXException e) {
            logger.error("Error connecting or executing the query: " + e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }        
    }
}
