package org.mifosplatform.infrastructure.xbrl.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface WriteTaxonomyService {
	
	CommandProcessingResult updateMapping(JsonCommand command);
}