package genson.com.file_manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public class Controller {


    public File handleChoose(DirectoryChooser dirChooser, Stage stage) {
        File dir = dirChooser.showDialog(stage);
        if (dir != null) {
            return new File(dir.toPath().toString());
        } else {
            return new File("/");
        }
    }

    public TreeView<GTreeItem> buildFileSystemBrowser(File file, ListViewCallback listViewCallback) {
        TreeItem<GTreeItem> root = createNode(file);
        TreeView<GTreeItem> treeView = new TreeView<>(root);

        treeView.getSelectionModel().selectedItemProperty().addListener((observableValue, gTreeItemTreeItem, t1) -> {
            TreeItem<GTreeItem> data = observableValue.getValue();
            listViewCallback.handle(data.getValue());
        });
        return treeView;
    }

    private TreeItem<GTreeItem> createNode(final File f) {
        Image fileIcon = Utils.getFileIcons(f.getName());
        return new TreeItem<>(new GTreeItem(f), new ImageView(fileIcon)) {

            private boolean isLeaf;

            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;

            @Override
            public ObservableList<TreeItem<GTreeItem>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;

                    super.getChildren().setAll(buildChildren(this));
                }
                return super.getChildren();
            }

            @Override
            public boolean isLeaf() {
                if (isFirstTimeLeaf) {
                    isFirstTimeLeaf = false;
                    File f = getValue().getFile();
                    isLeaf = f.isFile();
                }

                return isLeaf;
            }

            private ObservableList<TreeItem<GTreeItem>> buildChildren(TreeItem<GTreeItem> TreeItem) {
                File f = TreeItem.getValue().getFile();
                if (f != null && f.isDirectory()) {
                    File[] files = f.listFiles();
                    if (files != null) {
                        ObservableList<TreeItem<GTreeItem>> children = FXCollections.observableArrayList();
                        for (File childFile : files) {
                            children.add(createNode(childFile));
                        }

                        return children;
                    }
                }

                return FXCollections.emptyObservableList();
            }
        };
    }

    public TableView<FileInfo> createTableView(GTreeItem item) {
        ObservableList<FileInfo> list = getListFileInfo(item.getFile());

        TableView<FileInfo> table = new TableView();
        TableColumn<FileInfo, ImageView> imageColumn = new TableColumn<>("images");
        TableColumn<FileInfo, String> dateColumn = new TableColumn<>("date");
        TableColumn<FileInfo, String> nameColumn = new TableColumn<>("name");
        TableColumn<FileInfo, String> sizeColumn = new TableColumn<>("size");

        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        table.setStyle("-fx-background-color: transparent; -fx-border-width: 2px; -fx-border-color: #9de0ad");


        table.getColumns().addAll(imageColumn, nameColumn, sizeColumn, dateColumn);
        table.setItems(list);


        table.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                FileInfo selectedItem = table.getSelectionModel().getSelectedItem();
                if (selectedItem.getFile().isDirectory()) {
                    table.setItems(getListFileInfo(selectedItem.getFile()));
                } else {
                    new Thread(() -> {
                        try {
                            Desktop.getDesktop().open(selectedItem.getFile());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        });

        MenuItem mi1 = new MenuItem("add");
        TextField tf = new TextField();
        mi1.setGraphic(tf);
        mi1.setOnAction((ActionEvent event) -> {
            File file = table.getSelectionModel().getSelectedItem().getFile();
            if (file != null && file.isDirectory()) {
                new Thread(() -> {
                    boolean success = false;
                    String path = file.getAbsolutePath() + File.separator + tf.getText();
                    System.out.println("path: " + path);
                    File newFile = new File(path);
                    try {
                        if (Utils.getFileExtension(tf.getText()).equals("")) {
                            success = newFile.mkdir();
                        } else {
                            success = newFile.createNewFile();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("newFile = " + success);
                }).start();
            }
        });

        MenuItem mi2 = new MenuItem("open");
        mi2.setStyle("-fx-text-alignment: center");
        mi2.setOnAction((ActionEvent event) -> {
            File file = table.getSelectionModel().getSelectedItem().getFile();
            if (file.exists() && !file.isDirectory()) {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });

        MenuItem mi3 = new MenuItem("delete");
        mi3.setOnAction((ActionEvent event) -> {
            File file = table.getSelectionModel().getSelectedItem().getFile();
            File parent = file.getParentFile();
            if (file.exists()) {
                new Thread(() -> {
                    boolean success;
                    success = file.delete();
                    System.out.println("delete = " + success);
                    table.setItems(getListFileInfo(parent));
                }).start();
            }
        });

        ContextMenu menu = new ContextMenu();
        menu.setStyle("-fx-background-color: #9de0ad");
        menu.getItems().addAll(mi1, mi2, mi3);
        table.setContextMenu(menu);

        return table;
    }

    public ObservableList<FileInfo> getListFileInfo(File file) {
        File[] files = file.listFiles();
        ObservableList<FileInfo> list = FXCollections.emptyObservableList();

        if (files != null) {
            FileInfo[] st = new FileInfo[files.length];
            for (int i = 0; i < files.length; i++) {
                String s1 = null;
                String s2 = null;
                String s3 = null;
                ImageView img = null;
                try {
                    BasicFileAttributes attrs = Files.readAttributes(files[i].toPath(),
                            BasicFileAttributes.class);
                    img = new ImageView(Utils.getFileIcons(files[i].getName()));
                    s1 = files[i].getName();
                    s2 = Utils.calculateSize(attrs.size());
                    s3 = Utils.formatDateTime(attrs.lastModifiedTime());
                } catch (Exception x) {
                    System.out.println("Exception detected in tableview strings: " + x.getMessage());
                }
                st[i] = new FileInfo(files[i], img, s1, s2, s3);
            }
            list = FXCollections.observableArrayList(st);
        }
        return list;
    }

}
