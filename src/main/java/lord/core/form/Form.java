package lord.core.form;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Setter;
import lombok.experimental.Accessors;
import lord.core.gamer.Gamer;

@Accessors(fluent = true)
public abstract class Form<Resp> {

    /** Тип формы */
    private final String type;
    
    /** Заголовок */
    @Setter
    private String title = "";
    
    public Form (String type) {
        this.type = type;
    }
    
    /**
     * Для реализации в типе формы
     */
    public abstract void handleResponse (Gamer p, JsonNode node);
    
    /**
     * Для реализации в конечной форме
     * При отправке игроком
     */
    public abstract void onSubmit (Gamer gamer, Resp resp);
    
    public void onClose (Gamer gamer) {
        // aka abstract
    }
    
    public void onError (Gamer gamer) {
        // aka abstract
    }

    protected void submit (Gamer player, Resp response) {
        if (response == null) {
            onClose(player);
            return;
        }
        onSubmit(player, response);
    }
    
    public void send (Gamer gamer) {
        gamer.sendForm(this);
    }
    
}
