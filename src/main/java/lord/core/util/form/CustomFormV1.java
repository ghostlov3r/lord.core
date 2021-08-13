package lord.core.util.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lord.core.LordCore;
import lord.core.util.form.element.*;
import lord.core.util.form.response.CustomFormResponseV1;
import lord.core.util.form.util.ImageData;
import lord.core.gamer.Gamer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CustomFormV1 extends FormV1<CustomFormResponseV1, CustomFormV1> {
    
    private final static String TYPE = "custom_form";
    
    @JsonProperty("content")
    private List<Element> elements;

    private ImageData icon;

    public CustomFormV1 () {
        super(TYPE);
    }

    /**
     * @param num Порядковый номер элемента
     * @return Элемент под указанным номером
     * @throws IndexOutOfBoundsException if the num is out of range
     */
    public Element getElement (int num) {
        return elements.get(num - 1);
    }
    
    @Override
    protected CustomFormV1 self () {
        return this;
    }

    @Override
    public void handleResponse (Gamer gamer, JsonNode node) {
        if (!node.isArray()) {
            error(gamer);
            LordCore.log.error("Received invalid response for CustomForm");
            return;
        }

        submit(gamer, new CustomFormResponseV1(this, node));
    }
    
    /**
     * Выпадающий список
     * @param options Элементы списка
     */
    public CustomFormV1 dropdown (String text, String... options) {
        return dropdown(text, Arrays.asList(options));
    }
    
    /**
     * Выпадающий список
     * @param options Элементы списка
     */
    public CustomFormV1 dropdown (String text, List<String> options) {
        return element(new ElementDropdown(text, options));
    }
    
    /**
     * Выпадающий список
     * @param options Элементы списка
     * @param defaultOption Выбранный по умолчанию элемент
     */
    public CustomFormV1 dropdown (String text, int defaultOption, String... options) {
        return dropdown(text, defaultOption, Arrays.asList(options));
    }
    
    /**
     * Выпадающий список
     * @param options Элементы списка
     * @param defaultOption Выбранный по умолчанию элемент
     */
    public CustomFormV1 dropdown (String text, int defaultOption, List<String> options) {
        return element(new ElementDropdown(text, options, defaultOption));
    }
    
    public CustomFormV1 input (String text) {
        return element(new ElementInput(text));
    }
    
    public CustomFormV1 input (String text, String placeholder) {
        return element(new ElementInput(text, placeholder));
    }
    
    public CustomFormV1 input (String text, String placeholder, String defaultText) {
        return element(new ElementInput(text, placeholder, defaultText));
    }
    
    public CustomFormV1 label (String text) {
        return element(new ElementLabel(text));
    }
    
    public CustomFormV1 slider (String elementText) {
        return element(new ElementSlider(elementText));
    }
    
    public CustomFormV1 slider (String elementText, float minimum, float maximum) {
        return element(new ElementSlider(elementText, minimum, maximum));
    }
    
    public CustomFormV1 slider (String elementText, float minimum, float maximum, int stepCount) {
        return element(new ElementSlider(elementText, minimum, maximum, stepCount));
    }
    
    public CustomFormV1 slider (String elementText, float minimum, float maximum, int stepCount, float defaultValue) {
        return element(new ElementSlider(elementText, minimum, maximum, stepCount, defaultValue));
    }
    
    public CustomFormV1 stepSlider (String elementText) {
        return element(new ElementStepSlider(elementText));
    }
    
    public CustomFormV1 stepSlider (String elementText, String... stepOptions) {
        return stepSlider(elementText, Arrays.asList(stepOptions));
    }
    
    public CustomFormV1 stepSlider (String elementText, List<String> stepOptions) {
        return element(new ElementStepSlider(elementText, stepOptions));
    }
    
    public CustomFormV1 stepSlider (String elementText, int defaultStepIndex, String... stepOptions) {
        return stepSlider(elementText, defaultStepIndex, Arrays.asList(stepOptions));
    }
    
    public CustomFormV1 stepSlider (String elementText, int defaultStepIndex, List<String> stepOptions) {
        return element(new ElementStepSlider(elementText, stepOptions, defaultStepIndex));
    }
    
    public CustomFormV1 toggle (String text) {
        return element(new ElementToggle(text));
    }
    
    /**
     * Переключатель
     * @param isEnabled Значение по умолчанию
     */
    public CustomFormV1 toggle (String text, boolean isEnabled) {
        return element(new ElementToggle(text, isEnabled));
    }
    
    public CustomFormV1 element (Element element) {
        elements.add(element);
        return this;
    }
    
    public CustomFormV1 elements (Element element, Element... elements) {
        this.elements.add(element);
        this.elements.addAll(Arrays.asList(elements));
        return this;
    }
    
    public CustomFormV1 elements (Collection<Element> elements) {
        this.elements.addAll(elements);
        return this;
    }
    
    /**
     * Set an icon of the form
     * The icon is visible only in case of server settings form
     *
     * @param type ImgType тип иконки
     * @param data Путь к изображению
     */
    public CustomFormV1 icon (String type, String data) {
        this.icon = new ImageData(type, data);
        return this;
    }
    
}
