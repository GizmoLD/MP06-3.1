package cat.iesesteveterradas.exemples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class PR32QueryMain {
    private static final Logger logger = LoggerFactory.getLogger(PR32QueryMain.class);   
    public static void main(String[] args) {
        try (var mongoClient = MongoClients.create("mongodb://root:example@localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("xml_ddbb"); 
            MongoCollection<Document> collection = database.getCollection("questions");
            logger.info("Connected to MongoDB");

            // Exercici 1
            List<Document> allDocuments = new ArrayList<>();
            MongoCursor<Document> allCursor = collection.find().iterator();
            while (allCursor.hasNext()) {
                allDocuments.add(allCursor.next());
            }
            logger.info("All documents retrieved");

            double totalViewCount = 0;
            for (Document doc : allDocuments) {
                totalViewCount += doc.getInteger("viewCount");
            }
            double averageViewCount = totalViewCount / allDocuments.size();
            logger.info("Average viewCount: " + averageViewCount);

            Document query = new Document("viewCount", new Document("$gt", averageViewCount));
            
            FindIterable<Document> result = collection.find(query);

            logger.info("Query 1 done");

            try (PDDocument doc = new PDDocument()) {
                PDPage page = new PDPage();
                doc.addPage(page);

                try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                    contents.beginText();
                    contents.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                    contents.setLeading(14.5f);
                    contents.newLineAtOffset(25, 750);

                    for (Document docMongoDB : result) {
                        contents.showText(docMongoDB.getString("title") + "; ViewCounts: " + docMongoDB.getInteger("viewCount"));
                        contents.newLine();
                    }
                    contents.endText();
                }

                String outputPath = System.getProperty("user.dir") + "/data/out/informe1.pdf";
                doc.save(outputPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Exercici 2
            List<String> wordsToSearch = Arrays.asList("pug", "wig", "yak", "nap", "jig", "mug", "zap", "gag", "oaf", "elf", "hat", "D&D");
            String regexPattern = String.join("|", wordsToSearch);

            query = new Document("title", new Document("$regex", regexPattern));
            result = collection.find(query);

            logger.info("Query 2 done");

            try (PDDocument doc = new PDDocument()) {
                PDPage page = new PDPage();
                doc.addPage(page);

                try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                    contents.beginText();
                    contents.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                    contents.setLeading(14.5f);
                    contents.newLineAtOffset(25, 750);

                    for (Document docMongoDB : result) {
                        contents.showText(docMongoDB.getString("title"));
                        contents.newLine();
                    }
                    contents.endText();
                }

                String outputPath = System.getProperty("user.dir") + "/data/out/informe2.pdf";
                doc.save(outputPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}
