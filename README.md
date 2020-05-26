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
