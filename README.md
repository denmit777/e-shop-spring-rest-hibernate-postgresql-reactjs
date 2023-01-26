1.Name of project: e-shop-spring-rest-hibernate-postgresql-reactjs

2.Launch of project: 
backend part: mvn run clean tomcat7:run
node modules: e-shop-spring-rest-hibernate-postgresql-reactjs\src\frontend-react\java-learn-app-main>npm install
frontend part: e-shop-spring-rest-hibernate-postgresql-reactjs\src\frontend-react\java-learn-app-main>npm start

3.Ports of the project:
backend: http://localhost:8081
frontend: http://localhost:3000

4.Start page: http://localhost:3000

5.Logins/passwords/emails/roles of users:

Den/1234/den_mogilev@yopmail.com/ROLE_ADMIN,
Peter/4321/peter_mogilev@yopmail.com/ROLE_BUYER,
Asya/5678/asya_mogilev@yopmail.com/ROLE_BUYER,
Jimmy/P@ssword1/jimmy_mogilev@yopmail.com/ROLE_ADMIN,
Maricel/221182/maricel_mogilev@yopmail.com/ROLE_BUYER

6.Templates: webapp/WEB-INF/view

7.Database scripts: resources/db/data.sql

8.Database properties: resources/db/hibernate.properties

9.Database PostgreSQL connection:

Name: dbase@localhost
User: denmit
Password: 1981
Port: 5432

10.Mail properties: resources/mail/email.properties

11.Security properties: resources/security/security.properties

12.Sender's email: "denmit777@yandex.by"

13.Rest controllers:

UserController:
registerUser(POST): http://localhost:8081 + body;
authenticationUser(POST): http://localhost:8081/auth + body

GoodController:
save(POST): http://localhost:8081/goods/forAdmin + body;
getAllForAdmin(GET): http://localhost:8081/goods/forAdmin;
getAllForBuyer(GET): http://localhost:8081/goods/forBuyer;
getById(GET): http://localhost:8081/goods/forAdmin/{id};
update(PUT): http://localhost:8081/goods/forAdmin/{id} + body;
delete(DELETE): http://localhost:8081/goods/forAdmin/{id};
getTotalAmount(GET): http://localhost:8081/goods/forAdmin/total;

OrderController:
save(POST): http://localhost:8081/orders + body;
getAll(GET): http://localhost:8081/orders;
getById(GET): http://localhost:8081/orders/{id};
getTotalAmount(GET): http://localhost:8081/orders/total;

AttachmentController:
uploadFile(POST): http://localhost:8081/orders/{orderId}/attachments + body;
getById(GET): http://localhost:8081/orders/{orderId}/attachments/{attachmentId};
getAllByOrderId(GET): http://localhost:8081/orders/{orderId}/attachments;
deleteFile(DELETE): http://localhost:8081/orders/{orderId}/attachments/{attachmentName};

CommentController:
save(POST): http://localhost:8081/orders/{orderId}/comments + body;
getAllByOrderId(GET): http://localhost:8081/orders/{orderId}/comments;

FeedbackController:
save(POST): http://localhost:8081/orders/{orderId}/feedbacks + body;
getAllByOrderId(GET): http://localhost:8081/orders/{orderId}/feedbacks;

HistoryController:
getAllByOrderId(GET): http://localhost:8081/orders/{orderId}/history;