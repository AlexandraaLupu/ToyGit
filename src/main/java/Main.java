import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class Main {
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.err.println("Logs from your program will appear here!");
    // Uncomment this block to pass the first stage
    //
    final String command = args[0];

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
      default -> System.out.println("Unknown command: " + command);
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

  private static void catFileCommand(String[] args) {
    String hash = args[2];
    String dirHash = hash.substring(0, 2); // first 2 for the directory
    String fileHash = hash.substring(2); // the rest for the file name
    // we need to read fileHash from the directory dirHash from .git/objects/
    File blobFile = new File(".git/objects/" + dirHash + "/" + fileHash);
    try {
      // FileInputStream -> read raw file bytes
      // InflaterInputStream -> decompress the bytes
      // InputStreamReader -> converts bytes to chars
      // BufferedReader -> buffers the characters
      String blob = new BufferedReader(new InputStreamReader(new InflaterInputStream(new FileInputStream(blobFile)))).readLine();
      String content = blob.substring(blob.indexOf("\0") + 1);
      System.out.print(content); // System.out.println has \n at the end
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void hashObjectCommand(String[] args) {
    String fileName = args[2];
    try {
      String fileContent = new BufferedReader(new FileReader(fileName)).readLine();
      String blobHeader = "blob " + fileContent.length() + "\0";
      String blobContent = blobHeader + fileContent;
      String sha1hex = DigestUtils.sha1Hex(blobContent);
      System.out.println(sha1hex); // print the hash
      String directory = sha1hex.substring(0, 2);
      String file = sha1hex.substring(2);
      File f = new File(".git/objects/" + directory + "/" + file);
      f.getParentFile().mkdirs();
      f.createNewFile();
      try (OutputStream fos = new FileOutputStream(f); DeflaterOutputStream dos = new DeflaterOutputStream(fos)) {
        dos.write(blobContent.getBytes());
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void lsTreeCommand(String[] args) {
    // first we need to get the directory and file name from the hash
    // content of tree:
    // tree <size>\0
    // <mode> <name>\0<20_byte_sha>
    String treeDirectory = args[2].substring(0, 2);
    String treeFile = args[2].substring(2);
    String treePath = ".git/objects/" + treeDirectory + "/" + treeFile;
    File f = new File(treePath);
    try {
      // open the file, decompress and read
      BufferedReader bf = new BufferedReader(new InputStreamReader(new InflaterInputStream(new FileInputStream(f))));
      String content = bf.readLine();
      content = content.substring(content.indexOf("\0") + 1); // get read of the header

      List<String> names = new ArrayList<>();
      String[] lines;
      String modesRegex = "40000|120000|100755|100644";
      lines = content.split(modesRegex); // here we get the mode name and sha
      for(String line : lines) {
        if(!line.isEmpty())
          names.add(line.substring(0, line.indexOf("\0")).trim());
      }
      Collections.sort(names);
      for (String name: names) {
        System.out.println(name);
      }

    }
    catch (IOException e) {
      e.printStackTrace();
    }


  }
}
