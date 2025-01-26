package fr.pantheonsorbonne.dao;

import fr.pantheonsorbonne.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class UserDAO {


    @Inject
    EntityManager em;

    public boolean isUserPresent(String userEmail) {
        return !em.createQuery("SELECT u from User u WHERE u.email=:email")
                .setParameter("email", userEmail)
                .getResultList().isEmpty();
    }

    public void saveUser(User user) {
        em.persist(user);
    }

    public User getById(Long id) {
        return em.find(User.class, id);
    }

    public void updateUser(User user) {
        em.merge(user);
    }

}