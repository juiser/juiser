/*
 * Copyright (C) 2017 Stormpath, Inc.
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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * @since 0.1.0
 */
public class DefaultMapUserFactory implements Function<Map<String, ?>, User> {

    protected static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC").normalized();

    @Override
    public User apply(Map<String, ?> data) {

        UserBuilder builder = new UserBuilder();

        //if we're in this method, the assumption is that the data has been verified to be authentic, so we
        //consider the instance authenticated:
        builder.setAuthenticated(true);

        String value = getString(data, "href");
        if (value != null) {
            builder.setHref(value);
        }

        value = getString(data, "sub");
        if (value != null) {
            builder.setId(value);
        }

        StringBuilder fullName = null;
        value = getString(data, "name", "full_name", "fullName");
        if (value != null) {
            builder.setName(value);
        } else {
            fullName = new StringBuilder();
        }

        value = getString(data, "given_name", "givenName", "first_name", "firstName");
        if (value != null) {
            builder.setGivenName(value);
            append(fullName, value);
        }

        value = getString(data, "middle_name", "middleName", "middle_names", "middleNames");
        if (value != null) {
            builder.setMiddleName(value);
            append(fullName, value);
        }

        value = getString(data, "family_name", "familyName", "surname");
        if (value != null) {
            builder.setFamilyName(value);
            append(fullName, value);
        }

        if (fullName != null && fullName.length() > 0) {
            builder.setName(fullName.toString());
        }

        value = getString(data, "nickname");
        if (value != null) {
            builder.setNickname(value);
        }

        value = getString(data, "username", "userName");
        if (value != null) {
            builder.setUsername(value);
        }

        value = getString(data, "profile");
        if (value != null) {
            builder.setProfile(value);
        }

        value = getString(data, "picture");
        if (value != null) {
            builder.setPicture(value);
        }

        value = getString(data, "website");
        if (value != null) {
            builder.setWebsite(value);
        }

        value = getString(data, "email");
        if (value != null) {
            builder.setEmail(value);
        }

        Boolean truth = getBoolean(data, "email_verified", "emailVerified");
        if (truth != null) {
            builder.setEmailVerified(truth);
        } else {
            value = getString(data, "email_verification_status", "emailVerificationStatus");
            if (value != null) {
                builder.setEmailVerified("verified".equalsIgnoreCase(value));
            }
        }

        value = getString(data, "gender", "sex");
        if (value != null) {
            builder.setGender(value);
        }

        value = getString(data, "birthdate", "birth_date", "birthDate");
        if (value != null) {
            builder.setBirthdate(value);
        }

        value = getString(data, "zoneinfo", "zone_info", "zoneInfo", "time_zone", "timezone", "timeZone");
        if (value != null) {
            builder.setZoneInfo(value);
        }

        value = getString(data, "locale");
        if (value != null) {
            builder.setLocale(value);
        }

        boolean verified = false;
        truth = getBoolean(data, "phone_number_verified", "phoneNumberVerified", "phone_verified", "phoneVerified");
        if (truth != null) {
            verified = truth;
        } else {
            value = getString(data, "phone_number_verification_status", "phoneNumberVerificationStatus",
                "phone_verification_status", "phoneVerificationStatus");
            if (value != null) {
                verified = "verified".equalsIgnoreCase(value);
            }
        }

        value = getString(data, "phone_number", "phoneNumber", "phone");
        if (value != null) {
            builder.setPhone(new ImmutablePhone(value, null, null, verified));
        }

        ZonedDateTime zdt = getZonedDateTime(data, "created_at", "createdAt");
        if (zdt != null) {
            builder.setCreatedAt(zdt);
        }

        zdt = getZonedDateTime(data, "updated_at", "updatedAt", "modified_at", "modifiedAt");
        if (zdt != null) {
            builder.setUpdatedAt(zdt);
        }

        return builder.build();
    }

    protected static ZonedDateTime getZonedDateTime(Map<String, ?> data, String... propertyNames) {
        Object obj = getFirst(data, propertyNames);
        if (obj == null) {
            return null;
        }
        if (obj instanceof String && isNumber((String) obj)) {
            obj = Long.parseLong((String) obj);
        }
        if (obj instanceof Number) {
            //openid connect has this number be *seconds*.  Need to multiple by 1000 to be millis for Java:
            long seconds = ((Number) obj).longValue();
            return fromEpochSeconds(seconds);
        } else if (obj instanceof String) {
            return ZonedDateTime.parse((String) obj, DateTimeFormatter.ISO_DATE_TIME);
        }

        throw new IllegalArgumentException("Unrecognized timestamp object: " + obj);
    }

    protected static boolean isNumber(String s) {
        if (s != null) {
            for (char c : s.toCharArray()) {
                if (!Character.isDigit(c)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    protected static ZonedDateTime fromEpochSeconds(long seconds) {
        long millis = seconds * 1000;
        Date date = new Date(millis);
        return ZonedDateTime.ofInstant(date.toInstant(), UTC_ZONE_ID);

    }

    private static Object getFirst(Map<String, ?> data, String... propertyNames) {
        for (String s : propertyNames) {
            Object value = data.get(s);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private String getString(Map<String, ?> data, String... propertyNames) {
        Object value = getFirst(data, propertyNames);
        if (value != null) {
            return String.valueOf(value);
        }
        return null;
    }

    private Boolean getBoolean(Map<String, ?> data, String... propertyNames) {
        for (String s : propertyNames) {
            Object value = data.get(s);
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value != null) {
                return Boolean.valueOf(String.valueOf(value));
            }
        }
        return null;
    }

    private void append(StringBuilder sb, String value) {
        if (sb == null) return;
        if (sb.length() > 0) {
            sb.append(" ");
        }
        sb.append(value);
    }


}
