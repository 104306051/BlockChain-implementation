//Name: Yu Chen
//Andrew Id: yuc3

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This is the Project 3 for 95-702.
 * The reference doc is: https://www.andrew.cmu.edu/course/95-702/examples/javadoc/blockchaintask0/BlockChain.html
 *
 * @author Jennifer Chen (yuc3@andrew.cmu.edu)
 */
public class BlockChain {
    /**
     * Instance member - an ArrayList to hold Blocks.
     */
    private List<Block> blocks;
    /**
     * Instance variable - a chain hash to hold a SHA256 hash of the most recently added Block.
     */
    private String chainHash;
    /**
     * Instance variable - approximate number of hashes per second on this computer.
     */
    private long hashesPerSecond;

    /**
     * Constructor - This BlockChain has exactly three instance members:
     * an ArrayList to hold Blocks and a chain hash to hold a SHA256 hash of the most recently added Block.
     */
    public BlockChain() {
        blocks = new ArrayList<>();
        chainHash = "";
        hashesPerSecond = 0;
    }

    /**
     * A new Block is being added to the BlockChain.
     * This new block's previous hash must hold the hash of the most recently added block.
     * After this call on addBlock, the new block becomes the most recently added block on the BlockChain.
     *
     * @param newBlock - is added to the BlockChain as the most recent block
     * @throws NoSuchAlgorithmException
     */
    public void addBlock(Block newBlock) throws NoSuchAlgorithmException {
        newBlock.setPreviousHash(chainHash);
        newBlock.proofOfWork();
        blocks.add(newBlock);
        chainHash = newBlock.calculateHash();
    }

