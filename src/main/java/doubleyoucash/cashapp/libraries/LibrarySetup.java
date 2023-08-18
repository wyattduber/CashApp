package doubleyoucash.cashapp.libraries;

import doubleyoucash.cashapp.CashApp;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;

import java.util.Collections;
import java.util.List;

public class LibrarySetup implements AbstractLibraryLoader<Library, BukkitLibraryManager> {

    private final BukkitLibraryManager bukkitLibraryManager = new BukkitLibraryManager(CashApp.getPlugin(CashApp.class));

    @Override
    public List<Library> initLibraries() {
        return Collections.singletonList(
                createLibrary(LibraryConfig.JAVACORD));
    }

    @Override
    public void loadLibraries() {
        bukkitLibraryManager.addMavenCentral();
        initLibraries().forEach(bukkitLibraryManager::loadLibrary);
    }

    @Override
    public BukkitLibraryManager getLibraryManager() {
        return bukkitLibraryManager;
    }

    public Library createLibrary(LibraryObject libraryObject) {
        return Library.builder().groupId(libraryObject.groupID()).artifactId(libraryObject.artifactID()).version(libraryObject.version()).relocate(libraryObject.oldRelocation(), libraryObject.newRelocation()).checksum("Base64-encoded SHA-256 checksum").build();
    }

}
