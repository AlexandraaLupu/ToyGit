import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CommitObject extends GitObject {
    CommitObject(int size, byte[] content) throws IOException{
        super(null, size, content);
    }

    public static CommitObject generateCommitContent(String treeShaHex, String parentShaHex, String message, String author, String committer) throws IOException {
        ByteArrayOutputStream contentStream = new ByteArrayOutputStream();

        contentStream.write(String.format("tree %s\nparent %s\nauthor %s %s %s %s\ncommitter %s %s %s %s\n\n%s\n",
                treeShaHex,
                parentShaHex,
                author, author, Instant.now().getEpochSecond(), getTimeZone(),
                committer, committer, Instant.now().getEpochSecond(), getTimeZone(),
                message).getBytes());

        byte[] commitContent = contentStream.toByteArray();
        String header = "commit " + commitContent.length + "\0";
        ByteArrayOutputStream finalOutput = new ByteArrayOutputStream();
        finalOutput.write(header.getBytes());
        finalOutput.write(commitContent);
        return new CommitObject(contentStream.size(), finalOutput.toByteArray());
    }

    private static String getTimeZone() {
        ZonedDateTime now = ZonedDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("XX"));
    }

}
