package es.perez.tim_decryptsqlmsg;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.*;
import java.util.*;

public class Controller {
    @FXML
    private TextField elegir;
    @FXML
    private TextArea puertorico;
    @FXML
    private Pane zroot;
    @FXML
    private TableView tb;

    private final ObservableList<AmigoList> tabla = FXCollections.observableArrayList();

    public Controller() {
    }

    @FXML
    protected void OpenFileDlg(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("SQL Database", "*.db"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File f = fileChooser.showOpenDialog(zroot.getScene().getWindow());
        if(f != null && f.exists()) {
            elegir.setText(f.toString());
        }
    }

    @FXML
    protected void Accionar(ActionEvent ave) {
        if(elegir.getText().trim().isEmpty() || puertorico.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lack of necessary information");
            alert.setHeaderText("Please input complete file path and IMEI key information");
            alert.showAndWait();
        } else {
            ObservableList<TableColumn> observableList = tb.getColumns();
            observableList.get(0).setCellValueFactory(new PropertyValueFactory("Age"));
            observableList.get(1).setCellValueFactory(new PropertyValueFactory("Alias"));
            observableList.get(2).setCellValueFactory(new PropertyValueFactory("Gender"));
            observableList.get(3).setCellValueFactory(new PropertyValueFactory("Name"));
            observableList.get(4).setCellValueFactory(new PropertyValueFactory("Remark"));
            observableList.get(5).setCellValueFactory(new PropertyValueFactory("QQID"));
            try {
                Connection cnx = DbConnection.getConnection(elegir.getText());
                HashMap<String, String> contiendo = DBProc.getColumns(cnx);
                ArrayList<ArrayList> db = DBProc.getValues(cnx, contiendo);
                String[] col_names = contiendo.keySet().toArray(new String[]{});
                Object[][] objArr = new Object[db.size()][col_names.length];
                //FileWriter fw = new FileWriter("D:/Tierra.txt",false);
                for(int i = 0; i < db.size(); i++) { // Length of rows
                    AmigoList ami = new AmigoList();
                    for(int j = 0; j < col_names.length; j++) { // Length of columns
                        HashMap tengo = (HashMap) (db.get(i)).get(j);
                        String tipo = contiendo.get(col_names[j]);
                        objArr[i][j] = tengo.get(col_names[j]);
                        if(objArr[i][j] != null) objArr[i][j] = DBProc.Proc(tipo, objArr[i][j], puertorico.getText());
                        else objArr[i][j] = "NULL";
                        // Gender : 0 unknown, 1 male, 2 female
                        if(col_names[j].equals("age")) ami.setAge((Integer) objArr[i][j]);
                        if(col_names[j].equals("alias")) ami.setAlias((String) objArr[i][j]);
                        if(col_names[j].equals("gender")) {
                            int v0 = (Integer)objArr[i][j];
                            String gv = (v0 == 0) ? "Unknown" : ((v0 == 1) ? "Male" : "Female");
                            ami.setGender(gv);
                        }
                        if(col_names[j].equals("name")) ami.setName((String) objArr[i][j]);
                        if(col_names[j].equals("remark")) ami.setRemark((String) objArr[i][j]);
                        if(col_names[j].equals("uin")) ami.setQQID((String) objArr[i][j]);
                        /*if(col_names[j].equals("name") || col_names[j].equals("gender") || col_names[j].equals("uin") || col_names[j].equals("remark") || col_names[j].equals("age")) {
                            fw.write("Column " + col_names[j] + "\n");
                            fw.write("At (i->" + i + ", j->" + j + "):\n");
                            fw.write("Value: " + objArr[i][j] + "\n");
                            fw.write("Types: " + tipo + "\n");
                            fw.write("DataType: " + objArr[i][j].getClass() + "\n");
                        }*/
                    }
                    tabla.add(ami);
                }
                tb.setItems(tabla);
                DbConnection.closeConnection(cnx);
                //fw.close();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Encountered exception:");
                alert.setContentText(e + "\n");
                alert.showAndWait();
            }
        }
    }
}
