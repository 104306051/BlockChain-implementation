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
public class RequestMessage {

    /**
     * I use Jackson as the Java JSON library
     * reference 1 : https://www.baeldung.com/jackson-object-mapper-tutorial
     * reference 2 : http://tutorials.jenkov.com/java-json/jackson-objectmapper.html
     *
     * @param selection
     * @param data
     * @return json format request message to send to server
     * @throws JsonProcessingException
     */
    public String toJsonFormat(int selection, String data) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode outerObject = mapper.createObjectNode();
        outerObject.putPOJO("selection", selection);
        String[] strs = data.split("#");

        switch (selection) {
            case 0:
            case 2:
            case 3:
            case 5:
                break; //if the user choice is 0 or 2 or 3 or 5, just send the selection field to server
            case 1:
                String tx = mapper.writeValueAsString(strs[1]);
                outerObject.putPOJO("difficulty", Integer.parseInt(strs[0]));
                outerObject.putPOJO("Tx", tx);
                break;
            case 4:
                String newTx = mapper.writeValueAsString(strs[1]);
                outerObject.putPOJO("index", Integer.parseInt(strs[0]));
                outerObject.putPOJO("newData", newTx);
                break;

        }

        return outerObject.toString();
    }
}
