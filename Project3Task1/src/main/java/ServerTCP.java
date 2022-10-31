//Name: Yu Chen
//Andrew Id: yuc3

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * This is the Project 3 Task 1 for 95-702.
 *
 * @author Jennifer Chen (yuc3@andrew.cmu.edu)
 */
public class ServerTCP {
    //some of this part I reused my code in Project2 Task4
    //I use Jackson as the Java JSON library
    //reference : https://www.baeldung.com/jackson-object-mapper-tutorial
    //reference : http://tutorials.jenkov.com/java-json/jackson-objectmapper.html

    public static void main(String args[]) throws NoSuchAlgorithmException {
        BlockChain bc = new BlockChain();
        Block genesis = new Block(0, bc.getTime(), "Genesis", 2);
        bc.addBlock(genesis);
        bc.computeHashesPerSecond();

        System.out.println("The server is running.");
        Scanner input = new Scanner(System.in);
        System.out.println("Please input the port number to listen on: ");
        int serverPort = input.nextInt();
        System.out.println("Now listen on port: " + serverPort);
        System.out.println("============");

        System.out.println("Blockchain server running");

        Socket clientSocket = null;
        try {
            // Create a new server socket
            ServerSocket listenSocket = new ServerSocket(serverPort);

            /*
             * Block waiting for a new connection request from a client.
             * When the request is received, "accept" it, and the rest
             * the tcp protocol handshake will then take place, making
             * the socket ready for reading and writing.
             */
            while (true) {
                clientSocket = listenSocket.accept();
                // If we get here, then we are now connected to a client.

                // Set up "in" to read from the client socket
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Set up "out" to write to the client socket
                PrintWriter out;
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

                String data = in.readLine();

                ResponseMessage rm = new ResponseMessage();
                ObjectMapper mapper = new ObjectMapper();
                if (data != null) {
                    JsonNode root = mapper.readTree(data);
                    int choice = root.get("selection").asInt();
                    StringBuilder sb = new StringBuilder();
                    String res = "";
                    long start;
                    long end;

                    switch (choice) {
                        case 0:
                            sb.append(bc.getChainSize()).append("#").append(bc.getLatestBlock().getDifficulty()).append("#")
                                    .append(bc.getTotalDifficulty()).append("#").append(bc.getHashesPerSecond()).append("#")
                                    .append(bc.getTotalExpectedHashes()).append("#").append(bc.getLatestBlock().getNonce())
                                    .append("#").append(bc.getChainHash());
                            res = rm.toJsonFormat(0, sb.toString());
                            System.out.println("Response: " + res);
                            System.out.println();
                            break;

                        case 1:
                            System.out.println("Adding a block");
                            Block b = new Block(bc.getChainSize(), bc.getTime(), root.get("Tx").textValue(), root.get("difficulty").asInt());
                            start = System.currentTimeMillis();
                            bc.addBlock(b);
                            end = System.currentTimeMillis();
                            long time = end - start;
                            sb.append("Total execution time to add this block was ").append(time).append(" milliseconds");
                            System.out.println("Setting response to " + sb);
                            res = rm.toJsonFormat(1, sb.toString());
                            System.out.println("... " + res);
                            System.out.println();
                            break;

                        case 2:
                            System.out.println("Verifying entire chain");
                            start = System.currentTimeMillis();
                            String valid = bc.isChainValid();
                            end = System.currentTimeMillis();
                            sb.append("Total execution time to verify this block was ").append(end - start).append(" milliseconds");
                            System.out.println("Chain verification: " + valid);
                            System.out.println(sb);
                            System.out.println("Setting response to " + sb);
                            sb.append("#").append(valid);
                            res = rm.toJsonFormat(2, sb.toString());
                            //System.out.println("Response: " + res);
                            System.out.println();
                            break;
                        case 3:
                            System.out.println("View the Blockchain");
                            System.out.println("Setting response to " + bc);
                            res = bc.toString();
                            System.out.println();
                            break;
                        case 4:
                            System.out.println("Corrupt the Blockchain");
                            bc.getBlock(root.get("index").asInt()).setData(root.get("newData").textValue());
                            sb.append("Block ").append(root.get("index").asInt()).append(" now holds ").append(root.get("newData").textValue());
                            System.out.println(sb);
                            System.out.println("Setting response to " + sb);
                            res = rm.toJsonFormat(4, sb.toString());
                            System.out.println();
                            break;
                        case 5:
                            System.out.println("Repairing the entire chain");
                            start = System.currentTimeMillis();
                            bc.repairChain();
                            end = System.currentTimeMillis();
                            sb.append("Total execution time to repair this block was ").append(end - start).append(" milliseconds");
                            System.out.println("Setting response to " + sb);
                            res = rm.toJsonFormat(5, sb.toString());
                            System.out.println();
                            break;

                    }

                    out.println(res);
                    out.flush();
                }
            }

            // Handle exceptions
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());

            // If quitting (typically by you sending quit signal) clean up sockets
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
    }


}
