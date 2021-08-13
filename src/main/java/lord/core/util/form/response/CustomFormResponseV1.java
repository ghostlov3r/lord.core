package lord.core.util.form.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lord.core.util.form.CustomFormV1;
import lord.core.util.form.element.ElementDropdown;
import lord.core.util.form.element.ElementStepSlider;

@RequiredArgsConstructor
public class CustomFormResponseV1 {

    private final CustomFormV1 form;
    private final JsonNode responses;
    
    private JsonNode get (int num) {
        return responses.get(num - 1);
    }

    /**
     * @param num Порядковый номер
     * @return dropdown response object
     */
    public ElementDropdown.Response getDropdown (int num) {
        JsonNode node = get(num);
        
        if (!node.isInt()) {
            wrongValue(num, "dropdown");
        }

        int optionIndex = node.asInt();
        return new ElementDropdown.Response(optionIndex, ((ElementDropdown) form.getElement(num)).getOption(optionIndex));
    }

    /**
     * @param num Порядковый номер
     * @return step slider response object
     */
    public ElementStepSlider.Response getStepSlider (int num) {
        JsonNode node = get(num);
        
        if (!node.isInt()) {
            wrongValue(num, "step slider");
        }
    
        int stepIndex = node.asInt();
        return new ElementStepSlider.Response(stepIndex, ((ElementStepSlider) form.getElement(num)).getStep(stepIndex));
    }

    /**
     * @param num Порядковый номер
     * @return input response value
     */
    public String getInput (int num) {
        JsonNode node = get(num);
        
        if (!node.isTextual()) {
            wrongValue(num, "input");
        }

        return node.asText();
    }

    /**
     * @param num Порядковый номер
     * @return slider response value
     */
    public float getSlider (int num) {
        JsonNode node = get(num);
        
        if (!node.isDouble()) {
            wrongValue(num, "slider");
        }

        return (float) node.asDouble();
    }

    /**
     * @param num Порядковый номер
     * @return toggle response value
     */
    public boolean getToggle (int num) {
        JsonNode node = get(num);
        
        if (!node.isBoolean()) {
            wrongValue(num, "toggle");
        }

        return node.asBoolean();
    }

    private static void wrongValue(int index, String expected) {
        throw new IllegalStateException(String.format("Wrong element at index %d expected '%s'", index, expected));
    }
}
