package fr.pantheonsorbonne.camel;

import fr.pantheonsorbonne.dto.PharmacyRequestDTO;
import fr.pantheonsorbonne.dto.PharmacyStockRequestDTO;
import fr.pantheonsorbonne.dto.PharmacyResponseDTO;
import fr.pantheonsorbonne.service.PharmacyService;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PharmacyRoutes extends RouteBuilder {

    @Inject
    PharmacyService pharmacyService;

    @Override
    public void configure() throws Exception {

        from("sjms2:M1.PharmacyService")
                .log("Received PharmacyRequestDTO: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, PharmacyRequestDTO.class)
                .process(exchange -> {
                    PharmacyRequestDTO requestDTO = exchange.getIn().getBody(PharmacyRequestDTO.class);
                    exchange.getIn().setHeader("userId", requestDTO.userId());
                    exchange.getIn().setHeader("userAddress", requestDTO.address());

                    PharmacyStockRequestDTO stockRequestDTO = pharmacyService.prepareStockRequest(requestDTO);
                    exchange.getIn().setBody(stockRequestDTO);
                })
                .marshal().json(JsonLibrary.Jackson)
                .log("Sending PharmacyStockRequestDTO to StockService: ${body}")
                .to("sjms2:M1.pharmacyStockRequestQueue");

        from("sjms2:M1.stockResponseQueue")
                .log("Received response from StockService: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, PharmacyResponseDTO[].class)
                .bean(
                        pharmacyService,
                        "processStockResponse(${body},${header.userId}, ${header.userAddress})"
                )
                .marshal().json(JsonLibrary.Jackson)
                .log("Enriched response ready to be sent: ${body}")
                .to("sjms2:M1.itineraryRequestQueue");
    }
}
