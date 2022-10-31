//Name: Yu Chen
//Andrew Id: yuc3

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * This is the Project 3 Task 1 for 95-702.
 *
 * @author Jennifer Chen (yuc3@andrew.cmu.edu)
 */
public class ClientTCP {
    private static int serverPort;

    //some of this part I reused my code in Project2 Task4
    public static String toServer(String str) {

        Socket clientSocket = null;
        String res = "";
        try {
            clientSocket = new Socket("localhost", serverPort);

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

            res = in.readLine();

        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }

        return res;

    }

    public static void main(String args[]) throws IOException {
        RequestMessage rm = new RequestMessage();
        Scanner input = new Scanner(System.in);
        System.out.println("The client is running."); //declare that the client is running
        System.out.println("Please input the server port number: ");
        serverPort = input.nextInt(); // get user input as serverPort
        System.out.println("Server port: " + serverPort);
        System.out.println("=============");

        //I use Jackson as the Java JSON library
        //reference : https://www.baeldung.com/jackson-object-mapper-tutorial
        //reference : http://tutorials.jenkov.com/java-json/jackson-objectmapper.html

        int choice;
        while (true) {
            long start;
            long end;
            System.out.println("0. View basic blockchain status.");
            System.out.println("1. Add a transaction to the blockchain.");
            System.out.println("2. Verify the blockchain.");
            System.out.println("3. View the blockchain.");
            System.out.println("4. Corrupt the chain.");
            System.out.println("5. Hide the corruption by repairing the chain.");
            System.out.println("6. Exit");
            choice = input.nextInt();
            StringBuilder sb = new StringBuilder();
            String request = "";
            String response = "";
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;

            switch (choice) {
                case 0:
                    request = rm.toJsonFormat(0, "");
                    response = toServer(request);
                    root = mapper.readTree(response);

                    System.out.println("Current size of chain:" + root.get("size"));
                    System.out.println("Difficulty of most recent block: " + root.get("diff"));
                    System.out.println("Total difficulty for all blocks: " + root.get("totalDiff"));
                    System.out.println("Approximate hashes per second on this machine: " + root.get("hps"));
                    System.out.println("Expected total hashes required for the whole chain: " + root.get("totalHashes"));
                    System.out.println("Nonce for most recent block: " + root.get("recentNonce"));
                    System.out.println("Chain hash: " + root.get("chainHash").textValue());
                    System.out.println();
                    //System.out.println(response);
                    break;

                case 1:
                    input.nextLine();
                    System.out.println("Enter difficulty > 0");
                    int diff = input.nextInt();
                    System.out.println("Enter transaction: ");
                    input.nextLine();
                    String tx = input.nextLine();
                    sb.append(diff).append("#").append(tx);
                    request = rm.toJsonFormat(1, sb.toString());
                    //System.out.println(request);
                    response = toServer(request);
                    root = mapper.readTree(response);
                    System.out.println(root.get("response").textValue());
                    System.out.println();

                    break;

                case 2:
                    request = rm.toJsonFormat(2, "");
                    response = toServer(request);
                    root = mapper.readTree(response);
                    System.out.println("Chain verification: " + root.get("valid").textValue());
                    System.out.println(root.get("response").textValue());
                    System.out.println();
                    break;

                case 3:
                    request = rm.toJsonFormat(3, "");
                    response = toServer(request);
                    System.out.println("View the Blockchain: ");
                    //for better read output
                    root = mapper.readTree(response);
                    String prettyFormat = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
                    System.out.println(prettyFormat);
                    System.out.println();
                    break;

                case 4:
                    System.out.println("corrupt the Blockchain!");
                    System.out.println("Enter block ID of block to corrupt:");
                    int index = input.nextInt();
                    System.out.println("Enter new data for block " + index);
                    input.nextLine();
                    String newData = input.nextLine();
                    sb.append(index).append("#").append(newData);
                    request = rm.toJsonFormat(4, sb.toString());
                    response = toServer(request);
                    root = mapper.readTree(response);
                    System.out.println(root.get("response").textValue());
                    System.out.println();

                    break;

                case 5:
                    request = rm.toJsonFormat(5, "");
                    response = toServer(request);
                    root = mapper.readTree(response);
                    System.out.println(root.get("response").textValue());

                    System.out.println();
                    break;

                case 6:
                    System.out.println("Bye Bye!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Enter only 0-6 please!");
                    break;
            }
        }
    }


}
