//Name: Yu Chen
//Andrew Id: yuc3
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

/**
 * This is the Project 3 for 95-702.
 * The reference doc is: https://www.andrew.cmu.edu/course/95-702/examples/javadoc/blockchaintask0/Block.html
 * @author Jennifer Chen (yuc3@andrew.cmu.edu)
 */
public class Block {
    /**
     * Instance variable - the position of the block on the chain.
     */
    private int index;
    /**
     * Instance variable - holds the time of the block's creation.
     */
    private Timestamp timestamp;
    /**
     * Instance variable - holding the block's single transaction details.
     */
    private String data;
    /**
     * Instance variable - the minimum number of left most hex digits needed by a proper hash.
     */
    private int difficulty;
    /**
     * Instance variable - determined by a proof of work routine.
     */
    private BigInteger nonce;
    /**
     * Instance variable - the SHA256 hash of a block's parent(hash pointer).
     */
    private String previousHash;

    /**
     * This the Block constructor.
     * @param index - This is the position within the chain. Genesis is at 0.
     * @param timestamp - This is the time this block was added.
     * @param data - This is the transaction to be included on the blockchain.
     * @param difficulty - This is the number of leftmost nibbles that need to be 0.
     */
    public Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
    }

    /**
     * This method returns the nonce for this block.
     * The nonce is a number that has been found to cause
     * the hash of this block to have the correct number of leading hexadecimal zeroes.
     *
     * @return a BigInteger representing the nonce for this block
     */
    public BigInteger getNonce() {
        return nonce;
    }

    /**
     * Simple getter method.
     *
     * @return difficulty
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * @return data
     */
    public String getData() {
        return data;
    }

    /**
     * @return index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return previous hash
     */
    public String getPreviousHash() {
        return previousHash;
    }

    /**
     * @return timestamp
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Simple setter method
     * @param difficulty - determines how much work is required to produce a proper hash
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Simple setter method
     * @param previousHash - a hashpointer to this block's parent
     */
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    /**
     * Simple setter method
     * @param index - the index of this block in the chain
     */
    public void setIndex(int index) {
        this.index = index;

    }

    /**
     * Simple setter method
     * @param data - represents the transaction held by this block
     */
    public void setData(String data) {
        this.data = data;

    }

    /**
     * Simple setter method
     * @param timestamp - of when this block was created
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * This method computes a hash of the concatenation of the index, timestamp, data, previousHash, nonce, and difficulty.
     *
     * @return a String holding Hexadecimal characters
     */
    public String calculateHash() throws NoSuchAlgorithmException {
        StringBuilder sb = new StringBuilder();
        sb.append(index).append(timestamp).append(data).append(previousHash).append(nonce).append(difficulty);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(sb.toString().getBytes());
        return bytesToHex(md.digest());
    }

    // Reference from: https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * The proof of work methods finds a good hash.
     * It increments the nonce until it produces a good hash.
     * This method calls calculateHash() to compute a hash of the concatenation of the index, timestamp, data, previousHash, nonce, and difficulty.
     * If the hash has the appropriate number of leading hex zeroes, it is done and returns that proper hash.
     * If the hash does not have the appropriate number of leading hex zeroes, it increments the nonce by 1 and tries again.
     * It continues this process, burning electricity and CPU cycles, until it gets lucky and finds a good hash.
     *
     * @return a String with a hash that has the appropriate number of leading hex zeroes.
     */
    public String proofOfWork() throws NoSuchAlgorithmException {
        BigInteger n = new BigInteger("0");
        String leadingZeros = "0".repeat(Math.max(0, difficulty));
        String s = calculateHash();

        while (!s.substring(0, difficulty).equals(leadingZeros)) {
            n = n.add(new BigInteger("1"));
            nonce = n; //set the nonce
            s = calculateHash();
        }

        return calculateHash();
    }


    /**
     * @return A JSON representation of all of this block's data is returned.
     */
    @Override
    public String toString() { //output to a json format
        return "{\"index\": "+ getIndex() +", \"time stamp\": \""+ getTimestamp() +
                "\", \"Tx\": \""+ getData() +"\", \"PrevHash\": \""+ getPreviousHash() +
                "\", \"nonce\": "+ getNonce() + ", \"difficulty\": "+ getDifficulty() +
                "}";
    }

    public static void main(String[] args){

    }
}
