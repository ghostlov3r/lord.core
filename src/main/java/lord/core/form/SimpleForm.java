package lord.core.form;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Setter;
import lombok.experimental.Accessors;
import lord.core.LordCore;
import lord.core.form.element.ElementButton;
import lord.core.form.element.NoClickButton;
import lord.core.form.response.SimpleFormResponse;
import lord.core.gamer.Gamer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Accessors(fluent = true)
public abstract class SimpleForm extends Form<SimpleFormResponse> {
    
    private final static String TYPE = "form";

    @Setter
    private String content = "";
    
    private List<ElementButton> buttons = new ArrayList<>();

    public SimpleForm (String title) {
        super(TYPE);
        title(title);
    }
    
    /**
     * @param num Порядковый номер
     * @return Кпокпа из формы
     */
    public ElementButton getButton (int num) {
        return buttons.size() >= num ? buttons.get(num - 1) : null;
    }

    @Override
    public void handleResponse(Gamer gamer, JsonNode node) {
        if (!node.isInt()) {
            onError(gamer);
            LordCore.log.error("Received invalid response for SimpleForm");
            return;
        }

        int clicked = node.intValue();

        /*Consumer<Player> buttonListener = buttonListeners.get(clicked);
        if (buttonListener != null) {
            buttonListener.accept(gamer);
        }*/ // NOT IMPLEMENTED

        submit(gamer, new SimpleFormResponse(clicked+1, buttons.get(clicked)));
    }
    
    public void button (ElementButton button) {
        this.buttons.add(button);
    }
    
    public void button (String text) {
        this.buttons.add(new NoClickButton(text));
    }
    
    public void button (String text, String imgType, String imgData) {
        this.buttons.add(new NoClickButton(text, imgType, imgData));
    }
    
    public void buttons (Collection<ElementButton> buttons) {
        this.buttons.addAll(buttons);
    }
    
}
