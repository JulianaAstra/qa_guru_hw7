import java.io.Reader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;

import model.Product;
import model.ProductsList;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParsingTest {

    private ClassLoader cl = ZipParsingTests.class.getClassLoader();
    
    @Test
    void jsonFileParsingTest() throws Exception {
        try (Reader reader = new InputStreamReader(
            cl.getResourceAsStream("products_list.json")
        )) {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductsList productsList = objectMapper.readValue(reader, ProductsList.class);
            assertEquals("Roses Store", productsList.getShopName());
            assertEquals(3, productsList.getProducts().size());

            Product malvernHillsRose =  

            for(Product product: productsList.getProducts()) {
                assertEquals(234234, product.getProductId());
                assertEquals("SGML", actual.getGlossary().getSortAs());
                assertEquals("Standard Generalized Markup Language", actual.getGlossary().getGlossTerm());
            }
        }
    }

}
