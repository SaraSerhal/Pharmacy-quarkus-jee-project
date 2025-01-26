package fr.pantheonsorbonne.camel;

import fr.pantheonsorbonne.dto.PharmacyRequestDTO;
import fr.pantheonsorbonne.dto.PharmacyStockRequestDTO;
import fr.pantheonsorbonne.dto.PharmacyResponseDTO;
import fr.pantheonsorbonne.service.PharmacyService;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PharmacyRoutes extends RouteBuilder {

    @Inject
    PharmacyService pharmacyService;

    @Override
    public void configure()  {

        from("sjms2:M1.PharmacyService")
                .log("Received PharmacyRequestDTO: ${body}")
                .unmarshal().json(PharmacyRequestDTO.class)
                .process(exchange -> {
                    PharmacyRequestDTO requestDTO = exchange.getIn().getBody(PharmacyRequestDTO.class);
                    exchange.getIn().setHeader("userId", requestDTO.userId());
                    exchange.getIn().setHeader("userAddress", requestDTO.address());

                    PharmacyStockRequestDTO stockRequestDTO = pharmacyService.prepareStockRequest(requestDTO);
                    exchange.getIn().setBody(stockRequestDTO);
                })
                .marshal().json()
                .log("Sending PharmacyStockRequestDTO to StockService: ${body}")
                .to("sjms2:M1.pharmacyStockRequestQueue");

        from("sjms2:M1.stockResponseQueue")
                .log("Received response from StockService: ${body}")
                .unmarshal().json(PharmacyResponseDTO[].class)
                .bean(pharmacyService, "processStockResponse(${body},${header.userId}, ${header.userAddress})")
                .marshal().json()
                .log("Enriched response ready to be sent: ${body}")
                .to("sjms2:M1.itineraryRequestQueue");
    }
}
