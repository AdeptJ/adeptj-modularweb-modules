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

package com.adeptj.modules.commons.validator.service.internal;

import com.adeptj.modules.commons.validator.service.ValidatorService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.parameternameprovider.ParanamerParameterNameProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.NoProviderFoundException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.lang.invoke.MethodHandles;
import java.util.Set;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * {@link HibernateValidator} based ValidatorService.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true)
public class HibernateValidatorService implements ValidatorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private ValidatorFactory validatorFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void validate(T instance) {
        Validate.notNull(instance, "Object to be validated can't be null!!");
        Set<ConstraintViolation<T>> violations = this.validatorFactory.getValidator().validate(instance);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Set<ConstraintViolation<T>> validateProperty(T instance, String property) {
        Validate.notNull(instance, "Object to be validated can't be null!!");
        Validate.isTrue(StringUtils.isNotEmpty(property), "property [%s] can't be blank!!", property);
        return this.validatorFactory.getValidator().validateProperty(instance, property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Set<ConstraintViolation<T>> getConstraintViolations(T instance) {
        Validate.notNull(instance, "Object to be validated can't be null!!");
        return this.validatorFactory.getValidator().validate(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }

    // --------------------------------------------------- INTERNAL ----------------------------------------------------

    @Activate
    protected void start() {
        try {
            final long startTime = System.nanoTime();
            this.validatorFactory = Validation.byProvider(HibernateValidator.class)
                    .configure()
                    .parameterNameProvider(new ParanamerParameterNameProvider())
                    .buildValidatorFactory();
            long endTime = System.nanoTime() - startTime;
            LOGGER.info("HibernateValidator initialized in [{}] ms!!", NANOSECONDS.toMillis(endTime));
        } catch (NoProviderFoundException ex) {
            LOGGER.error("Could not create ValidatorFactory!!", ex);
            throw ex;
        }
    }
}
