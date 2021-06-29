package com.wonkmonk.digikhata.userauth.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="applicationuser")
public class ApplicationUser{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private long id;
    @Column(unique = true)
    private String username;
    private String firstName;
    private String lastName;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Address address;
    private String password;
    private  long retailerId;
    private boolean  isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isEnabled;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public ApplicationUser() {
    }

    public ApplicationUser(ApplicationUser applicationUser) {
        this.id = applicationUser.getId();
        this.username = applicationUser.getUsername();
        this.firstName = applicationUser.getFirstName();
        this.lastName = applicationUser.getLastName();
        this.address = applicationUser.getAddress();
        this.password = applicationUser.getPassword();
        this.retailerId = applicationUser.getRetailerId();
        this.isAccountNonExpired = applicationUser.isAccountNonExpired();
        this.isAccountNonLocked = applicationUser.isAccountNonLocked();
        this.isEnabled = applicationUser.isEnabled();
        this.roles = applicationUser.getRoles();
    }
    public ApplicationUser(
                           String username,
                           String firstName,
                           String lastName,
                           Address address,
                           String password,
                           long retailerId,
                           boolean isAccountNonExpired,
                           boolean isAccountNonLocked,
                           boolean isEnabled,
                           Set<Role> roles) {

        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.password = password;
        this.retailerId = retailerId;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isEnabled = isEnabled;
        this.roles = roles;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public long getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(long retailerId) {
        this.retailerId = retailerId;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
