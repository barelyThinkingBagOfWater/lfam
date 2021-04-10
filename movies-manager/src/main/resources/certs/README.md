To create the certs:

Root-ca for the trust store, the main cert:
openssl genrsa -out root-ca.key 2048
openssl req -x509 -new -nodes -key root-ca.key -sha256 -days 1024 -out root-ca.pem

Client cert for the keystore:
openssl genrsa -out client.key 2048
openssl req -new -key client.key -out client.csr
openssl x509 -req -in client.csr -CA root-ca.pem -CAkey root-ca.key -CAcreateserial -out client.crt \
  -days 700 -sha256
cat client.key client.crt > client.pem


Then to create the files used by Java:

TrustStore
keytool -keyalg RSA -import -file root-ca.pem -alias client -keystore client.ts -storepass password

KeyStore
openssl pkcs12 -export -in client.pem -inkey client.key -out client.p12 -name client
keytool -keyalg RSA -importkeystore -srckeystore client.p12 -destkeystore client.jks -srcstoretype pkcs12 -alias client -destkeypass password


You must use "-keyalg RSA" otherwise TLS1.3 fails, by default it's DSA, old but kept for backward compatibility reasons

Now as PKCS12 is the default since Java9 (common standard, jks was java-specific) this is how you create the .p12
keytool -genkeypair -alias service -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore service.p12 -validity 3650