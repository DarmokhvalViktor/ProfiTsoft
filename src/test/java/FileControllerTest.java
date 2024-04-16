import com.darmokhval.controller.FileController;
import com.darmokhval.utility.ArgsValidator;
import com.darmokhval.utility.FileParser;
import com.darmokhval.utility.FileWriter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

/**
 * Testing interaction between dependencies of FileController.
 */
class FileControllerTest {
    private static final String emptyDirectory = "src" + File.separator + "test" + File.separator + "empty";
    private static final String notEmptyDirectory = "src" + File.separator + "test" + File.separator + "not_empty";

    @Mock
    private FileParser fileParserMock;

    @Mock
    private FileWriter fileWriterMock;

    @Mock
    private ArgsValidator argsValidatorMock;
    @Mock
    private ExecutorService executorServiceMock;
    private FileController fileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileController = new FileController(fileParserMock, fileWriterMock, argsValidatorMock);

        GenerateFilesForTestCases generator = new GenerateFilesForTestCases(new File(emptyDirectory), new File(notEmptyDirectory));
        try {
            generator.generateSpecificAmountOfFiles();
            System.out.println("Files generated successfully.");
        } catch (IOException e) {
            System.out.println("Error generating files: " + e.getMessage());
        }
    }

    @AfterAll
    static void deleteAllFiles() {
        File[] files = new File(notEmptyDirectory).listFiles();
        if (files != null) {
            for (File file : files) {
                boolean deleted = file.delete();
                if (deleted) {
                    System.out.println("deleted file: " + file.getName());
                } else {
                    System.out.println("Failed to delete file: " + file.getName());
                }
            }
        }
        if (new File(emptyDirectory).delete()) {
            System.out.println("Deleted empty directory");
        }
        if (new File(notEmptyDirectory).delete()) {
            System.out.println("Deleted not empty directory");
        }
    }

    /**
     * test if methods are called in controller.
     */
    @Test
    public void testFileControllerArgsValidatorCall() {
        String[] validArgs = {notEmptyDirectory, "brand"};
        Map<String, Integer> countFieldMap = new HashMap<>();

        doNothing().when(fileWriterMock).generateXML(any(), any());

        fileController.parse(validArgs);

//        parseFile invoked only once, because only 1 file in dir.
        verify(argsValidatorMock, times(1)).checkForCorrectArgs(validArgs);
        verify(fileParserMock, times(1)).parseFile(any(), any(), any());
        verify(fileWriterMock, times(1)).generateXML(countFieldMap, validArgs[1]);
    }

    @Test
    void testExecutorServiceTerminatesWithinTimeout() throws InterruptedException {
        when(executorServiceMock.awaitTermination(anyLong(), any(TimeUnit.class))).thenReturn(true);
        fileController.parse(notEmptyDirectory, "output.xml");
        verifyNoInteractions(executorServiceMock);
    }

    @Test
    void testParseFilesExecutorServiceDoesNotTerminateWithinTimeout() throws InterruptedException {
        when(executorServiceMock.awaitTermination(anyLong(), any(TimeUnit.class))).thenReturn(false);
        fileController.parse(notEmptyDirectory, "output.xml");
        verify(fileParserMock).parseFile(any(), anyString(), any());
    }
}
