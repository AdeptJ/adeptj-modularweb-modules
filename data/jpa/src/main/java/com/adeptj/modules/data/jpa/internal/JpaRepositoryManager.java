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

package com.adeptj.modules.data.jpa.internal;

import com.adeptj.modules.commons.jdbc.service.DataSourceService;
import com.adeptj.modules.commons.validator.service.ValidatorService;
import com.adeptj.modules.data.jpa.JpaRepository;
import com.adeptj.modules.data.jpa.core.EntityManagerFactoryConfig;
import com.adeptj.modules.data.jpa.core.JpaProperties;
import com.adeptj.modules.data.jpa.exception.JpaBootstrapException;
import com.adeptj.modules.data.jpa.exception.JpaRepositoryBindException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.ValidationMode;
import java.lang.invoke.MethodHandles;
import java.util.Map;

import static javax.persistence.ValidationMode.AUTO;
import static javax.persistence.ValidationMode.CALLBACK;
import static org.eclipse.persistence.config.PersistenceUnitProperties.NON_JTA_DATASOURCE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.VALIDATOR_FACTORY;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.jpa.EntityManagerFactoryBuilder.JPA_UNIT_NAME;

/**
 * Manages {@link javax.persistence.EntityManagerFactory} and sets it to the {@link JpaRepository} implementation.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = EntityManagerFactoryConfig.class)
@Component(service = JpaRepositoryManager.class, immediate = true, configurationPolicy = REQUIRE)
public class JpaRepositoryManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private JpaRepositoryWrapper repositoryWrapper;

    @Reference
    private DataSourceService dataSourceService;

    @Reference
    private ValidatorService validatorService;

    // <------------------------------------- JpaRepositoryManager Lifecycle -------------------------------------->

    @Activate
    public void start(EntityManagerFactoryConfig config) {
        Validate.validState(this.repositoryWrapper != null, "JpaRepositoryWrapper must not be null!!");
        String persistenceUnit = config.persistenceUnit();
        Validate.validState(StringUtils.equals(this.repositoryWrapper.getPersistenceUnit(), persistenceUnit),
                String.format("JpaRepository [%s]'s service property [%s] must be equal to EntityManagerFactoryConfig#persistenceUnit!!",
                        this.repositoryWrapper.getJpaRepository(), JPA_UNIT_NAME));
        try {
            Validate.isTrue(StringUtils.isNotEmpty(persistenceUnit), "PersistenceUnit name can't be blank!!");
            Map<String, Object> jpaProperties = JpaProperties.from(config);
            jpaProperties.put(NON_JTA_DATASOURCE, this.dataSourceService.getDataSource(config.dataSourceName()));
            ValidationMode validationMode = ValidationMode.valueOf(config.validationMode());
            if (validationMode == AUTO || validationMode == CALLBACK) {
                jpaProperties.put(VALIDATOR_FACTORY, this.validatorService.getValidatorFactory());
            }
            LOGGER.info("Creating EntityManagerFactory for PersistenceUnit: [{}]", persistenceUnit);
            this.repositoryWrapper.initEntityManagerFactory(jpaProperties);
            LOGGER.info("Created EntityManagerFactory for PersistenceUnit: [{}]", persistenceUnit);
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            // Throw exception so that SCR won't register the component instance.
            throw new JpaBootstrapException(ex);
        }
    }

    @Deactivate
    public void stop(EntityManagerFactoryConfig config) {
        try {
            this.repositoryWrapper.getJpaRepository().onClose();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        this.repositoryWrapper.disposeJpaRepository();
        LOGGER.info("Closed EntityManagerFactory for PU [{}]", config.persistenceUnit());
    }

    // <<----------------------------------- JpaRepository Lifecycle ------------------------------------>>

    @Reference(service = JpaRepository.class)
    public void bindJpaRepository(JpaRepository repository, Map<String, Object> properties) {
        String persistenceUnit = (String) properties.get(JPA_UNIT_NAME);
        try {
            Validate.isTrue(StringUtils.isNotEmpty(persistenceUnit),
                    String.format("%s must specify the [%s] service property!!", repository, JPA_UNIT_NAME));
            LOGGER.info("Binding JpaRepository for PU [{}]", persistenceUnit);
            this.repositoryWrapper = new JpaRepositoryWrapper(persistenceUnit, repository);
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            throw new JpaRepositoryBindException(ex);
        }
    }
}
