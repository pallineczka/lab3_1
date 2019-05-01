package pl.com.bottega.ecommerce.sales.domain.invoicing;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestBookKeeper {

    @Test
    void testOneElementInvoice(){
        Id id = Id.generate();
        ClientData clientData = new ClientData(id, "Client");
        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);
        InvoiceFactory invoiceFactory = new InvoiceFactory();
        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);

        TaxPolicy tax = mock(TaxPolicy.class);
        ProductData productData = mock(ProductData.class);

        when(tax.calculateTax(ProductType.STANDARD, new Money(10))).thenReturn(new Tax(new Money(10), "5%"));
        when(productData.getType()).thenReturn(ProductType.STANDARD);

        RequestItem requestItem = new RequestItem(productData, 5, new Money(10));
        invoiceRequest.add(requestItem);

        Invoice invoiceResult = bookKeeper.issuance(invoiceRequest, tax);

        assertThat(invoiceResult.getItems().size(), Matchers.equalTo(1));
    }
}
