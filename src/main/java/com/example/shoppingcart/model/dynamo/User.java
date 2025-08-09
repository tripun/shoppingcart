package com.example.shoppingcart.model.dynamo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Collection;
import java.util.List;

/**
 * Represents a user in the shopping cart application.
 * Implements Spring Security's UserDetails interface for authentication and authorization.
 */
@NoArgsConstructor
@Data
@DynamoDbBean
public class User implements UserDetails {

    private String username;

    private String password;

    private String role;

    private String baseCurrency;

    private boolean enabled = true;

    @DynamoDbPartitionKey
    @Override
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public String getBaseCurrency() { return baseCurrency; }

    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    // Backwards-compatible accessors used by CDC/service mapping
    public String getPasswordHash() { return this.password; }

    public String getUserCurrency() { return this.baseCurrency; }

    public boolean isActive() { return this.enabled; }

    /**
     * Returns the authorities granted to the user.
     *
     * @return A collection of GrantedAuthority objects.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true if the user's account is valid (i.e., non-expired), false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // Accounts never expire in this application
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return true if the user is not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // Accounts are never locked in this application
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     *
     * @return true if the user's credentials are valid (i.e., non-expired), false otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Credentials never expire in this application
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true if the user is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
