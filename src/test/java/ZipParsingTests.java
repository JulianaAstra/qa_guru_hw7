import com.codeborne.pdftest.PDF;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZipParsingTests {
    private ClassLoader cs = ZipParsingTests.class.getClassLoader();

    @Test
    @DisplayName("Архив содержит файл pdf")
    void zipWithPDFParsingTest() throws Exception {
        final String PDF_ZIP_FILE = "file_pdf.zip";
        final String PDF_FILE_NAME = "file_pdf.pdf";

        try (ZipInputStream zis = new ZipInputStream(
            cs.getResourceAsStream(PDF_ZIP_FILE)
        )) {
            ZipEntry entry;
            boolean archiveHasFiles = false;

            while ((entry = zis.getNextEntry()) != null) {
                archiveHasFiles = true;
                byte[] pdfContent = zis.readAllBytes();

                assertEquals(PDF_FILE_NAME, entry.getName());
                assertThat(pdfContent.length).isGreaterThan(0);
                assertThat(pdfContent).startsWith("%PDF".getBytes());

                try (InputStream pdfStream = new ByteArrayInputStream(pdfContent)) {
                    PDF pdf = new PDF(pdfStream);
                    assertThat(pdf.text).contains("Тест файла pdf");
                    assertThat(pdf.numberOfPages).isGreaterThan(0);
                }
            }
            Assertions.assertTrue(archiveHasFiles, "Archive is empty!");
        }
    }
}
