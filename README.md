# Instructions for candidates

This is the Java version of the Payment Gateway challenge. If you haven't already read this [README.md](https://github.com/cko-recruitment/) on the details of this exercise, please do so now.

## Requirements
- JDK 17
- Docker

## Template structure

src/ - A skeleton SpringBoot Application

test/ - Some simple JUnit tests

imposters/ - contains the bank simulator configuration. Don't change this

.editorconfig - don't change this. It ensures a consistent set of rules for submissions when reformatting code

docker-compose.yml - configures the bank simulator


## API Documentation

## Areas of Improvement

1. POST /payments endpoint should immediately return a pending status, without waiting for a response from bank.

<img width="793" alt="Screenshot 2025-05-06 at 14 54 59" src="https://github.com/user-attachments/assets/96775f59-ea05-4161-8b07-52d44828c407" />

2. JWT Authentication for the application security.
3. Validate payment is already created by ensuring idempotency on POST /payments
4. Need to track different statuses for the payment created along with timestamp.
