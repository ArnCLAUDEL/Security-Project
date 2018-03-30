package client;

import certificate.CertificatedEntity;
import certificate.CertificationProtocolHandler;
import io.IOEntity;
import protocol.NonceGenerator;

public interface Client extends NonceGenerator, ClientProtocolHandler, CertificationProtocolHandler, IOEntity, CertificatedEntity {

}
