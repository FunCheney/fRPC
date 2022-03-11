/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
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
package com.fan.rpc.common;


import com.fan.rpc.common.utils.ArrayUtils;
import com.fan.rpc.common.utils.CollectionUtils;
import com.fan.rpc.common.utils.NetUtils;
import com.fan.rpc.common.utils.StringUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static com.fan.rpc.common.utils.StringUtils.isBlank;


/**
 * URL - Uniform Resource Locator (Immutable, ThreadSafe)
 * <p>
 * url example:
 * <ul>
 * <li>http://www.facebook.com/friends?param1=value1&amp;param2=value2
 * <li>http://username:password@10.20.130.230:8080/list?version=1.0.0
 * <li>ftp://username:password@192.168.1.7:21/1/read.txt
 * <li>registry://192.168.1.7:9090/org.apache.dubbo.service1?param1=value1&amp;param2=value2
 * </ul>
 * <p>
 * Some strange example below:
 * <ul>
 * <li>192.168.1.3:20880<br>
 * for this case, url protocol = null, url host = 192.168.1.3, port = 20880, url path = null
 * <li>file:///home/user1/router.js?type=script<br>
 * for this case, url protocol = file, url host = null, url path = home/user1/router.js
 * <li>file://home/user1/router.js?type=script<br>
 * for this case, url protocol = file, url host = home, url path = user1/router.js
 * <li>file:///D:/1/router.js?type=script<br>
 * for this case, url protocol = file, url host = null, url path = D:/1/router.js
 * <li>file:/D:/1/router.js?type=script<br>
 * same as above file:///D:/1/router.js?type=script
 * <li>/home/user1/router.js?type=script <br>
 * for this case, url protocol = null, url host = null, url path = home/user1/router.js
 * <li>home/user1/router.js?type=script <br>
 * for this case, url protocol = null, url host = home, url path = user1/router.js
 * </ul>
 *
 * @see java.net.URL
 * @see java.net.URI
 */
public /*final**/
class URL implements Serializable {

    private static final long serialVersionUID = -1985165475234910535L;

    private final String protocol;

    private final String username;

    private final String password;

    // by default, host to registry
    private final String host;

    // by default, port to registry
    private final int port;

    private final String path;

    private final Map<String, String> parameters;

    private final Map<String, Map<String, String>> methodParameters;

    // ==== cache ====

    private volatile transient Map<String, Number> numbers;

    private volatile transient Map<String, Map<String, Number>> methodNumbers;

    private volatile transient Map<String, URL> urls;

    private volatile transient String ip;

    private volatile transient String full;

    private volatile transient String identity;

    private volatile transient String parameter;

    private volatile transient String string;

    private transient String serviceKey;

    private transient String address;

    protected URL() {
        this.protocol = null;
        this.username = null;
        this.password = null;
        this.host = null;
        this.port = 0;
        this.address = null;
        this.path = null;
        this.parameters = null;
        this.methodParameters = null;
    }

    public URL(String protocol, String host, int port) {
        this(protocol, null, null, host, port, null, (Map<String, String>) null);
    }

    public URL(String protocol, String host, int port, String[] pairs) { // varargs ... conflict with the following path argument, use array instead.
        this(protocol, null, null, host, port, null, CollectionUtils.toStringMap(pairs));
    }

    public URL(String protocol, String host, int port, Map<String, String> parameters) {
        this(protocol, null, null, host, port, null, parameters);
    }

    public URL(String protocol, String host, int port, String path) {
        this(protocol, null, null, host, port, path, (Map<String, String>) null);
    }

    public URL(String protocol, String host, int port, String path, String... pairs) {
        this(protocol, null, null, host, port, path, CollectionUtils.toStringMap(pairs));
    }

    public URL(String protocol, String host, int port, String path, Map<String, String> parameters) {
        this(protocol, null, null, host, port, path, parameters);
    }

    public URL(String protocol, String username, String password, String host, int port, String path) {
        this(protocol, username, password, host, port, path, (Map<String, String>) null);
    }

