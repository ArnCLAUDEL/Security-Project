package service.file.impl;

import certification.ICertificationStorer;
import service.file.IFileService;
import service.file.IFileServiceProvider;

public class NotConnectedFileServiceProtocolHandler extends AFileServiceProtocolHandler {

	public NotConnectedFileServiceProtocolHandler(ICertificationStorer storer, IFileService service, IFileServiceProvider provider) {
		super(service, provider, null, null);
	}

}
