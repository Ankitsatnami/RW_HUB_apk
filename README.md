<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:4285F4,100:7F52FF&height=200&section=header&text=Robotics%20Wala%20Hub&fontSize=60&fontAlignY=35&animation=twinkling&fontColor=ffffff" width="100%"/>

### ✧ *Enterprise-Grade Cyber-Robotics Lab Management & Research Ecosystem* ✧

[![Android CI](https://img.shields.io/github/actions/workflow/status/Ankitsatnami/RW_HUB_apk/build-apk.yml?branch=main&label=CI%20Build&logo=github-actions&logoColor=white&style=for-the-badge&color=2088FF)](https://github.com/Ankitsatnami/RW_HUB_apk/actions)
[![Kotlin Version](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android API](https://img.shields.io/badge/API-24%20--%2035-34A853?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)

<br/>

**[ 📥 Download APK (Direct) ](https://ankitsatnami.github.io/RW_HUB_apk/RW_HUB.apk) ❖ [ 🌐 Live Web Simulator ](https://ankitsatnami.github.io/RW_HUB_apk/) ❖ [ 📖 User Guide ](USER_GUIDE.md) ❖ [ 🐛 Report Bug ](https://github.com/Ankitsatnami/RW_HUB_apk/issues/new?template=bug_report.yml)**

<br/>

</div>

---

> [!NOTE]  
> **Robotics Wala Hub (RW HUB)** is a comprehensive, production-ready Android mobile application and web-based management platform built specifically for university robotics laboratories, makerspaces, innovation centers, and hardware research clubs.

<br/>

## ❖ ❲ 📑 Table of Contents ❳

<details>
<summary><b>Click to Expand</b></summary>

- [Overview](#-overview)
- [Key Features](#-key-features)
- [System Architecture](#-system-architecture)
- [Tech Stack](#-tech-stack)
- [Project Directory Structure](#-project-directory-structure)
- [Getting Started & Build Instructions](#-getting-started--build-instructions)
- [Web Simulator](#-web-simulator)
- [Contributing & License](#-contributing)
</details>

<br/>

## ❖ ❲ ⚡ Key Features ❳

<table>
  <tr>
    <td width="50%">
      <h3>🔐 <kbd>Authentication & Role Isolation</kbd></h3>
      <ul>
        <li>Secure Firebase Authentication (Email & Password).</li>
        <li>Approval-gated student onboarding (Pending, Approved, Suspended).</li>
        <li>Dedicated dashboards tailored for <code>Students</code> vs. <code>Admins</code>.</li>
      </ul>
    </td>
    <td width="50%">
      <h3>📷 <kbd>Digital QR Attendance Engine</kbd></h3>
      <ul>
        <li>Lightning-fast QR scanning via <b>Google ML Kit + CameraX</b>.</li>
        <li>Dynamic attendance QR generation using <b>ZXing</b> for faculty.</li>
        <li>Automated lab work hours calculation and duplicate prevention.</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td width="50%">
      <h3>🗓️ <kbd>Lab Slot & Bench Scheduling</kbd></h3>
      <ul>
        <li>Interactive workstation and equipment booking calendar.</li>
        <li>Automated conflict detection prevents overlapping reservations.</li>
        <li>Admin one-click approval, rescheduling, and cancellation alerts.</li>
      </ul>
    </td>
    <td width="50%">
      <h3>⚙️ <kbd>Hardware & Tool Inventory</kbd></h3>
      <ul>
        <li>Catalog for microcontrollers (ESP32, STM32, Arduino), sensors, actuators.</li>
        <li>Atomic checkout & return system with real-time stock deductions.</li>
        <li>Low-stock triggers and overdue equipment tracking.</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td width="50%">
      <h3>🏆 <kbd>Continuous Achievement Showcase</kbd></h3>
      <ul>
        <li>Dynamic sliding image showcase on student dashboard.</li>
        <li>Admin photo upload pipeline with instant live broadcast.</li>
        <li>Digital certificate portfolio with gamification points & badges.</li>
      </ul>
    </td>
    <td width="50%">
      <h3>🚀 <kbd>Projects, Tasks & Budget Tracker</kbd></h3>
      <ul>
        <li>Multi-student team milestones with progress tracking.</li>
        <li>Weekly milestone submission and faculty review workflow.</li>
        <li>Bill & expense receipt upload with automated balance auditing.</li>
      </ul>
    </td>
  </tr>
</table>

<br/>

## ❖ ❲ 🏛️ System Architecture ❳

> [!TIP]
> RW HUB strictly adheres to the modern Android **MVVM (Model-View-ViewModel)** architectural pattern with Google's recommended **Clean Architecture** principles.

```mermaid
graph TD
    subgraph UI_Layer["🎨 UI Presentation Layer (Jetpack Compose)"]
        A[StudentHomeScreen]
        B[AdminDashboardScreen]
        C[QRAttendanceScreen]
    end

    subgraph ViewModel_Layer["🧠 State & ViewModel Layer"]
        VM1[StudentHomeViewModel]
        VM2[AdminDashboardViewModel]
        VM3[AttendanceViewModel]
    end

    subgraph Domain_Layer["📦 Repository & Domain Layer"]
        R1[AuthRepository]
        R2[AttendanceRepository]
        R3[InventoryRepository]
    end

    subgraph Data_Layer["☁️ Backend & Storage (Google Firebase)"]
        F1[(Cloud Firestore)]
        F2[Firebase Authentication]
        F3[Firebase Cloud Storage]
    end

    UI_Layer --> ViewModel_Layer
    ViewModel_Layer --> Domain_Layer
    Domain_Layer --> Data_Layer
```

<br/>

## ❖ ❲ 🛠️ Tech Stack ❳

### 💠 Android Application
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.10.00-4285F4?style=flat-square&logo=android&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%20Design-3-0066FF?style=flat-square&logo=material-design&logoColor=white)
![Coroutines](https://img.shields.io/badge/Coroutines-StateFlow-00599C?style=flat-square)
![Coil](https://img.shields.io/badge/Image%20Loading-Coil%20Compose-1C7C54?style=flat-square)

### ☁️ Backend & Cloud Services
![Firebase Auth](https://img.shields.io/badge/Firebase-Authentication-FFCA28?style=flat-square&logo=firebase&logoColor=black)
![Cloud Firestore](https://img.shields.io/badge/Database-Cloud%20Firestore-FFCA28?style=flat-square&logo=firebase&logoColor=black)
![Cloud Storage](https://img.shields.io/badge/Storage-Firebase%20Cloud%20Storage-FFCA28?style=flat-square&logo=firebase&logoColor=black)
![ML Kit](https://img.shields.io/badge/Vision-Google%20ML%20Kit-4285F4?style=flat-square&logo=google&logoColor=white)

<br/>

## ❖ ❲ 💻 Getting Started & Build Instructions ❳

> [!IMPORTANT]
> - **Android Studio** Ladybug (2024.2.1+) or newer is required.
> - Ensure **JDK 17** or **JDK 21** is configured in your `JAVA_HOME`.

```bash
# 1. Clone the repository
git clone https://github.com/Ankitsatnami/RW_HUB_apk.git

# 2. Navigate into project directory
cd RW_HUB_apk

# 3. Assemble Debug APK
./gradlew assembleDebug
```

> [!WARNING]
> Before running the app, make sure to add your own `google-services.json` from Firebase Console to the `app/` directory!

<br/>

## ❖ ❲ 🌐 Web Simulator ❳

Can't install the APK right now? RW HUB includes a high-fidelity, interactive **Web Simulator** that runs in any modern browser!

**[ 🚀 Launch Simulator Online ](https://ankitsatnami.github.io/RW_HUB_apk/)**

*Or run locally:*
```bash
npx serve .
```

<br/>

## ❖ ❲ 🤝 Contributing & License ❳

<div align="center">

Contributions are what make the open-source community an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**!

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for more details.

---

**Lead Maintainer:** [@Ankitsatnami](https://github.com/Ankitsatnami) ✦ **Project:** Robotics Wala Hub (RW HUB) 🤖

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:7F52FF,100:4285F4&height=100&section=footer" width="100%"/>

</div>
