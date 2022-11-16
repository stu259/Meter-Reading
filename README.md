"# Meter-Reading" 

Endpoints are secured with basic authentication. The username is user and the password is generated in startup and present in the console.

e.g. Using generated security password: f31f65c6-2a31-44d7-a4db-c801a6dc57fa

Endpoints
Get
-http://localhost:8081/api/smart/reads  Displays all accounts
-http://localhost:8081/api/smart/reads/{accountNumber} Displays specific account (updates additional fields)
Post
-http://localhost:8081/api/smart/reads/addreading Adds a single Reading
-example json - {"id":3,"meterId":123,"reading":70000,"dateReading":"11-12-2022","meterType":"gas","accountId":1}
-http://localhost:8081/api/smart/reads/addmultiplereadings Adds a list of Readings
-example json
-http://localhost:8081/api/smart/reads/addaccount Adds an account with specific ID
-example json - {"accountId":3}


Assumptions
-Alot of the additional fields assume at least 2 readings present. Some values are preloaded for ease of use.
-For comparison to other customers. Assumed average total use against other customers.
-Assumed readings are added and stored in date order.

If I was to spend more time on the project I would do the following:
-Add superclass for readings so they can be handle together. (There was a error for linking electric and gas readings to account as a child class. Seperated out classes in order to save time)
-Better security, set password and user
-Better error responses for incorrect meter submissions
-Discuss and implement what to do with a batch of meter readings were only 1 is invalid.
-Better way to calculate average instead of recalculating every get account call. (Maybe calculate periodically and save to database)

