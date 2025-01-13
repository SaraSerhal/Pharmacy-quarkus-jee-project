package fr.pantheonsorbonne.gateway;

import fr.pantheonsorbonne.dto.UserDocumentDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.ProducerTemplate;
import java.util.List;

@ApplicationScoped
public class DocumentGateway {

    @Inject
    ProducerTemplate producerTemplate;

    //public void handleNewUser(UserDTO user) {
     //   producerTemplate.sendBody("direct:newUser", user);
    //}

    public void sendDocumentsToPharmacy(Long userId, List<String> medications, String address) {
        UserDocumentDTO userDocumentDTO = new UserDocumentDTO(userId, medications,address);
        producerTemplate.sendBody("direct:sendDocumentsToPharmacy", userDocumentDTO);
    }
}
