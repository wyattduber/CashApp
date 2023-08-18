package doubleyoucash.cashapp.libraries;

public class LibraryConfig {

    /*
        <groupId>org.javacord</groupId>
        <artifactId>javacord</artifactId>
        <version>3.8.0</version>
        <scope>provided</scope>
        <type>pom</type>
     */
    /*public static LibraryObject JAVACORD = new LibraryObject("org.javacord", "javacord", "3.8.0", "javacord_api",
            "org.javacord", "libs.org.javacord");*/

    public static LibraryObject JAVACORD_API = new LibraryObject("org.javacord", "javacord-api", "3.8.0", "javacord_api",
            "org.javacord-api", "libs.org.javacord-api");

    public static LibraryObject JAVACORD_CORE = new LibraryObject("org.javacord", "javacord-core", "3.8.0", "javacord_core",
            "org.javacord-core", "libs.org.javacord-core");

}