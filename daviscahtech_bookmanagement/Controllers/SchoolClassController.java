/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daviscahtech_bookmanagement.Controllers;

import daviscahtech_bookmanagement.dao.DatabaseConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author davis
 */
public class SchoolClassController implements Initializable {
    
    Image errorFlag = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/errorFlag.png").toExternalForm());
    ImageView errorImage = new ImageView(errorFlag);
    
    Image thumbsImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/thumbs.png").toExternalForm());
    ImageView imageSuccess = new ImageView(thumbsImage);
    
    Image schoolImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/School.png").toExternalForm());
    ImageView schoolSuccess = new ImageView(schoolImage);

    //###############################For DB link##########################
    Connection con = null;
    PreparedStatement pstmt;
    ResultSet rs;
    Statement st;
   
    @FXML
    private TextField searchClassName;
    @FXML
    private Text searchedclassID;
    @FXML
    private TextField className;
    @FXML
    private ListView<String> classNameListView;
    ObservableList<String> classNameData = FXCollections.observableArrayList();
    @FXML
    private CheckBox isClassFinal;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //connect to the database
        con = DatabaseConnection.connectDb();
        
        refreshClassName();
    }   
    
    //method to clear fields after every operation
    private void clearFields(){
        searchClassName.clear();
        searchedclassID.setText(null);
        className.clear();
        isClassFinal.setSelected(false);
    }

     @FXML
    private void searchClassNameOperation(ActionEvent event) {
        if (searchClassName.getText().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Search Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The class has failed Search operation.\n"
                        + "Ensure the class name has been inserted.");
            errorIssue.showAndWait(); 
            
        }else {
            try {
                String sql = "SELECT * FROM class WHERE className = ?  ";
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, searchClassName.getText());

                rs=pstmt.executeQuery();
                if (rs.next()) {
                    String add1 = rs.getString("id");
                    searchedclassID.setText(add1); 
                    String add2 = rs.getString("className");
                    className.setText(add2);
                    boolean isFinal = rs.getBoolean("isFinal");
                    if (isFinal) {
                        isClassFinal.setSelected(true);
                    } else {
                        isClassFinal.setSelected(false);
                    }
                      
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid class");
                    alert.setHeaderText(null);
                    alert .setGraphic(errorImage);
                    alert.setContentText("Record not Found or class does not Existing!");
                    alert.showAndWait(); 
                }
            } catch (SQLException e) {
            } finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {
                    }        
               }  
        }
    }
    

      @FXML
    private void class_register(ActionEvent event) {
        
         if (className.getText().isEmpty() ) {             
                 String msg = null; 
                 
                if (className.getText().isEmpty()) {
                     msg = "Class/Level name is empty, you must enter it";                 
                } 
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Registration Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The class/Level has failed registration.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {              
             //show confirm alert before registering data of issued
            String ms = " Are you sure you want to register class = "+className.getText();           
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm class registration");
            alert.setGraphic(schoolSuccess);
            alert.setHeaderText(null);
            alert.setContentText(ms);
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
                
                int isSelected;
                if (isClassFinal.isSelected()) {
                    isSelected = 1;
                } else {
                    isSelected = 0;
                }
                
               try {
                    String sql = "INSERT INTO class (className,	isFinal) VALUES(?,?)";
                    pstmt= con.prepareStatement(sql);

                    pstmt.setString(1, className.getText());
                    pstmt.setInt(2, isSelected);

                    int i = pstmt.executeUpdate(); // load data into the database             
                    if (i>0) {
                        //show alert before registering data of employee
                    String msg = " Class Name = "+className.getText()+ " has been registered and ready to be allocated to a student.";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Registration Information Dialog");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                    clearFields();
                    
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not register class due  to internal error.");
                        error.showAndWait();
                    } 
                    
                    refreshClassName();
                    pstmt.close();
                    
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in registration of class");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not register class due  to internal error. " +e);
                    at.showAndWait();                         
                }finally {
                   try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {}               
               }
               
            }
            isClassFinal.setSelected(false);   
        }
    }
    
    @FXML
    private void class_delete(ActionEvent event) {
        
         if (searchedclassID.getText().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Delete Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The class has failed DELETE operation.\n"
                        + "Ensure the class name has been inserted and retrieve class information before deleting.");
            errorIssue.showAndWait(); 
            
        }else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete class");
            alert.setGraphic(schoolSuccess);
            alert.setHeaderText(null);
            alert.setContentText("Are you sure to delete this class?");         
            Optional <ButtonType> obt = alert.showAndWait();

            if (obt.get()== ButtonType.OK) {
                try {
               String query = "DELETE FROM class WHERE id = ? ";
               pstmt = con.prepareStatement(query);
               pstmt.setString(1, searchedclassID.getText());
               pstmt.executeUpdate();            
               pstmt.close(); 
               
               clearFields();
               refreshClassName();
               
               } catch (SQLException ex) {
                   Logger.getLogger(SchoolClassController.class.getName()).log(Level.SEVERE, null, ex);
               }finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {
                    }        
                }
                      
           }            
        }
    }

    @FXML
    private void class_update(ActionEvent event) {
        
         if (className.getText().isEmpty() || searchedclassID.getText().isEmpty() ) {             
                 String msg = null; 
                 
                if (className.getText().isEmpty()) {
                     msg = "Class/Level name is empty, you must enter it";                 
                } else if (searchedclassID.getText().isEmpty()) {
                    msg = "Enter the class name to retrieve it first in order to update the class";
             }
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Update Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The class/Level has failed update.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {              
             //show confirm alert before registering data of issued
            String ms = " Are you sure you want to update class = "+className.getText();           
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm class update");
            alert.setGraphic(schoolSuccess);
            alert.setHeaderText(null);
            alert.setContentText(ms);
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
               
                int isSelected;
                if (isClassFinal.isSelected()) {
                    isSelected = 1;
                } else {
                    isSelected = 0;
                }
                
               try {
                    String sql = "UPDATE class SET className=?,	isFinal=? WHERE id = '"+searchedclassID.getText()+"'";
                    pstmt= con.prepareStatement(sql);

                    pstmt.setString(1, className.getText());
                    pstmt.setInt(2, isSelected);

                    int i = pstmt.executeUpdate(); // load data into the database             
                    if (i>0) {
                        //show alert before registering data of employee
                    String msg = " Class Name = "+className.getText()+ " has been updated and ready to be allocated to a student.";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Update Information Dialog");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                    clearFields();
                    refreshClassName();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not update class due  to internal error.");
                        error.showAndWait();
                    } 
                    pstmt.close();
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in updating of class");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not update class due  to internal error. " +e);
                    at.showAndWait();                         
                }finally {
                   try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {}               
               }
            }
            isClassFinal.setSelected(false);
        }
    }
   
    
    private void refreshClassName(){        
        classNameData.clear();
        refreshList();         
    }

    private void refreshList() {
        try {
            String sql = "SELECT * FROM class ";
            pstmt = con.prepareStatement(sql);
            rs=pstmt.executeQuery();
            while (rs.next()) {
                      classNameData.add(rs.getString("className"));            
               }
                                
                pstmt.close(); 
        } catch (SQLException ex) {
            Logger.getLogger(SchoolClassController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // show this details on the listview
        classNameListView.getItems().setAll(classNameData);
    }

    @FXML
    private void classNameListViewMouseClicked(MouseEvent event) {
        
            try {
                String name = classNameListView.getSelectionModel().getSelectedItem();
                String sql = "SELECT * FROM class WHERE className = ?  ";
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, name);
                rs=pstmt.executeQuery();
                
                if (rs.next()) {
                    String add1 = rs.getString("id");
                    searchedclassID.setText(add1); 
                    String add2 = rs.getString("className");
                    className.setText(add2);
                    
                    boolean isFinal = rs.getBoolean("isFinal");
                    if (isFinal) {
                        isClassFinal.setSelected(true);
                    } else {
                        isClassFinal.setSelected(false);
                    }
                      
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid class");
                    alert.setHeaderText(null);
                    alert .setGraphic(errorImage);
                    alert.setContentText("Record not Found or class does not Existing!");
                    alert.showAndWait(); 
                }
            } catch (SQLException e) {
            } finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {
                    }        
               } 
        
    }
    
    

}// end of class
