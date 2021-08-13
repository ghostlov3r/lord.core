package lord.core.util.form.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class ElementStepSlider extends Element {
    
    private final static String TYPE = "step_slider";
    
    private final List<String> steps;
    
    @JsonProperty("default")
    private int defaultStepIndex = 0;

    public ElementStepSlider (String text) {
        super(TYPE, text);
        steps = new ArrayList<>();
    }

    public ElementStepSlider (String text, List<String> steps) {
        super(TYPE, text);

        this.steps = steps;
    }

    public ElementStepSlider (String text, List<String> steps, int defaultStepIndex) {
        super(TYPE, text);
        
        this.steps = steps;
        this.defaultStepIndex = defaultStepIndex;
    }

    public String getStep(int index) {
        return steps.get(index);
    }
    
    @Getter
    @RequiredArgsConstructor
    @ToString
    public static class Response {

        private final int stepIndex;
        private final String option;
        
    }
}
