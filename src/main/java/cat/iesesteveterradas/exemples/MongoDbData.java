package cat.iesesteveterradas.exemples;
import org.bson.Document;

public class MongoDbData {
    private Document question;

    public MongoDbData(
        String id, 
        String postTypeId, 
        String acceptedAnswerId, 
        String creationDate, 
        String score, 
        String viewCount, 
        String body, 
        String ownerUserId, 
        String lastActivityDate, 
        String title, 
        String tags, 
        String answerCount, 
        String commentCount, 
        String contentLicense) {
        question = new Document("id", Integer.parseInt(id))
                        .append("postTypeId", postTypeId)
                        .append("acceptedAnswerId", acceptedAnswerId)
                        .append("creationDate", creationDate)
                        .append("score", Integer.parseInt(score))
                        .append("viewCount", Integer.parseInt(viewCount))
                        .append("body", body)
                        .append("ownerUserId", ownerUserId)
                        .append("lastActivityDate", lastActivityDate)
                        .append("title", title)
                        .append("tags", tags)
                        .append("answerCount", answerCount)
                        .append("commentCount", commentCount)
                        .append("contentLicense", contentLicense);
    }

    public Document getDocument() {
        return question;
    }
}
