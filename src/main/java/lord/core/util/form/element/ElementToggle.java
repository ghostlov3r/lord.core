package lord.core.util.form.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public final class ElementToggle extends Element {
    
    private final static String TYPE = "toggle";
    
    @JsonProperty("default")
    private boolean defaultValue;

    public ElementToggle(String text) {
        super(TYPE, text);
    }

    public ElementToggle(String text, boolean defaultValue) {
        super(TYPE, text);
        this.defaultValue = defaultValue;
    }
    
}
