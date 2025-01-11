package fr.pantheonsorbonne.camel;

import fr.pantheonsorbonne.dto.MedicamentAvailabilityRequestDTO;
import fr.pantheonsorbonne.service.PharmacyStockService;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.model.dataformat.JsonLibrary;

@ApplicationScoped
public class StockRoutes extends RouteBuilder {
    @Inject
    PharmacyStockService stockService;

    @Override
    public void configure() throws Exception {
        from("sjms2:M1.pharmacyStockRequestQueue")
                .log("Received message: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, MedicamentAvailabilityRequestDTO.class)
                .bean(stockService, "processRequest")
                .marshal().json(JsonLibrary.Jackson)
                .to("sjms2:M1.stockResponseQueue");
    }
}
