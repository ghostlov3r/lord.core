package lord.core.util.form.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Строка ввода текста для формы
 */
@Getter
public final class ElementInput extends Element {
    
    private final static String TYPE = "input";
    
    private String placeholder;
    
    @JsonProperty("default")
    private String defaultText;

    public ElementInput (String text) {
        super(TYPE, text);
    }

    public ElementInput (String text, String placeholder) {
        super(TYPE, text);
        this.placeholder = placeholder;
    }
   
    public ElementInput (String text, String placeholder, String defaultText) {
        super(TYPE, text);
        this.placeholder = placeholder;
        this.defaultText = defaultText;
    }
}
