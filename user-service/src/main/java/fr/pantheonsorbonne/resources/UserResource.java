package fr.pantheonsorbonne.resources;

import fr.pantheonsorbonne.dto.UserDTO;
import fr.pantheonsorbonne.exception.InvalidUserException;
import fr.pantheonsorbonne.exception.MissingAddressException;
import fr.pantheonsorbonne.exception.UserAlreadyExistWithTheSameEmail;
import fr.pantheonsorbonne.exception.EmptyDocumentListException;
import fr.pantheonsorbonne.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("user")
public class UserResource {

    @Inject
    UserService userService;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") Long id) {

        UserDTO user = userService.getUserByID(id);
        if (user == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return Response.ok(user).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postUser(UserDTO userDTO) {
        try {
            Long userId = userService.checkAndSaveUSer(userDTO);
            return Response.created(URI.create("/user/" + userId)).build();
        } catch (InvalidUserException | MissingAddressException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
        } catch (UserAlreadyExistWithTheSameEmail e) {
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT).build());
        }
    }

    @POST
    @Path("/documents")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response uploadDocuments(@QueryParam("userId") Long userId, List<String> medications) {
        try {
            if (medications == null || medications.isEmpty()) {
                throw new EmptyDocumentListException("The medications list cannot be empty.");
            }
            userService.uploadDocuments(userId, medications);
            return Response.ok().entity("Medications successfully sent to the pharmacy service").build();
        } catch (EmptyDocumentListException | InvalidUserException | MissingAddressException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
        }
    }

    @PUT
    @Path("{id}/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") Long userId, UserDTO updatedUserDTO) {
        try {
            userService.updateUser(userId, updatedUserDTO);
            return Response.ok("User information updated successfully").build();
        } catch (InvalidUserException | MissingAddressException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
        }
    }


}
