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
package com.adeptj.modules.viewengine.core;

/**
 * ViewEngineException.
 * 
 * @author Rakesh.Kumar, AdeptJ.
 */
public class ViewEngineException extends RuntimeException {

	private static final long serialVersionUID = 3619274574814729058L;

	public ViewEngineException(String message) {
		super(message);
	}

	public ViewEngineException(String message, Throwable cause) {
		super(message, cause);
	}

	public ViewEngineException(Throwable cause) {
		super(cause);
	}
}
