package lord.core.form;

import com.fasterxml.jackson.databind.JsonNode;
import lord.core.LordCore;
import lord.core.util.form.element.*;
import lord.core.util.form.util.ImageData;
import lord.core.form.response.CustomFormResponse;
import lord.core.gamer.Gamer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class CustomForm extends Form<CustomFormResponse> {
    
    private final static String TYPE = "custom_form";
    
    private List<Element> content;

    private ImageData icon;
    
    public CustomForm (String title) {
        super(TYPE);
        title(title);
    }

    /**
     * @param num Порядковый номер элемента
     * @return Элемент под указанным номером
     */
    public Element getElement (int num) {
        return content.size() >= num ? content.get(num - 1) : null;
    }

    @Override
    public void handleResponse (Gamer gamer, JsonNode node) {
        if (!node.isArray()) {
            onError(gamer);
            LordCore.log.error("Received invalid response for CustomForm");
            return;
        }

        submit(gamer, new CustomFormResponse(this, node));
    }
    
    /**
     * Выпадающий список
     * @param options Элементы списка
     */
    public CustomForm dropdown (String text, String... options) {
        return dropdown(text, Arrays.asList(options));
    }
    
    /**
     * Выпадающий список
     * @param options Элементы списка
     */
    public CustomForm dropdown (String text, List<String> options) {
        return element(new ElementDropdown(text, options));
    }
    
    /**
     * Выпадающий список
     * @param options Элементы списка
     * @param defaultOption Выбранный по умолчанию элемент
     */
    public CustomForm dropdown (String text, int defaultOption, String... options) {
        return dropdown(text, defaultOption, Arrays.asList(options));
    }
    
    /**
     * Выпадающий список
     * @param options Элементы списка
     * @param defaultOption Выбранный по умолчанию элемент
     */
    public CustomForm dropdown (String text, int defaultOption, List<String> options) {
        return element(new ElementDropdown(text, options, defaultOption));
    }
    
    public CustomForm input (String text) {
        return element(new ElementInput(text));
    }
    
    public CustomForm input (String text, String placeholder) {
        return element(new ElementInput(text, placeholder));
    }
    
    public CustomForm input (String text, String placeholder, String defaultText) {
        return element(new ElementInput(text, placeholder, defaultText));
    }
    
    public CustomForm label (String text) {
        return element(new ElementLabel(text));
    }
    
    public CustomForm slider (String elementText) {
        return element(new ElementSlider(elementText));
    }
    
    public CustomForm slider (String elementText, float minimum, float maximum) {
        return element(new ElementSlider(elementText, minimum, maximum));
    }
    
    public CustomForm slider (String elementText, float minimum, float maximum, int stepCount) {
        return element(new ElementSlider(elementText, minimum, maximum, stepCount));
    }
    
    public CustomForm slider (String elementText, float minimum, float maximum, int stepCount, float defaultValue) {
        return element(new ElementSlider(elementText, minimum, maximum, stepCount, defaultValue));
    }
    
    public CustomForm stepSlider (String elementText) {
        return element(new ElementStepSlider(elementText));
    }
    
    public CustomForm stepSlider (String elementText, String... stepOptions) {
        return stepSlider(elementText, Arrays.asList(stepOptions));
    }
    
    public CustomForm stepSlider (String elementText, List<String> stepOptions) {
        return element(new ElementStepSlider(elementText, stepOptions));
    }
    
    public CustomForm stepSlider (String elementText, int defaultStepIndex, String... stepOptions) {
        return stepSlider(elementText, defaultStepIndex, Arrays.asList(stepOptions));
    }
    
    public CustomForm stepSlider (String elementText, int defaultStepIndex, List<String> stepOptions) {
        return element(new ElementStepSlider(elementText, stepOptions, defaultStepIndex));
    }
    
    public CustomForm toggle (String text) {
        return element(new ElementToggle(text));
    }
    
    /**
     * Переключатель
     * @param isEnabled Значение по умолчанию
     */
    public CustomForm toggle (String text, boolean isEnabled) {
        return element(new ElementToggle(text, isEnabled));
    }
    
    public CustomForm element (Element element) {
        content.add(element);
        return this;
    }
    
    public CustomForm elements (Element element, Element... elements) {
        this.content.add(element);
        this.content.addAll(Arrays.asList(elements));
        return this;
    }
    
    public CustomForm elements (Collection<Element> elements) {
        this.content.addAll(elements);
        return this;
    }
    
    /**
     * Set an icon of the form
     * The icon is visible only in case of server settings form
     *
     * @param type ImgType тип иконки
     * @param data Путь к изображению
     */
    public CustomForm icon (String type, String data) {
        this.icon = new ImageData(type, data);
        return this;
    }
    
}
