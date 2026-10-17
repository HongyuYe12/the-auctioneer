package util;

import java.net.URL;

import javafx.scene.image.Image;

/** Loads packaged application resources from the classpath. */
public final class ResourceUtil {

    private static final String IMAGE_ROOT = "/image/";
    private static final String PLACEHOLDER_IMAGE = "未解锁问号.png";

    private ResourceUtil() {
    }

    public static Image loadImage(String fileName) {
        URL url = ResourceUtil.class.getResource(IMAGE_ROOT + fileName);
        return url == null ? null : new Image(url.toExternalForm());
    }

    public static Image loadImageOrPlaceholder(String fileName) {
        Image image = loadImage(fileName);
        return image != null ? image : loadImage(PLACEHOLDER_IMAGE);
    }
}
