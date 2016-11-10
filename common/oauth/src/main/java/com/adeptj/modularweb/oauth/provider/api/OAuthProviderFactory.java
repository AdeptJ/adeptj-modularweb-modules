/* 
 * =============================================================================
 * 
 * Copyright (c) 2016 AdeptJ
 * Copyright (c) 2016 Rakesh Kumar <irakeshk@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * =============================================================================
 */
package com.adeptj.modularweb.oauth.provider.api;

import com.adeptj.modularweb.oauth.common.OAuthProvider;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
 * OAuthProviderFactory.
 * 
 * @author Rakesh.Kumar, AdeptJ
 */
public interface OAuthProviderFactory {

	OAuthProvider getProvider(String providerName);
	
	void addOAuth2Service(String providerName, OAuth20Service service);
	
	OAuth20Service getOAuth2Service(String providerName);
}
