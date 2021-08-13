package lord.core.util.form;

import com.fasterxml.jackson.databind.JsonNode;
import lord.core.LordCore;
import lord.core.gamer.Gamer;

public class ModalFormV1 extends FormV1<Boolean, ModalFormV1> {
    
    private final static String TYPE = "modal";
    
    private String button1;
    private String button2;
    private String content;

    public ModalFormV1 () {
        super(TYPE);
    }

    @Override
    public void handleResponse(Gamer gamer, JsonNode node) {
        if (!node.isBoolean()) {
            error(gamer);
            LordCore.log.error("Received invalid response for ModalForm");
            return;
        }

        submit(gamer, node.booleanValue());
    }
    
    @Override
    protected ModalFormV1 self () {
        return null;
    }
    
    /**
     * Set the form text content
     *
     * @param content form text content
     * @return self builder instance
     */
    public ModalFormV1 content(String content) {
        this.content = content;
        return this;
    }
    
    /**
     * Set a displayed value for true boolean value
     *
     * @param text string value for {@link Boolean#TRUE}
     * @return self builder instance
     */
    public ModalFormV1 button1(String text) {
        this.button1 = text;
        return this;
    }
    
    /**
     * Set a displayed value for false boolean value
     *
     * @param text string value for {@link Boolean#FALSE}
     * @return self builder instance
     */
    public ModalFormV1 button2(String text) {
        this.button2 = text;
        return this;
    }
    
}
