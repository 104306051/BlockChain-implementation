//Name: Yu Chen
//Andrew Id: yuc3

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * This is the Project 3 Task 1 for 95-702.
 *
 * @author Jennifer Chen (yuc3@andrew.cmu.edu)
 */
public class ResponseMessage {
    /**
     * I use Jackson as the Java JSON library
     * reference 1 : https://www.baeldung.com/jackson-object-mapper-tutorial
     * reference 2 : http://tutorials.jenkov.com/java-json/jackson-objectmapper.html
     *
     * @param selection
     * @param data
     * @return json format response message to send back to client
     * @throws JsonProcessingException
     */
    public String toJsonFormat(int selection, String data) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode outerObject = mapper.createObjectNode();
        outerObject.putPOJO("selection", selection);
        String[] strs = data.split("#");

        switch (selection) {
            case 0:
                String chainHash = mapper.writeValueAsString(strs[6]);
                outerObject.putPOJO("size", strs[0]);
                outerObject.putPOJO("chainHash", chainHash);
                outerObject.putPOJO("totalHashes", strs[4]);
                outerObject.putPOJO("totalDiff", strs[2]);
                outerObject.putPOJO("recentNonce", strs[5]);
                outerObject.putPOJO("diff", strs[1]);
                outerObject.putPOJO("hps", strs[3]);
                break;
            case 1: //for case 1,4,5 the json field will all be selection and response
            case 4:
            case 5:
                String tx = mapper.writeValueAsString(strs[0]);
                outerObject.putPOJO("response", tx);
                break;
            case 2:
                String time = mapper.writeValueAsString(strs[0]);
                String valid = mapper.writeValueAsString(strs[1]);
                outerObject.putPOJO("valid", valid);
                outerObject.putPOJO("response", time);
                break;
            case 3: //since the BlockChain class's toString method has already format to json string for us,
                // we could simply do nothing here
                break;

        }

        return outerObject.toString();
    }
}
