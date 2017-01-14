package com.stormpath.juiser.model;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @since 0.1.0
 */
public class ImmutableUser implements User {

    private final String href;
    private final String id;
    private final String name;
    private final String givenName;
    private final String familyName;
    private final String middleName;
    private final String nickname;
    private final String username;
    private final URL profile;
    private final URL picture;
    private final URL website;
    private final String email;
    private final boolean emailVerified;
    private final String gender;
    private final LocalDate birthdate;
    private final TimeZone zoneInfo;
    private final Locale locale;
    private final Phone phone;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;

    public ImmutableUser(String href, String id, String name, String givenName, String familyName, String middleName,
                         String nickname, String username, URL profile, URL picture, URL website, String email,
                         boolean emailVerified, String gender, LocalDate birthdate, TimeZone zoneInfo, Locale locale,
                         Phone phone, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.href = href;
        this.id = id;
        this.name = name;
        this.givenName = givenName;
        this.familyName = familyName;
        this.middleName = middleName;
        this.nickname = nickname;
        this.username = username;
        this.profile = profile;
        this.picture = picture;
        this.website = website;
        this.email = email;
        this.emailVerified = emailVerified;
        this.gender = gender;
        this.birthdate = birthdate;
        this.zoneInfo = zoneInfo;
        this.locale = locale;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean isAuthenticated() {
        return true; //an instance of this class should only ever be created after a valid authentication
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }

    @Override
    public String getHref() {
        return this.href;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getGivenName() {
        return this.givenName;
    }

    @Override
    public String getFamilyName() {
        return this.familyName;
    }

    @Override
    public String getMiddleName() {
        return this.middleName;
    }

    @Override
    public String getNickname() {
        return this.nickname;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public URL getProfile() {
        return this.profile;
    }

    @Override
    public URL getPicture() {
        return this.picture;
    }

    @Override
    public URL getWebsite() {
        return this.website;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public boolean isEmailVerified() {
        return this.emailVerified;
    }

    @Override
    public String getGender() {
        return this.gender;
    }

    @Override
    public LocalDate getBirthdate() {
        return this.birthdate;
    }

    @Override
    public TimeZone getZoneInfo() {
        return this.zoneInfo;
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }

    @Override
    public Phone getPhone() {
        return this.phone;
    }

    @Override
    public String getPhoneNumber() {
        return this.phone != null ? this.phone.getNumber() : null;
    }

    @Override
    public boolean isPhoneNumberVerified() {
        return this.phone != null && this.phone.isVerified();
    }

    @Override
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }
}
