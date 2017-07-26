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

package com.adeptj.modules.webconsole.security;

import org.apache.felix.webconsole.WebConsoleSecurityProvider;
import org.apache.felix.webconsole.WebConsoleSecurityProvider3;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static javax.servlet.http.HttpServletResponse.SC_FOUND;
import static org.osgi.service.http.HttpContext.AUTHORIZATION;
import static org.osgi.service.http.HttpContext.REMOTE_USER;

/**
 * Felix {@link WebConsoleSecurityProvider} implementation.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = WebConsoleSecurityRoleConfig.class)
@Component(immediate = true, service = WebConsoleSecurityProvider.class)
public class OSGiConsoleSecurityProvider implements WebConsoleSecurityProvider3 {

	private static final String URL_TOOLS_LOGOUT = "/tools/logout";

	private static final String HEADER_LOC = "Location";

	private static final String ADMIN = "admin";
	
	private String[] roles;

	/**
	 * Role [OSGiAdmin] is already set by Undertow SecurityHandler.
	 */
	@Override
	public boolean authenticate(HttpServletRequest request, HttpServletResponse response) {
        return Arrays.stream(this.roles).map(request::isUserInRole).count() != 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object authenticate(String username, String password) {
		return ADMIN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean authorize(Object user, String role) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		// Note: Semantics of this method states that Session invalidation should not happen here.
		// Not using response.sendRedirect due to exception handling we need to do, avoiding that.
		// Set the status to [302] and location header to [/tools/logout] so that browser could redirect there.
		// AuthServlet will take care of Session invalidation later.
        request.removeAttribute(REMOTE_USER);
        request.removeAttribute(AUTHORIZATION);
		response.setStatus(SC_FOUND);
		response.setHeader(HEADER_LOC, URL_TOOLS_LOGOUT);
	}

	// LifeCycle methods

    @Activate
    protected void start(WebConsoleSecurityRoleConfig config) {
	    this.roles = config.roles();
    }

}
