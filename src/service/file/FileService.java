package service.file;

import certificate.CertificatedEntity;
import certificate.CertificationProtocolHandler;
import service.ServiceProvider;

public interface FileService extends ServiceProvider, FileServiceProtocolHandler, CertificationProtocolHandler, CertificatedEntity {

}
