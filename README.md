# Hospital Management System

An object-oriented Java desktop application for managing hospital operations:
appointments, consultations, prescriptions, billing, lab requests, staff shifts
and hospital assets. Built as a group project for Object-Oriented Development
with Java (OODJ) at Asia Pacific University (APU).

## User roles
- **Admin** - [what the admin can do, e.g. manage users, departments, assets, shift roster]
- **Doctor** - [e.g. view appointments, write consultation notes and prescriptions]
- **Patient** - [e.g. register, book appointments, view bills, rate doctors]
- **Medical Manager** - [e.g. oversee lab requests, assignments]

## My contribution
I built the **Admin** part: `AdminStaff`, `AdminDashboard` [and any other classes you wrote].

## Tech
- Java [version], desktop GUI ([Swing / JavaFX])
- File-based storage in `data/` (plain text files, no database)
- Password handling in `PasswordUtil`
- OOP concepts used: [e.g. inheritance (User subclasses), interface (Persistable),
  custom exception (DataAccessException)]

## Run
1. Clone the repo and open it in [IntelliJ IDEA / NetBeans / Eclipse]
2. Run the main class `src/Main.java`
3. Log in with a sample account: [username / password, fictional only]

## Project structure
| Path | Purpose |
|------|---------|
| `src/Main.java` | Entry point |
| `src/LoginFrame.java`, `RegisterFrame.java` | Login and registration screens |
| `src/*Dashboard.java` | Dashboards for each role |
| `src/User.java`, `Doctor.java`, `Patient.java`, `AdminStaff.java`, `MedicalManager.java` | User model |
| `src/Appointment.java`, `Bill.java`, `Prescription.java`, `LabRequest.java`, `ConsultationNote.java`, `Rating.java` | Hospital records |
| `src/FileManager.java`, `Persistable.java`, `DataAccessException.java` | File storage layer |
| `src/UITheme.java`, `NavButton.java` | Shared UI styling |
| `data/` | Sample data files (all fictional) |

## Team
- [@tanmay1802-art](https://github.com/tanmay1802-art) - Admin