    public URL(String protocol, String username, String password, String host, int port, String path, String... pairs) {
        this(protocol, username, password, host, port, path, CollectionUtils.toStringMap(pairs));
    }

    public URL(String protocol,
               String username,
               String password,
               String host,
               int port,
               String path,
               Map<String, String> parameters) {
        this(protocol, username, password, host, port, path, parameters, toMethodParameters(parameters));
    }

    public URL(String protocol,
               String username,
               String password,
               String host,
               int port,
               String path,
               Map<String, String> parameters,
               Map<String, Map<String, String>> methodParameters) {
        if (StringUtils.isEmpty(username)
                && StringUtils.isNotEmpty(password)) {
            throw new IllegalArgumentException("Invalid url, password without username!");
        }
        this.protocol = protocol;
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = Math.max(port, 0);
        this.address = getAddress(this.host, this.port);

        // trim the beginning "/"
        while (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        this.path = path;
        if (parameters == null) {
            parameters = new HashMap<>();
        } else {
            parameters = new HashMap<>(parameters);
        }
        this.parameters = Collections.unmodifiableMap(parameters);
        this.methodParameters = Collections.unmodifiableMap(methodParameters);
    }

    private static String getAddress(String host, int port) {
        return port <= 0 ? host : host + ':' + port;
    }


    public static URL valueOf(String url) {
       return null;
    }

    public static Map<String, Map<String, String>> toMethodParameters(Map<String, String> parameters) {
        Map<String, Map<String, String>> methodParameters = new HashMap<>();
        return methodParameters;
    }

    public static URL valueOf(String url, String... reserveParams) {
        URL result = valueOf(url);
        if (reserveParams == null || reserveParams.length == 0) {
            return result;
        }
        Map<String, String> newMap = new HashMap<>(reserveParams.length);
        Map<String, String> oldMap = result.getParameters();
        for (String reserveParam : reserveParams) {
            String tmp = oldMap.get(reserveParam);
            if (StringUtils.isNotEmpty(tmp)) {
                newMap.put(reserveParam, tmp);
            }
        }
        return result.clearParameters().addParameters(newMap);
    }

    public static URL valueOf(URL url, String[] reserveParams, String[] reserveParamPrefixs) {
        Map<String, String> newMap = new HashMap<>();
        Map<String, String> oldMap = url.getParameters();
        if (reserveParamPrefixs != null && reserveParamPrefixs.length != 0) {
            for (Map.Entry<String, String> entry : oldMap.entrySet()) {
                for (String reserveParamPrefix : reserveParamPrefixs) {
                    if (entry.getKey().startsWith(reserveParamPrefix) && StringUtils.isNotEmpty(entry.getValue())) {
                        newMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        if (reserveParams != null) {
            for (String reserveParam : reserveParams) {
                String tmp = oldMap.get(reserveParam);
                if (StringUtils.isNotEmpty(tmp)) {
                    newMap.put(reserveParam, tmp);
                }
            }
        }
        return newMap.isEmpty() ? new URL(url.getProtocol(), url.getUsername(), url.getPassword(), url.getHost(), url.getPort(), url.getPath())
                : new URL(url.getProtocol(), url.getUsername(), url.getPassword(), url.getHost(), url.getPort(), url.getPath(), newMap);
    }

    public static String encode(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String decode(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    static String appendDefaultPort(String address, int defaultPort) {
        if (address != null && address.length() > 0 && defaultPort > 0) {
            int i = address.indexOf(':');
            if (i < 0) {
                return address + ":" + defaultPort;
            } else if (Integer.parseInt(address.substring(i + 1)) == 0) {
                return address.substring(0, i + 1) + defaultPort;
            }
        }
        return address;
    }

    public String getProtocol() {
        return protocol;
    }

    public URL setProtocol(String protocol) {
        return new URL(protocol, username, password, host, port, path, getParameters());
    }

    public String getUsername() {
        return username;
    }

    public URL setUsername(String username) {
        return new URL(protocol, username, password, host, port, path, getParameters());
    }

    public String getPassword() {
        return password;
    }

    public URL setPassword(String password) {
        return new URL(protocol, username, password, host, port, path, getParameters());
    }

    public String getAuthority() {
        if (StringUtils.isEmpty(username)
                && StringUtils.isEmpty(password)) {
            return null;
        }
        return (username == null ? "" : username)
                + ":" + (password == null ? "" : password);
    }

    public String getHost() {
        return host;
    }

    public URL setHost(String host) {
        return new URL(protocol, username, password, host, port, path, getParameters());
    }

    /**
     * Fetch IP address for this URL.
     * <p>
     * Pls. note that IP should be used instead of Host when to compare with socket's address or to search in a map
     * which use address as its key.
     *
     * @return ip in string format
     */
    public String getIp() {
        if (ip == null) {
            ip = NetUtils.getIpByHost(host);
        }
        return ip;
    }

    public int getPort() {
        return port;
    }

    public URL setPort(int port) {
        return new URL(protocol, username, password, host, port, path, getParameters());
    }

    public int getPort(int defaultPort) {
        return port <= 0 ? defaultPort : port;
    }

    public String getAddress() {
        if (address == null) {
            address = getAddress(host, port);
        }
        return address;
    }

    public URL setAddress(String address) {
        int i = address.lastIndexOf(':');
        String host;
        int port = this.port;
        if (i >= 0) {
            host = address.substring(0, i);
            port = Integer.parseInt(address.substring(i + 1));
        } else {
            host = address;
        }
        return new URL(protocol, username, password, host, port, path, getParameters());
    }

    public String getBackupAddress() {
        return getBackupAddress(0);
    }

    public String getBackupAddress(int defaultPort) {
        StringBuilder address = new StringBuilder(appendDefaultPort(getAddress(), defaultPort));

        return address.toString();
    }

    public List<URL> getBackupUrls() {
        List<URL> urls = new ArrayList<>();
        urls.add(this);

        return urls;
    }

    public String getPath() {
        return path;
    }

    public URL setPath(String path) {
        return new URL(protocol, username, password, host, port, path, getParameters());
    }

    public String getAbsolutePath() {
        if (path != null && !path.startsWith("/")) {
            return "/" + path;
        }
        return path;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Get the parameters to be selected(filtered)
     *
     * @param nameToSelect the {@link Predicate} to select the parameter name
     * @return non-null {@link Map}
     * @since 2.7.8
     */
    public Map<String, String> getParameters(Predicate<String> nameToSelect) {
        Map<String, String> selectedParameters = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : getParameters().entrySet()) {
            String name = entry.getKey();
            if (nameToSelect.test(name)) {
                selectedParameters.put(name, entry.getValue());
            }
        }
        return Collections.unmodifiableMap(selectedParameters);
    }

    public Map<String, Map<String, String>> getMethodParameters() {
        return methodParameters;
    }

    public String getParameterAndDecoded(String key) {
        return getParameterAndDecoded(key, null);
    }

    public String getParameterAndDecoded(String key, String defaultValue) {
        return decode(getParameter(key, defaultValue));
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public String getParameter(String key, String defaultValue) {
        String value = getParameter(key);
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    /**
     * Get parameter
     *
     * @param key       the key of parameter
     * @param valueType the type of parameter value
     * @param <T>       the type of parameter value
     * @return get the parameter if present, or <code>null</code>
     * @since 2.7.8
     */
    public <T> T getParameter(String key, Class<T> valueType) {
        return getParameter(key, valueType, null);
    }

    /**
     * Get parameter
     *
     * @param key          the key of parameter
     * @param valueType    the type of parameter value
     * @param defaultValue the default value if parameter is absent
     * @param <T>          the type of parameter value
     * @return get the parameter if present, or <code>defaultValue</code> will be used.
     * @since 2.7.8
     */
    public <T> T getParameter(String key, Class<T> valueType, T defaultValue) {
        String value = getParameter(key);
        T result = null;
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    private Map<String, Number> getNumbers() {
        // concurrent initialization is tolerant
        if (numbers == null) {
            numbers = new ConcurrentHashMap<>();
        }
        return numbers;
    }

    private Map<String, Map<String, Number>> getMethodNumbers() {
        if (methodNumbers == null) { // concurrent initialization is tolerant
            methodNumbers = new ConcurrentHashMap<>();
        }
        return methodNumbers;
    }

    private Map<String, URL> getUrls() {
        // concurrent initialization is tolerant
        if (urls == null) {
            urls = new ConcurrentHashMap<>();
        }
        return urls;
    }

    public URL getUrlParameter(String key) {
        URL u = getUrls().get(key);
        if (u != null) {
            return u;
        }
        String value = getParameterAndDecoded(key);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        u = URL.valueOf(value);
        getUrls().put(key, u);
        return u;
    }

    public double getParameter(String key, double defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.doubleValue();
        }
        String value = getParameter(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        double d = Double.parseDouble(value);
        getNumbers().put(key, d);
        return d;
    }

    public float getParameter(String key, float defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.floatValue();
        }
        String value = getParameter(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        float f = Float.parseFloat(value);
        getNumbers().put(key, f);
        return f;
    }

    public long getParameter(String key, long defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.longValue();
        }
        String value = getParameter(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        long l = Long.parseLong(value);
        getNumbers().put(key, l);
        return l;
    }

    public int getParameter(String key, int defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.intValue();
        }
        String value = getParameter(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        int i = Integer.parseInt(value);
        getNumbers().put(key, i);
        return i;
    }

    public short getParameter(String key, short defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.shortValue();
        }
        String value = getParameter(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        short s = Short.parseShort(value);
        getNumbers().put(key, s);
        return s;
    }

    public byte getParameter(String key, byte defaultValue) {
        Number n = getNumbers().get(key);
        if (n != null) {
            return n.byteValue();
        }
        String value = getParameter(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        byte b = Byte.parseByte(value);
        getNumbers().put(key, b);
        return b;
    }

    public float getPositiveParameter(String key, float defaultValue) {
        if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue <= 0");
        }
        float value = getParameter(key, defaultValue);
        return value <= 0 ? defaultValue : value;
    }

    public double getPositiveParameter(String key, double defaultValue) {
        if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue <= 0");
        }
        double value = getParameter(key, defaultValue);
        return value <= 0 ? defaultValue : value;
    }

    public long getPositiveParameter(String key, long defaultValue) {
        if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue <= 0");
        }
        long value = getParameter(key, defaultValue);
        return value <= 0 ? defaultValue : value;
    }

    public int getPositiveParameter(String key, int defaultValue) {
        if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue <= 0");
        }
        int value = getParameter(key, defaultValue);
        return value <= 0 ? defaultValue : value;
    }

    public short getPositiveParameter(String key, short defaultValue) {
        if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue <= 0");
        }
        short value = getParameter(key, defaultValue);
        return value <= 0 ? defaultValue : value;
    }

    public byte getPositiveParameter(String key, byte defaultValue) {
        if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue <= 0");
        }
        byte value = getParameter(key, defaultValue);
        return value <= 0 ? defaultValue : value;
    }

    public char getParameter(String key, char defaultValue) {
        String value = getParameter(key);
        return StringUtils.isEmpty(value) ? defaultValue : value.charAt(0);
    }

    public boolean getParameter(String key, boolean defaultValue) {
        String value = getParameter(key);
        return StringUtils.isEmpty(value) ? defaultValue : Boolean.parseBoolean(value);
    }

    public boolean hasParameter(String key) {
        String value = getParameter(key);
        return value != null && value.length() > 0;
    }

    public String getMethodParameterAndDecoded(String method, String key) {
        return URL.decode(getMethodParameter(method, key));
    }

    public String getMethodParameterAndDecoded(String method, String key, String defaultValue) {
        return URL.decode(getMethodParameter(method, key, defaultValue));
    }

    public String getMethodParameter(String method, String key) {
        Map<String, String> keyMap = methodParameters.get(method);
        String value = null;
        if (keyMap != null) {
            value = keyMap.get(key);
        }
        if (StringUtils.isEmpty(value)) {
            value = parameters.get(key);
        }
        return value;
    }

    public String getMethodParameter(String method, String key, String defaultValue) {
        String value = getMethodParameter(method, key);
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    public double getMethodParameter(String method, String key, double defaultValue) {
        Number n = getCachedNumber(method, key);
        if (n != null) {
            return n.doubleValue();
        }
        String value = getMethodParameter(method, key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        double d = Double.parseDouble(value);
        updateCachedNumber(method, key, d);
        return d;
    }

    public float getMethodParameter(String method, String key, float defaultValue) {
        Number n = getCachedNumber(method, key);
        if (n != null) {
            return n.floatValue();
        }
        String value = getMethodParameter(method, key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        float f = Float.parseFloat(value);
        updateCachedNumber(method, key, f);
        return f;
    }

    public long getMethodParameter(String method, String key, long defaultValue) {
        Number n = getCachedNumber(method, key);
        if (n != null) {
            return n.longValue();
        }
        String value = getMethodParameter(method, key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        long l = Long.parseLong(value);
        updateCachedNumber(method, key, l);
        return l;
    }

    public int getMethodParameter(String method, String key, int defaultValue) {
        Number n = getCachedNumber(method, key);
        if (n != null) {
            return n.intValue();
        }
        String value = getMethodParameter(method, key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        int i = Integer.parseInt(value);
        updateCachedNumber(method, key, i);
        return i;
    }

    public short getMethodParameter(String method, String key, short defaultValue) {
        Number n = getCachedNumber(method, key);
        if (n != null) {
            return n.shortValue();
        }
        String value = getMethodParameter(method, key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        short s = Short.parseShort(value);
        updateCachedNumber(method, key, s);
        return s;
    }

    public byte getMethodParameter(String method, String key, byte defaultValue) {
        Number n = getCachedNumber(method, key);
        if (n != null) {
            return n.byteValue();
        }
        String value = getMethodParameter(method, key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        byte b = Byte.parseByte(value);
        updateCachedNumber(method, key, b);
        return b;
    }

    private Number getCachedNumber(String method, String key) {
        Map<String, Number> keyNumber = getMethodNumbers().get(method);
        if (keyNumber != null) {
            return keyNumber.get(key);
        }
        return null;
    }

    private void updateCachedNumber(String method, String key, Number n) {
        Map<String, Number> keyNumber = getMethodNumbers().computeIfAbsent(method, m -> new HashMap<>());
        keyNumber.put(key, n);
    }

    public double getMethodPositiveParameter(String method, String key, double defaultValue) {
        if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue <= 0");
        }
        double value = getMethodParameter(method, key, defaultValue);
        return value <= 0 ? defaultValue : value;
    }

    public float getMethodPositiveParameter(String method, String key, float defaultValue) {
        if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue <= 0");
        }
        float value = getMethodParameter(method, key, defaultValue);
        return value <= 0 ? defaultValue : value;
    }

    public long getMethodPositiveParameter(String method, String key, long defaultValue) {
        if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue <= 0");
        }
        long value = getMethodParameter(method, key, defaultValue);
        return value <= 0 ? defaultValue : value;
    }

    public int getMethodPositiveParameter(String method, String key, int defaultValue) {
        if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue <= 0");
        }
        int value = getMethodParameter(method, key, defaultValue);
        return value <= 0 ? defaultValue : value;
    }

    public short getMethodPositiveParameter(String method, String key, short defaultValue) {
        if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue <= 0");
        }
        short value = getMethodParameter(method, key, defaultValue);
        return value <= 0 ? defaultValue : value;
    }

    public byte getMethodPositiveParameter(String method, String key, byte defaultValue) {
        if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue <= 0");
        }
        byte value = getMethodParameter(method, key, defaultValue);
        return value <= 0 ? defaultValue : value;
    }

    public char getMethodParameter(String method, String key, char defaultValue) {
        String value = getMethodParameter(method, key);
        return StringUtils.isEmpty(value) ? defaultValue : value.charAt(0);
    }

    public boolean getMethodParameter(String method, String key, boolean defaultValue) {
        String value = getMethodParameter(method, key);
        return StringUtils.isEmpty(value) ? defaultValue : Boolean.parseBoolean(value);
    }

    public boolean hasMethodParameter(String method, String key) {
        if (method == null) {
            String suffix = "." + key;
            for (String fullKey : parameters.keySet()) {
                if (fullKey.endsWith(suffix)) {
                    return true;
                }
            }
            return false;
        }
        if (key == null) {
            String prefix = method + ".";
            for (String fullKey : parameters.keySet()) {
                if (fullKey.startsWith(prefix)) {
                    return true;
                }
            }
            return false;
        }
        String value = getMethodParameter(method, key);
        return StringUtils.isNotEmpty(value);
    }

    public boolean hasMethodParameter(String method) {
        if (method == null) {
            return false;
        }
        return getMethodParameters().containsKey(method);
    }


    public URL addParameterAndEncoded(String key, String value) {
        if (StringUtils.isEmpty(value)) {
            return this;
        }
        return addParameter(key, encode(value));
    }

    public URL addParameter(String key, boolean value) {
        return addParameter(key, String.valueOf(value));
    }

    public URL addParameter(String key, char value) {
        return addParameter(key, String.valueOf(value));
    }

    public URL addParameter(String key, byte value) {
        return addParameter(key, String.valueOf(value));
    }

    public URL addParameter(String key, short value) {
        return addParameter(key, String.valueOf(value));
    }

    public URL addParameter(String key, int value) {
        return addParameter(key, String.valueOf(value));
    }

    public URL addParameter(String key, long value) {
        return addParameter(key, String.valueOf(value));
    }

    public URL addParameter(String key, float value) {
        return addParameter(key, String.valueOf(value));
    }

    public URL addParameter(String key, double value) {
        return addParameter(key, String.valueOf(value));
    }

    public URL addParameter(String key, Enum<?> value) {
        if (value == null) {
            return this;
        }
        return addParameter(key, String.valueOf(value));
    }

    public URL addParameter(String key, Number value) {
        if (value == null) {
            return this;
        }
        return addParameter(key, String.valueOf(value));
    }

    public URL addParameter(String key, CharSequence value) {
        if (value == null || value.length() == 0) {
            return this;
        }
        return addParameter(key, String.valueOf(value));
    }

    public URL addParameter(String key, String value) {
        if (StringUtils.isEmpty(key)
                || StringUtils.isEmpty(value)) {
            return this;
        }
        // if value doesn't change, return immediately
        if (value.equals(getParameters().get(key))) { // value != null
            return this;
        }

        Map<String, String> map = new HashMap<>(getParameters());
        map.put(key, value);

        return new URL(protocol, username, password, host, port, path, map);
    }

    public URL addParameterIfAbsent(String key, String value) {
        if (StringUtils.isEmpty(key)
                || StringUtils.isEmpty(value)) {
            return this;
        }
        if (hasParameter(key)) {
            return this;
        }
        Map<String, String> map = new HashMap<>(getParameters());
        map.put(key, value);

        return new URL(protocol, username, password, host, port, path, map);
    }

    public URL addMethodParameter(String method, String key, String value) {
        if (StringUtils.isEmpty(method)
                || StringUtils.isEmpty(key)
                || StringUtils.isEmpty(value)) {
            return this;
        }

        Map<String, String> map = new HashMap<>(getParameters());
        map.put(method + "." + key, value);
        Map<String, Map<String, String>> methodMap = toMethodParameters(map);
        URL.putMethodParameter(method, key, value, methodMap);

        return new URL(protocol, username, password, host, port, path, map, methodMap);
    }

    public URL addMethodParameterIfAbsent(String method, String key, String value) {
        if (StringUtils.isEmpty(method)
                || StringUtils.isEmpty(key)
                || StringUtils.isEmpty(value)) {
            return this;
        }
        if (hasMethodParameter(method, key)) {
            return this;
        }

        Map<String, String> map = new HashMap<>(getParameters());
        map.put(method + "." + key, value);
        Map<String, Map<String, String>> methodMap = toMethodParameters(map);
        URL.putMethodParameter(method, key, value, methodMap);

        return new URL(protocol, username, password, host, port, path, map, methodMap);
    }

    /**
     * Add parameters to a new url.
     *
     * @param parameters parameters in key-value pairs
     * @return A new URL
     */
    public URL addParameters(Map<String, String> parameters) {
        if (CollectionUtils.isEmptyMap(parameters)) {
            return this;
        }

        boolean hasAndEqual = true;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String value = getParameters().get(entry.getKey());
            if (value == null) {
                if (entry.getValue() != null) {
                    hasAndEqual = false;
                    break;
                }
            } else {
                if (!value.equals(entry.getValue())) {
                    hasAndEqual = false;
                    break;
                }
            }
        }
        // return immediately if there's no change
        if (hasAndEqual) {
            return this;
        }

        Map<String, String> map = new HashMap<>(getParameters());
        map.putAll(parameters);
        return new URL(protocol, username, password, host, port, path, map);
    }

    public URL addParametersIfAbsent(Map<String, String> parameters) {
        if (CollectionUtils.isEmptyMap(parameters)) {
            return this;
        }
        Map<String, String> map = new HashMap<>(parameters);
        map.putAll(getParameters());
        return new URL(protocol, username, password, host, port, path, map);
    }

    public URL addParameters(String... pairs) {
        if (pairs == null || pairs.length == 0) {
            return this;
        }
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("Map pairs can not be odd number.");
        }
        Map<String, String> map = new HashMap<>();
        int len = pairs.length / 2;
        for (int i = 0; i < len; i++) {
            map.put(pairs[2 * i], pairs[2 * i + 1]);
        }
        return addParameters(map);
    }

    public URL addParameterString(String query) {
        if (StringUtils.isEmpty(query)) {
            return this;
        }
        return addParameters(StringUtils.parseQueryString(query));
    }

    public URL removeParameter(String key) {
        if (StringUtils.isEmpty(key)) {
            return this;
        }
        return removeParameters(key);
    }

    public URL removeParameters(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return this;
        }
        return removeParameters(keys.toArray(new String[0]));
    }

    public URL removeParameters(String... keys) {
        if (keys == null || keys.length == 0) {
            return this;
        }
        Map<String, String> map = new HashMap<>(getParameters());
        for (String key : keys) {
            map.remove(key);
        }
        if (map.size() == getParameters().size()) {
            return this;
        }
        return new URL(protocol, username, password, host, port, path, map);
    }

    public URL clearParameters() {
        return new URL(protocol, username, password, host, port, path, new HashMap<>());
    }



    public String toParameterString() {
        if (parameter != null) {
            return parameter;
        }
        return parameter = toParameterString(new String[0]);
    }

    public String toParameterString(String... parameters) {
        StringBuilder buf = new StringBuilder();
        buildParameters(buf, false, parameters);
        return buf.toString();
    }

    private void buildParameters(StringBuilder buf, boolean concat, String[] parameters) {
        if (CollectionUtils.isNotEmptyMap(getParameters())) {
            List<String> includes = (ArrayUtils.isEmpty(parameters) ? null : Arrays.asList(parameters));
            boolean first = true;
            for (Map.Entry<String, String> entry : new TreeMap<>(getParameters()).entrySet()) {
                if (StringUtils.isNotEmpty(entry.getKey())
                        && (includes == null || includes.contains(entry.getKey()))) {
                    if (first) {
                        if (concat) {
                            buf.append("?");
                        }
                        first = false;
                    } else {
                        buf.append("&");
                    }
                    buf.append(entry.getKey());
                    buf.append("=");
                    buf.append(entry.getValue() == null ? "" : entry.getValue().trim());
                }
            }
        }
    }

    public java.net.URL toJavaURL() {
        try {
            return new java.net.URL(toString());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public InetSocketAddress toInetSocketAddress() {
        return new InetSocketAddress(host, port);
    }


    private void append(StringBuilder target, String parameterName, boolean first) {
        String parameterValue = this.getParameter(parameterName);
        if (!isBlank(parameterValue)) {
            if (!first) {
                target.append(":");
            }
            target.append(parameterValue);
        } else {
            target.append(":");
        }
    }






    /**
     * @see #getParameter(String, int)
     * @deprecated Replace to <code>getParameter(String, int)</code>
     */
    @Deprecated
    public int getIntParameter(String key) {
        return getParameter(key, 0);
    }

    /**
     * @see #getParameter(String, int)
     * @deprecated Replace to <code>getParameter(String, int)</code>
     */
    @Deprecated
    public int getIntParameter(String key, int defaultValue) {
        return getParameter(key, defaultValue);
    }

    /**
     * @see #getPositiveParameter(String, int)
     * @deprecated Replace to <code>getPositiveParameter(String, int)</code>
     */
    @Deprecated
    public int getPositiveIntParameter(String key, int defaultValue) {
        return getPositiveParameter(key, defaultValue);
    }

    /**
     * @see #getParameter(String, boolean)
     * @deprecated Replace to <code>getParameter(String, boolean)</code>
     */
    @Deprecated
    public boolean getBooleanParameter(String key) {
        return getParameter(key, false);
    }

    /**
     * @see #getParameter(String, boolean)
     * @deprecated Replace to <code>getParameter(String, boolean)</code>
     */
    @Deprecated
    public boolean getBooleanParameter(String key, boolean defaultValue) {
        return getParameter(key, defaultValue);
    }

    /**
     * @see #getMethodParameter(String, String, int)
     * @deprecated Replace to <code>getMethodParameter(String, String, int)</code>
     */
    @Deprecated
    public int getMethodIntParameter(String method, String key) {
        return getMethodParameter(method, key, 0);
    }

    /**
     * @see #getMethodParameter(String, String, int)
     * @deprecated Replace to <code>getMethodParameter(String, String, int)</code>
     */
    @Deprecated
    public int getMethodIntParameter(String method, String key, int defaultValue) {
        return getMethodParameter(method, key, defaultValue);
    }

    /**
     * @see #getMethodPositiveParameter(String, String, int)
     * @deprecated Replace to <code>getMethodPositiveParameter(String, String, int)</code>
     */
    @Deprecated
    public int getMethodPositiveIntParameter(String method, String key, int defaultValue) {
        return getMethodPositiveParameter(method, key, defaultValue);
    }

    /**
     * @see #getMethodParameter(String, String, boolean)
     * @deprecated Replace to <code>getMethodParameter(String, String, boolean)</code>
     */
    @Deprecated
    public boolean getMethodBooleanParameter(String method, String key) {
        return getMethodParameter(method, key, false);
    }

    /**
     * @see #getMethodParameter(String, String, boolean)
     * @deprecated Replace to <code>getMethodParameter(String, String, boolean)</code>
     */
    @Deprecated
    public boolean getMethodBooleanParameter(String method, String key, boolean defaultValue) {
        return getMethodParameter(method, key, defaultValue);
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + port;
        result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        URL other = (URL) obj;
        if (!StringUtils.isEquals(host, other.host)) {
            return false;
        }
        if (parameters == null) {
            if (other.parameters != null) {
                return false;
            }
        } else if (!parameters.equals(other.parameters)) {
            return false;
        }
        if (!StringUtils.isEquals(password, other.password)) {
            return false;
        }
        if (!StringUtils.isEquals(path, other.path)) {
            return false;
        }
        if (port != other.port) {
            return false;
        }
        if (!StringUtils.isEquals(protocol, other.protocol)) {
            return false;
        }
        if (!StringUtils.isEquals(username, other.username)) {
            return false;
        }
        return true;
    }

    public static void putMethodParameter(String method, String key, String value, Map<String, Map<String, String>> methodParameters) {
        Map<String, String> subParameter = methodParameters.computeIfAbsent(method, k -> new HashMap<>());
        subParameter.put(key, value);
    }

}