// PROG2 VT2022, Inlämningsuppgift, del 2
// Grupp 064
// abal 7627
// vela 1859
// guhe 8938


import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class Dialog extends Alert {

    private TextField nameField = new TextField();
    private TextField timeField = new TextField();

    public Dialog() {
        super(AlertType.CONFIRMATION);
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("Name: "), nameField);
        grid.addRow(1, new Label("Time: "), timeField);
        getDialogPane().setContent(grid);
    }

    public Dialog(TextField nameField, TextField timeField) {
        super(AlertType.CONFIRMATION);
        this.nameField = nameField;
        this.timeField = timeField;
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("Name: "), nameField);
        grid.addRow(1, new Label("Time: "), timeField);
        getDialogPane().setContent(grid);
    }

    public Dialog(TextArea textArea) {
        super(AlertType.INFORMATION);
        GridPane grid = new GridPane();
        grid.addRow(0, textArea);
        getDialogPane().setContent(grid);
    }

    public String getNameField() {
        return nameField.getText();
    }

    public int getTimeField() {
        return Integer.parseInt(timeField.getText());
    }

}