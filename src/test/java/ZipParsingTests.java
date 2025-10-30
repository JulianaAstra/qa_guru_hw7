import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ZipParsingTests {
    private ClassLoader cl = ZipParsingTests.class.getClassLoader();

    final String XLSX_ZIP_FILE = "file_xlsx.zip";
    final String XLSX_FILE_NAME = "file_xlsx.xlsx";
    final String PDF_ZIP_FILE = "file_pdf.zip";
    final String PDF_FILE_NAME = "file_pdf.pdf";
    final String CSV_ZIP_FILE = "file_csv.zip";
    final String CSV_FILE_NAME = "file_csv.csv";
    final String[] FIRST_ROW = new String[] {"email", "name"};
    final String[] SECOND_ROW = new String[] {"testemail@gmail.com", "kate"};


    @Test
    @DisplayName("Zip Архив содержит файл pdf")
    void zipWithPdfParsingTest() throws Exception {
        InputStream pdfResource = cl.getResourceAsStream(PDF_ZIP_FILE);
        assertNotNull(pdfResource, "Could not find resource: " + PDF_ZIP_FILE);
        try (ZipInputStream zis = new ZipInputStream(pdfResource)) {
            ZipEntry entry;
            boolean archiveHasFiles = false;

            while ((entry = zis.getNextEntry()) != null) {
                archiveHasFiles = true;
                byte[] pdfContent = getZipContent(PDF_FILE_NAME, zis, entry);

                try (InputStream pdfStream = new ByteArrayInputStream(pdfContent)) {
                    PDF pdf = new PDF(pdfStream);
                    checkPDFData(pdf, "Тест файла pdf", 1);
                }
            }
            assertTrue(archiveHasFiles, "Archive is empty!");
        }
    }

    @Test
    @DisplayName("Zip Архив содержит файл xlsx")
    void zipWithXlsxParsingTest() throws Exception {
        InputStream xlsxResource = cl.getResourceAsStream(XLSX_ZIP_FILE);
        assertNotNull(xlsxResource, "Could not find resource: " + XLSX_ZIP_FILE);
        try (ZipInputStream zis = new ZipInputStream(xlsxResource)) {
            ZipEntry entry;
            boolean archiveHasFiles = false;

            while ((entry = zis.getNextEntry()) != null) {
                archiveHasFiles = true;
                byte[] xlsxContent = getZipContent(XLSX_FILE_NAME, zis, entry);

                try (InputStream xlsxStream = new ByteArrayInputStream(xlsxContent)) {
                    XLS xls = new XLS(xlsxStream);
                    checkXLSXData(xls, "test_mail@ya.ru");       
                }
            }
            assertTrue(archiveHasFiles, "Archive is empty!");
        }
    }

    @Test
    @DisplayName("Zip Архив содержит файл csv")
    void zipWithCsvxParsingTest() throws Exception {
        InputStream csvResource = cl.getResourceAsStream(CSV_ZIP_FILE);
        assertNotNull(csvResource, "Could not find resource: " + CSV_ZIP_FILE);
        try (ZipInputStream zis = new ZipInputStream(csvResource)) {
            ZipEntry entry;
            boolean archiveHasFiles = false;

            while ((entry = zis.getNextEntry()) != null) {
                archiveHasFiles = true;
                byte[] csvContent = getZipContent(CSV_FILE_NAME, zis, entry);
                try (InputStream csvStream = new ByteArrayInputStream(csvContent);
                     CSVReader csvReader = new CSVReader(new InputStreamReader(csvStream))) {
                    List<String[]> data = csvReader.readAll();
                    checkCSVData(data, 2, FIRST_ROW, SECOND_ROW);
                }
            }
            assertTrue(archiveHasFiles, "Archive is empty!");
        }
    }

    void checkCSVData(List<String[]> data, int expectedSize, String[] firstRow, String[] secondRow) {
        assertThat(data).isNotEmpty();
        assertEquals(expectedSize, data.size());
        assertArrayEquals(firstRow, data.get(0));
        assertArrayEquals(secondRow, data.get(1));
    }

    void checkXLSXData(XLS xls, String expectedEmail) {
        assertThat(xls.excel.getNumberOfSheets()).isGreaterThan(0);
        assertThat(xls.excel.getSheetAt(0).getLastRowNum()).isGreaterThanOrEqualTo(1);
        assertEquals(expectedEmail, xls.excel.getSheetAt(0).getRow(1).getCell(0).getStringCellValue());
    }

    void checkPDFData(PDF pdf, String expectedText, int expectedPages) {
        assertThat(pdf.text).contains(expectedText);
        assertThat(pdf.numberOfPages).isGreaterThan(0);
    }

    byte[] getZipContent(String fileName, ZipInputStream zis, ZipEntry entry) throws Exception {
        byte[] content = zis.readAllBytes();
        assertEquals(fileName, entry.getName());
        assertThat(content.length).isGreaterThan(0);
        return content;
    }
}
