package com.adeptj.modules.mvc;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.locator.PathTemplateLocator;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.Set;

import static com.adeptj.modules.mvc.TemplateEngineConstants.I18N_HEADER;
import static com.adeptj.modules.mvc.TemplateEngineConstants.TEMPLATE_HEADER;

public class BundleTemplateLocator extends PathTemplateLocator<String> implements BundleTrackerCustomizer<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<Bundle> templateBundles;

    private final DelegatingResourceBundleHelper resourceBundleHelper;

    public BundleTemplateLocator(int priority, String rootPath, String suffix) {
        super(priority, rootPath, suffix);
        this.resourceBundleHelper = new DelegatingResourceBundleHelper();
        this.templateBundles = new ArrayList<>();
    }

    public DelegatingResourceBundleHelper getResourceBundleHelper() {
        return resourceBundleHelper;
    }

    // <------------------------ PathTemplateLocator ---------------------->

    @Override
    protected String constructVirtualPath(String source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getAllIdentifiers() {
        return Collections.emptySet();
    }

    @Override
    public Reader locate(String name) {
        for (Bundle bundle : this.templateBundles) {
            Enumeration<URL> entries = bundle.findEntries(bundle.getHeaders().get(TEMPLATE_HEADER), "*.html", true);
            if (entries != null) {
                while (entries.hasMoreElements()) {
                    URL template = entries.nextElement();
                    if (StringUtils.endsWith(template.getPath(), name + "." + this.getSuffix())) {
                        try {
                            Reader reader = IOUtils.buffer(new InputStreamReader(template.openStream(), this.getDefaultFileEncoding()));
                            LOGGER.debug("Template {} located: {}", name, template);
                            return reader;
                        } catch (IOException ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }
                    }
                }
            }
        }
        return null;
    }

    // <------------------------ BundleTrackerCustomizer ---------------------->

    @Override
    public Object addingBundle(Bundle bundle, BundleEvent event) {
        String templatesLocation = bundle.getHeaders().get(TEMPLATE_HEADER);
        if (StringUtils.isNotEmpty(templatesLocation)) {
            LOGGER.info("Bundle: {} has provided templates under path: {}", bundle, templatesLocation);
            this.templateBundles.add(bundle);
            String resourceBundlesLocation = bundle.getHeaders().get(I18N_HEADER);
            if (StringUtils.isNotEmpty(resourceBundlesLocation)) {
                LOGGER.info("Bundle: {} has provided ResourceBundle(s) under path: {}", bundle, resourceBundlesLocation);
                Enumeration<URL> resourceBundles = bundle.findEntries(resourceBundlesLocation, "*.properties", true);
                if (resourceBundles != null) {
                    while (resourceBundles.hasMoreElements()) {
                        try (BufferedInputStream bis = IOUtils.buffer(resourceBundles.nextElement().openStream())) {
                            ResourceBundleWrapper wrapper = new ResourceBundleWrapper(new PropertyResourceBundle(bis), bundle.getBundleId());
                            this.resourceBundleHelper.addResourceBundleWrapper(wrapper);
                        } catch (IOException ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }
                    }
                }
            }
            return bundle;
        }
        return null;
    }

    @Override
    public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
        // NOP
    }

    @Override
    public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
        this.templateBundles.removeIf(b -> b.getBundleId() == bundle.getBundleId());
        this.resourceBundleHelper.removeResourceBundleWrapper(bundle.getBundleId());
    }
}
