package org.mifosplatform.xbrl.mapping.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.xbrl.mapping.data.TaxonomyMappingData;
import org.mifosplatform.xbrl.mapping.service.ReadTaxonomyMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/xbrlmapping")
@Component
@Scope("singleton")
public class TaxonomyMappingApiResource {
	
	private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(
			Arrays.asList("identifier", "config"));
	
	private final String resourceNameForPermission = "XBRLMAPPING";
	
	private final PlatformSecurityContext context;
	private final ToApiJsonSerializer<TaxonomyMappingData> toApiJsonSerializer;
	private final ReadTaxonomyMappingService readTaxonomyMappingService;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	
	@Autowired
	public TaxonomyMappingApiResource(
			PlatformSecurityContext context,
			ToApiJsonSerializer<TaxonomyMappingData> toApiJsonSerializer,
			ReadTaxonomyMappingService readTaxonomyMappingService,
			PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			ApiRequestParameterHelper apiRequestParameterHelper) {

		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.readTaxonomyMappingService = readTaxonomyMappingService;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String retrieveTaxonomyMapping(@Context final UriInfo uriInfo) {
		context.authenticatedUser();
		TaxonomyMappingData mappingData = this.readTaxonomyMappingService.retrieveTaxonomyMapping();
		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, mappingData, RESPONSE_DATA_PARAMETERS);
	}
	
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String updateTaxonomyMapping(final String jsonRequestBody) {
		final Long mappingId = (long) 1;
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateTaxonomyMapping(mappingId).withJson(jsonRequestBody).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
	}
	
}
