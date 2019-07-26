# CSYE 6225 - Summer 2019

## Team Information

| Name | NEU ID | Email Address |
| --- | --- | --- |
|Jagmandeep Kaur | 001426439|kaur.j@husky.neu.edu |  | | |
|Akshata Honrao| 001444239| hanrao.a@husky.neu.edu|
|Yogita Patil| 001435442|patil.yo@husky.neu.edu |


## Technology Stack

Rest API Development

`Spring boot`

`Maven`

`Tomcat server`

`AWS EC2`

`AWS RDS`

`AWS S3`

Unit testing

`Junit`

`Mockito`

## Build Instructions
Instructions for IntelliJ Editor
1) Git pull from master - git pull origin master
2) Create network stack
3) Create application stack
4) Create war file
   
   `mvn clean package/install`

## Deploy Instructions
1) Copy war file to Ec2 instance(/home/ec2user or any other folder) using secure copy
2) Set environment variables in tomcat.service file .
`Environment="SPRING_PROFILES_ACTIVE=prod/dev"`
`Environment="AWS_ACCESS_KEY_ID=your_aws_access_key"`
`Environment="AWS_SECRET_ACCESS_KEY=your_secret_access_key"`
`Environment="SPRING_DATASOURCE_USERNAME=rds_username"`
`Environment="SPRING_DATASOURCE_PASSWORD=rds_password"`
3) Secure copy war file from location in point 1 to tomcat webapps folder (/opt/tomcat/latest/webapps)
4) Restart tomcat service

## Running Tests
Below Apis can be tested using **POSTMAN** .

1) Application run locally- Should use local database. Local file system for storing images
2) Application running on Cloud(EC2 instance) - Should use RDS instance. S3 bucket for storing images

|HTTP Method |	   Endpoint  |  Authenticated Endpoint |  Description|
| --- | --- | --- | ---|
|GET 	    |        / 	      |      Yes 	       |             Get current time|
POST 	|/user/register 	No 	Create account for user
POST |	/book 	|Yes 	|Create book
GET 	|/book 	|Yes 	|Get all books
GET |	/book/{id} |	Yes |	Get a books
DELETE 	|/book/{id} |	Yes |	Delete a book
PUT 	|/book |	Yes 	|Update book information
POST 	|/book/{idBook}/image |	Yes 	|Attach a image to the book
GET 	|/book/{idBook}/image/ |	Yes 	|Get book image 
PUT 	|/book/{idBook}/image/{idImage} |	Yes 	|Update image attached to the book
DELETE 	|/book/{idBook}/image/{idImage} |	Yes 	|Delete file attached to the book

## CI/CD
Circleci used for the continuous deployment.
<p>In order to run the Circleci for a specific branch change the filters to that specific branch in ./circleci/config.yml</p>

`filters:
      branches:
        only:
          - master
          - assignment5 `
        
## CloudWatch Agent
CloudWatch agent collects custom metrics for every API endpoint using StatsD protocol.
