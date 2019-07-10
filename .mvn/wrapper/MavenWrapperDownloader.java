import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Properties;

public class MavenWrapperDownloader {

    /**
     * Default URL to download the maven-wrapper.jar from, if no 'downloadUrl' is provided.
     */
    private static final String DEFAULT_DOWNLOAD_URL =
            "https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/0.4.2/maven-wrapper-0.4.2.jar";

    /**
     * Path to the maven-wrapper.properties file, which might contain a downloadUrl property to
     * use instead of the default one.
     */
    private static final String MAVEN_WRAPPER_PROPERTIES_PATH =
            ".mvn/wrapper/maven-wrapper.properties";

    /**
     * Path where the maven-wrapper.jar will be saved to.
     */
    private static final String MAVEN_WRAPPER_JAR_PATH =
            ".mvn/wrapper/maven-wrapper.jar";

    /**
     * Name of the property which should be used to override the default download url for the wrapper.
     */
    private static final String PROPERTY_NAME_WRAPPER_URL = "wrapperUrl";

    public static void main(String args[]) {
        System.out.println("- Downloader started");
        if (args.length == 0 || args[0] == null || args[0].isEmpty()) {
            System.err.println("- ERROR: Base directory argument is missing or empty.");
            System.exit(1);
        }

        File baseDirectory = new File(args[0]);
        System.out.println("- Using base directory: " + baseDirectory.getAbsolutePath());

        // Ensure base directory exists and is a directory
        if (!baseDirectory.exists() || !baseDirectory.isDirectory()) {
            System.err.println("- ERROR: Base directory does not exist or is not a directory: " + baseDirectory.getAbsolutePath());
            System.exit(1);
        }

        // If the maven-wrapper.properties exists, read it and check if it contains a custom
        // wrapperUrl parameter.
        File mavenWrapperPropertyFile = new File(baseDirectory, MAVEN_WRAPPER_PROPERTIES_PATH);
        String url = DEFAULT_DOWNLOAD_URL;
        if (mavenWrapperPropertyFile.exists()) {
            try (FileInputStream mavenWrapperPropertyFileInputStream = new FileInputStream(mavenWrapperPropertyFile)) {
                Properties mavenWrapperProperties = new Properties();
                mavenWrapperProperties.load(mavenWrapperPropertyFileInputStream);
                url = mavenWrapperProperties.getProperty(PROPERTY_NAME_WRAPPER_URL, url);
            } catch (IOException e) {
                System.err.println("- ERROR loading '" + MAVEN_WRAPPER_PROPERTIES_PATH + "': " + e.getMessage());
                // Continue with default URL if properties file cannot be loaded
            }
        }
        System.out.println("- Downloading from: " + url);

        File outputFile = new File(baseDirectory, MAVEN_WRAPPER_JAR_PATH); // Use baseDirectory directly
        File outputDirectory = outputFile.getParentFile();

        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                System.err.println(
                        "- ERROR creating output directory '" + outputDirectory.getAbsolutePath() + "'");
                System.exit(1); // Exit if directory creation fails
            }
        }
        System.out.println("- Downloading to: " + outputFile.getAbsolutePath());
        try {
            downloadFileFromURL(url, outputFile);
            System.out.println("Done");
            System.exit(0);
        } catch (Throwable e) {
            System.err.println("- Error downloading: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void downloadFileFromURL(String urlString, File destination) throws IOException {
        URL website = new URL(urlString);
        try (ReadableByteChannel rbc = Channels.newChannel(website.openStream());
             FileOutputStream fos = new FileOutputStream(destination)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }
}