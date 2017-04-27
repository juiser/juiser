/*
 * Copyright 2017 Les Hazlewood and the respective Juiser contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.juiser.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @since 0.1.0
 */
public class UserBuilder {

    private boolean authenticated;
    private String href;
    private String id;
    private String name;
    private String givenName;
    private String middleName;
    private String familyName;
    private String nickname;
    private String username;
    private URL profile;
    private URL picture;
    private URL website;
    private String email;
    private boolean emailVerified;
    private String gender;
    private LocalDate birthdate;
    private TimeZone zoneInfo;
    private Locale locale;
    private Phone phone;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public UserBuilder setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        return this;
    }

    public UserBuilder setHref(String href) {
        this.href = href;
        return this;
    }

    public UserBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public UserBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder setGivenName(String givenName) {
        this.givenName = givenName;
        return this;
    }

    public UserBuilder setMiddleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public UserBuilder setFamilyName(String familyName) {
        this.familyName = familyName;
        return this;
    }

    public UserBuilder setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public UserBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder setProfile(String profile) {
        setProfile(toUrl(profile, "profile"));
        return this;
    }

    private URL toUrl(String url, String propertyName) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            String msg = "Invalid " + propertyName + " URL: " + profile;
            throw new IllegalArgumentException(msg);
        }
    }

    public UserBuilder setProfile(URL profile) {
        this.profile = profile;
        return this;
    }

    public UserBuilder setPicture(String picture) {
        setPicture(toUrl(picture, "picture"));
        return this;
    }

    public UserBuilder setPicture(URL picture) {
        this.picture = picture;
        return this;
    }

    public UserBuilder setWebsite(String website) {
        setWebsite(toUrl(website, "website"));
        return this;
    }

    public UserBuilder setWebsite(URL website) {
        this.website = website;
        return this;
    }

    public UserBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
        return this;
    }

    public UserBuilder setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public UserBuilder setBirthdate(String iso8601) {
        LocalDate date = LocalDate.parse(iso8601, DateTimeFormatter.ISO_LOCAL_DATE);
        setBirthdate(date);
        return this;
    }

    public UserBuilder setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public UserBuilder setZoneInfo(String info) {
        setZoneInfo(TimeZone.getTimeZone(info));
        return this;
    }

    public UserBuilder setZoneInfo(TimeZone zoneInfo) {
        this.zoneInfo = zoneInfo;
        return this;
    }

    public UserBuilder setLocale(String locale) {
        setLocale(Locale.forLanguageTag(locale));
        return this;
    }

    public UserBuilder setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public UserBuilder setPhone(Phone phone) {
        this.phone = phone;
        return this;
    }

    public UserBuilder setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public UserBuilder setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public User build() {
        return new ImmutableUser(authenticated, href, id, name, givenName, familyName, middleName, nickname, username,
            profile, picture, website, email, emailVerified, gender, birthdate, zoneInfo, locale, phone,
            createdAt, updatedAt);
    }
}
