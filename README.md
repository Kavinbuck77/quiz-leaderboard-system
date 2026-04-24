# Quiz Leaderboard System

## Overview

This project implements a backend system that consumes quiz event data from an external API, processes it correctly, and generates a leaderboard.

The key challenge is handling duplicate API responses, which can occur in distributed systems due to retries or repeated data delivery.

---

## Objective

- Poll quiz data from the API 10 times
- Remove duplicate events
- Aggregate scores per participant
- Generate a sorted leaderboard
- Compute total score
- Submit final result

---

## Tech Stack

- Java (JDK 11+)
- Java HttpClient
- org.json library

---

## Approach

### 1. API Polling

The API is called 10 times using `poll=0` to `poll=9`.
A 5-second delay is maintained between each request as required.

---

### 2. Handling Duplicate Data

Duplicate events may appear across multiple API responses.

Each event is uniquely identified using:
(roundId + "\_" + participant)

A HashSet is used to ensure each event is processed only once.

---

### 3. Score Aggregation

A HashMap is used to store total scores:

participant → totalScore

Scores are accumulated only for unique events.

---

### 4. Leaderboard Generation

The leaderboard is sorted using:

- Primary: totalScore (descending)
- Secondary: participant name (ascending)

---

### 5. Submission

Final output is submitted in the required JSON format:

```json id="r2n0ab"
{
  "regNo": "YOUR_REG_NO",
  "leaderboard": [
    { "participant": "Name", "totalScore": value }
  ]
}
```

---

## Output

Example response:

```json id="v9l2cz"
{
  "regNo": "RA2311003020070",
  "totalPollsMade": 30,
  "submittedTotal": 1365,
  "attemptCount": 3
}
```

Total Score:
1365

Note: The program performs exactly 10 polls per execution.
The `totalPollsMade` value may increase if the program is executed multiple times, as the server maintains cumulative counts.

---

## How to Run

### Compile

```
javac -cp ".;lib/json-20231013.jar" src/QuizLeaderboard.java
```

### Run

```
java -cp ".;lib/json-20231013.jar;src" QuizLeaderboard
```

---

## Key Concepts Demonstrated

- Idempotent data processing
- Handling duplicate data in distributed systems
- REST API integration in Java
- Data aggregation and sorting

---

## Conclusion

The system correctly processes quiz data, avoids duplicate counting, and generates an accurate leaderboard for submission.
