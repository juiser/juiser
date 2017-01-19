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

    private final boolean authenticated;
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

    public ImmutableUser(boolean authenticated, String href, String id, String name, String givenName,
                         String familyName, String middleName, String nickname, String username, URL profile,
                         URL picture, URL website, String email, boolean emailVerified, String gender,
                         LocalDate birthdate, TimeZone zoneInfo, Locale locale, Phone phone, ZonedDateTime createdAt,
                         ZonedDateTime updatedAt) {
        this.authenticated = authenticated;
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
        return this.authenticated;
    }

    @Override
    public boolean isAnonymous() {
        return !this.authenticated;
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

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || (!(o instanceof ImmutableUser))) return false;

        ImmutableUser that = (ImmutableUser) o;

        if (authenticated != that.authenticated) return false;
        if (emailVerified != that.emailVerified) return false;
        if (href != null ? !href.equals(that.href) : that.href != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (givenName != null ? !givenName.equals(that.givenName) : that.givenName != null) return false;
        if (familyName != null ? !familyName.equals(that.familyName) : that.familyName != null) return false;
        if (middleName != null ? !middleName.equals(that.middleName) : that.middleName != null) return false;
        if (nickname != null ? !nickname.equals(that.nickname) : that.nickname != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (profile != null ? !profile.equals(that.profile) : that.profile != null) return false;
        if (picture != null ? !picture.equals(that.picture) : that.picture != null) return false;
        if (website != null ? !website.equals(that.website) : that.website != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (gender != null ? !gender.equals(that.gender) : that.gender != null) return false;
        if (birthdate != null ? !birthdate.equals(that.birthdate) : that.birthdate != null) return false;
        if (zoneInfo != null ? !zoneInfo.equals(that.zoneInfo) : that.zoneInfo != null) return false;
        if (locale != null ? !locale.equals(that.locale) : that.locale != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null) return false;
        return updatedAt != null ? updatedAt.equals(that.updatedAt) : that.updatedAt == null;
    }

    @Override
    public int hashCode() {
        int result = (authenticated ? 1 : 0);
        result = 31 * result + (href != null ? href.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
        result = 31 * result + (familyName != null ? familyName.hashCode() : 0);
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (profile != null ? profile.hashCode() : 0);
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        result = 31 * result + (website != null ? website.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (emailVerified ? 1 : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (birthdate != null ? birthdate.hashCode() : 0);
        result = 31 * result + (zoneInfo != null ? zoneInfo.hashCode() : 0);
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        return result;
    }
}
