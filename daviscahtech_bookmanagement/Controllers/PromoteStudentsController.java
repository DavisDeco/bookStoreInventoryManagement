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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author davis
 */
public class PromoteStudentsController implements Initializable {
    
    Image errorFlag = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/errorFlag.png").toExternalForm());
    ImageView errorImage = new ImageView(errorFlag);
    
    Image thumbsImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/thumbs.png").toExternalForm());
    ImageView imageSuccess = new ImageView(thumbsImage);
    
    Image studentImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/student.png").toExternalForm());
    ImageView studentSuccess = new ImageView(studentImage);
    
     //###############################For DB link##########################
    Connection con = null;
    PreparedStatement pstmt;
    ResultSet rs;
    Statement st;
    @FXML
    private ComboBox<?> promote_comboCurrentClass;
    private final ObservableList comboCurrentClassData = FXCollections.observableArrayList();
    
    
    @FXML
    private ComboBox<?> promote_comboTargetClass;
    private final ObservableList comboCurrentTargetData = FXCollections.observableArrayList();
    
    
   
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //connect to the database
        con = DatabaseConnection.connectDb();
        
        
    }   
    
      //####################### METHODS TO HANDLE CURRENT STUDENT CLASS DETAILS ##########
    private void updateComboCurrentClass(){  
        try { 
            String sql = "SELECT className FROM class";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){                
                comboCurrentClassData.add( rs.getString("className"));         
            }
            // populate class combo box 
            promote_comboCurrentClass.setItems(comboCurrentClassData);
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
            }        
        }
    }
    
    // call this from anatha class
    public void refreshComboCurrentClass(){    
        comboCurrentClassData.clear();
        updateComboCurrentClass();
    }
    
    @FXML
    private void promote_refreshComboCurrentClass(ActionEvent event) {
        refreshComboCurrentClass();
    }
    
   //####################### END METHODS TO HANDLE CURRENT STUDENT CLASS DETAILS ##########
    
   //####################### METHODS TO HANDLE TARGET STUDENT CLASS DETAILS ##########
    private void updateTargetClass(){  
        try { 
            String sql = "SELECT className FROM class";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){                
                comboCurrentTargetData.add( rs.getString("className"));         
            }
            // populate class combo box 
            promote_comboTargetClass.setItems(comboCurrentTargetData);
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
            }        
        }
    }
    
    // call this from anatha class
    public void refreshComboTargetClass(){    
        comboCurrentTargetData.clear();
        updateTargetClass();
    }
    
   @FXML
    private void promote_refreshComboTargetClass(ActionEvent event) {
        refreshComboTargetClass();
    }

    
     //####################### END METHODS TO HANDLE CURRENT STUDENT CLASS DETAILS ##########
    
    
    @FXML
    private void loadInstructionOperation(ActionEvent event) {
         //show instruction alert before promoting class
            String ms = "Be careful will promoting a class to the next level. All students in a particular class will be "
                    + "automatically be promoted.\n\n"
                    + "To stop promoting a particular student to the next level, you have to do that"
                    + " manually after promoting all the students including them."
                    + " This can be done in home screen under 'Configure student.\n\n"
                    + "To depromote a class, the function is the same but reverse the TARGET CLASS.\n\n"
                    + "VERY IMPORTANT: Promoting a class starts from upper final classes then proceeding downwards, that is,"
                    + "First delete students from final classes to make them empty, then promote the lower preceeding class to final class"
                    + "then the rest follow in a top-bottom approach. ";           
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Promote instruction");
            alert.setGraphic(studentSuccess);
            alert.setHeaderText(null);
            alert.setContentText(ms);
            alert.showAndWait();
    }

    
    @FXML
    private void promoteClassOperation(ActionEvent event) throws SQLException {  
        
        
        if (promote_comboCurrentClass.getSelectionModel().isEmpty() || promote_comboTargetClass.getSelectionModel().isEmpty()) { 
                 
                 String msg = null;
            
                if (promote_comboCurrentClass.getSelectionModel().isEmpty()) {
                     msg = "Current class is not choosen, you must choose it";                 
                } else if(promote_comboTargetClass.getSelectionModel().isEmpty()) {
                   msg = "Target class is not choosen, you must choose it";
                } 
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Promotion Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The students could not be promoted.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else { 

            String currentClass  = promote_comboCurrentClass.getSelectionModel().getSelectedItem().toString();
            String targetClass  = promote_comboTargetClass.getSelectionModel().getSelectedItem().toString();
            
                String s = "SELECT * FROM class WHERE className = ?  ";
                pstmt = con.prepareStatement(s);
                pstmt.setString(1, currentClass);
                rs=pstmt.executeQuery();
                
                if (rs.next()) {                    
                  boolean i =  rs.getBoolean("isFinal");                  
                    if (i) {
                        Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                        errorIssue.setTitle("Promotion Failed");
                        errorIssue.setGraphic(errorImage);
                        errorIssue.setHeaderText(null);
                        errorIssue.setContentText(""+currentClass+" is the final class in the school system.\n"
                                + " You can not promote students any further.\n\n"
                                + "You can delete all students from this class and allow lower students be promoted into it");
                        errorIssue.showAndWait();
                    } else {
                        String ss = "SELECT * FROM class WHERE className = ?  ";
                        pstmt = con.prepareStatement(ss);
                        pstmt.setString(1, targetClass);
                        ResultSet rs2 = pstmt.executeQuery();
                        
                        if (rs2.next()) {
                            
                            boolean ii =  rs2.getBoolean("isFinal");
                            
                            if (ii) {
                                triggerPromotionWhenTargetClassIsFinalButStudentsHaveBeenRemoved();
                            } else {
                                 triggerPromotionWhenBothClassesNotFinal();
                            }
                        }
                    }
                }
        }
    }
    
  
    //method to trigger promotion if both current class and target class are not final in school
    private void triggerPromotionWhenBothClassesNotFinal(){
         try {
                    String sql = "UPDATE students SET student_class = ? WHERE student_class = '"+promote_comboCurrentClass.getSelectionModel().getSelectedItem().toString()+"'";
                    
                    pstmt= con.prepareStatement(sql);

                    pstmt.setString(1, promote_comboTargetClass.getSelectionModel().getSelectedItem().toString());

                    int i = pstmt.executeUpdate(); // load data into the database             
                    if (i>0) {
                        //show alert before promoting
                    String msg = "All Student from class = "+promote_comboCurrentClass.getSelectionModel().getSelectedItem().toString()+""
                            + " have successfully been promoted to "+promote_comboTargetClass.getSelectionModel().getSelectedItem().toString()+ ".\n\n"
                            + "CLOSE THE APPLICATION AND RESTART FOR CHANGES TO REFLECT.";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Promotion status");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not promote Student due  to internal error.");
                        error.showAndWait();
                    } 
                    pstmt.close();
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in promotion");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not promote Student due  to internal error. ");
                    at.showAndWait();  
                }finally {
                   try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {}               
               }
    }
    
    
    
    //Method to trigger promotion when target class is final but students hopefully have been deleted from it.
    private void triggerPromotionWhenTargetClassIsFinalButStudentsHaveBeenRemoved(){
        
             //show confirm alert before promoting
            String ms = ""+promote_comboTargetClass.getSelectionModel().getSelectedItem().toString()+""
            + "is a final class and might have students already!\n\n"
                    + "Are you sure you have deleted students and ready promote new students from "+promote_comboCurrentClass.getSelectionModel().getSelectedItem().toString()+" to"
                    + " "+promote_comboTargetClass.getSelectionModel().getSelectedItem().toString() +" ?";           
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm promotion - ALERT");
            alert.setGraphic(studentSuccess);
            alert.setHeaderText(null);
            alert.setContentText(ms);
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
               try {
                    String sql = "UPDATE students SET student_class = ? WHERE student_class = '"+promote_comboCurrentClass.getSelectionModel().getSelectedItem().toString()+"'";
                    
                    pstmt= con.prepareStatement(sql);
                    pstmt.setString(1, promote_comboTargetClass.getSelectionModel().getSelectedItem().toString());

                    int i = pstmt.executeUpdate(); // load data into the database             
                    if (i>0) {
                        //show alert before promoting
                    String msg = "All Student from class = "+promote_comboCurrentClass.getSelectionModel().getSelectedItem().toString()+""
                            + " have successfully been promoted to "+promote_comboTargetClass.getSelectionModel().getSelectedItem().toString()+ ".\n\n"
                            + "CLOSE THE APPLICATION AND RESTART FOR CHANGES TO REFLECT.";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Promotion status");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not promote Student due  to internal error.");
                        error.showAndWait();
                    } 
                    pstmt.close();
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in promotion");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not promote Student due  to internal error. ");
                    at.showAndWait();  
                }finally {
                   try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {}               
               }
            }
    }
    
    
    
}// end of class
