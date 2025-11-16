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
import static org.junit.jupiter.api.Assertions.*;

public class ZipParsingTests {
    private ClassLoader cl = ZipParsingTests.class.getClassLoader();

    final String THREE_ZIP_FILE = "three_files.zip";

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

        try (ZipInputStream zis = new ZipInputStream(cl.getResourceAsStream(PDF_ZIP_FILE))) {
            ZipEntry entry;
            boolean archiveHasFiles = false;

            while ((entry = zis.getNextEntry()) != null) {
                archiveHasFiles = true;
                checkPdf(PDF_FILE_NAME, zis, entry, "Тест файла pdf");
            }
            assertTrue(archiveHasFiles, "Archive is empty!");
        }
    }

    @Test
    @DisplayName("Zip Архив содержит файл xlsx")
    void zipWithXlsxParsingTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(cl.getResourceAsStream(XLSX_ZIP_FILE))) {
            ZipEntry entry;
            boolean archiveHasFiles = false;

            while ((entry = zis.getNextEntry()) != null) {
                archiveHasFiles = true;
                checkXlsx(XLSX_FILE_NAME, zis, entry,"test_mail@ya.ru");
            }
            assertTrue(archiveHasFiles, "Archive is empty!");
        }
    }

    @Test
    @DisplayName("Zip Архив содержит файл csv")
    void zipWithCsvxParsingTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(cl.getResourceAsStream(CSV_ZIP_FILE))) {
            ZipEntry entry;
            boolean archiveHasFiles = false;

            while ((entry = zis.getNextEntry()) != null) {
                archiveHasFiles = true;
                checkCsv(CSV_FILE_NAME, zis, entry, FIRST_ROW, SECOND_ROW);
            }
            assertTrue(archiveHasFiles, "Archive is empty!");
        }
    }

    @Test
    @DisplayName("Zip Архив содержит три файла pdf, xlsx, csv")
    void zipWitThreeFilesParsingTest() throws Exception {
        boolean pdfFound = false;
        boolean csvFound = false;
        boolean xlsxFound = false;

        try (ZipInputStream zis = new ZipInputStream(cl.getResourceAsStream(THREE_ZIP_FILE))) {
            ZipEntry entry;
            boolean archiveHasFiles = false;

            while ((entry = zis.getNextEntry()) != null) {
                archiveHasFiles = true;
                String fileName = entry.getName();

                switch (fileName) {
                    case PDF_FILE_NAME:
                        pdfFound = true;
                        checkPdf(PDF_FILE_NAME, zis, entry, "Тест файла pdf");
                        break;
                    case CSV_FILE_NAME:
                        csvFound = true;
                        checkCsv(CSV_FILE_NAME, zis, entry, FIRST_ROW, SECOND_ROW);
                        break;
                    case XLSX_FILE_NAME:
                        xlsxFound = true;
                        checkXlsx(XLSX_FILE_NAME, zis, entry,"test_mail@ya.ru");
                        break;
                    default:
                        fail("Найден неожиданный файл в архиве: " + fileName);
                }
            }
            assertTrue(archiveHasFiles, "Archive is empty!");
            assertTrue(pdfFound, "PDF файл не найден в архиве: " + PDF_FILE_NAME);
            assertTrue(csvFound, "CSV файл не найден в архиве: " + CSV_FILE_NAME);
            assertTrue(xlsxFound, "XLSX файл не найден в архиве: " + XLSX_FILE_NAME);
        }
    }

    void checkCsv(String fileName, ZipInputStream zis, ZipEntry entry, String[] firstRowData, String[] secondRowData) throws Exception{
        byte[] csvContent = getZipContent(fileName, zis, entry);

        try (InputStream csvStream = new ByteArrayInputStream(csvContent);
             CSVReader csvReader = new CSVReader(new InputStreamReader(csvStream))) {
            List<String[]> data = csvReader.readAll();
            checkCSVData(data, 2, firstRowData, secondRowData);
        }
    }

    void checkXlsx(String fileName, ZipInputStream zis, ZipEntry entry, String checkData) throws Exception {
        byte[] xlsxContent = getZipContent(fileName, zis, entry);

        try (InputStream xlsxStream = new ByteArrayInputStream(xlsxContent)) {
            XLS xls = new XLS(xlsxStream);
            checkXLSXData(xls, checkData);
        }
    }

    void checkPdf(String fileName, ZipInputStream zis, ZipEntry entry, String expectedText) throws Exception {
        byte[] pdfContent = getZipContent(fileName, zis, entry);

        try (InputStream pdfStream = new ByteArrayInputStream(pdfContent)) {
            PDF pdf = new PDF(pdfStream);
            checkPDFData(pdf, expectedText);
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

    void checkPDFData(PDF pdf, String expectedText) {
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
