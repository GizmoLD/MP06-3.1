package cat.iesesteveterradas.exemples;

import org.basex.core.BaseXException;
import org.basex.core.cmd.XQuery;
import org.basex.api.client.ClientSession;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.BsonInt32;
import org.bson.BsonString;

public class PR32CreateMain {
    private static final Logger logger = LoggerFactory.getLogger(PR32CreateMain.class);

    public static void main(String[] args) throws IOException, BaseXException {
        // Conectar con la base de datos MongoDB
        try (ClientSession session = new ClientSession("127.0.0.1", 1984, "admin", "admin")) {
            File logDir = new File("./data/logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            logger.info("Connected to BaseX server.");
    
            String xquery = "declare option output:method \"xml\"; " +
                "declare option output:indent \"yes\"; " +
                "<questions>{ " +
                "  for $question at $i in doc(\"C:/Users/usuario/Documents/DAM2/MP06/UF3/xml_ddbb/Posts.xml/\")//row[@PostTypeId='1'] " +
                "  order by xs:integer($question/@ViewCount) descending " +
                "  where $i <= 50 " +
                "  return " +
                "    <question>{ " +
                "      $question/@Id, " +
                "      $question/@PostTypeId, " +
                "      $question/@AcceptedAnswerId, " +
                "      $question/@CreationDate, " +
                "      $question/@Score, " +
                "      $question/@Body, " +
                "      $question/@OwnerUserId, " +
                "      $question/@LastActivityDate, " +
                "      $question/@Title, " +
                "      $question/@Tags, " +
                "      $question/@AnswerCount, " +
                "      $question/@CommentCount, " +
                "      $question/@ContentLicense, " +
                "      $question/@ViewCount " +
                "    }</question> " +
                "} </questions>";
            String xmlResult = session.execute(new XQuery(xquery));

            List<org.bson.Document> documents = parseQuestions(xmlResult);
            insertIntoMongoDB(documents);
        }
    }

    private static List<org.bson.Document> parseQuestions(String xml) {
        List<org.bson.Document> documents = new ArrayList<>();

        try {
            Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
            Elements rows = doc.select("question");
    
            for (Element row : rows) {
                String id = row.attr("Id");
                String postTypeId = row.attr("PostTypeId");
                String acceptedAnswerId = row.attr("AcceptedAnswerId");
                String creationDate = row.attr("CreationDate");
                String score = row.attr("Score");
                String body = row.attr("Body");
                String ownerUserId = row.attr("OwnerUserId");
                String lastActivityDate = row.attr("LastActivityDate");
                String title = row.attr("Title");
                String tags = row.attr("Tags");
                String answerCount = row.attr("AnswerCount");
                String commentCount = row.attr("CommentCount");
                String contentLicense = row.attr("ContentLicense");
                int viewCount = Integer.parseInt(row.attr("ViewCount"));
    
                org.bson.Document document = new org.bson.Document();
                document.put("id", new BsonString(id));
                document.put("postTypeId", new BsonString(postTypeId));
                document.put("acceptedAnswerId", new BsonString(acceptedAnswerId));
                document.put("creationDate", new BsonString(creationDate));
                document.put("score", new BsonString(score));
                document.put("body", new BsonString(body));
                document.put("ownerUserId", new BsonString(ownerUserId));
                document.put("lastActivityDate", new BsonString(lastActivityDate));
                document.put("title", new BsonString(title));
                document.put("viewCount", new BsonInt32(viewCount));
                document.put("tags", new BsonString(tags));
                document.put("answerCount", new BsonString(answerCount));
                document.put("commentCount", new BsonString(commentCount));
                document.put("contentLicense", new BsonString(contentLicense));
    
                documents.add(document);
            }
        } catch (Exception e) {
            logger.error("Error parsing questions: {}", e.getMessage());
        }

        return documents;
    }

    private static void insertIntoMongoDB(List<org.bson.Document> documents) {
        try (var mongoClient = MongoClients.create("mongodb://root:example@localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("xml_ddbb"); 
            MongoCollection<org.bson.Document> collection = database.getCollection("questions");

            for (org.bson.Document document : documents) {
                collection.insertOne(document);
            }

            logger.info("Document inserted successfully");
        } catch (Exception e) {
            logger.error("An error occurred: {}", e.getMessage());
        }
    }
}
