package it.aulab.progetto_finale_danilo_gesuito.services;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String text);
}
