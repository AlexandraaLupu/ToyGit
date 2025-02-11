import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class TreeObject extends GitObject {
    public TreeObject(String mode, int size, byte[] content) {
        super(mode, size, content);
    }

    public static TreeObject createFromDirectory(File directory) throws IOException {
        File[] files = directory.listFiles(file -> !file.getName().equals(".git"));
        if (files == null || files.length == 0) {
            return null;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Arrays.sort(files, Comparator.comparing(File::getName)); // sort ascending

        for (File file : files) {
            GitObject gitObject;
            String mode;
            if (file.isFile()) {
                gitObject = BlobObject.createFromFile(file.getPath());
                mode = "100644"; // Mode for regular files
            } else {
                gitObject = TreeObject.createFromDirectory(file);
                mode = "40000"; // Mode for directories
            }
            if (gitObject != null) {
                outputStream.write((mode + " ").getBytes());
                outputStream.write(file.getName().getBytes());
                outputStream.write("\0".getBytes());
                outputStream.write(gitObject.getHashBytes());
                gitObject.writeObject();
            }
        }

        byte[] treeContent = outputStream.toByteArray();
        String header = "tree " + treeContent.length + "\0";
        ByteArrayOutputStream finalOutput = new ByteArrayOutputStream();
        finalOutput.write(header.getBytes());
        finalOutput.write(treeContent);

        return new TreeObject("40000", treeContent.length, finalOutput.toByteArray());
    }

    public List<String> getObjectNames() {
        List<String> names = new ArrayList<>();
        String contentStr = new String(content);
        contentStr = contentStr.substring(contentStr.indexOf("\0") + 1); // get rid of the header
        String modesRegex = "40000|120000|100755|100644";
        String[] lines = contentStr.split(modesRegex); // split by modes
        for(String line : lines) {
            if(!line.isEmpty())
                names.add(line.substring(0, line.indexOf("\0")).trim());
        }
        Collections.sort(names);
        return names;
    }
}
