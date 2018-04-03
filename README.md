# Security-Project

## Description

Ce projet est une version allégée du protocole Kerberos.
Il contient 4 entités principales :

* L'Autorité de Certification (CA)
* Le Serveur de Session
* Le Serveur de Services (SS)
* Le Client

La CA permet de charger, valider et construire des certificats à partir de requêtes faites par des Clients ou des SS.
Les certificats sont stockés dans le keystore local de la CA.

Le Serveur de Session permet de générer des clés symmétriques à utiliser par un client pour demander un service à un SS.

Le Serveur de Services fourni est un service de lecture/écriture dans des fichiers locaux du serveur. 
Chaque demande requête devra contenir un identifiant de session valide.

Le client fourni demande simplement la lecture d'un fichier au SS fourni.

## Installation
	
Ce projet utilise trois jars, deux de bouncy-castle et une autre fournissant une implémentation de la partie réseau.
Cette dernière permet d'abstraire toute la couche réseau vis à vis du reste du programme.
Avant de lancer les serveurs et les clients, il faut initialiser les keystores à la racine du projet.
La CA cherchera le keystore "store", le SS "store_service1" et le client "store_client". (Le nom du keystore peut être modifié dans le main).
Lorsque la CA sera lancée, elle génèrera son propre certificat qu'elle mettra dans son keystore.
Il faudra alors l'exporter puis l'importer dans les keystores du SS et du Client. Ces derniers tenteront de charger le certificat
du serveur au démarrage.

Les commandes suivantes permettront d'initialiser les keystores :

keytool -genkey -alias key -keystore store -storepass azerty
keytool -delete -alias key -keystore store -storepass azerty

keytool -genkey -alias key -keystore store_client -storepass azerty
keytool -delete -alias key -keystore store_client -storepass azerty

keytool -genkey -alias key -keystore store_service1 -storepass azerty
keytool -delete -alias key -keystore store_service1 -storepass azerty

** lancement de la CA **

keytool -exportcert -alias "root-certification-authority" -file ca_cert -keystore store -storepass azerty
keytool -importcert -alias "root-certification-authority" -file ca_cert -keystore store_client -storepass azerty
keytool -importcert -alias "root-certification-authority" -file ca_cert -keystore store_service1 -storepass azerty

## Lancement


Pour démarrer la CA et le serveur de session (qui sont regroupés en un seul serveur), il faut exécuter le main de 
src.certification.server.impl.Certification Server.
Le serveur démarera sur le port 8888, chargera son keystore et générera son certificat auto-signé.
Le SS se lancera sur le port 8890 et chargera son keystore.
Le client se lancera sur le port 8890 et chargera son keystore.

L'ordre de lancement des entités est important. Il faut d'abord lancer la CA, puis le SS et enfin le client. Le client cherchera à se connecter au SS au démarrage.
Le client et le SS chercheront à charger le certificat du CA au démarrage et ne se termineront s'il le certificat n'est pas dans leur keystore. 
Ils écrironts ensuite leur demande de certificat dans un fichier puis enverront au serveur une demande de certification avec le nom du fichier à charger.

Une fois leur certificat reçu, le SS attend une session de la part d'un client.
Il suffira d'écrire quelque chose sur l'entrée standard du client pour lancer la génération d'une session et la demande de lecture du fichier test.txt au SS.

## Service

Ici sera détaillé le déroulement d'une demande de service et de session.
Lorsqu'un client veut demander un service, il lui fait d'abord créer une nouvelle session avec le SS.
Il va alors envoyer une requête au serveur de session en fournissant une nonce, en indiquant son identité et celle du SS.
Le serveur va alors renvoyer la clé de session, la nonce et la clé de session cryptée avec la publique du SS.
Le client récupère la clé de session et transfère le message crypté au SS.
Celui-ci tente de récupérer la clé et, s'il y parvient, envoie un acquitement au Client avec une nouvelle nonce, le tout crypté par la clé de session.
Le client doit alors répondre en modifiant légèrement cette nonce, de sorte à prouver qu'il a bien été capable de la décrypter et la renvoie au SS.
A partir de maintenant, le client estime que la session a été crée et est valide.
Le SS reçoit alors la nonce modifiée et la valide.
Le SS pourra maintenant accepter une nouvelle demande service du client utilisant cette session.

A tout moment, s'il y a une nonce incorrecte ou si le déchiffrement échoue, la session est immédiatement jetée et les demandes de service avec cette 
session échoueront.

## Structure

Le projet est constitué de trois entités : 

* Le serveur de certification représenté par la classe certification.server.impl.CertificationServer 
* Le serveur de service représenté par la classe service.file.impl.FileService. Ce serveur fournit un service de lecteur et d’écriture de fichiers  
* Le client représenté par la classe client.impl.Client

Chaque entité dispose de plusieurs handlers qui servent chacun a gérer un aspect spécifique de l’entité.
	
#### Serveur de certification :

* CertificationServerNetworkHandler : Gestion de la communication réseau avec les clients
* CertificationServerMessageHandler : Gestion de l’échange des messages avec le client (initialisation d’une session, demande authentification, demande d’un certificat  …) 
* ConnectedCertificationServerProtocolHandler : Gestion des demandes d’authentification ainsi que les demande de certificats 
	
#### Serveur de service : 

* ConnectedFileServiceProtocolHandler : Gestion de la lecteur et l’écriture des fichiers en utilisant une clé symétrique 
* FileServiceMessageHandler : Gestion de l’échange des messages avec le client 
* FileServiceTCPNetworkHandler : Gestion de la communication TCP 
* FileServiceUDPNetworkHandler : Gestion de la communication UDP 

#### Client : 

* ClientMessageHandler : Gestion de l’échange des messages avec le serveur de certificats et le serveur de service 
* ClientTCPNetworkHandler : Gestion de la communication TCP 
* ClientUDPNetworkHandler : Gestion de la communication UDP 
* ConnectedClientProtocolHandler : Gestion de la lecture et l’écriture des fichiers auprès du serveur de service 