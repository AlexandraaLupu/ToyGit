import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class Main {
    public static void main(String[] args) {
        final String command = args[0];
        try {
            switch (command) {
                case "init" -> {
                    initCommand(args);
                }
                case "cat-file" -> {
                    catFileCommand(args);
                }
                case "hash-object" -> {
                    hashObjectCommand(args);
                }
                case "ls-tree" -> {
                    lsTreeCommand(args);
                }
                case "write-tree" -> {
                    writeTreeCommand();
                }
                default -> System.out.println("Unknown command: " + command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initCommand(String[] args) {
        final File root = new File(".git");
        new File(root, "objects").mkdirs();
        new File(root, "refs").mkdirs();
        final File head = new File(root, "HEAD");

        try {
            head.createNewFile();
            Files.write(head.toPath(), "ref: refs/heads/main\n".getBytes());
            System.out.println("Initialized git directory");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void catFileCommand(String[] args) throws IOException {
        GitObject obj = GitObject.readObject(args[2]);
        if (obj instanceof BlobObject) {
            System.out.print(((BlobObject) obj).getContent());
        }
    }

    private static void hashObjectCommand(String[] args) throws IOException {
        BlobObject blob = BlobObject.createFromFile(args[2]);
        blob.writeObject();
        System.out.println(blob.getHashHex());
    }

    private static void lsTreeCommand(String[] args) throws IOException {
        GitObject obj = GitObject.readObject(args[2]);
        if (obj instanceof TreeObject tree) {
            for (String name : tree.getObjectNames()) {
                System.out.println(name);
            }
        }
    }

    private static void writeTreeCommand() throws IOException {
        // get files from the current directory
        // if you get a file, write a blob object to the tree
        // if you get a directory, write a tree and call recursively the function
        TreeObject tree = TreeObject.createFromDirectory(new File("."));
        if (tree != null) {
            tree.writeObject();
            System.out.println(tree.getHashHex());
        }
    }
}
