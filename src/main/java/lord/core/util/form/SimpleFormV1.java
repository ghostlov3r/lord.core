package lord.core.util.form;

import cn.nukkit.Player;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import lord.core.LordCore;
import lord.core.util.form.element.ElementButtonV1;
import lord.core.util.form.response.SimpleFormResponseV1;
import lord.core.gamer.Gamer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class SimpleFormV1 extends FormV1<SimpleFormResponseV1, SimpleFormV1> {
    
    private final static String TYPE = "form";

    private String content = "";
    private List<ElementButtonV1> buttons = new ArrayList<>();

    public SimpleFormV1 () {
        super(TYPE);
    }

    public ElementButtonV1 getButton (int index) {
        return buttons.size() > index ? buttons.get(index) : null;
    }

    @Override
    public void handleResponse(Gamer gamer, JsonNode node) {
        if (!node.isInt()) {
            error(gamer);
            LordCore.log.error("Received invalid response for SimpleForm");
            return;
        }

        int clicked = node.intValue();

        /*Consumer<Player> buttonListener = buttonListeners.get(clicked);
        if (buttonListener != null) {
            buttonListener.accept(gamer);
        }*/ // NOT IMPLEMENTED

        submit(gamer, new SimpleFormResponseV1(clicked, getButton(clicked)));
    }
    
    /**
     * Set the form text content
     *
     * @param content form text content
     * @return self builder instance
     */
    public SimpleFormV1 content(String content) {
        Preconditions.checkNotNull(content, "content must not be null");
        this.content = content;
        return this;
    }
    
    /**
     * Add a button
     *
     * @param text button title
     * @return self builder instance
     */
    public SimpleFormV1 button(String text) {
        this.buttons.add(new ElementButtonV1(text));
        return this;
    }
    
    /**
     * Add a button with image
     *
     * @param text      button title
     * @param imgType button image type
     * @param imgData button image data
     * @return self builder instance
     */
    public SimpleFormV1 button(String text, String imgType, String imgData) {
        this.buttons.add(new ElementButtonV1(text, imgType, imgData));
        return this;
    }
    
    /**
     * Adda button with on click callback
     *
     * @param text   button title
     * @param action callback called when the button is clicked
     * @return self builder instance
     */
    public SimpleFormV1 button(String text, Consumer<Player> action) {
        this.buttons.add(new ElementButtonV1(text));
        // this.buttonListeners.put(buttons.size() - 1, action);
        return this;
    }
    
    /**
     * Add a button with image and on click callback
     *
     * @param text      button title
     * @param imgType button image type
     * @param imgData button image data
     * @param action    callback called when the button is clicked
     * @return self builder instance
     */
    public SimpleFormV1 button(String text, String imgType, String imgData, Consumer<Player> action) {
        this.buttons.add(new ElementButtonV1(text, imgType, imgData));
        // this.buttonListeners.put(buttons.size() - 1, action);
        return this;
    }
    
    /**
     * Add one or more buttons
     *
     * @param button  button element
     * @param buttons list of button elements
     * @return self builder instance
     */
    public SimpleFormV1 buttons(ElementButtonV1 button, ElementButtonV1... buttons) {
        this.buttons.add(button);
        this.buttons.addAll(Arrays.asList(buttons));
        return this;
    }
    
    /**
     * Add list of buttons
     *
     * @param buttons list of button elements
     * @return self builder instance
     */
    public SimpleFormV1 buttons(Collection<ElementButtonV1> buttons) {
        this.buttons.addAll(buttons);
        return this;
    }
    
    @Override
    protected SimpleFormV1 self () {
        return this;
    }
    
}