    /**
     * This method computes exactly 2 million hashes and times how long that process takes.
     * So, hashes per second is approximated as (2 million / number of seconds).
     * It is run on start up and sets the instance variable hashesPerSecond.
     * It uses a simple string - "00000000" to hash.
     */
    public void computeHashesPerSecond() throws NoSuchAlgorithmException {
        long start = System.currentTimeMillis();
        int i = 0;
        while (i < 2000000) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update("00000000".getBytes());
            bytesToHex(md.digest());
            i++;
        }
        long end = System.currentTimeMillis();
        hashesPerSecond = (long) ((long) 2000000 / ((end - start) / 1000.0));
    }

    // Reference from : https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
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
     * @param i
     * @return block at postion i
     */
    public Block getBlock(int i) {
        return blocks.get(i);
    }

    public String getChainHash() {
        return chainHash;
    }

    /**
     * @return the size of the chain in blocks.
     */
    public int getChainSize() {
        return blocks.size();
    }

    /**
     * @return the instance variable approximating the number of hashes per second.
     */
    public int getHashesPerSecond() {
        return (int) hashesPerSecond;
    }

    /**
     * @return a reference to the most recently added Block.
     */
    public Block getLatestBlock() {
        return blocks.get(blocks.size() - 1);
    }

    /**
     * @return the current system time.
     */
    public Timestamp getTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * Compute and return the total difficulty of all blocks on the chain. Each block knows its own difficulty.
     *
     * @return total difficulty
     */
    public int getTotalDifficulty() {
        int res = 0;
        for (Block b : blocks) {
            res += b.getDifficulty();
        }
        return res;
    }

    /**
     * Compute and return the expected number of hashes required for the entire chain.
     *
     * @return getTotalExpectedHashes
     */
    public double getTotalExpectedHashes() {
        double res = 0;
        for (Block b : blocks) {
            int blockPerHash = 1;
            int times = b.getDifficulty();
            for (int i = 0; i < times; i++) {
                blockPerHash *= 16;
            }
            res += blockPerHash;
        }
        return res;
    }

    /**
     * If the chain only contains one block, the genesis block at position 0,
     * this routine computes the hash of the block and checks that the hash has the requisite number of
     * leftmost 0's (proof of work) as specified in the difficulty field.
     * It also checks that the chain hash is equal to this computed hash.
     * If either check fails, return an error message. Otherwise, return the string "TRUE".
     * <p>
     * If the chain has more blocks than one, begin checking from block one.
     * Continue checking until you have validated the entire chain.
     * The first check will involve a computation of a hash in Block 0 and a comparison with the hash pointer in Block 1.
     * If they match and if the proof of work is correct, go and visit the next block in the chain.
     * At the end, check that the chain hash is also correct.
     *
     * @return "TRUE" if the chain is valid, otherwise return a string with an appropriate error message
     * @throws NoSuchAlgorithmException
     */
    public String isChainValid() throws NoSuchAlgorithmException {
        if (blocks.size() == 1) { //if the chain only contains one block
            String hash = blocks.get(0).calculateHash();
            int hashZeros = 0;
            int index = 0;
            while (hash.charAt(index) == '0') {
                hashZeros++;
                index++;
            }
            if (!hash.equals(chainHash) && hashZeros != blocks.get(0).getDifficulty()) {
                return "FALSE! Improper hash on node 0. Does not begin with " + "0".repeat(blocks.get(0).getDifficulty());
            }
        } else { //if the chain contains more then one block
            for (int i = 0; i < blocks.size(); i++) {
                String hash = blocks.get(i).calculateHash();
                int hashZeros = 0;
                int index = 0;
                while (hash.charAt(index) == '0') {
                    hashZeros++;
                    index++;
                }
                if (i != blocks.size() - 1 &&
                        !hash.equals(blocks.get(i + 1).getPreviousHash()) && hashZeros != blocks.get(i).getDifficulty()) {
                    return "FALSE! Improper hash on node " + i + ". " +
                            "Does not begin with " + "0".repeat(blocks.get(i).getDifficulty());
                }
                if (i == blocks.size() - 1 && // for the last block of the chain
                        !hash.equals(chainHash) && hashZeros != blocks.get(i).getDifficulty()) {
                    return "FALSE! Improper hash on node " + i + ". " +
                            "Does not begin with " + "0".repeat(blocks.get(i).getDifficulty());
                }
            }
        }
        return "TRUE";
    }

    /**
     * This routine repairs the chain.
     * It checks the hashes of each block and ensures that any illegal hashes are recomputed.
     * After this routine is run, the chain will be valid. The routine does not modify any difficulty values.
     * It computes new proof of work based on the difficulty specified in the Block.
     *
     * @throws NoSuchAlgorithmException
     */
    public void repairChain() throws NoSuchAlgorithmException {
        if (blocks.size() == 1) { //if the chain only contains one block
            String hash = blocks.get(0).calculateHash();
            int hashZeros = 0;
            int index = 0;
            while (hash.charAt(index) == '0') {
                hashZeros++;
                index++;
            }
            if (!hash.equals(chainHash) && hashZeros != blocks.get(0).getDifficulty()) {
                blocks.get(0).proofOfWork(); //need to compute proofOfWork again
            }
        } else { //if the chain contains more then one block
            for (int i = 0; i < blocks.size(); i++) {
                String hash = blocks.get(i).calculateHash();
                int hashZeros = 0;
                int index = 0;
                while (hash.charAt(index) == '0') {
                    hashZeros++;
                    index++;
                }
                if (i != blocks.size() - 1 &&
                        !hash.equals(blocks.get(i + 1).getPreviousHash()) && hashZeros != blocks.get(i).getDifficulty()) {
                    String newHash = blocks.get(i).proofOfWork();
                    blocks.get(i + 1).setPreviousHash(newHash); //reset the hashes
                    blocks.get(i + 1).setTimestamp(getTime()); //reset the time stamp
                }
                if (i == blocks.size() - 1 &&
                        !hash.equals(chainHash) && hashZeros != blocks.get(i).getDifficulty()) {
                    blocks.get(i).proofOfWork();
                    chainHash = blocks.get(i).calculateHash();
                }
            }
        }


    }

    @Override
    public String toString() { //out put to a json format
        StringBuilder sb = new StringBuilder();
        sb.append("{\"ds_chain\" : [ ");
        for (int i = 0; i < blocks.size(); i++) {
            if (i != blocks.size() - 1) {
                sb.append(blocks.get(i)).append(",");
            } else {
                sb.append(blocks.get(i));
            }
        }
        sb.append(" ], \"chainHash\":\"").append(chainHash).append("\"}");
        return sb.toString();
    }

    public static void main(String[] args){

    }


}
