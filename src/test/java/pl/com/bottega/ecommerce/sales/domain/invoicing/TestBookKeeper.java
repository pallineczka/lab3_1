package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.Test;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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

        when(tax.calculateTax(ProductType.STANDARD, new Money(5))).thenReturn(new Tax(new Money(5), "23%"));
        when(productData.getType()).thenReturn(ProductType.STANDARD);

        RequestItem requestItem = new RequestItem(productData, 2, new Money(5));
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, tax);

        assertThat(invoice.getItems().size(), is(1));
    }
}
