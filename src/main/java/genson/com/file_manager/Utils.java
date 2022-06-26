package genson.com.file_manager;

import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import javafx.embed.swing.SwingFXUtils;


public class Utils {
    public static HashMap<String, Image> icons = new HashMap<>();

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");


    static HashMap<String, Image> mapOfFileExtToSmallIcon = new HashMap<>();

    public static String getFileExtension(String fname) {
        String ext = "";
        String[] el = fname.split("\\.");
        if (el.length >= 2) {
            ext = el[el.length - 1];
        }
        return ext.toLowerCase();
    }

    private static String getFileExt(String fname) {
        String ext = ".";
        int p = fname.lastIndexOf('.');
        if (p >= 0) {
            ext = fname.substring(p);
        }
        return ext.toLowerCase();
    }

    // using icon of operating system
    private static javax.swing.Icon getJSwingIconFromFileSystem(File file) {

        // Windows {
//        FileSystemView view = FileSystemView.getFileSystemView();
//        javax.swing.Icon icon = view.getSystemIcon(file);
        // }

        // OS X {
        final javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        // }

        return fc.getUI().getFileView(fc).getIcon(file);
    }

    // get icon of resource
    public static Image getFileIcons(String fname) {
        final String ext = getFileExtension(fname);
        Image icon = icons.get(ext);
        if (icon != null && !ext.equals("")) {
            return icon;
        } else if (!ext.equals("")) {
            return icons.get("file");
        }
        return icons.get("folder");
    }

    // get icon of operating system
    public static Image getFileIcon(String fname) {
        final String ext = getFileExt(fname);

        Image fileIcon = mapOfFileExtToSmallIcon.get(ext);
        if (fileIcon == null) {

            javax.swing.Icon jswingIcon = null;

            File file = new File(fname);
            if (file.exists()) {
                jswingIcon = getJSwingIconFromFileSystem(file);
            } else {
                File tempFile = null;
                try {
                    tempFile = File.createTempFile("icon", ext);
                    jswingIcon = getJSwingIconFromFileSystem(tempFile);
                } catch (IOException ignored) {
                    // Cannot create temporary file.
                } finally {
                    if (tempFile != null) tempFile.delete();
                }
            }

            if (jswingIcon != null) {
                fileIcon = jswingIconToImage(jswingIcon);
                mapOfFileExtToSmallIcon.put(ext, fileIcon);
            }
        }

        return fileIcon;
    }

    private static Image jswingIconToImage(javax.swing.Icon jswingIcon) {
        BufferedImage bufferedImage = new BufferedImage(jswingIcon.getIconWidth(), jswingIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        jswingIcon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }


    public static String calculateSize(long bytes) {

        long kilobytes = (bytes / 1024);
        long megabytes = (kilobytes / 1024);
        long gigabytes = (megabytes / 1024);

        if (gigabytes >= 1) {
            return String.format("%,d GB", gigabytes);
        } else if (megabytes >= 1) {
            return String.format("%,d MB", megabytes);
        } else if (kilobytes >= 1) {
            return String.format("%,d KB", kilobytes);
        } else {
            return String.format("%,d Bytes", bytes);
        }
    }

    public static String formatDateTime(FileTime fileTime) {

        LocalDateTime localDateTime = fileTime
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return localDateTime.format(DATE_FORMATTER);
    }

    public static void loadIcons() {
        try {
            URI uri = App.class.getResource("/icons").toURI();
            Path dirPath = Paths.get(uri);
            Files.list(dirPath)
                    .forEach(p -> {
                        String fileName = p.getFileName().toString().split("\\.")[0];
                        Image img = new Image("file:" + p, 20, 20, false, false);
                        icons.put(fileName, img);
                    });
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}
