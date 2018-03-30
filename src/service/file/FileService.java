package service.file;

import certification.CertificatedEntity;
import certification.CertificationProtocolHandler;
import service.ServiceProvider;

public interface FileService extends ServiceProvider, FileServiceProtocolHandler, CertificationProtocolHandler, CertificatedEntity {

}
