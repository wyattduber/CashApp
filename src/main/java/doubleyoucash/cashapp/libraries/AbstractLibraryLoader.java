package doubleyoucash.cashapp.libraries;

import java.util.List;

public interface AbstractLibraryLoader<Library, LibraryManager> {

    public abstract List<Library> initLibraries();

    public abstract void loadLibraries();

    public abstract LibraryManager getLibraryManager();

}
