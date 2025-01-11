package fr.pantheonsorbonne.camel;

import fr.pantheonsorbonne.dto.PharmacyRequestDTO;
import fr.pantheonsorbonne.dto.PharmacyResponseDTO;
import fr.pantheonsorbonne.service.PharmacyService;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PharmacyRoutes extends RouteBuilder {

    @Inject
    PharmacyService pharmacyService;

    @Override
    public void configure() throws Exception {

        from("sjms2:M1.PharmacyService")
                .log("Received request: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, PharmacyRequestDTO.class)
                .bean(pharmacyService, "prepareStockRequest")
                .marshal().json(JsonLibrary.Jackson)
                .to("sjms2:M1.pharmacyStockRequestQueue"); // Envoi vers le StockService

        from("sjms2:M1.stockResponseQueue")
                .log("Received response from StockService: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, PharmacyResponseDTO[].class)
                .bean(pharmacyService, "processStockResponse")
                .marshal().json(JsonLibrary.Jackson)
                //.to("direct:itineraryServiceQueue");
                .log("${body}");
    }
}
