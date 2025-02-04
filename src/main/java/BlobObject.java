import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class BlobObject extends GitObject{
    public BlobObject(String mode, int size, byte[] content) {
        super(mode, size, content);
    }

    public static BlobObject createFromFile(String fileName) throws IOException {
        byte[] fileContent = Files.readAllBytes(new File(fileName).toPath());
        String content = new String(fileContent).replace("\r\n", "\n"); // from CRLF to LF
        byte[] normalizedBytes = content.getBytes();
        String header = "blob " + normalizedBytes.length + "\0";
        byte[] headerBytes = header.getBytes();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(headerBytes);
        outputStream.write(normalizedBytes);
        return new BlobObject("100644",  normalizedBytes.length, outputStream.toByteArray());
    }

    public String getContent() {
        return new String(content).split("\0")[1]; // get rid of the header
    }
}
