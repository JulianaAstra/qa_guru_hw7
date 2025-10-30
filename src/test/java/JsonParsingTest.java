import java.io.Reader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.InputStreamReader;
import org.junit.jupiter.api.Test;
import model.Product;
import model.ProductsList;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParsingTest {

    private ClassLoader cl = ZipParsingTests.class.getClassLoader();
    private final String JSON_FILE = "products_list.json";
    private final Integer PRODUCTS_COUNT = 3;
    private final String SHOP_NAME = "Roses Store";
    private final Integer ID = 3;
    private final String NAME = "Malwern Hills";
    private final String COLOR = "yellow";
    private final Integer AMOUNT = 1;
    private final double PRICE = 480;
    
    @Test
    void jsonFileParsingTest() throws Exception {
        try (Reader reader = new InputStreamReader(
            cl.getResourceAsStream(JSON_FILE)
        )) {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductsList productsList = objectMapper.readValue(reader, ProductsList.class);
            assertEquals(SHOP_NAME, productsList.getShopName());
            assertEquals(PRODUCTS_COUNT, productsList.getProducts().size());

            Product malvernHillsRose = productsList.getProducts().get(2);
            assertEquals(ID, malvernHillsRose.getProductId());
            assertEquals(NAME, malvernHillsRose.getProductName());
            assertEquals(COLOR, malvernHillsRose.getProductColor());
            assertEquals(AMOUNT, malvernHillsRose.getProductAmount());
            assertEquals(PRICE, malvernHillsRose.getProductPrice());
        }
    }
}
