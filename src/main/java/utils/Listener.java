package utils;

public interface Listener {

    void onConfirm();

    @SuppressWarnings("EmptyMethod")
    void onCancel();
}
