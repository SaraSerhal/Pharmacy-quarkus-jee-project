package fr.pantheonsorbonne.camel;

import fr.pantheonsorbonne.dto.ItineraryRequestDTO;
import fr.pantheonsorbonne.processor.ItineraryProcessor;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ItineraryRoutes extends RouteBuilder {

    @Inject
    ItineraryProcessor itineraryProcessor;

    @Override
    public void configure() {
        from("sjms2:M1.itineraryRequestQueue")
                .log("Received PharmacyService Response: ${body}")
                .unmarshal().json( ItineraryRequestDTO[].class)
                .bean(itineraryProcessor, "processItinerary")
                .marshal().json()
                .log("${body}")
                .to("sjms2:M1.itineraryResponseQueue");
    }
}