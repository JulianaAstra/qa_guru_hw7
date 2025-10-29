import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
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
    @DisplayName("Zip Архив содержит файл pdf")
    void zipWithPdfParsingTest() throws Exception {
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

    @Test
    @DisplayName("Zip Архив содержит файл xlsx")
    void zipWithXlsxParsingTest() throws Exception {
        final String XLSX_ZIP_FILE = "file_xlsx.zip";
        final String XLSX_FILE_NAME = "file_xlsx.xlsx";

        try (ZipInputStream zis = new ZipInputStream(
                cs.getResourceAsStream(XLSX_ZIP_FILE)
        )) {
            ZipEntry entry;
            boolean archiveHasFiles = false;

            while ((entry = zis.getNextEntry()) != null) {
                archiveHasFiles = true;
                byte[] xlsxContent = zis.readAllBytes();
                assertEquals(XLSX_FILE_NAME, entry.getName());
                assertThat(xlsxContent.length).isGreaterThan(0);

                try (InputStream xlsxStream = new ByteArrayInputStream(xlsxContent)) {
                    XLS xls = new XLS(xlsxStream);

                    assertThat(xls.excel.getNumberOfSheets()).isGreaterThan(0);
                    assertThat(xls.excel.getSheetAt(0).getLastRowNum()).isGreaterThanOrEqualTo(1);
                    assertEquals("test_mail@ya.ru", xls.excel.getSheetAt(0).getRow(1).getCell(0).getStringCellValue());
                }
            }
            Assertions.assertTrue(archiveHasFiles, "Archive is empty!");
        }
    }
}
