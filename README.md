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

`MySql database`

`Tomcat server`

Unit testing

`Junit`

`Mockito`
## Build Instructions
Instructions for IntelliJ Editor
1) Git pull from master - git pull origin master
2) Open Webapp folder in IntelliJ
3) Right click Run project "LibraryManagementSystem"
4) If required run maven imports - right click pom.xml, click reimport

## Deploy Instructions
As of now, this webapp is run using localhost.
Tomcat server is used to run the application using localhost port 8080 unless changed by the user

## Running Tests
Below Apis can be tested using **POSTMAN**

|HTTP Method |	   Endpoint  |  Authenticated Endpoint   Description|

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


Unit tests can be run directly from IntelliJ

## CI/CD
NA for now


