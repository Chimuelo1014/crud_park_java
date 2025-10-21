# 🚗 CRUD Park - Parking Management System

A comprehensive Java-based parking lot management system with QR code integration, membership handling, and real-time billing calculations.

![Java](https://img.shields.io/badge/Java-8+-orange.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13+-blue.svg)
![Swing](https://img.shields.io/badge/GUI-Swing-green.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Business Rules](#business-rules)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

CRUD Park is a desktop application designed to streamline parking lot operations. It provides a complete solution for vehicle entry/exit management, automatic billing, membership handling, and daily revenue reporting.

### Key Highlights

- ✅ **QR Code Integration**: Automatic ticket generation with QR codes for fast exits
- ✅ **Membership Management**: Support for monthly subscribers with automatic exemptions
- ✅ **Dynamic Pricing**: Configurable rates with grace period support
- ✅ **Operator Control**: Multi-user system with role-based access
- ✅ **Real-time Billing**: Minute-by-minute calculation based on parking duration
- ✅ **Audit Trail**: Complete tracking of all operations with operator assignment

## 🚀 Features

### Entry Management
- Vehicle registration with automatic ticket generation
- Duplicate entry prevention
- Membership status detection
- QR code ticket printing
- Real-time database logging

### Exit Management
- Search by license plate or QR code scan
- Automatic fare calculation
- Grace period application
- Membership exemptions
- Payment processing and ticket closure

### Administration
- Operator authentication system
- Daily revenue reports
- Active membership tracking
- Configurable pricing rates

### Technical Features
- MVC architecture with Service and DAO layers
- PostgreSQL database integration
- Swing-based GUI
- ZXing QR code generation
- Java Print API integration

## 🏗️ Architecture

The project follows a layered architecture pattern:

```
┌─────────────────────────────────────┐
│         VIEW (Swing GUI)            │
│  LoginView, MainMenuView, etc.      │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         CONTROLLER                  │
│  Business logic coordination        │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         SERVICE                     │
│  Business rules implementation      │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         DAO (Data Access)           │
│  Database operations                │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         DATABASE (PostgreSQL)       │
└─────────────────────────────────────┘
```

## 💻 Technologies

- **Java 8+**: Core programming language
- **Swing**: GUI framework
- **PostgreSQL**: Relational database
- **JDBC**: Database connectivity
- **ZXing**: QR code generation library
- **Java Print API**: Ticket printing functionality

## 📦 Installation

### Prerequisites

- JDK 8 or higher
- PostgreSQL 13+
- Maven (optional, for dependency management)
- ZXing library (core and javase modules)

### Steps

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/crudpark.git
cd crudpark
```

2. **Configure database connection**

Edit `src/main/resources/db.properties`:
```properties
db.url=jdbc:postgresql://localhost:5432/crud_park
db.user=your_username
db.password=your_password
```

3. **Add required libraries**

Add to your classpath or Maven dependencies:
```xml
<!-- ZXing Core -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.1</version>
</dependency>

<!-- ZXing JavaSE -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.5.1</version>
</dependency>

<!-- PostgreSQL JDBC Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.6.0</version>
</dependency>
```

4. **Compile and run**
```bash
javac -d bin src/main/java/com/crudpark/**/*.java
java -cp bin com.crudpark.app.Main
```

## 🗄️ Database Setup

### Schema Creation

```sql
-- Create database
CREATE DATABASE crud_park;

-- Operators table
CREATE TABLE operadores (
    id SERIAL PRIMARY KEY,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    password_hash VARCHAR(255) NOT NULL
);

-- Tickets table
CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    placa VARCHAR(20) NOT NULL,
    fecha_ingreso TIMESTAMP NOT NULL,
    fecha_salida TIMESTAMP,
    operador_ingreso_id INTEGER REFERENCES operadores(id),
    operador_salida_id INTEGER REFERENCES operadores(id),
    tipo_vehiculo VARCHAR(20) DEFAULT 'AUTO'
);

-- Rates table
CREATE TABLE tarifas (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    valor_hora_base DECIMAL(10,2) NOT NULL,
    valor_fraccion_minuto DECIMAL(10,2) NOT NULL,
    minutos_gracia INTEGER DEFAULT 0,
    activo BOOLEAN DEFAULT TRUE
);

-- Memberships table
CREATE TABLE mensualidades (
    id SERIAL PRIMARY KEY,
    placa VARCHAR(20) NOT NULL,
    nombre_cliente VARCHAR(100) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    activa BOOLEAN DEFAULT TRUE
);

-- Payments table
CREATE TABLE pagos (
    id SERIAL PRIMARY KEY,
    ticket_id INTEGER REFERENCES tickets(id),
    monto DECIMAL(10,2) NOT NULL,
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    operador_id INTEGER REFERENCES operadores(id)
);
```

### Sample Data

```sql
-- Create default operator (password: admin123)
INSERT INTO operadores (usuario, nombre, activo, password_hash) 
VALUES ('operador1', 'John Doe', TRUE, 'admin123');

-- Create default rate ($2.00/hour, $0.033/minute, 15 min grace)
INSERT INTO tarifas (nombre, valor_hora_base, valor_fraccion_minuto, minutos_gracia, activo)
VALUES ('Standard Rate', 2.00, 0.033, 15, TRUE);

-- Create sample membership
INSERT INTO mensualidades (placa, nombre_cliente, fecha_inicio, fecha_fin, activa)
VALUES ('ABC123', 'Jane Smith', CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', TRUE);
```

## 🎮 Usage

### Login
1. Launch the application
2. Enter credentials (default: `operador1` / `admin123`)
3. Click "Iniciar Sesión"

### Register Vehicle Entry
1. From main menu, click "Ingreso de Vehículo"
2. Enter license plate (e.g., `XYZ789`)
3. Click "Registrar Ingreso"
4. Ticket with QR code is automatically printed
5. System displays success message with folio number

### Register Vehicle Exit
1. From main menu, click "Salida de Vehículo"
2. Enter license plate OR scan QR code
   - QR format: `FOLIO:123|PLACA:ABC123`
3. Click "Buscar Ticket"
4. Review displayed information and amount due
5. Click "PAGAR Y CERRAR SALIDA"
6. System processes payment and closes ticket

### View Daily Report
1. From main menu, click "Reporte Diario"
2. System displays total revenue for current day
3. (Note: Only accessible by admin operators)

## 📁 Project Structure

```
crudpark/
├── src/main/java/com/crudpark/
│   ├── app/
│   │   └── Main.java                    # Application entry point
│   ├── config/
│   │   └── DatabaseConnection.java      # Database configuration
│   ├── controller/
│   │   ├── LoginController.java         # Login logic
│   │   ├── IngresoController.java       # Entry logic
│   │   ├── SalidaController.java        # Exit logic
│   │   └── ReporteController.java       # Reports logic
│   ├── dao/
│   │   ├── OperadorDAO.java             # Operator data access
│   │   ├── TicketDAO.java               # Ticket data access
│   │   ├── TarifaDAO.java               # Rate data access
│   │   ├── MensualidadDAO.java          # Membership data access
│   │   └── ReporteDAO.java              # Report data access
│   ├── model/
│   │   ├── Operador.java                # Operator entity
│   │   ├── Ticket.java                  # Ticket entity
│   │   ├── Tarifa.java                  # Rate entity
│   │   └── Mensualidad.java             # Membership entity
│   ├── service/
│   │   ├── LoginService.java            # Authentication service
│   │   ├── ParkingService.java          # Core parking service
│   │   └── ReporteService.java          # Reporting service
│   ├── util/
│   │   ├── QRUtil.java                  # QR code generation
│   │   └── PrintUtil.java               # Ticket printing
│   └── view/
│       ├── LoginView.java               # Login screen
│       ├── MainMenuView.java            # Main menu screen
│       ├── IngresoView.java             # Entry screen
│       └── SalidaView.java              # Exit screen
└── src/main/resources/
    └── db.properties                     # Database configuration
```

## 📊 Business Rules

### Entry Rules
- **No Duplicate Entry**: Cannot register a vehicle that already has an open ticket
- **Membership Detection**: System automatically identifies subscribers and marks them
- **Automatic Folio**: Each ticket receives a unique sequential identifier

### Exit Rules
- **Grace Period**: First N minutes are free (configurable in rates table)
- **Membership Exemption**: Monthly subscribers pay $0.00
- **Minute-based Billing**: Charges apply per minute after grace period
- **Dual Search**: Supports both license plate and QR code lookup

### Calculation Formula
```
IF (has_active_membership):
    total = 0
ELSE IF (minutes <= grace_period):
    total = 0
ELSE:
    billable_minutes = total_minutes - grace_period
    total = billable_minutes × rate_per_minute
```

## 🔐 Security Notes

**⚠️ Important**: Current implementation uses plain-text password comparison. For production use, implement:
- BCrypt or Argon2 password hashing
- Salted password storage
- Session management
- Input sanitization
- SQL injection prevention (use PreparedStatements - already implemented)

## 🎨 QR Code Format

Tickets include a QR code with the following payload structure:
```
FOLIO:[ticket_id]|PLACA:[license_plate]

Example: FOLIO:123|PLACA:ABC123
```

This allows operators to quickly scan tickets at exit instead of manually typing license plates.

## 🐛 Known Issues & Future Improvements

### Current Limitations
- No actual password hashing (uses plain text)
- Payment table insertion not implemented (commented out)
- Single active rate limitation
- No connection pooling
- No transaction management

### Planned Enhancements
- [ ] Implement BCrypt password hashing
- [ ] Complete payment table integration
- [ ] Add multi-rate support (motorcycle, truck, etc.)
- [ ] Implement database connection pooling
- [ ] Add transaction rollback handling
- [ ] Export reports to PDF/Excel
- [ ] Dashboard with statistics and charts
- [ ] Historical ticket search
- [ ] Email notifications for memberships expiring
- [ ] API REST for mobile integration

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards
- Follow Java naming conventions
- Add JavaDoc comments to public methods
- Include unit tests for new features
- Keep methods focused and concise
- Use meaningful variable names

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Your Name** - *Initial work* - [YourGitHub](https://github.com/yourusername)

## 🙏 Acknowledgments

- ZXing library for QR code generation
- PostgreSQL community for excellent documentation
- Java Swing tutorials and community

## 📞 Support

For issues, questions, or suggestions:
- Open an issue on GitHub
- Email: your.email@example.com
- Documentation: [Wiki](https://github.com/yourusername/crudpark/wiki)

---

**Made with ☕ and Java**
