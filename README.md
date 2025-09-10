# DebateHub Phone App

## Overview
This is a mobile debate application built in **Android Studio Narwhal** using **Android SDK 36 (Android 16.0 "Baklava")**.  
It demonstrates structured app architecture with Activities, Fragments, an external API, and local persistence.  
The app is designed as a simplified mobile companion for managing debates and their messages.

---

## Features
- **Login/Authentication screen** (dummy auth flow with SharedPreferences for persistence).
- **Debates list** – fetch debates from external JSON API.
- **Debate details** – view debate messages, post new messages, and like/unlike messages.
- **Offline caching** – debates and messages stored locally in SQLite via Room.
- **Optimistic UI** for liking messages: UI updates instantly and syncs later with server.
- **Human-readable timestamps** for messages.

---

## Activities
The app contains **4 Activities**:
1. `MainActivity` – entry point, checks login status.
2. `AuthActivity` – handles login.
3. `DebatesActivity` – lists all debates fetched from API/local DB.
4. `DebateDetailActivity` – shows a single debate’s messages, uses `DebateDetailFragment` for UI.

**Data transfer between Activities**:
- Done via **Intents with extras**, e.g. debateId and debateTitle passed from `DebatesActivity` → `DebateDetailActivity` → `DebateDetailFragment`.

---

## Fragments
- **DebateDetailFragment**:  
  Used for debate messages screen to separate UI logic from the hosting Activity.  
  Benefits:
    - Lifecycle-aware (handles rotation and process death better).
    - Arguments passed safely via `newInstance()` factory method.
    - Keeps Activities simpler and more focused.

---

## External API / Mock Database
The app connects to a **JSON server** acting as a mock backend.  
The database file is located at:  

Run the mock API with:
```bash
    json-server --watch db.json --port 8080 --host 0.0.0.0
```