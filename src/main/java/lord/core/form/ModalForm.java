package lord.core.form;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lord.core.LordCore;
import lord.core.gamer.Gamer;

@Accessors(fluent = true) @Setter @Getter
public abstract class ModalForm extends Form<Boolean> {
    
    private final static String TYPE = "modal";
    
    private String button1;
    private String button2;
    private String content;

    public ModalForm(String title) {
        super(TYPE);
        title(title);
    }

    @Override
    public void handleResponse(Gamer gamer, JsonNode node) {
        if (!node.isBoolean()) {
            onError(gamer);
            LordCore.log.error("Received invalid response for ModalForm");
            return;
        }

        submit(gamer, node.booleanValue());
    }
    
}
