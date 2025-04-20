# JWT Auth – Identity Provider

A secure authentication and authorization service built with **Java Spring Boot** using **JWT (JSON Web Tokens)**. This identity provider handles user sign-up, login, and token-based authentication for downstream services.

The system includes a development mail server to support email verification during the registration process.

---

## 🔐 Features

- ✅ JWT-based stateless authentication
- ✅ Email verification on sign-up
- ✅ Secure password hashing with Argon2
- ✅ Role-based authorization
- ✅ Refresh token support
- ✅ Spring Security integration
- ✅ Development mail server using Mailpit

---

## 📧 Email Verification (Dev)

To simulate email confirmation during development, the project uses **[Mailpit](https://github.com/axllent/mailpit)** – a lightweight, Docker-based fake SMTP server.

- All verification emails will be visible via the Mailpit web UI.
- Mailpit runs on `http://localhost:8025` after `docker-compose` is started.

---
