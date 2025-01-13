package fr.pantheonsorbonne.camel;

import org.apache.camel.builder.RouteBuilder;
import fr.pantheonsorbonne.dto.UserDocumentDTO;

public class CamelRoutes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        //from("direct:newUser")
          //      .log("New user sent to Invoice MS")
            //    .marshal().json()
              //  .to("sjms2:M1.InvoiceService");

        from("direct:sendDocumentsToPharmacy")
                .log("Sending user ID, medications, and address to pharmacy service: ${body}")
                .marshal().json()
                .to("sjms2:M1.PharmacyService");
    }
}
