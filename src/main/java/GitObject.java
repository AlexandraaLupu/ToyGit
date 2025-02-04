import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

abstract class GitObject {
    protected String mode;
    protected int size;
    protected byte[] content;

    public GitObject(String mode, int size, byte[] content) {
        this.mode = mode;
        this.size = size;
        this.content = content;
    }

    public String getMode() {
        return mode;
    }

    public int getSize() {
        return size;
    }

    public String getHashHex() {
        return DigestUtils.sha1Hex(content);
    }

    public byte[] getHashBytes() {
        return DigestUtils.sha1(content);
    }

    public void writeObject() throws IOException {
        String hash = getHashHex();
        String directory = hash.substring(0, 2);
        String file = hash.substring(2);
        File f = new File(".git/objects/" + directory + "/" + file);
        f.getParentFile().mkdirs();

        try (DeflaterOutputStream dos = new DeflaterOutputStream(new FileOutputStream(f))) {
            dos.write(content);
        }
    }

    public static GitObject readObject(String hash) throws IOException {
        String dirHash = hash.substring(0, 2);
        String fileHash = hash.substring(2);
        File objectFile = new File(".git/objects/" + dirHash + "/" + fileHash);

        try (InputStream inputStream = new InflaterInputStream(new FileInputStream(objectFile))) {
            byte[] data = inputStream.readAllBytes();
            String header = new String(data).split("\0")[0];
            String[] headerParts = header.split(" ");
            String type = headerParts[0];
            int size = Integer.parseInt(headerParts[1]);

            if (type.equals("blob")) {
                return new BlobObject("100644", size, data);
            } else if (type.equals("tree")) {
                return new TreeObject("40000", size, data);
            } else {
                throw new IllegalArgumentException("Unknown object type: " + type);
            }
        }
    }
}
