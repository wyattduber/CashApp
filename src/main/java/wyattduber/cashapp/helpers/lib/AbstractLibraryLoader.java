package wyattduber.cashapp.helpers.lib;

import java.util.List;

public interface AbstractLibraryLoader<Library> {

    List<Library> initLibraries();

    void loadLibraries();

}
