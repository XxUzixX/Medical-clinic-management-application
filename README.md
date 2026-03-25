Medical Clinic Management Application

Web application supporting the management of a medical clinic with AI-assisted triage functionality.

The system enables patient appointment booking, prescription management, doctor scheduling and automatic medical specialization suggestion based on symptoms using an integrated Large Language Model.

Features

User management
	•	registration and login with role-based access
	•	roles:
	•	ADMIN
	•	DOCTOR
	•	USER (PATIENT)

Appointment management
	•	booking medical appointments
	•	viewing patient appointment history
	•	doctor appointment panel
	•	appointment status handling

AI triage system
	•	automatic medical specialization suggestion based on symptoms
	•	integration with LLM API
	•	structured JSON response processing
	•	confidence level estimation

Doctor functionality
	•	doctor schedule management
	•	patient notes
	•	prescription issuing

Administration panel
	•	user management
	•	doctor registration
	•	system data overview

 Architecture

Application follows a layered architecture:
	•	Controller layer – REST endpoints and web views
	•	Service layer – business logic
	•	Repository layer – database communication (Spring Data JPA)
	•	Entity layer – domain model
	•	AI integration layer – LLM communication service

Technology stack:
	•	Java
	•	Spring Boot
	•	Spring Security
	•	Spring Data JPA
	•	Hibernate
	•	Thymeleaf
	•	REST API
	•	PostgreSQL
	•	Maven

 AI Triage Mechanism

User provides symptoms → system sends structured prompt to LLM →
model returns specialization proposal in JSON format:

{
  "specialization": "CARDIOLOGIST",
  "confidence": 0.82
}

Database

System stores:
	•	users
	•	appointments
	•	prescriptions
	•	doctor schedules
	•	patient notes

Each entity is mapped using JPA annotations.

Running the application

Requirements
	•	Java 17+
	•	Maven
	•	PostgreSQL

Run

mvn spring-boot:run

Application will be available at:

http://localhost:8080

Security
	•	role-based authorization
	•	secured endpoints using Spring Security
	•	session-based authentication

Future improvements
	•	JWT authentication
	•	two-factor authentication
	•	external medical API integration
	•	advanced AI triage with conversation context
	•	microservices architecture

Engineering thesis project

The application was developed as part of an engineering thesis focused on:

using modern web technologies and AI integration to support decision-making processes in healthcare management systems.
