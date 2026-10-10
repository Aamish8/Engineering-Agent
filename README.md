# 🤖 AI Engineering Agent Dashboard

An intelligent, codebase-aware AI engineering assistant built with **Java, Spring Boot, Spring AI, and PGVector**. This application indexes Java repositories using Abstract Syntax Tree (AST) parsing, performs semantic vector searches, and streams real-time responses to a modern Tailwind CSS dashboard with full conversation memory.

---

## 🚀 Key Features

* **AST-Powered Code Chunking**: Uses `JavaParser` to structurally scan repositories and break Java files down into clean class- and method-level chunks, preserving package and method context.
* **Semantic Vector Search & RAG**: Integrates **Spring AI** with **PGVector** to perform high-precision similarity searches across indexed codebases.
* **Real-Time Streaming Chat**: Leverages Spring WebFlux (`Flux<String>`) to stream live AI responses token-by-token.
* **User-Controlled Stream Interruption**: Features an `AbortController`-backed **Stop** button in the UI, allowing users to cut off active streams instantly.
* **Persistent Conversation Memory**: Backed by **Redis** to maintain context and history across chat sessions.
* **Modern Developer Interface**: A sleek, dark-mode dashboard styled with Tailwind CSS, featuring active workspace indicators, repository management, and real-time loading feedback.

---

## 🛠️ Tech Stack

* **Backend**: Java 17+, Spring Boot, Spring AI, Spring WebFlux, Spring Data JDBC
* **Parsing & Indexing**: JavaParser
* **Vector Store & Database**: PostgreSQL (`pgvector` extension), Redis
* **Frontend**: HTML5, Vanilla JavaScript, Tailwind CSS

---

## 📂 Architecture Overview

```text
[Frontend (Tailwind UI)] 
       │ (REST / SSE Stream)
       ▼
[Spring Boot Controller / WebFlux]
       │
       ├──► [CodeIndexerService] ──► (JavaParser AST) ──► [PGVectorStore]
       ├──► [EngineeringAgentService] ──► [Spring AI ChatClient] 
       └──► [ConversationMemoryService] ──► [Redis]
