package fr.tikione.c2e.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * Authentication data on CanardPC website.
 */
@Data
@AllArgsConstructor
public class Auth {
    
    private Map<String, String> cookies;
    private String login;
    private String password;
}
