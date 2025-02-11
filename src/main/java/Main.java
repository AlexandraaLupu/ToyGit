import java.io.*;
import java.nio.file.Files;


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
                case "commit-tree" -> {
                    commitTreeCommand(args);
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

    private static void commitTreeCommand(String[] args) throws IOException {
        // commit-tree <tree_sha> -p <commit_sha> -m <message>
        String treeShaHex = args[1];
        String commitShaHex = args[3];
        String message = args[5];
        String author = "Jane Doe";
        String committer = "Jane Doe";

        CommitObject commit = CommitObject.generateCommitContent(treeShaHex, commitShaHex, message, author, committer);
        commit.writeObject();
        System.out.println(commit.getHashHex());
    }
}
