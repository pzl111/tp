package seedu.address.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlySchedule;
import seedu.address.model.ScheduleList;
import seedu.address.testutil.TypicalLessons;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalLessons.getTypicalScheduleList;

public class JsonScheduleListStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonScheduleListStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readScheduleList_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readScheduleList(null));
    }

    private java.util.Optional<ReadOnlySchedule> readScheduleList(String filePath) throws Exception {
        return new JsonScheduleListStorage(Paths.get(filePath)).readScheduleList(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readScheduleList("notJsonFormatScheduleList.json"));
    }
    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readScheduleList("NonExistentFile.json").isPresent());
    }

    @Test
    public void readScheduleList_invalidScheduleList_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readScheduleList("invalidScheduleList.json"));
    }

    @Test
    public void readAndSaveScheduleList_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempScheduleList.json");
        ScheduleList original = getTypicalScheduleList();
        JsonScheduleListStorage jsonScheduleListStorage = new JsonScheduleListStorage(filePath);

        // save in new file and read back
        jsonScheduleListStorage.saveScheduleList(original, filePath);
        ReadOnlySchedule readBack = jsonScheduleListStorage.readScheduleList(filePath).get();
        assertEquals(original, new ScheduleList(readBack));

        original.addLesson(TypicalLessons.getSample1());
        // remove the second lesson
        original.removeLesson(original.getLessonList().get(1));
        jsonScheduleListStorage.saveScheduleList(original, filePath);
        readBack = jsonScheduleListStorage.readScheduleList(filePath).get();
        assertEquals(original, new ScheduleList(readBack));
    }


}
