package genson.com.file_manager;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.ImageView;

import java.io.File;


public class FileInfo {
    private File file;
    private ImageView image;
    private SimpleStringProperty name;
    private SimpleStringProperty size;
    private SimpleStringProperty date;

    public FileInfo(File file, ImageView image, String name, String size, String date){
        this.file = file;
        this.image = image;
        this.name = new SimpleStringProperty(name);
        this.size = new SimpleStringProperty(size);
        this.date = new SimpleStringProperty(date);
    }

    public String getDate(){ return date.get();}
    public String getSize(){return size.get();}
    public String getName(){return name.get();}
    public void setImage(ImageView value) {image = value;}
    public ImageView getImage() {return image;}

    public File getFile() {
        return file;
    }
}
