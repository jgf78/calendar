# 📅 Google Calendar Notifier

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)
![Google Calendar API](https://img.shields.io/badge/Google%20Calendar-API-blue?logo=googlecalendar)
![Docker](https://img.shields.io/badge/Docker-ready-blue?logo=docker)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

**Google Calendar Notifier** es una aplicación **Spring Boot** que se integra con **Google Calendar API** para sincronizar eventos y generar notificaciones automáticas de forma diaria.

Su objetivo es centralizar la agenda personal y convertirla en un sistema de notificaciones automático, enviando cada mañana un resumen estructurado de eventos programados.

---

## 🚀 Características principales

- 🔗 Integración completa con **Google Calendar API**
- 📅 Sincronización automática de eventos
- ⏰ Notificación diaria programada (ej: 08:00 AM)
- 📬 Generación de resumen de agenda diaria
- ⚙️ Arquitectura basada en **Spring Boot 3 + Java 17**
- 🐳 Soporte para **Docker**
- 🔐 Autenticación segura con Google OAuth
- 🧩 Diseño modular y extensible para futuros canales

---

## 🧩 Tecnologías

| Tecnología | Uso |
|-------------|-----|
| Java 17 | Lenguaje principal |
| Spring Boot 3.x | Framework backend |
| Google Calendar API | Fuente de eventos |
| Spring Scheduler | Programación de tareas |
| Maven | Build system |
| Docker | Contenedorización |

---

## 🏗️ Arquitectura del proyecto

```text
google-calendar-notifier/
├── src/
│   ├── main/java/...        # Lógica de negocio
│   └── main/resources/      # Configuración
├── docker/
│   └── Dockerfile
├── pom.xml
└── README.md

## ⚙️ Configuración local

### 1️⃣ Clonar el repositorio

```bash
git clone https://github.com/jgf78/calendar.git
cd calendar

### 1️⃣ Configurar credenciales de Google

Se requiere un proyecto en Google Cloud Console con acceso a Calendar API.

Variables necesarias:

GOOGLE_SERVICE_ACCOUNT=xxxxx

Ademas de añadir los id's de los calendarios que se quieran consultar

3️⃣ Ejecutar la aplicación
mvn spring-boot:run

Disponible en:

👉 http://localhost:8080

🐳 Uso con Docker
🧱 Construir imagen
docker build -t jgf78/calendar-notifier .
▶️ Ejecutar contenedor
docker run -d \
  -p 8080:8080 \
  -e GOOGLE_SERVICE_ACCOUNT=xxxxx \
  --name calendar-notifier \
  jgf78/calendar-notifier
⏰ Funcionamiento

El sistema ejecuta un job programado diariamente:

🕗 08:00 AM (configurable)

Flujo:

Consulta eventos del día en Google Calendar
Filtra y organiza la información
Genera un resumen de agenda
Envía notificación (email / bot / log / futuro Telegram)
📬 Ejemplo de salida
📅 Agenda de hoy - 25/05

🕘 10:00 - Reunión equipo backend
🕚 12:30 - Code review
🕔 17:00 - Deploy producción

✔️ Tienes 3 eventos programados
🧠 Casos de uso
📅 Resumen diario automático de agenda personal
💼 Productividad para desarrolladores
👤 Autor

Julián Gómez Fernández
💻 Java Backend Developer
📅 Automatización de procesos y APIs
📦 GitHub: @jgf78

📄 Licencia

Este proyecto se distribuye bajo licencia MIT.
Uso libre para aprendizaje y desarrollo personal.

🧠 “Tu calendario no debería recordarte cosas… debería trabajar por ti.”