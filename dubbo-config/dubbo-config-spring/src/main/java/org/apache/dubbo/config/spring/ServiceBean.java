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
package org.apache.dubbo.config.spring;

import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConfigCenterConfig;
import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.MetricsConfig;
import org.apache.dubbo.config.ModuleConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.config.spring.context.event.ServiceBeanExportedEvent;
import org.apache.dubbo.config.spring.extension.SpringExtensionFactory;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.apache.dubbo.common.constants.CommonConstants.COMMA_SPLIT_PATTERN;
import static org.apache.dubbo.config.spring.util.BeanFactoryUtils.addApplicationListener;

/**
 * ServiceFactoryBean
 *
 * @export
 */
public class ServiceBean<T> extends ServiceConfig<T> implements InitializingBean, DisposableBean,
        ApplicationContextAware, ApplicationListener<ContextRefreshedEvent>, BeanNameAware,
        ApplicationEventPublisherAware {

    private static final long serialVersionUID = 213195494150089726L;

    private final transient Service service;

    private transient ApplicationContext applicationContext;

    private transient String beanName;

    private transient boolean supportedApplicationListener;

    private ApplicationEventPublisher applicationEventPublisher;

    public ServiceBean() {
        super();
        this.service = null;
    }

    public ServiceBean(Service service) {
        super(service);
        this.service = service;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        SpringExtensionFactory.addApplicationContext(applicationContext);
        supportedApplicationListener = addApplicationListener(applicationContext, this);
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    /**
     * Gets associated {@link Service}
     *
     * @return associated {@link Service}
     */
    public Service getService() {
        return service;
    }

    /**
     * dubbo 服务导出
     *
     * Spring 容器发布刷新事件，Dubbo 在接收到事件后，会立即执行服务导出逻辑。
     * @see ApplicationListener
     * 参考：https://cloud.tencent.com/developer/article/1395017
     *
     * Dubbo采用 URL 作为配置信息的统一格式，所有扩展点都通过传递 URL 携带配置信息。
     *
     * Dubbo进行服务导出，主要由下面三部分工作组成：
     *
     * 第一部分是前置工作，主要用于检查参数，组装 URL。
     *  前置工作主要包含两个部分，分别是配置检查，以及 URL 装配。
     *  在导出服务之前，Dubbo 需要检查用户的配置是否合理，或者为用户补充缺省配置。
     *  配置检查完成后，接下来需要根据这些配置组装 URL。
     *  在 Dubbo 中，URL 的作用十分重要。Dubbo 使用 URL 作为配置载体，所有的拓展点都是通过 URL 获取配置。
     *
     * 第二部分是导出服务，包含导出服务到本地 (JVM)，和导出服务到远程两个过程。
     *
     * 第三部分是向注册中心注册服务，用于服务发现。
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 判断是否已导出 && 是不是已被取消导出
        if (!isExported() && !isUnexported()) {
            if (logger.isInfoEnabled()) {
                logger.info("The service ready on spring started. service: " + getInterface());
            }
            // 导出服务
            export();
        }
    }

    /**
     * afterPropertiesSet检查ServiceBean的某个属性是否为空，如果为空，
     * 从ApplicationContext获取相应类型的bean，如果获取到了，则进行相应的设置。常见的6个属性如下（还有其它的)：
     *
     * 1、ProviderConfig provider：其实就是看有没有配置<dubbo:provider>
     *
     * 2、ApplicationConfig application：其实就是看有没有配置<dubbo:application>
     *
     * 3、ModuleConfig module：其实就是看有没有配置<dubbo:module>
     *
     * 4、List<RegistryConfig> registries：其实就是看有没有配置<dubbo:registry>
     *
     * 5、MonitorConfig monitor：其实就是看有没有配置<dubbo:monitor>
     *
     * 6、List<ProtocolConfig> protocols：其实就是看有没有配置<dubbo:protocol>
     *
     * String path：服务名称
     *
     * @throws Exception
     */
    @Override
    @SuppressWarnings({"unchecked", "deprecation"})
    public void afterPropertiesSet() throws Exception {
        // 初始化provider
        if (getProvider() == null) {
            // 读取 spring applicationContext 的 ProviderConfig
            Map<String, ProviderConfig> providerConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ProviderConfig.class, false, false);
            if (providerConfigMap != null && providerConfigMap.size() > 0) {
                // 读取 spring applicationContext 的 ProtocolConfig
                Map<String, ProtocolConfig> protocolConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ProtocolConfig.class, false, false);
                if (CollectionUtils.isEmptyMap(protocolConfigMap)
                        && providerConfigMap.size() > 1) { // backward compatibility
                    List<ProviderConfig> providerConfigs = new ArrayList<ProviderConfig>();
                    for (ProviderConfig config : providerConfigMap.values()) {
                        if (config.isDefault() != null && config.isDefault()) {
                            providerConfigs.add(config);
                        }
                    }
                    if (!providerConfigs.isEmpty()) {
                        setProviders(providerConfigs);
                    }
                } else {
                    ProviderConfig providerConfig = null;
                    for (ProviderConfig config : providerConfigMap.values()) {
                        if (config.isDefault() == null || config.isDefault()) {
                            if (providerConfig != null) {
                                throw new IllegalStateException("Duplicate provider configs: " + providerConfig + " and " + config);
                            }
                            providerConfig = config;
                        }
                    }
                    if (providerConfig != null) {
                        setProvider(providerConfig);
                    }
                }
            }
        }
        // 初始化 application
        if (getApplication() == null
                && (getProvider() == null || getProvider().getApplication() == null)) {
            // 读取 spring applicationContext 的 ApplicationConfig
            Map<String, ApplicationConfig> applicationConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ApplicationConfig.class, false, false);
            if (applicationConfigMap != null && applicationConfigMap.size() > 0) {
                ApplicationConfig applicationConfig = null;
                for (ApplicationConfig config : applicationConfigMap.values()) {
                    if (applicationConfig != null) {
                        throw new IllegalStateException("Duplicate application configs: " + applicationConfig + " and " + config);
                    }
                    applicationConfig = config;
                }
                if (applicationConfig != null) {
                    setApplication(applicationConfig);
                }
            }
        }
        if (getModule() == null
                && (getProvider() == null || getProvider().getModule() == null)) {
            Map<String, ModuleConfig> moduleConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ModuleConfig.class, false, false);
            if (moduleConfigMap != null && moduleConfigMap.size() > 0) {
                ModuleConfig moduleConfig = null;
                for (ModuleConfig config : moduleConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault()) {
                        if (moduleConfig != null) {
                            throw new IllegalStateException("Duplicate module configs: " + moduleConfig + " and " + config);
                        }
                        moduleConfig = config;
                    }
                }
                if (moduleConfig != null) {
                    setModule(moduleConfig);
                }
            }
        }

        if (StringUtils.isEmpty(getRegistryIds())) {
            if (getApplication() != null && StringUtils.isNotEmpty(getApplication().getRegistryIds())) {
                setRegistryIds(getApplication().getRegistryIds());
            }
            if (getProvider() != null && StringUtils.isNotEmpty(getProvider().getRegistryIds())) {
                setRegistryIds(getProvider().getRegistryIds());
            }
        }

        if ((CollectionUtils.isEmpty(getRegistries()))
                && (getProvider() == null || CollectionUtils.isEmpty(getProvider().getRegistries()))
                && (getApplication() == null || CollectionUtils.isEmpty(getApplication().getRegistries()))) {
            Map<String, RegistryConfig> registryConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, RegistryConfig.class, false, false);
            if (CollectionUtils.isNotEmptyMap(registryConfigMap)) {
                List<RegistryConfig> registryConfigs = new ArrayList<>();
                if (StringUtils.isNotEmpty(registryIds)) {
                    Arrays.stream(COMMA_SPLIT_PATTERN.split(registryIds)).forEach(id -> {
                        if (registryConfigMap.containsKey(id)) {
                            registryConfigs.add(registryConfigMap.get(id));
                        }
                    });
                }

                if (registryConfigs.isEmpty()) {
                    for (RegistryConfig config : registryConfigMap.values()) {
                        if (StringUtils.isEmpty(registryIds)) {
                            registryConfigs.add(config);
                        }
                    }
                }
                if (!registryConfigs.isEmpty()) {
                    super.setRegistries(registryConfigs);
                }
            }
        }
        if (getMetadataReportConfig() == null) {
            Map<String, MetadataReportConfig> metadataReportConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, MetadataReportConfig.class, false, false);
            if (metadataReportConfigMap != null && metadataReportConfigMap.size() == 1) {
                super.setMetadataReportConfig(metadataReportConfigMap.values().iterator().next());
            } else if (metadataReportConfigMap != null && metadataReportConfigMap.size() > 1) {
                throw new IllegalStateException("Multiple MetadataReport configs: " + metadataReportConfigMap);
            }
        }

        if (getConfigCenter() == null) {
            Map<String, ConfigCenterConfig> configenterMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ConfigCenterConfig.class, false, false);
            if (configenterMap != null && configenterMap.size() == 1) {
                super.setConfigCenter(configenterMap.values().iterator().next());
            } else if (configenterMap != null && configenterMap.size() > 1) {
                throw new IllegalStateException("Multiple ConfigCenter found:" + configenterMap);
            }
        }

        if (getMonitor() == null
                && (getProvider() == null || getProvider().getMonitor() == null)
                && (getApplication() == null || getApplication().getMonitor() == null)) {
            Map<String, MonitorConfig> monitorConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, MonitorConfig.class, false, false);
            if (monitorConfigMap != null && monitorConfigMap.size() > 0) {
                MonitorConfig monitorConfig = null;
                for (MonitorConfig config : monitorConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault()) {
                        if (monitorConfig != null) {
                            throw new IllegalStateException("Duplicate monitor configs: " + monitorConfig + " and " + config);
                        }
                        monitorConfig = config;
                    }
                }
                if (monitorConfig != null) {
                    setMonitor(monitorConfig);
                }
            }
        }

        if (getMetrics() == null) {
            Map<String, MetricsConfig> metricsConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, MetricsConfig.class, false, false);
            if (metricsConfigMap != null && metricsConfigMap.size() > 0) {
                MetricsConfig metricsConfig = null;
                for (MetricsConfig config : metricsConfigMap.values()) {
                    if (metricsConfig != null) {
                        throw new IllegalStateException("Duplicate metrics configs: " + metricsConfig + " and " + config);
                    }
                    metricsConfig = config;
                }
                if (metricsConfig != null) {
                    setMetrics(metricsConfig);
                }
            }
        }

        if (StringUtils.isEmpty(getProtocolIds())) {
            if (getProvider() != null && StringUtils.isNotEmpty(getProvider().getProtocolIds())) {
                setProtocolIds(getProvider().getProtocolIds());
            }
        }

        if (CollectionUtils.isEmpty(getProtocols())
                && (getProvider() == null || CollectionUtils.isEmpty(getProvider().getProtocols()))) {
            Map<String, ProtocolConfig> protocolConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ProtocolConfig.class, false, false);
            if (protocolConfigMap != null && protocolConfigMap.size() > 0) {
                List<ProtocolConfig> protocolConfigs = new ArrayList<ProtocolConfig>();
                if (StringUtils.isNotEmpty(getProtocolIds())) {
                    Arrays.stream(COMMA_SPLIT_PATTERN.split(getProtocolIds()))
                            .forEach(id -> {
                                if (protocolConfigMap.containsKey(id)) {
                                    protocolConfigs.add(protocolConfigMap.get(id));
                                }
                            });
                }

                if (protocolConfigs.isEmpty()) {
                    for (ProtocolConfig config : protocolConfigMap.values()) {
                        if (StringUtils.isEmpty(protocolIds)) {
                            protocolConfigs.add(config);
                        }
                    }
                }

                if (!protocolConfigs.isEmpty()) {
                    super.setProtocols(protocolConfigs);
                }
            }
        }
        if (StringUtils.isEmpty(getPath())) {
            if (StringUtils.isNotEmpty(beanName)
                    && StringUtils.isNotEmpty(getInterface())
                    && beanName.startsWith(getInterface())) {
                setPath(beanName);
            }
        }
        if (!supportedApplicationListener) {
            export();
        }
    }

    /**
     * Get the name of {@link ServiceBean}
     *
     * @return {@link ServiceBean}'s name
     * @since 2.6.5
     */
    public String getBeanName() {
        return this.beanName;
    }

    /**
     * @since 2.6.5
     */
    @Override
    public void export() {
        super.export();
        // Publish ServiceBeanExportedEvent
        publishExportEvent();
    }

    /**
     * @since 2.6.5
     */
    private void publishExportEvent() {
        ServiceBeanExportedEvent exportEvent = new ServiceBeanExportedEvent(this);
        applicationEventPublisher.publishEvent(exportEvent);
    }

    @Override
    public void destroy() throws Exception {
        // no need to call unexport() here, see
        // org.apache.dubbo.config.spring.extension.SpringExtensionFactory.ShutdownHookListener
    }

    // merged from dubbox
    @Override
    protected Class getServiceClass(T ref) {
        if (AopUtils.isAopProxy(ref)) {
            return AopUtils.getTargetClass(ref);
        }
        return super.getServiceClass(ref);
    }

    /**
     * @param applicationEventPublisher
     * @since 2.6.5
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
