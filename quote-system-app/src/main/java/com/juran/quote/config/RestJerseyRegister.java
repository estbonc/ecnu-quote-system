package com.juran.quote.config;

import com.google.common.collect.Sets;
import com.juran.quote.resource.v1.*;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * RestJerseyRegister
 */
public class RestJerseyRegister extends ResourceConfig {

    public RestJerseyRegister() {
        register(RequestContextFilter.class);
        register(ObjectMapperContextResolver.class);
        registerClasses(findRegisterV1Resources());
    }

    @PostConstruct
    public void initSwagger() {
        register(ApiListingResource.class);
        register(SwaggerSerializers.class);
        register(MultiPartFeature.class);
    }

    private Set<Class<?>> findRegisterV1Resources() {
        Set<Class<?>> registerResources = Sets.newHashSet();
        registerResources.add(QuoteResource.class);
        registerResources.add(PackageResource.class);
        registerResources.add(MaterialResource.class);
        registerResources.add(ConstructResource.class);
        registerResources.add(RoomResource.class);
        registerResources.add(DictionaryResource.class);
        registerResources.add(CommentResource.class);
        registerResources.add(DecorationTypeResource.class);
        registerResources.add(QuoteVersionResource.class);
        registerResources.add(QuoteTypeResource.class);
        registerResources.add(ConstructRelationShipResource.class);
        registerResources.add(ChargeTypeResource.class);
        registerResources.add(ThirdPartyResource.class);
        return registerResources;
    }

}
