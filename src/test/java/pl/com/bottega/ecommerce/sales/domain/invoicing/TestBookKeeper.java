package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.mockito.Mockito.*;

public class TestBookKeeper {

    Id id = Id.generate();
    ClientData clientData = new ClientData(id, "Client");
    InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);
    InvoiceFactory invoiceFactory = new InvoiceFactory();
    BookKeeper bookKeeper = new BookKeeper(invoiceFactory);

    @Test
    void testOneElementInvoice(){
        TaxPolicy tax = mock(TaxPolicy.class);
        ProductData productData = mock(ProductData.class);

        when(tax.calculateTax(ProductType.STANDARD, new Money(5))).thenReturn(new Tax(new Money(5), "23%"));
        when(productData.getType()).thenReturn(ProductType.STANDARD);

        RequestItem requestItem = new RequestItem(productData, 2, new Money(5));
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, tax);

        Assert.assertEquals(invoice.getItems().size(), 1);
    }

    @Test
    void testTwoElementInvoiceCalledCalculateTaxTwoTimes(){
        TaxPolicy tax = mock(TaxPolicy.class);
        ProductData productData = mock(ProductData.class);

        when(tax.calculateTax(ProductType.STANDARD, new Money(5))).thenReturn(new Tax(new Money(5), "23%"));
        when(productData.getType()).thenReturn(ProductType.STANDARD);

        RequestItem requestItem = new RequestItem(productData, 2, new Money(5));
        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem);

        bookKeeper.issuance(invoiceRequest, tax);

        verify(tax, times(2)).calculateTax(ProductType.STANDARD, new Money(5));
    }

    @Test
    void testZeroElementInvoice(){
        TaxPolicy tax = mock(TaxPolicy.class);
        ProductData productData = mock(ProductData.class);

        when(tax.calculateTax(ProductType.STANDARD, new Money(5))).thenReturn(new Tax(new Money(5), "23%"));
        when(productData.getType()).thenReturn(ProductType.STANDARD);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, tax);

        Assert.assertEquals(invoice.getItems().size(), 0);
    }

    @Test
    void testZeroElementInvoiceNeverCalledCalculateTax(){
        TaxPolicy tax = mock(TaxPolicy.class);
        ProductData productData = mock(ProductData.class);

        when(tax.calculateTax(ProductType.STANDARD, new Money(5))).thenReturn(new Tax(new Money(5), "23%"));
        when(productData.getType()).thenReturn(ProductType.STANDARD);

        bookKeeper.issuance(invoiceRequest, tax);

        verify(tax, times(0)).calculateTax(any(), any());
    }

    @Test
    void testOneElementInvoiceWithProductType(){
        TaxPolicy tax = mock(TaxPolicy.class);
        ProductData productData = mock(ProductData.class);

        when(tax.calculateTax(ProductType.STANDARD, new Money(5))).thenReturn(new Tax(new Money(5), "23%"));
        when(productData.getType()).thenReturn(ProductType.STANDARD);

        RequestItem requestItem = new RequestItem(productData, 2, new Money(5));
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, tax);

        Assert.assertEquals(invoice.getItems().get(0).getProduct().getType(), ProductType.STANDARD);
    }
}
