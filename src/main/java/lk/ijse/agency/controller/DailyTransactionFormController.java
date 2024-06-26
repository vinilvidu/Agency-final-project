package lk.ijse.agency.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.agency.model.Customer;
import lk.ijse.agency.model.DailyTransaction;
import lk.ijse.agency.model.tm.DailyTransactionTm;
import lk.ijse.agency.repository.CustomerRepo;
import lk.ijse.agency.repository.DailyTransactionRepo;
import lk.ijse.agency.repository.VanRepo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DailyTransactionFormController {

    @FXML
    private TableColumn<?, ?> colAmount;

    @FXML
    private TableColumn<?, ?> colDate;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colVanId;

    @FXML
    private TableView<DailyTransactionTm> tblDailyTransaction;

    @FXML
    private TextField txtAmount;

    @FXML
    private TextField txtDate;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtVanId;
    @FXML
    private AnchorPane root;

    @FXML
    private ComboBox<String> cmbVanId;

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        AnchorPane rootNode = FXMLLoader.load(this.getClass().getResource("/view/dashboard_form.fxml"));

        Scene scene = new Scene(rootNode);

        Stage stage = (Stage) this.root.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Dashboard Form");
    }

    private List<DailyTransaction> dailyTransactionList = new ArrayList<>();

    public void initialize() throws SQLException {
        this.dailyTransactionList = getAllDailyTransaction();
        setCellValueFactory();
        loadDailyTransactionTable();
        getVanId();
    }

    private void getVanId() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<String> codeList = VanRepo.getVanId();
            for (String id : codeList) {
                obList.add(id);
            }

            cmbVanId.setItems(obList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("billId"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colVanId.setCellValueFactory(new PropertyValueFactory<>("vanId"));

    }

    private void loadDailyTransactionTable() {
        ObservableList<DailyTransactionTm> tmList = FXCollections.observableArrayList();

        for (DailyTransaction dailyTransaction : dailyTransactionList) {
            DailyTransactionTm dailyTransactionTm = new DailyTransactionTm(
                    dailyTransaction.getBillId(),
                    dailyTransaction.getAmount(),
                    dailyTransaction.getDate(),
                    dailyTransaction.getVanId()

            );

            tmList.add(dailyTransactionTm);
        }
        tblDailyTransaction.setItems(tmList);
        DailyTransactionTm selectedItem = (DailyTransactionTm) tblDailyTransaction.getSelectionModel().getSelectedItem();
        System.out.println("selectedItem = " + selectedItem);
    }

    private List<DailyTransaction> getAllDailyTransaction() throws SQLException {
        List<DailyTransaction> dailyTransactionList = null;
        dailyTransactionList = DailyTransactionRepo.getAll();
        return dailyTransactionList;
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        txtId.setText("");
        txtAmount.setText("");
        txtDate.setText("");
        cmbVanId.setValue("");
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) throws SQLException {
        String billId = txtId.getText();

        boolean isDeleted = DailyTransactionRepo.delete(billId);
        if (isDeleted) {
            new Alert(Alert.AlertType.CONFIRMATION, "transaction deleted!").show();
            initialize();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        boolean isValidate = validateTransaction();

        if (isValidate) {
            String billId = txtId.getText();
            double amount = Double.parseDouble(txtAmount.getText());
            String date = txtDate.getText();
            String vanId = cmbVanId.getValue();


            DailyTransaction dailyTransaction = new DailyTransaction(billId, amount, date, vanId);

            try {
                boolean isSaved = DailyTransactionRepo.save(dailyTransaction);
                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION, "daily transaction saved!").show();
                    initialize();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }

    private boolean validateTransaction() {
        int num=0;
        String id = txtId.getText();
        boolean isIdValidate= Pattern.matches("(B0)[0-9]{3,7}",id);
        if (!isIdValidate){
            num=1;
            vibrateTextField(txtId);
        }

        String amount=txtAmount.getText();
        boolean isAmountValidate= Pattern.matches("[0-9 .]{3,}",amount);
        if (!isAmountValidate){
            num=1;
            vibrateTextField(txtAmount);
        }

        String date=txtDate.getText();
        boolean isDateValidate= Pattern.matches("[0-9 -]{10,12}",date);
        if (!isDateValidate){
            num=1;
            vibrateTextField(txtDate);
        }


        if(num==1){
            num=0;
            return false;
        }else {
            num=0;
            return true;

        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String billId = txtId.getText();
        double amount = Double.parseDouble(txtAmount.getText());
        String date = txtDate.getText();
        String vanId = cmbVanId.getValue();


        DailyTransaction dailyTransaction = new DailyTransaction(billId, amount, date, vanId);

        try {
            boolean isUpdated = DailyTransactionRepo.update(dailyTransaction);
            if (isUpdated) {
                new Alert(Alert.AlertType.CONFIRMATION, "daily transaction updated!").show();
                initialize();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) throws SQLException {
        String billId = txtId.getText();

        DailyTransaction dailyTransaction = DailyTransactionRepo.searchById(billId);

        if (dailyTransaction != null) {
            txtId.setText(dailyTransaction.getBillId());
            txtAmount.setText(String.valueOf(dailyTransaction.getAmount()));
            txtDate.setText(dailyTransaction.getDate());
            cmbVanId.setValue(dailyTransaction.getVanId());

        }
    }

    private void vibrateTextField(TextField textField) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(textField.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(50), new KeyValue(textField.translateXProperty(), -6)),
                new KeyFrame(Duration.millis(100), new KeyValue(textField.translateXProperty(), 6)),
                new KeyFrame(Duration.millis(150), new KeyValue(textField.translateXProperty(), -6)),
                new KeyFrame(Duration.millis(200), new KeyValue(textField.translateXProperty(), 6)),
                new KeyFrame(Duration.millis(250), new KeyValue(textField.translateXProperty(), -6)),
                new KeyFrame(Duration.millis(300), new KeyValue(textField.translateXProperty(), 6)),
                new KeyFrame(Duration.millis(350), new KeyValue(textField.translateXProperty(), -6)),
                new KeyFrame(Duration.millis(400), new KeyValue(textField.translateXProperty(), 0))

        );

        textField.setStyle("-fx-border-color: red;");
        timeline.play();

        Timeline timeline1 = new Timeline(
                new KeyFrame(Duration.seconds(3), new KeyValue(textField.styleProperty(), "-fx-border-color: #bde0fe;"))
        );

        timeline1.play();
    }
}
