keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass tonmdp -validity 360 -keysize 2048 -ext san=ip:127.0.0.1
