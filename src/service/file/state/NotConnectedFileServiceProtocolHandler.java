package service.file.state;

import certification.CertificationStorer;
import service.file.FileService;
import service.file.FileServiceProvider;

public class NotConnectedFileServiceProtocolHandler extends AbstractFileServiceProtocolHandler {

	public NotConnectedFileServiceProtocolHandler(CertificationStorer storer, FileService service, FileServiceProvider provider) {
		super(service, storer, provider, null);
	}

}
