package service.file;

import certification.client.ICertificationClient;
import certification.client.ICertificationClientProtocolHandler;
import io.IOEntity;
import service.IServiceProvider;
import session.client.ISessionClientProtocolHandler;
import session.client.ISessionManager;

public interface IFileService extends IOEntity, IServiceProvider, IFileServiceProtocolHandler, ICertificationClientProtocolHandler, ICertificationClient, ISessionClientProtocolHandler, ISessionManager {

}
