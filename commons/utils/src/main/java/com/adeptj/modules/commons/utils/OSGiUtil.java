/*
###############################################################################
#                                                                             # 
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/
package com.adeptj.modules.commons.utils;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import java.util.Optional;

import static org.osgi.framework.Constants.FRAGMENT_HOST;
import static org.osgi.framework.Constants.OBJECTCLASS;
import static org.osgi.framework.Constants.SERVICE_DESCRIPTION;

/**
 * Utility for creating OSGi Filter for tracking/finding Services etc.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class OSGiUtil {

    private static final String FILTER_AND = "(&(";

    private static final String EQ = "=";

    private static final String ASTERISK = "*";

    private static final String PARENTHESIS_CLOSE = ")";

    // No instantiation. Utility methods only.
    private OSGiUtil() {
    }

    public static boolean isFragment(Bundle bundle) {
        return bundle.getHeaders().get(FRAGMENT_HOST) != null;
    }

    public static boolean isNotFragment(Bundle bundle) {
        return bundle.getHeaders().get(FRAGMENT_HOST) == null;
    }

    public static Filter specificServiceFilter(BundleContext context, Class<?> objectClass, String filterExpr) {
        try {
            return context.createFilter(FILTER_AND +
                    OBJECTCLASS +
                    EQ +
                    objectClass.getName() +
                    PARENTHESIS_CLOSE +
                    filterExpr +
                    PARENTHESIS_CLOSE);
        } catch (InvalidSyntaxException ex) {
            // Filter expression is malformed, not RFC-1960 based Filter.
            throw new IllegalArgumentException(ex);
        }
    }

    public static Filter anyServiceFilter(BundleContext context, String filterExpr) {
        try {
            return context.createFilter(FILTER_AND +
                    OBJECTCLASS +
                    EQ +
                    ASTERISK +
                    PARENTHESIS_CLOSE +
                    filterExpr +
                    PARENTHESIS_CLOSE);
        } catch (InvalidSyntaxException ex) {
            // Filter expression is malformed, not RFC-1960 based Filter.
            throw new IllegalArgumentException(ex);
        }
    }

    public void close(ServiceTracker<Object, Object> tracker) {
        tracker.close();
    }

    public void closeQuietly(ServiceTracker<Object, Object> tracker) {
        try {
            tracker.close();
        } catch (Exception ex) { // NOSONAR
            // Ignore, anyway Framework is managing it as the Tracked service is being removed from service registry.
        }
    }

    public static void unregisterService(ServiceRegistration<?> registration) {
        Optional.ofNullable(registration).ifPresent(ServiceRegistration::unregister);
    }

    public static String getServiceDesc(ServiceReference<?> reference) {
        return (String) reference.getProperty(SERVICE_DESCRIPTION);
    }
}