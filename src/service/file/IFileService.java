package service.file;

import certification.client.ICertificationClient;
import certification.client.ICertificationClientProtocolHandler;
import service.IServiceProvider;

public interface IFileService extends IServiceProvider, IFileServiceProtocolHandler, ICertificationClientProtocolHandler, ICertificationClient {

}
