package com.tasksystem;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;

public class TaskController {
    @FXML
    private ListView<Task> taskListView;
    @FXML
    private TextField descriptionField;
    @FXML
    private DatePicker dueDatePicker;
    @FXML
    private ComboBox<Task.Priority> priorityComboBox;
    @FXML
    private CheckBox completedCheckBox;

    private ObservableList<Task> tasks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        taskListView.setItems(tasks);
        priorityComboBox.setItems(FXCollections.observableArrayList(Task.Priority.values()));

        taskListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                completedCheckBox.setSelected(newVal.isCompleted());
            }
        });

        completedCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                selectedTask.setCompleted(newVal);
                taskListView.refresh();
            }
        });
    }

    @FXML
    private void handleAddTask() {
        String description = descriptionField.getText();
        LocalDate dueDate = dueDatePicker.getValue();
        Task.Priority priority = priorityComboBox.getValue();

        if (description != null && !description.isEmpty() && dueDate != null && priority != null) {
            Task newTask = new Task(description, dueDate, priority);
            tasks.add(newTask);
            clearInputFields();
        } else {
            showAlert("Please fill all fields");
        }
    }
    @FXML
    private void handleExportPDF() {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("tasks.pdf"));
            document.open();

            document.add(new Paragraph("Task List"));
            document.add(new Paragraph("Generated on: " + LocalDate.now()));
            document.add(new Paragraph("-------------------"));

            for (Task task : tasks) {
                document.add(new Paragraph(String.format(
                        "• %s (Due: %s, Priority: %s, Completed: %s)",
                        task.getDescription(),
                        task.getDueDate(),
                        task.getPriority(),
                        task.isCompleted() ? "Yes" : "No"
                )));
            }

            document.close();
            showAlert(Alert.AlertType.INFORMATION, "Success", "PDF created successfully!\nLocation: " + System.getProperty("user.dir") + "\\tasks.pdf");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not create PDF: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearInputFields() {
        descriptionField.clear();
        dueDatePicker.setValue(null);
        priorityComboBox.setValue(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}