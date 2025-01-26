package fr.pantheonsorbonne.camel;

import fr.pantheonsorbonne.dto.MedicamentAvailabilityRequestDTO;
import fr.pantheonsorbonne.service.PharmacyStockService;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StockRoutes extends RouteBuilder {
    @Inject
    PharmacyStockService stockService;

    @Override
    public void configure() throws Exception {
        from("sjms2:M1.pharmacyStockRequestQueue")
                .log("Received message: ${body}")
                .unmarshal().json(MedicamentAvailabilityRequestDTO.class)
                .bean(stockService, "processRequest")
                .log("Sending pharmacies with available medicaments: ${body}")
                .marshal().json()
                .to("sjms2:M1.stockResponseQueue");
    }
}
