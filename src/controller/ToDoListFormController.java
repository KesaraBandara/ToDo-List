package controller;

import db.DBconnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.ToDoTm;

import java.io.IOException;
import java.sql.*;
import java.util.Locale;
import java.util.Optional;

public class ToDoListFormController {
    public Label lblWelcome;
    public TextField txtToDoUpdate;
    public Pane paneAdd;
    public TextField txtAdd;
    public Label lblUserId;
    public AnchorPane root;
    public ListView<ToDoTm> lstTodos;
    public Button btnDelete;
    public Button btnUpdate;
    public Label lblEmptyRecord;

    String newId;
    String id;

    public void listMake(){
        ObservableList<ToDoTm> items = lstTodos.getItems();
        items.clear();


        Connection connection = DBconnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todos where user_id=?");
            preparedStatement.setObject(1,LoginFormController.userid);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String userid = resultSet.getString(3);

                ToDoTm toDoTm = new ToDoTm(id, description, userid);
                items.add(toDoTm);

            }
            lstTodos.refresh();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }



    }

    public void initialize(){
        System.out.println(LoginFormController.username);
        lblWelcome.setText("HI "+LoginFormController.userid +" WELCOME to TO Do List");
        lblUserId.setText(LoginFormController.userid);

        paneAdd.setVisible(false);
        txtToDoUpdate.setDisable(true);
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
        lblEmptyRecord.setVisible(false);

        listMake();

        lstTodos.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTm>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTm> observable, ToDoTm oldValue, ToDoTm newValue) {
                txtToDoUpdate.setDisable(false);
                btnDelete.setDisable(false);
                btnUpdate.setDisable(false);


                paneAdd.setVisible(false);

               // String description1 = newValue.getDescription();


                ToDoTm selectedItem = lstTodos.getSelectionModel().getSelectedItem();
                if(selectedItem==null) {
                    return;
                }


                String description = selectedItem.getDescription();

                txtToDoUpdate.setText(description);
                txtToDoUpdate.requestFocus();

                id = newValue.getId();
            }
        });

    }

    public void btnOnActionUpdate(ActionEvent actionEvent) {
        String text = txtToDoUpdate.getText();

        Connection connection = DBconnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todos set description=? where id=?");
            preparedStatement.setObject(1,text);
            preparedStatement.setObject(2,id);

            preparedStatement.executeUpdate();

            listMake();
            txtToDoUpdate.clear();
            txtToDoUpdate.setDisable(true);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void btnOnActionDelete(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "do you want to delete the selected record.", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            Connection connection = DBconnection.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from todos where id=?");
                preparedStatement.setObject(1,id);
                preparedStatement.executeUpdate();

                listMake();

                txtToDoUpdate.clear();
                txtToDoUpdate.setDisable(true);
                btnDelete.setDisable(true);
                btnUpdate.setDisable(true);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }




    }

    public void btnOnActionAddToList(ActionEvent actionEvent) {
        if(txtAdd.getText().trim().isEmpty()){
            lblEmptyRecord.setVisible(true);
            txtToDoUpdate.requestFocus();
            System.out.println("blank");
        }
        else{
            lblEmptyRecord.setVisible(false);
            System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
            String id = autoGenerateId();
            System.out.println(id);

            String description = txtAdd.getText();
            String userId=LoginFormController.userid;

            Connection connection = DBconnection.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into todos values(?,?,?)");
                preparedStatement.setObject(1,id);
                preparedStatement.setObject(2,description);
                preparedStatement.setObject(3,userId);

                preparedStatement.executeUpdate();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            paneAdd.setDisable(true);
            listMake();
        }


    }

    public String autoGenerateId(){
        Connection connection = DBconnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from todos order by id desc limit 1");
            boolean isExist = resultSet.next();

            if(isExist){
                String oldId = resultSet.getString(1);
                oldId=oldId.substring(1,oldId.length());
                int intOldId = Integer.parseInt(oldId);

                intOldId=intOldId+1;


                if(intOldId<10){
                    newId="t00"+intOldId;
                }
                else if(intOldId<100){
                    newId="t0"+intOldId;
                }
                else{
                    newId="t"+intOldId;
                }

            }else{
                newId="t001";
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return newId;
    }

    public void btnOnActionLogOut(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "do you want to log out", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if(buttonType.get().equals(ButtonType.YES)){
            Parent parent= FXMLLoader.load(getClass().getResource("../view/LoginForm.fxml"));
            Scene scene=new Scene(parent);
            Stage stage= (Stage) this.root.getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle("login form");
            stage.centerOnScreen();
        }
    }

    public void btnOnActionAddNewToDo(ActionEvent actionEvent) {
        paneAdd.setVisible(true);
        txtAdd.requestFocus();
        txtToDoUpdate.setDisable(true);
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
    }
}
