# Web Application: Association Management

## Development Team

| Name | Email | GitHub |
|------|-------|--------|
| Elisa Donet | e.donet.2024@alumnos.urjc.es | dntelisa |
| Matheo Renault | m.renault.2024@alumnos.urjc.es | Rath0me |


## Project Coordination
- **[Trello Board](https://trello.com/invite/623787fba3139956f2e254f9/ATTIcc5e4f3d4670f971016a3c76493b78b9276AAE4D)**
## Key Features
### Entities
The main entities of the application are:
- **Association**: Represents an organization, with members and meeting minutes.
- **Member**: Represents a person registered in the system, can be part of an association and attend to meetings
- **MemberType**: Defines the roles that users have in an association.
- **Minute**: Stores meeting details of an association.

Relationships:
- An **Association** has multiple **MemberType** roles.
- An **Association** has multiple **Minutes**.
- A **Member** can belong to multiple **Associations** via **MemberType**.
- A **Minute** is linked to an Association and has multiple **Member** participants.

### User Permissions
- **All users, guest**  
   -> View content of associations, minutes and members  
- **Member**  
   -> Edit and delete own profile  
   -> Join association  
   -> Create minutes if part of the association  
- **Admin**:  
   -> Create, update and delete associations  
   -> Delete members  
   -> Delete minutes  
   -> Update minutes if part of the association  

### Images
- **Associations** can have a logo or banner image.

## Development Contributions

### Elisa DONET
#### Tasks completed in the first part
- All the controllers
- All the services
- HTML and CSS files

#### 5 Most Important Commits
1. [UtilisateurEntity to Member](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-1/commit/90505308ad984d6759c810429aa6efc7e347db75)
2. Add data on db and display on home page
3. List asso + members + search user by with nav bar
4. Role in DB
5. Add new association

#### 5 Key Files
1. AssoController.java
2. SecurityConfiguration.java
3. Association.java
4. UtilisateurEntity.java
5. Index.html

#### Tasks completed in the second part
- Security configuration file
- CSRF Handler Configuration

#### 5 Most Important Commits

#### 5 Key Files
- SecurityConfiguration
- CSRFHandlerConfiguration

### Mathéo RENAULT
#### Tasks completed
- Creation of entities
- Navigation diagram
- Class diagram


## Screenshots & Navigation Flow

- **Home**: ![](index.png)
- **Association Details**: ![](associationDetail.png)
- **Association Details for admin members**: ![](assoDetailAdminMember.png)
- **Association Details for members who are not members of the association and not administrators**: ![](assoDetailAuthNoMember.png)
- **Members List**: ![](members.png)
- **Member Details**: ![](memberDetail.png)
- **Login**: ![](login.png)
- **Profile**: ![](profile.png)
- **Create Account Page**: ![](createAccount.png)
- **Create Minute**: ![](createMinute.png)
- **Create Asso**: ![](createAsso.png)



### Navigation Diagram


## Execution Instructions
### Prerequisites
- **Java**: Version 21.0.5
- **MySQL**: Version 8.0.4
- **Maven**: Version 3.9.9

### Running the Application
1. Clone the repository:
   ```sh
   git clone (https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-1.git)
   ```
2. Navigate to the project directory:
3. Configure the database in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/associations
   spring.datasource.username=root
   spring.datasource.password=password
   ```
4. Install dependencies and build the project:
   ```sh
   mvn clean install
   ```
5. Create database associations in MySQL Workbench
6. Run the application:
   ```sh
   mvn spring-boot:run
   ```

## Diagrams
### Database Entity Diagram 
![entities_diagram](entitiesDiagram.png) 

### Class Diagram

### Navigation Diagram

---



