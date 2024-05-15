package lk.ijse.agency.controller;

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
import lk.ijse.agency.model.CreditBill;
import lk.ijse.agency.model.Customer;
import lk.ijse.agency.model.Employee;
import lk.ijse.agency.model.Expenses;
import lk.ijse.agency.model.tm.CreditBillTm;
import lk.ijse.agency.repository.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreditBillFormController {

    @FXML
    private TableColumn<?, ?> colAmount;

    @FXML
    private TableColumn<?, ?> colCusId;

    @FXML
    private TableColumn<?, ?> colDate;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colRouteId;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<CreditBillTm> tblCreditBill;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtId1;

    @FXML
    private TextField txtId11;

    @FXML
    private TextField txtId2;

    @FXML
    private TextField txtId21;

    @FXML
    private ComboBox<String> cmbCusId;


    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        AnchorPane rootNode = FXMLLoader.load(this.getClass().getResource("/view/dashboard_form.fxml"));

        Scene scene = new Scene(rootNode);

        Stage stage = (Stage) this.root.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Dashboard Form");
    }

    private List<CreditBill> creditBillList = new ArrayList<CreditBill>();

    public void initialize() {
        this.creditBillList = getAllCreditBill();
        setCellValueFactory();
        loadCreditBillTable();
        getCusId();
    }

    private void getCusId() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<String> codeList = CustomerRepo.getCusId();
            for (String id : codeList) {
                obList.add(id);
            }

            cmbCusId.setItems(obList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("billId"));
        colCusId.setCellValueFactory(new PropertyValueFactory<>("cusId"));
        colRouteId.setCellValueFactory(new PropertyValueFactory<>("routeId"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    private void loadCreditBillTable() {
        ObservableList<CreditBillTm> tmList = FXCollections.observableArrayList();

        for (CreditBill creditBill : creditBillList) {
            CreditBillTm creditBillTm = new CreditBillTm(
                    creditBill.getBillId(),
                    creditBill.getCusId(),
                    creditBill.getRouteId(),
                    creditBill.getAmount(),
                    creditBill.getDate()
            );

            tmList.add(creditBillTm);
        }
        tblCreditBill.setItems(tmList);
        CreditBillTm selectedItem = (CreditBillTm) tblCreditBill.getSelectionModel().getSelectedItem();
        System.out.println("selectedItem = " + selectedItem);
    }

    private List<CreditBill> getAllCreditBill() {
        List<CreditBill> creditBillList = null;
        try {
            creditBillList = CreditBillRepo.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return creditBillList;
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) throws SQLException {
        String billId = txtId.getText();

        boolean isDeleted = CreditBillRepo.delete(billId);
        if (isDeleted) {
            new Alert(Alert.AlertType.CONFIRMATION, "credit bill deleted!").show();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String billId = txtId.getText();
        String cusId = cmbCusId.getValue();
        String routeId = txtId11.getText();
        double amount = Double.valueOf(txtId1.getText());
        String date = txtId21.getText();

        CreditBill creditBill = new CreditBill(billId, cusId, routeId, amount,date);

        try {
            boolean isSaved = CreditBillRepo.save(creditBill);
            if (isSaved) {
                new Alert(Alert.AlertType.CONFIRMATION, "credit bill saved!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }

    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String billId = txtId.getText();
        String cusId = cmbCusId.getValue();
        String routeId = txtId11.getText();
        double amount = Double.valueOf(txtId1.getText());
        String date = txtId21.getText();

        CreditBill creditBill = new CreditBill(billId, cusId, routeId, amount,date);

        try {
            boolean isUpdated = CreditBillRepo.update(creditBill);
            if (isUpdated) {
                new Alert(Alert.AlertType.CONFIRMATION, "credit bill updated!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {
        String billId = txtId.getText();

        try {
            CreditBill creditBill = CreditBillRepo.searchById(billId);

            if (creditBill != null) {
                txtId.setText(creditBill.getBillId());
                cmbCusId.setValue(creditBill.getCusId());
                txtId11.setText(creditBill.getRouteId());
                txtId1.setText(String.valueOf(creditBill.getAmount()));
                txtId21.setText(creditBill.getDate());
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    void cmbCusIdOnAction(ActionEvent event) {
        String id = cmbCusId.getValue();
        try {
            Customer customer = CustomerRepo.searchByCusId(id);
            if (customer != null) {
                txtId11.setText(customer.getRouteId());

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        txtId.setText("");
        cmbCusId.setValue("");
        txtId11.setText("");
        txtId1.setText("");
        txtId21.setText("");
    }
}