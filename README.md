# ItinerarySync – Travel & Excursion Management Platform

An Android mobile application for planning, tracking, and reporting multi-day vacations and scheduled excursions. Built with an object-oriented architecture and local Room/SQLite persistence, with an active cloud-synchronization extension in development using Spring Boot, PostgreSQL, and Docker.

## Shipped Core Features (v1.0 – Android)
* **Object-Oriented Domain Architecture:** Designed an abstract `ItineraryItem` base class extended by `Vacation` and `Excursion` subclasses to enforce shared behaviors and clean polymorphism across scheduling entities.
* **Local Data Persistence:** Implemented Room Database entities, Data Access Objects (DAOs), and Repository patterns for reliable offline storage and lifecycle-aware UI updates.
* **Validation & Scheduling Alerts:** Built strict date-range validation (ensuring excursions fall within parent vacation dates) and custom notification alarms for upcoming trip milestones.
* **Search, Reporting & Sharing:** Added multi-row search filtering, dynamic itinerary report generation with timestamps, and native Android intent sharing.
* **Automated Testing & Documentation:** Verified core business logic and date validation rules with JUnit test suites, backed by full UML class diagrams and Entity-Relationship Diagrams (ERD).

## In Active Development (v2.0 – Cloud Sync Extension)
* **Spring Boot REST API:** Building a backend service with Spring Data JPA to mirror local Room entities and expose CRUD endpoints for cross-device synchronization.
* **PostgreSQL & Docker:** Containerizing a PostgreSQL database and Spring Boot service via Docker to support centralized cloud persistence and offline-to-online sync reconciliation.

## Tech Stack
* **Mobile Client:** Java, Android SDK, Room Persistence Library, SQLite, XML, Gradle
* **Testing & Design:** JUnit, UML Class Diagrams, ERD Modeling
* **Cloud Extension (In Progress):** Spring Boot, Spring Data JPA, PostgreSQL, Docker, REST APIs
