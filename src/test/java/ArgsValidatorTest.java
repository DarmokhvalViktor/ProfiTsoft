import com.darmokhval.utility.ArgsValidator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ArgsValidatorTest {
    private final static String directoryPath = "src" + File.separator + "main" + File.separator;
    private final static String emptyDir = "src" + File.separator + "test" + File.separator + "empty" + File.separator;

    @BeforeEach
    void setUp() {
        GenerateFilesForTestCases generator = new GenerateFilesForTestCases();
        generator.createDirectory(emptyDir);
    }

    @AfterAll
    static void removeDirs() {
        if(new File(emptyDir).delete()) {
            System.out.println("Deleted empty directory");
        }
    }

    @Test
    void testValidArguments() {
        ArgsValidator argsValidator = new ArgsValidator();
        assertDoesNotThrow(() -> argsValidator.checkForCorrectArgs(directoryPath + "resources", "brand"));
    }

    @Test
    void testInvalidNumberOfArguments() {
        ArgsValidator argsValidator = new ArgsValidator();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> argsValidator.checkForCorrectArgs(directoryPath + "resources"));
        assertEquals("Number of arguments are not supported", exception.getMessage());
    }

    @Test
    void testInvalidDirectoryPath() {
        ArgsValidator argsValidator = new ArgsValidator();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> argsValidator.checkForCorrectArgs("nonexistent" + File.separator + "directory" + File.separator + "path", "brand"));
        assertEquals("Invalid directory path.", exception.getMessage());
    }

    @Test
    void testEmptyDirectory() {
        ArgsValidator argsValidator = new ArgsValidator();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> argsValidator.checkForCorrectArgs(emptyDir, "brand"));
        assertEquals("There are no files inside of a specified directory", exception.getMessage());
    }

    @Test
    void testBlankFieldAttribute() {
        ArgsValidator argsValidator = new ArgsValidator();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> argsValidator.checkForCorrectArgs(directoryPath + "resources", ""));
        assertEquals("Field attribute can't be blank", exception.getMessage());
    }
    @Test
    void testUnsupportedFieldAttribute() {
        ArgsValidator argsValidator = new ArgsValidator();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> argsValidator.checkForCorrectArgs(directoryPath + "resources", "color"));
        assertEquals("Field attribute not supported", exception.getMessage());
    }
}
