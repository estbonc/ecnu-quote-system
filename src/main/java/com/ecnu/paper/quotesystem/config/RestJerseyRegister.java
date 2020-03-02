package com.ecnu.paper.quotesystem.config;

import com.ecnu.paper.quotesystem.resource.ChargeTypeResource;
import com.ecnu.paper.quotesystem.resource.CommentResource;
import com.ecnu.paper.quotesystem.resource.ConstructRelationShipResource;
import com.ecnu.paper.quotesystem.resource.ConstructResource;
import com.ecnu.paper.quotesystem.resource.DecorationTypeResource;
import com.ecnu.paper.quotesystem.resource.DictionaryResource;
import com.ecnu.paper.quotesystem.resource.MaterialResource;
import com.ecnu.paper.quotesystem.resource.PackageResource;
import com.ecnu.paper.quotesystem.resource.QuoteResource;
import com.ecnu.paper.quotesystem.resource.QuoteTypeResource;
import com.ecnu.paper.quotesystem.resource.QuoteVersionResource;
import com.ecnu.paper.quotesystem.resource.RoomResource;
import com.ecnu.paper.quotesystem.resource.ThirdPartyResource;
import com.google.common.collect.Sets;
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
