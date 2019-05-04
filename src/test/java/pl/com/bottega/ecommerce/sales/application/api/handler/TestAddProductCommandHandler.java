package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.system.application.SystemContext;
import static org.mockito.Mockito.*;

public class TestAddProductCommandHandler {

    Id id = Id.generate();
    private AddProductCommand command;
    private AddProductCommandHandler addProductCommandHandler;
    private ReservationRepository reservationRepository;
    private ProductRepository productRepository;
    private SuggestionService suggestionService;
    private ClientRepository clientRepository;
    private SystemContext systemContext;
    private Product product;
    private Reservation reservation;
    private Client client;

    @Before
    public void before(){
        command = new AddProductCommand(id, id, 10);

        reservation = mock(Reservation.class);
        reservationRepository = mock(ReservationRepository.class);
        product = mock(Product.class);
        productRepository = mock(ProductRepository.class);
        systemContext = mock(SystemContext.class);
        suggestionService = mock(SuggestionService.class);

        when(reservationRepository.load(id)).thenReturn(reservation);
        when(product.isAvailable()).thenReturn(true);
        when(productRepository.load(id)).thenReturn(product);
        when(suggestionService.suggestEquivalent(product, client)).thenReturn(product);

        addProductCommandHandler = new AddProductCommandHandler(reservationRepository, productRepository,
                suggestionService, clientRepository, systemContext);

    }

    @Test
    public void testProductIsAvailable(){
        Assert.assertEquals(product.isAvailable(), true);
    }

    @Test
    public void testProductRepository(){
        addProductCommandHandler.handle(command);

        verify(productRepository,times(1)).load(id);
    }

    @Test
    public void testReservationRepository(){
        addProductCommandHandler.handle(command);

        verify(reservationRepository,times(1)).load(id);
    }

    @Test
    public void testSuggestionService(){
        addProductCommandHandler.handle(command);

        verify(suggestionService,times(0)).suggestEquivalent(product, client);
    }

    @Test
    public void testProductIsAvailableCalledMethodTwoTimes(){
        addProductCommandHandler.handle(command);
        addProductCommandHandler.handle(command);

        verify(product,times(2)).isAvailable();
    }
}
