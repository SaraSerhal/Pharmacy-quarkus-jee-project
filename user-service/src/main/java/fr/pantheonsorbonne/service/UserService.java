package fr.pantheonsorbonne.service;

import fr.pantheonsorbonne.dao.UserDAO;
import fr.pantheonsorbonne.dto.UserDTO;
import fr.pantheonsorbonne.entity.User;
import fr.pantheonsorbonne.exception.InvalidUserException;
import fr.pantheonsorbonne.exception.UserAlreadyExistWithTheSameEmail;
import fr.pantheonsorbonne.exception.EmptyDocumentListException;
import fr.pantheonsorbonne.gateway.DocumentGateway;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    UserDAO userDAO;

    @Inject
    DocumentGateway invoiceGateway;

    public UserDTO getUserByID(Long id) {
        User user = userDAO.getById(id);
        if (user == null) {
            return null;
        }
        return new UserDTO(user.getName(), user.getEmail());
    }

    @Transactional
    public Long checkAndSaveUSer(UserDTO userDTO) throws InvalidUserException, UserAlreadyExistWithTheSameEmail {

        if (!userDTO.email().contains("@")) {
            throw new InvalidUserException("Email is malformed");
        }

        if (userDAO.isUserPresent(userDTO.email())) {
            throw new UserAlreadyExistWithTheSameEmail();
        }

        User user = new User();
        user.setEmail(userDTO.email());
        user.setName(userDTO.name());
        userDAO.saveUser(user);

        invoiceGateway.handleNewUser(userDTO);
        return user.getId();
    }

    @Transactional
    public boolean loginUser(UserDTO userDTO) throws InvalidUserException {
        User user = userDAO.getByEmail(userDTO.email());
        if (user == null) {
            return false;
        }
        return true;
    }

    @Transactional
    public void uploadDocuments(Long userId, List<String> medications) throws EmptyDocumentListException {
        if (medications == null || medications.isEmpty()) {
            throw new EmptyDocumentListException("The medications list cannot be empty.");
        }

        invoiceGateway.sendDocumentsToPharmacy(userId, medications);
    }
}
