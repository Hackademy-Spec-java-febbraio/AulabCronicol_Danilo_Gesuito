package it.aulab.progetto_finale_danilo_gesuito.services;

import it.aulab.progetto_finale_danilo_gesuito.models.CareerRequest;
import it.aulab.progetto_finale_danilo_gesuito.models.User;

public interface CareerRequestService {
    boolean isRoleAlredyAssigned(User user, CareerRequest careerRequest);
    void save(CareerRequest careerRequest, User user);
    void careerAccepted(Long requestId);
    CareerRequest find(Long id);
}
