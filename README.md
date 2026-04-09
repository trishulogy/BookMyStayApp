# 🏨 BookMyStayApp

A **console-based Hotel Booking Management System** built using **Core Java and Data Structures**, designed to demonstrate how real-world software systems evolve from simple logic to scalable and reliable architectures.

---

## 📌 Objective

This project focuses on applying **Data Structures and Object-Oriented Programming (OOP)** concepts to solve real-world booking problems such as:

- Managing room availability
- Handling concurrent booking requests
- Preventing double booking
- Supporting add-on services
- Ensuring data consistency and recovery

---

## 🚀 Features

### 🔹 Core Functionality
- Room types using **Abstraction & Inheritance**
- Centralized inventory using **HashMap**
- Room search with **read-only filtering**
- Booking request handling using **Queue (FIFO)**

### 🔹 Advanced Booking System
- Room allocation with **unique IDs (Set)**
- Booking history tracking using **List**
- Add-on services using **Map + List**
- Booking cancellation with **Stack (LIFO rollback)**

### 🔹 Reliability & Safety
- Input validation and **custom exception handling**
- Fail-fast design to prevent invalid states
- Graceful error handling without crashes

### 🔹 Concurrency (Multi-Threading)
- Simulates multiple users booking simultaneously
- Uses `synchronized` methods to prevent race conditions
- Ensures thread-safe inventory updates

### 🔹 Persistence & Recovery
- Saves system state using **Serialization**
- Restores booking and inventory data on restart
- Handles missing/corrupt files safely

---

## 🧠 Concepts Used

| Concept | Implementation |
|--------|--------|
| OOP | Classes, Inheritance, Encapsulation |
| HashMap | Inventory & Mapping |
| Queue | Booking Requests (FIFO) |
| Set | Unique Room Allocation |
| List | Booking History |
| Stack | Cancellation Rollback |
| Exception Handling | Custom Exceptions |
| Multithreading | Concurrent Booking |
| File Handling | Data Persistence |

---

## 🏗️ System Design Highlights

- **Separation of Concerns**
  - Inventory, Booking, Search, and Services are independent

- **Single Source of Truth**
  - All availability managed centrally

- **Scalable Design**
  - Easy to add new room types or services

- **Fail-Safe Architecture**
  - Prevents inconsistent states and data corruption

---

## 🖥️ How to Run

```bash
javac BookMyStayApp.java
java BookMyStayApp
