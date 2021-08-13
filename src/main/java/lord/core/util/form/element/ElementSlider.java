package lord.core.util.form.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public final class ElementSlider extends Element {
    
    private final static String TYPE = "slider";
    
    private final float min;
    private final float max;
    private final int step;
    @JsonProperty("default")
    private final float defaultValue;

    public ElementSlider (String text) {
        super(TYPE, text);
        
        this.min = 0f;
        this.max = 100f;
        this.step = 1;
        this.defaultValue = 0f;
    }
    
    public ElementSlider (String text, float min, float max) {
        super(TYPE, text);
        
        if (min >= max) {
            throw new IllegalArgumentException("Maximal value can't be smaller or equal to the minimal value");
        }
        this.min = min;
        this.max = max;
        this.step = 1;
        this.defaultValue = min;
    }

    public ElementSlider(String text, float min, float max, int step) {
        super(TYPE, text);
        
        if (min >= max) {
            throw new IllegalArgumentException("Maximal value can't be smaller or equal to the minimal value");
        }
        this.min = min;
        this.max = max;
        this.step = step;
        this.defaultValue = min;
    }

    public ElementSlider(String text, float min, float max, int step, float defaultValue) {
        super(TYPE, text);
        
        if (min >= max) {
            throw new IllegalArgumentException("Maximal value can't be smaller or equal to the minimal value");
        }
        this.min = min;
        this.max = max;
        this.step = step;
        this.defaultValue = defaultValue;
    }
}
