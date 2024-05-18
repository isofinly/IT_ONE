#!/bin/bash


mkdir certs && cd certs

# Generate CA private key and self-signed certificate
openssl req -x509 -newkey rsa:4096 -keyout ca-key.pem -out ca-cert.pem -days 365 -nodes -subj "/CN=My CA"

# Generate server private key and certificate signing request (CSR)
openssl req -newkey rsa:4096 -keyout server-key.pem -out server-req.pem -nodes -subj "/CN=nats-server"

# Sign the server certificate with the CA certificate
openssl x509 -req -in server-req.pem -CA ca-cert.pem -CAkey ca-key.pem -CAcreateserial -out server-cert.pem -days 365

# Geneartion keys for the server application
openssl pkcs12 -export -out keystore.p12 -inkey server-key.pem -in server-cert.pem -password pass:password
keytool -importkeystore -srckeystore keystore.p12 -srcstoretype PKCS12 -srcstorepass password -destkeystore keystore.jks -deststorepass password
keytool -importcert -trustcacerts -file ca-cert.pem -storepass password -noprompt -keystore truststore.jks

mkdir ../server/certs
mv *.jks ../server/certs
cp ca-cert.pem ../server/certs/ca-cert.pem