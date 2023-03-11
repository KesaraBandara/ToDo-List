package controller;

import db.DBconnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LoginFormController {
    public AnchorPane root;
    public TextField txtUserName;
    public TextField txtPassword;

    public static  String userid;
    public static String username;


    public void btnOnActionLogin(ActionEvent actionEvent) throws IOException {

        String username = txtUserName.getText();
        String password = txtPassword.getText();

        Connection connection = DBconnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement =connection.prepareStatement("select * from user where user_name=? and password=?");
            preparedStatement.setObject(1,username);
            preparedStatement.setObject(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();
            boolean isExist = resultSet.next();

            if(isExist){
                userid=resultSet.getString(1);
                username=resultSet.getString(2);

                Parent parent = FXMLLoader.load(getClass().getResource("../view/ToDoListForm.fxml"));
                Scene scene=new Scene(parent);

                Stage stage= (Stage) this.root.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("to do list");
                stage.centerOnScreen();

            }
            else{
                new Alert(Alert.AlertType.ERROR,"invalid username or password").showAndWait();

                txtPassword.clear();
                txtUserName.clear();
                txtUserName.requestFocus();
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }



    public void lblOnMouseClickedreateNewAccount(MouseEvent mouseEvent) throws IOException {
        Parent parent= FXMLLoader.load(this.getClass().getResource("../view/CreateNewUserForm.fxml"));
        Scene scene=new Scene(parent);
        Stage primaryStage=(Stage) this.root.getScene().getWindow();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Create User");
        primaryStage.centerOnScreen();

    }
}
