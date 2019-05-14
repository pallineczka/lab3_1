package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.mockito.Mockito.*;

public class TestBookKeeper {

    private Id id;
    private ClientData clientData;
    private InvoiceRequest invoiceRequest;
    private InvoiceFactory invoiceFactory;
    private BookKeeper bookKeeper;
    private Money money ;
    private TaxPolicy tax;
    private ProductData productData;
    private RequestItem requestItem;

    @Before
    public void before(){

        id = Id.generate();
        clientData = new ClientData(id, "Client");
        invoiceRequest = new InvoiceRequest(clientData);
        invoiceFactory = new InvoiceFactory();
        bookKeeper = new BookKeeper(invoiceFactory);
        money = new Money(5);

        tax = mock(TaxPolicy.class);
        productData = mock(ProductData.class);

        when(tax.calculateTax(ProductType.STANDARD, money)).thenReturn(new Tax(money, "23%"));
        when(productData.getType()).thenReturn(ProductType.STANDARD);

        requestItem = new RequestItem(productData, 2, money);
    }


    @Test
    void testOneElementInvoice(){
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, tax);

        Assert.assertEquals(invoice.getItems().size(), 1);
    }

    @Test
    void testTwoElementInvoiceCalledCalculateTaxTwoTimes(){
        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem);

        bookKeeper.issuance(invoiceRequest, tax);

        verify(tax, times(2)).calculateTax(ProductType.STANDARD, money);
    }

    @Test
    void testZeroElementInvoice(){
        Invoice invoice = bookKeeper.issuance(invoiceRequest, tax);

        Assert.assertEquals(invoice.getItems().size(), 0);
    }

    @Test
    void testZeroElementInvoiceNeverCalledCalculateTax(){
        bookKeeper.issuance(invoiceRequest, tax);

        verify(tax, times(0)).calculateTax(any(), any());
    }

    @Test
    void testOneElementInvoiceWithProductType(){
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, tax);

        Assert.assertEquals(invoice.getItems().get(0).getProduct().getType(), ProductType.STANDARD);
    }

    @Test
    void testTwoElementInvoiceWithProductType(){
        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem);

        bookKeeper.issuance(invoiceRequest, tax);

        verify(productData, times(2)).getType();
    }
}
