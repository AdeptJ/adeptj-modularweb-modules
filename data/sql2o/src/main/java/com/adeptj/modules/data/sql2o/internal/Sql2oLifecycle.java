package com.adeptj.modules.data.sql2o.internal;

import com.adeptj.modules.commons.jdbc.service.DataSourceService;
import com.adeptj.modules.data.sql2o.Sql2oRepository;
import com.adeptj.modules.data.sql2o.core.AbstractSql2oRepository;
import org.apache.commons.lang3.ArrayUtils;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Sql2o;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@ProviderType
@Designate(ocd = Sql2oConfig.class)
@Component(service = Sql2oLifecycle.class, immediate = true, configurationPolicy = REQUIRE)
public class Sql2oLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Sql2o sql2o;

    @Activate
    public Sql2oLifecycle(@Reference DataSourceService dataSourceService, Sql2oConfig config) {
        this.sql2o = new Sql2o(dataSourceService.getDataSource());
        if (ArrayUtils.isNotEmpty(config.default_column_mappings())) {
            Map<String, String> defaultColumnMappings = new HashMap<>();
            for (String row : config.default_column_mappings()) {
                String[] mapping = row.split("=");
                defaultColumnMappings.put(mapping[0], mapping[1]);
            }
            this.sql2o.setDefaultColumnMappings(defaultColumnMappings);
        }
    }

    // <<------------------------------------- OSGi Internal  -------------------------------------->>

    @Deactivate
    protected void stop() {

    }

    // <<----------------------------------- Sql2oRepository Bind ------------------------------------>>

    @Reference(service = Sql2oRepository.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindSql2oRepository(Sql2oRepository<?, ?> repository) {
        LOGGER.info("Bind: {}", repository);
        ((AbstractSql2oRepository<?, ?>) repository).setSql2o(this.sql2o);
    }

    protected void unbindSql2oRepository(Sql2oRepository<?, ?> repository) {
        LOGGER.info("Unbind: {}", repository);
        ((AbstractSql2oRepository<?, ?>) repository).setSql2o(null);
    }
}
