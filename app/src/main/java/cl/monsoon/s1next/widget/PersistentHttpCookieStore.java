/* Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cl.monsoon.s1next.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.util.Base64;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A shared preference cookie store.
 * <p>
 * Forked from https://android.googlesource.com/platform/libcore/+/master/luni/src/main/java/java/net/CookieStoreImpl.java
 * blob: 36f74a9e1b5b41831f3ee0c4d59fdabd954edd96
 */
public final class PersistentHttpCookieStore implements CookieStore {

    private static final String PREFS_COOKIE = "CookiePrefsFile";
    private static final String COOKIES_URI = "url";

    /**
     * This map may have null keys!
     */
    private final Map<URI, List<HttpCookie>> map;

    private final SharedPreferences cookieSP;

    public PersistentHttpCookieStore(Context context) {
        map = new HashMap<>();
        cookieSP = context.getSharedPreferences(PREFS_COOKIE, Context.MODE_PRIVATE);

        // get each cookie's URI string
        Set<String> cookiesURL = cookieSP.getStringSet(COOKIES_URI, Collections.<String>emptySet());
        for (String uri : cookiesURL) {
            // get corresponding cookies' key of the shared preference
            Set<String> cookiesName = cookieSP.getStringSet(uri, Collections.<String>emptySet());

            // get corresponding cookies
            List<HttpCookie> httpCookies = new ArrayList<>();
            for (String name : cookiesName) {
                HttpCookie httpCookie = decodeCookie(cookieSP.getString(name, null));
                if (httpCookie != null) {
                    httpCookies.add(httpCookie);
                }
            }

            map.put(URI.create(uri), httpCookies);
        }
    }

    @Override
    public synchronized void add(URI uri, HttpCookie httpCookie) {
        if (httpCookie == null) {
            throw new NullPointerException("cookie == null");
        }

        boolean isUriNew = false;
        uri = cookiesUri(uri);
        List<HttpCookie> cookies = map.get(uri);
        if (cookies == null) {
            cookies = new ArrayList<>();
            map.put(uri, cookies);

            isUriNew = true;
        } else {
            cookies.remove(httpCookie);
        }
        cookies.add(httpCookie);

        String uriString = uri.toString();
        SharedPreferences.Editor editor = cookieSP.edit();

        if (isUriNew) {
            // add new cookie's URL string
            // see https://stackoverflow.com/questions/14034803/misbehavior-when-trying-to-store-a-string-set-using-sharedpreferences
            Set<String> cookiesURL = new HashSet<>(cookieSP.getStringSet(COOKIES_URI,
                    Collections.<String>emptySet()));
            cookiesURL.add(uriString);

            editor.putStringSet(COOKIES_URI, cookiesURL);
        }

        // add corresponding cookies
        Set<String> cookiesName = new HashSet<>(cookieSP.getStringSet(uriString,
                Collections.<String>emptySet()));
        String cookieNameWithUri = uriString + httpCookie.getName();
        cookiesName.add(cookieNameWithUri);

        editor.putStringSet(uriString, cookiesName);
        editor.putString(cookieNameWithUri, encodeCookie(httpCookie));
        editor.apply();
    }

    private URI cookiesUri(URI uri) {
        if (uri == null) {
            return null;
        }

        try {
            return new URI("http", uri.getHost(), null, null);
        } catch (URISyntaxException e) {
            return uri; // probably a URI with no host
        }
    }

    @Override
    public synchronized List<HttpCookie> get(URI uri) {
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }

        List<HttpCookie> result = new ArrayList<>();

        // get cookies associated with given URI. If none, returns an empty list
        List<HttpCookie> cookiesForUri = map.get(uri);
        if (cookiesForUri != null) {
            for (Iterator<HttpCookie> i = cookiesForUri.iterator(); i.hasNext(); ) {
                HttpCookie cookie = i.next();
                if (cookie.hasExpired()) {
                    i.remove(); // remove expired cookies
                } else {
                    result.add(cookie);
                }
            }
        }

        // get all cookies that domain matches the URI
        for (Map.Entry<URI, List<HttpCookie>> entry : map.entrySet()) {
            if (uri.equals(entry.getKey())) {
                continue; // skip the given URI; we've already handled it
            }
            List<HttpCookie> entryCookies = entry.getValue();
            for (Iterator<HttpCookie> i = entryCookies.iterator(); i.hasNext(); ) {
                HttpCookie cookie = i.next();
                if (!HttpCookie.domainMatches(cookie.getDomain(), uri.getHost())) {
                    continue;
                }
                if (cookie.hasExpired()) {
                    i.remove(); // remove expired cookies
                } else if (!result.contains(cookie)) {
                    result.add(cookie);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public synchronized List<HttpCookie> getCookies() {
        List<HttpCookie> result = new ArrayList<>();
        for (List<HttpCookie> list : map.values()) {
            for (Iterator<HttpCookie> i = list.iterator(); i.hasNext(); ) {
                HttpCookie cookie = i.next();
                if (cookie.hasExpired()) {
                    i.remove(); // remove expired cookies
                } else if (!result.contains(cookie)) {
                    result.add(cookie);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public synchronized List<URI> getURIs() {
        List<URI> result = new ArrayList<>(map.keySet());
        result.remove(null); // sigh
        return Collections.unmodifiableList(result);
    }

    @Override
    public synchronized boolean remove(URI uri, HttpCookie httpCookie) {
        if (httpCookie == null) {
            throw new NullPointerException("cookie == null");
        }

        uri = cookiesUri(uri);
        List<HttpCookie> cookies = map.get(uri);
        if (cookies != null) {
            SharedPreferences.Editor editor = cookieSP.edit();
            String uriString = uri.toString();
            Set<String> cookiesName = new HashSet<>(cookieSP.getStringSet(uriString,
                    Collections.<String>emptySet()));
            String cookieNameWithURI = uriString + httpCookie.getName();

            // remove cookie's URI string
            cookiesName.remove(cookieNameWithURI);
            editor.putStringSet(uriString, cookiesName);
            // remove corresponding cookies
            editor.remove(cookieNameWithURI);
            editor.apply();

            return cookies.remove(httpCookie);
        } else {
            return false;
        }
    }

    @Override
    public synchronized boolean removeAll() {
        // clear cookies from shared preference
        SharedPreferences.Editor editor = cookieSP.edit();
        editor.clear();
        editor.apply();

        // clear cookies from local store
        boolean result = !map.isEmpty();
        map.clear();
        return result;
    }

    /**
     * Parcels HttpCookie object into a String.
     */
    private String encodeCookie(HttpCookie httpCookie) {
        if (httpCookie == null) {
            return null;
        }

        Parcel parcel = Parcel.obtain();
        new HttpCookieParcelable(httpCookie).writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();

        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * Returns HttpCookie from cookie string.
     */
    private HttpCookie decodeCookie(String s) {
        if (s == null) {
            return null;
        }

        byte[] bytes = Base64.decode(s, Base64.DEFAULT);

        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);

        return HttpCookieParcelable.CREATOR.createFromParcel(parcel).getHttpCookie();
    }
}
