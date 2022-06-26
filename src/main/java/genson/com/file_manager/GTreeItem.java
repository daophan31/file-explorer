package genson.com.file_manager;

import java.io.File;

public class GTreeItem {
    private File file;

    public GTreeItem(File file) {
        this.file = file;
    }

    public String toString() {
        return file.getName();
    }

    public File getFile() {
        return file;
    }
}
