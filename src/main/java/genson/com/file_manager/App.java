package genson.com.file_manager;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;

public class App extends Application {
    private Controller ctrl;

    private AnchorPane header;
    private GridPane body;
    private TableView<FileInfo> detailBody;
    private VBox mainWrap;
    private File file;
    private Button chooser;
    private TextField textField;
    private Button connect;
    private TreeView treeView;


    @Override
    public void start(Stage stage) throws Exception {
        ctrl = new Controller();

        stage.setTitle("File explorer");
        header = new AnchorPane();
        header.setStyle("-fx-padding: 10px 0");
        body = new GridPane();
        detailBody = new TableView<>();
        mainWrap = new VBox();


        treeView = new TreeView<>();

        textField = new TextField();

        connect = new Button();
        connect.setText("connect");

        chooser = new Button();
        chooser.setText("choose");


        // handle chooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        chooser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                file = ctrl.handleChoose(directoryChooser, stage);
                treeView = ctrl.buildFileSystemBrowser(file, new ListViewCallback() {
                    @Override
                    public void handle(GTreeItem item) {
                        detailBody = ctrl.createTableView(item);
                        body.add(detailBody, 1, 0, 1, 1);
                        detailBody.setStyle("-fx-background-color: #9de0ad; -fx-border-width: 2px; -fx-border-color: #9de0ad");
                    }
                });
                body.add(treeView, 0, 0, 1, 1);
            }
        });


        // style for chooser button
        chooser.setStyle("-fx-start-margin: 100px;" +
                " -fx-background-color: #9de0ad;" +
                " -fx-min-width: 100px;" +
                " -fx-min-height: 30px; " +
                "-fx-border-color: #45ada8; " +
                "-fx-border-radius: 5px");

        textField.setStyle("-fx-min-width: 200px; -fx-border-color: #9de0ad");
        connect.setStyle("-fx-start-margin: 100px;" +
                " -fx-background-color: #9de0ad;" +
                " -fx-min-width: 100px;" +
                " -fx-min-height: 30px; " +
                "-fx-border-color: #45ada8; " +
                "-fx-border-radius: 5px");

        AnchorPane.setTopAnchor(chooser, 10.0);
        AnchorPane.setLeftAnchor(chooser, 10.0);
        AnchorPane.setBottomAnchor(chooser, 10.0);

        AnchorPane.setRightAnchor(connect, 10.0);
        AnchorPane.setTopAnchor(connect, 10.0);
        AnchorPane.setBottomAnchor(connect, 10.0);

        AnchorPane.setTopAnchor(textField, 10.0);
        AnchorPane.setRightAnchor(textField, 130.0);
        AnchorPane.setBottomAnchor(textField, 10.0);

        header.getChildren().addAll(chooser, textField, connect);
        header.setStyle("-fx-background-color: #dcedc2");

        treeView.setStyle("-fx-background-color: #9de0ad; -fx-border-width: 2px; -fx-border-color: #9de0ad");

        detailBody.setStyle("-fx-background-color: #9de0ad; -fx-border-width: 2px; -fx-border-color: #9de0ad");

        treeView.setMinWidth(350);
        treeView.setMinHeight(1000);

        detailBody.setMinWidth(1150);
        detailBody.setMinHeight(1000);

        TableColumn<FileInfo, ImageView> imageColumn = new TableColumn<>("images");
        TableColumn<FileInfo, String> dateColumn = new TableColumn<>("date");
        TableColumn<FileInfo, String> nameColumn = new TableColumn<>("name");
        TableColumn<FileInfo, String> sizeColumn = new TableColumn<>("size");

        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        detailBody.getColumns().addAll(imageColumn, nameColumn, sizeColumn, dateColumn);

        body.add(treeView, 0, 0, 1, 1);
        body.add(detailBody, 1, 0, 1, 1);


        mainWrap.getChildren().addAll(header, body);

        Scene scene = new Scene(mainWrap, 1500, 1000);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Utils.loadIcons();
        launch();
    }
}
