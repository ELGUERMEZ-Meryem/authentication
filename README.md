# authentication
I. Token format:
                           Authorization: Bearer <token string>
1. Header – typically includes the type of the token and hashing algorithm.
2. Payload – typically includes data about a user and for whom is token issued.
3. Signature – it’s used to verify if a message wasn’t changed along the way.

example:

Example token
A JWT token from authorization header will probably look like this:

Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxQDEiLCJjcmVhdGVkIjoxNTkwMTUyNzc3Nzk2LCJleHAiOjE1OTA3NTc1Nzd9.Kc8mT4RdUrrdcfQodRrusgnzZ36UqN4kPxwhKMB6XPRdrOmQU_TF3rUq3n6zydIFnxu6X-8_pP1pvE6XhPNdBQ

So there are three parts separated with comma – header, claims, and signature. Header and payload are Base64 encoded JSON objects. We can use https://jwt.io/ to see the tree parts:

1. Header: ALGORITHM & TOKEN TYPE

         {
	         "typ": "JWT",
             "alg": "HS512"
         }
	 
 2. PAYLOAD: DATA
 
            {
                "sub": "1@1",
                "created": 1590152777796,
                "exp": 1590757577
             }


II. add jwt dependencies:

JWT support for Java is provided by the library JJWT so we also need to add following dependencies to the pom.xml file:

            <dependency>
                   <groupId>io.jsonwebtoken</groupId>
                    <artifactId>jjwt-api</artifactId>
                    <version>0.10.5</version>
            </dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-impl</artifactId>
			<version>0.10.5</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-jackson</artifactId>
			<version>0.10.5</version>
			<scope>runtime</scope>
		</dependency>

III. Authentication filter:

Is used to authenticate user, it will check if username and password are correct, for that it calls Spring’s authentication manager to verify them. If they are it will generate the token and send it in  HTTP Authorization header, else we will get HTTP 401 Unauthorized response.

VI. Authorization filter:

Is used to handle all HTTP requests that demandes authentication and checks if there is an Authorization header with the correct token. If the token is valid then the filter will add authentication data into Spring’s security context, else we will get HTTP 403 Forbidden response when we call secured endpoint without a valid JWT.

# two-factor authentication (2fa)

We use the traditional authentication using email and password, but with existing computing powers, cyber attackers have facilities for testing billions of password combinations  in a second. So we have to find a way to more secure our system. 
Two-step verification helps strengthen the security of your Google Account by requiring a second validation step when you sign in. In addition to your password, you will need another code generated there are many ways to do that, I choose the seconde factor to be Google authenticator or sms using Twilio.

I. Two-factor authentication (also known as 2FA):
is a method of confirming a user’s identity in which user is granted access only after successfully presenting two pieces of evidence (or factors) to an authentication mechanism: knowledge (something they and only they know – login&password, PIN code, etc), and possession (something they and only they have). The possession factors may be – ID card, security token, smartphone, etc – something that is not a logical thing you know but a physical entity.

II. Google authenticator mobile app:

The code will be generated by the Google Authenticator application on your phone. So you have to install it on your phone.
Be attention Google Authenticator app may not working, either on Android or iPhone, because of the problem of the time sync. Fortunately it’s easy to fix this if Google Authenticator has stopped working. All you have to do is make sure your Google Authenticator app’s time is synced correctly. Launch the app, tap the Menu button (three dots), and go to Settings > Time Correction for Codes > Sync now.

1. TOTP (Time-based One-time Password Algorithm):

is an algorithm that computes a one-time password from a shared secret key and the current time. TOTP is an algorithm based on the HOTP (HMAC-based One-time Password) but uses a time-based component instead of a counter. 
TOTP and HOTP depend on a secret that two parties share. The secret is a randomly generated token that is usually displayed in Base32 to the user.

2. Dependencies to add in pom.xml:

This library provides methods for verifying TOTP codes and for generating secrets.

        <dependency>
            <groupId>org.jboss.aerogear</groupId>
            <artifactId>aerogear-otp-java</artifactId>
            <version>1.0.0</version>
        </dependency>

3. Sign Up:

When the user sign up and choose to enable 2fa and 2fa type is Google authenticator he will be saved in the database with the field isEnabled set to 0 and the secret key will be generated, and he will be redirect to activation account page where he will find the qr image to scann or he can enter the secret key using the Google authenticator on his phone, so he had to enter the 6-digit code from his authenticator app and our backend will verify those 6-digit if all is fine we will activate his account by setting the field isEnabled to 1.

4. Log In:

When the user enter his email and password, we check if his account is active(field isEnabled=1)if it is not activated we redirect him to activation account page to activate his account, else if the account is activated then the authenticationManager will check if the email and password are correct if it is we will check if the user has enabled 2fa and 2fa type is Google authenticator if he had we will redirect him to verification page to verify his secret key.

III. Send SMS using Twilio:

Twilio is an application used to send SMS and make voice calls from our application. It allows us to send the SMS and make voice calls programmatically. In our case, we are going to send 2fa key in sms by using Spring Boot with Twilio.

1. Dependencies to add in pom.xml:

Twilio library provides methods for sending sms with 2fa secret key.

        <dependency>
            <groupId>com.twilio.sdk</groupId>
            <artifactId>twilio</artifactId>
            <version>7.34.0</version>
        </dependency>
	
2. Twilio application:

We need to sign up in the twilio application to get an account and a trial number. 

3. Sign Up:

When the user sign up and choose to enable 2fa and 2fa type is sms he will be saved in the database with the field isEnabled set to 1.

4. Log In:

When the user enter his email and password, the authenticationManager will check if the email and password are correct if it is we will check if the user has enabled 2fa and 2fa type is sms we will generate a random number and send it in an sms this code is valid for 5 minutes, and we will redirect him to verification page to verify his secret key that he recevied in an sms.


