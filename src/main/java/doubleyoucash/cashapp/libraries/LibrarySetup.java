package doubleyoucash.cashapp.libraries;

import com.fasterxml.jackson.databind.ObjectMapper;
import doubleyoucash.cashapp.CashApp;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;

import java.io.*;
import java.util.Collections;
import java.util.List;

public class LibrarySetup implements AbstractLibraryLoader<Library, BukkitLibraryManager> {

    private final BukkitLibraryManager bukkitLibraryManager = new BukkitLibraryManager(CashApp.getPlugin(CashApp.class));
    private final CashApp ca = CashApp.getPlugin();

    @Override
    public List<Library> initLibraries() {

        List<Library> list = new java.util.ArrayList<>(Collections.emptyList());

        try {
            File jsonFile = getAzimFile();
            ObjectMapper objectMapper = new ObjectMapper();

            for (LibraryObject libraryObject : objectMapper.readValue(jsonFile, LibraryObject[].class)) {
                list.add(createLibrary(libraryObject));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;

       /* return Arrays.asList(
                createLibrary(LibraryConfig.JAVACORD_API),
                createLibrary(LibraryConfig.JAVACORD_CORE)
        );*/
    }

    @Override
    public void loadLibraries() {
        bukkitLibraryManager.addMavenCentral();
        initLibraries().forEach(bukkitLibraryManager::loadLibrary);
        /*bukkitLibraryManager.fromGeneratedResource(ca.getResource("AzimDP.json")).forEach(library->{
            try {
                bukkitLibraryManager.loadLibrary(library);
            }catch(RuntimeException e) { // in case some of the libraries cant be found or don't have .jar file or etc
                ca.log("Skipping download of\""+library+"\", it either doesnt exist or has no .jar file");
            }
        });*/
    }

    @Override
    public BukkitLibraryManager getLibraryManager() {
        return bukkitLibraryManager;
    }

    public Library createLibrary(LibraryObject libraryObject) {
        return Library.builder().groupId(libraryObject.groupId()).artifactId(libraryObject.artifactId()).version(libraryObject.version()).relocate(libraryObject.oldRelocation(), libraryObject.newRelocation()).build();
    }

    private File getAzimFile() throws IOException {
        InputStream inputStream = ca.getResource("AzimDP.json");

        // Create a temporary file
        File tempFile = File.createTempFile("temp", ".tmp");

        // Write the content of the InputStream to the temporary file
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return tempFile;

    }

}
