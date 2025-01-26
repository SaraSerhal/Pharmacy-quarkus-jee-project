package fr.pantheonsorbonne.camel;

import org.apache.camel.builder.RouteBuilder;


public class CamelRoutes extends RouteBuilder {
    @Override
    public void configure()  {

        from("direct:sendDocumentsToPharmacy")
                .log("Sending user ID, medications, and address to pharmacy service: ${body}")
                .marshal().json()
                .to("sjms2:M1.PharmacyService");

        from("sjms2:M1.itineraryResponseQueue")
                .log("Received response from itinerary service: ${body}");
    }
}
