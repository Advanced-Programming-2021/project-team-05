package utils;

public interface PromptListener {

    void onOk(String input);

    @SuppressWarnings("EmptyMethod")
    void onCancel();
}
