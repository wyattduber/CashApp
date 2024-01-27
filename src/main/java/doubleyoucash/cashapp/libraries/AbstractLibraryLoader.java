package doubleyoucash.cashapp.libraries;

import java.util.List;

public interface AbstractLibraryLoader<Library> {

    List<Library> initLibraries();

    void loadLibraries();

}
