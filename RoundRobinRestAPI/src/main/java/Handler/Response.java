package Handler;


import java.util.ArrayList;


/**
 * The type Response.
 */
public class Response {
    private ArrayList<String> ids;
    private String message;

    /**
     * Gets ids.
     *
     * @return the ids
     */
    public ArrayList<String> getIds() {
        return ids;
    }

    /**
     * Sets ids.
     *
     * @param ids the ids
     */
    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}