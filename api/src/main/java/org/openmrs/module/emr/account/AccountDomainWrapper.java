package org.openmrs.module.emr.account;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.utils.GeneralUtils;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AccountDomainWrapper {

    private Person person;

    private User user;

    private Provider provider;

    private String password;

    private String confirmPassword;

    private String secretQuestion;

    private String secretAnswer;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private PersonService personService;

    public AccountDomainWrapper() {
        this.person = new Person();
    }

    public AccountDomainWrapper(Person person) {
        this.person = person;
        this.user = getUserByPerson(this.person);
        this.provider = getProviderByPerson(this.person);
    }

    public AccountDomainWrapper(Person person, AccountService accountService, UserService userService,
                                ProviderService providerService, PersonService personService) {
        this.accountService = accountService;
        this.userService = userService;
        this.providerService = providerService;
        this.personService = personService;

        this.person = person;

        // only fetch user and provider if person has been persisted
        if (person.getId() != null) {
            this.user = getUserByPerson(this.person);
            this.provider = getProviderByPerson(this.person);
        }
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setProviderService(ProviderService providerService) {
        this.providerService = providerService;
    }

    public Person getPerson() {
        return person;
    }

    public User getUser() {
        return user;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setGivenName(String givenName) {
        initializePersonNameIfNecessary();
        person.getPersonName().setGivenName(givenName);
    }

    public String getGivenName() {
        return person.getGivenName();
    }

    public void setFamilyName(String familyName) {
        initializePersonNameIfNecessary();
        person.getPersonName().setFamilyName(familyName);
    }

    public String getFamilyName() {
        return person.getFamilyName();
    }

    public void setGender(String gender) {
        person.setGender(gender);
    }

    public String getGender() {
        return person.getGender();
    }

    public void setUsername(String username) {
        if (StringUtils.isNotBlank(username)) {
            initializeUserIfNecessary();
            user.setUsername(username);
        }
    }

    public String getUsername() {
        return user != null ? user.getUsername() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getSecretQuestion() {
        return secretQuestion;
    }

    public void setSecretQuestion(String secretQuestion) {
        this.secretQuestion = secretQuestion;
    }

    public String getSecretAnswer() {
        return secretAnswer;
    }

    public void setSecretAnswer(String secretAnswer) {
        this.secretAnswer = secretAnswer;
    }

    public void setDefaultLocale(Locale locale) {
        if (locale != null) {
            initializeUserIfNecessary();
            user.setUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCALE, locale.toString());
        }
        else if (user != null) {
            user.removeUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCALE);
        }
    }

    public Locale getDefaultLocale() {
        return GeneralUtils.getDefaultLocale(user);
    }

    public void setPrivilegeLevel(Role privilegeLevel) {

        if (privilegeLevel != null) {

            if(!accountService.getAllPrivilegeLevels().contains(privilegeLevel)) {
                throw new APIException("Attempting to set invalid privilege level");
            }

            initializeUserIfNecessary();

            if (!user.hasRole(privilegeLevel.getRole(), true)) {
                if (user.getRoles() != null) {
                    user.getRoles().removeAll(accountService.getAllPrivilegeLevels());
                }
                user.addRole(privilegeLevel);
            }
        }
        else if (user != null) {
            // privilege level is mandatory, so technically we shouldn't ever get here
            if (user.getRoles() != null) {
                user.getRoles().removeAll(accountService.getAllPrivilegeLevels());
            }
        }
    }

    public Role getPrivilegeLevel() {
        // use getRoles instead of getAllRoles since privilege-level should be explicitly set
        if (user != null && user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                if (role.getRole().startsWith(EmrConstants.ROLE_PREFIX_PRIVILEGE_LEVEL)) {
                    return role;
                }
            }

        }
        return null;
    }

    public void setCapabilities(Set<Role> capabilities) {

        if (capabilities != null && capabilities.size() > 0) {
            if (!accountService.getAllCapabilities().containsAll(capabilities)) {
                throw new APIException("Attempt to set invalid capability");
            }

            initializeUserIfNecessary();

            if (user.getRoles() != null) {
                user.getRoles().removeAll(accountService.getAllCapabilities());
            }

            for (Role role : capabilities) {
                user.addRole(role);
            }
        }
        else if (user != null && user.getRoles() != null) {
            user.getRoles().removeAll(accountService.getAllCapabilities());
        }
    }

    public Set<Role> getCapabilities() {

        if (user == null) {
            return null;
        }

        Set<Role> capabilities = new HashSet<Role>();

        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                if (role.getRole().startsWith(EmrConstants.ROLE_PREFIX_CAPABILITY)) {
                    capabilities.add(role);
                }
            }
        }
        return capabilities;
    }

    public void setUserEnabled(Boolean userEnabled) {
        if (user != null) {
            if (userEnabled && user.isRetired()) {
                user.setRetired(false);
                user.setRetireReason(null);
                user.setRetiredBy(null);
                user.setDateRetired(null);
            }
            else if (!userEnabled && !user.isRetired()) {
                user.setRetired(true);
                user.setRetireReason("retired during account management");
                user.setDateRetired(new Date());
                // TODO: figure out how to set retired by
            }
        }
        else if (userEnabled != null && userEnabled) {
            initializeUserIfNecessary();
        }
    }


    public Boolean getUserEnabled() {
        if (user == null) {
            return null;
        }
        else {
            return !user.isRetired();
        }
    }

    public void setProviderIdentifier(String providerIdentifier) {
        if (StringUtils.isNotBlank(providerIdentifier)) {
            initializeProviderIfNecessary();
            provider.setIdentifier(providerIdentifier);
        }
        else if (provider != null) {
            provider.setIdentifier(providerIdentifier);
        }
    }

    public String getProviderIdentifier() {
        return provider != null ? provider.getIdentifier() : null;
    }

    public void setProviderEnabled(Boolean providerEnabled) {
        if (provider != null) {
            if (providerEnabled && provider.isRetired()) {
                provider.setRetired(false);
                provider.setRetireReason(null);
                provider.setRetiredBy(null);
                provider.setDateRetired(null);
            }
            else if (!providerEnabled && !provider.isRetired()) {
                provider.setRetired(true);
                provider.setRetireReason("returned during account management");
                provider.setDateRetired(new Date());
                // TODO: figure out how to set retired by
            }
        }
        else if (providerEnabled != null && providerEnabled) {
            initializeProviderIfNecessary();
        }
    }

    public Boolean getProviderEnabled() {
        if (provider == null) {
            return null;
        }
        else {
            return !provider.isRetired();
        }
    }

    public boolean isLocked() {
        if (user == null) {
            return false;
        }
        String lockoutTimeProperty = user.getUserProperty(OpenmrsConstants.USER_PROPERTY_LOCKOUT_TIMESTAMP);
        if (lockoutTimeProperty != null) {
            try {
                Long lockedOutUntil = Long.valueOf(lockoutTimeProperty) + 300000;
                return System.currentTimeMillis() < lockedOutUntil;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
        return false;
    }

    /**
     * Unlocks this account (in case it has been locked for getting the password wrong too many times), and saves that
     * to the database.
     */
    public void unlock() {
        if (user == null) {
            throw new IllegalStateException("Cannot unlock an account that doesn't have a user");
        }
        user.removeUserProperty(OpenmrsConstants.USER_PROPERTY_LOCKOUT_TIMESTAMP);
        user.removeUserProperty(OpenmrsConstants.USER_PROPERTY_LOGIN_ATTEMPTS);
        userService.saveUser(user, null);
    }

    public void save() {

        if (person != null) {
            personService.savePerson(person);
        }

        if (user != null) {
            boolean existingUser = (user.getUserId() != null);
            userService.saveUser(user, password);

            // the saveUser(user, password) method will *only* set a password for a new user, it won't change an existing one
            if (existingUser && StringUtils.isNotBlank(password) && StringUtils.isNotBlank(confirmPassword)) {
                userService.changePassword(user, password);
            }

            if (StringUtils.isNotBlank(secretQuestion) && StringUtils.isNotBlank(secretAnswer)) {
                userService.changeQuestionAnswer(user, secretQuestion, secretAnswer);
            }
        }

        if (provider != null) {
            providerService.saveProvider(provider);
        }


    }

    private void initializePersonNameIfNecessary() {
        if (person.getPersonName() == null) {
            person.addName(new PersonName());
        }
    }

    private void initializeUserIfNecessary() {
        if (user == null) {
            user = new User();
            user.setPerson(person);
        }
    }

    private void initializeProviderIfNecessary() {
        if (provider == null) {
            provider = new Provider();
            provider.setPerson(person);
        }
    }

    private User getUserByPerson(Person person) {
        User user = null;
        List<User> users = userService.getUsersByPerson(person, false);
        //exclude daemon user
        for (Iterator<User> i = users.iterator(); i.hasNext();) {
            User candidate = i.next();
            if (EmrConstants.DAEMON_USER_UUID.equals(candidate.getUuid())) {
                i.remove();
                break;
            }
        }
        //return a retired account if they have none
        if (users.size() == 0)
            users = userService.getUsersByPerson(person, true);

        if (users.size() == 1)
            user = users.get(0);
        else if (users.size() > 1)
            throw new APIException("Found multiple users associated to the person with id: " + person.getPersonId());

        return user;
    }

    private Provider getProviderByPerson(Person person) {
        Provider provider = null;
        Collection<Provider> providers = providerService.getProvidersByPerson(person, false);
        for (Iterator<Provider> i = providers.iterator(); i.hasNext();) {
            Provider candidate = i.next();
            if (EmrConstants.DAEMON_USER_UUID.equals(candidate.getUuid())) {
                i.remove();
                break;
            }
        }
        //see if they have a retired account
        if (providers.size() == 0)
            providers = providerService.getProvidersByPerson(person, true);

        if (providers.size() == 1)
            provider = providers.iterator().next();
        else if (providers.size() > 1)
            throw new APIException("Found multiple providers associated to the person with id: " + person.getPersonId());

        return provider;
    }

}