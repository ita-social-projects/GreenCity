# Architecture Analysis - GreenCity

## Current Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                          │
│  42 HTML Templates + 64 JavaScript Files                    │
│  Issues: Duplicate code, inline styles, outdated libs       │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                     CONTROLLER LAYER                         │
│  29 @RestController classes                                 │
│  Issues: No API versioning, direct service coupling         │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER                           │
│  54 ServiceImpl classes                                      │
│  Issues: 100+ line methods, 17 dependencies, N+1 queries    │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ HabitService │  │ UserService  │  │ EventService │      │
│  │ 17 deps      │  │ 12 deps      │  │ 15 deps      │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                 │                  │               │
│         └─────────────────┴──────────────────┘               │
│                           │                                  │
│                  ┌────────▼────────┐                         │
│                  │  ModelMapper    │  <-- Performance issue  │
│                  │  (360+ calls)   │                         │
│                  └────────┬────────┘                         │
└───────────────────────────┼──────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    REPOSITORY LAYER                          │
│  41 Repository interfaces                                    │
│  Issues: Inconsistent patterns, some N+1 queries            │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                      DATABASE LAYER                          │
│  PostgreSQL + Liquibase                                     │
└─────────────────────────────────────────────────────────────┘
```

## Module Structure

```
greencity/
├── core/              (Web layer - Controllers, Templates)
├── service/           (Business logic - 54 implementations)
├── service-api/       (Service interfaces)
└── dao/               (Data access - Entities, Repositories)
```

### ✅ Strengths
- Clear module separation
- Spring Boot 3.2.2 (modern)
- Proper use of dependency injection
- Repository abstraction

### ❌ Weaknesses
- Service layer bloat (100+ line methods)
- Missing facade pattern
- Direct controller → multiple services coupling
- No bounded contexts

## Problem Areas Detailed

### 1. Service Layer Complexity

```java
HabitServiceImpl
├── 17 injected dependencies
├── 45+ public methods
├── buildPageableDtoForDifferentParameters() - 35 lines
│   ├── Maps entities to DTOs
│   ├── Enriches with favorite status
│   ├── Loads acquired users (N+1)
│   ├── Loads habit assigns (N+1)
│   └── Checks custom habits
└── Multiple responsibilities mixed
```

**Recommendation:** Apply SOLID principles
```java
HabitServiceImpl
├── HabitMapper (separate)
├── HabitEnricher (separate)
├── HabitValidator (separate)
└── Core business logic only
```

### 2. Data Access Patterns

**Current (N+1 Problem):**
```java
// In buildPageableDtoForDifferentParameters()
for (HabitDto habitDto : habits) {
    // 🔴 Repository call inside loop
    habitDto.setAmountAcquiredUsers(
        habitAssignRepo.findAmountOfUsersAcquired(habitDto.getId())
    );
    
    // 🔴 Another query in loop
    Habit habit = habitRepo.findById(habitDto.getId())
        .orElseThrow(...);
    
    // 🔴 Yet another query
    List<HabitAssign> habitAssigns = 
        habitAssignRepo.findHabitsByHabitIdAndUserId(habitDto.getId(), friendId);
}
// Total: 1 + (N * 3) queries for N habits
```

**Recommended (Batch Loading):**
```java
// Load all at once
List<Long> habitIds = habits.stream()
    .map(HabitDto::getId)
    .collect(Collectors.toList());

// Single batch query
Map<Long, Long> userCounts = 
    habitAssignRepo.findAmountOfUsersAcquiredBatch(habitIds);

// Single batch query
Map<Long, Habit> habitsById = 
    habitRepo.findAllByIdIn(habitIds);

// Enrich without additional queries
// Total: 1 + 2 queries regardless of N
```

### 3. DTO Proliferation

```
268 DTO classes organized as:
├── *Dto (150 classes) - Generic DTOs
├── *VO (45 classes) - Value Objects  
├── *Request (38 classes) - API Requests
├── *Response (35 classes) - API Responses
└── Mixed patterns causing confusion
```

**Issues:**
- Inconsistent naming
- Duplicate fields across DTOs
- Hard to find correct DTO to use
- Large mapping configuration

**Recommendation:**
```
Standardize to:
├── *Request - Client → Server
├── *Response - Server → Client
└── *Data - Internal transfer
```

### 4. Frontend Architecture

```
HTML Templates (42 files)
├── Inline styles (256 instances)
├── Embedded scripts
├── Duplicate CDN imports
└── jQuery 3.5.1 (2020)

JavaScript (64 files)
├── buttonsAJAX.js (10 files, 4,349 lines)
│   └── Massive duplication
├── ES5 syntax (var, function)
├── No module system
└── No build process
```

**Issues:**
- No separation of concerns
- Hard to maintain
- No reusability
- Security vulnerabilities

**Recommendation:**
```
Create modern structure:
src/
├── js/
│   ├── common/
│   │   ├── api.js (AJAX utilities)
│   │   ├── dom.js (DOM utilities)
│   │   └── url.js (URL utilities)
│   ├── components/
│   │   ├── sort-table.js
│   │   └── language-switcher.js
│   └── pages/
│       ├── habits.js
│       └── users.js
├── css/
│   ├── base/
│   ├── components/
│   └── pages/
└── Build with Webpack/Vite
```

## Dependency Graph (Simplified)

```
Controller
    ↓ (depends on)
Service (too many responsibilities)
    ↓ (360+ calls)
ModelMapper (performance bottleneck)
    ↓
DTO Layer (268 classes)
    ↓
Repository (41 interfaces)
    ↓
Entity Layer
```

**Problem:** Too many layers with unclear boundaries

**Recommended:**
```
Controller
    ↓
Facade (new - orchestration)
    ↓
Service (single responsibility)
    ↓
Mapper (compile-time - MapStruct)
    ↓
Repository
```

## SOLID Principles Violations

### Single Responsibility Principle
❌ `HabitServiceImpl` handles:
- Habit CRUD
- Translation management
- Custom habit logic
- User assignments
- Friend habits
- DTO mapping
- Validation

✅ Should be split into:
- `HabitService` - Core CRUD
- `HabitTranslationService` - I18n
- `HabitAssignmentService` - User assignments
- `HabitSharingService` - Friend features

### Open/Closed Principle
❌ Adding new habit types requires modifying existing methods

✅ Use strategy pattern:
```java
interface HabitTypeHandler {
    HabitDto process(Habit habit);
}

class StandardHabitHandler implements HabitTypeHandler { }
class CustomHabitHandler implements HabitTypeHandler { }
```

### Dependency Inversion
❌ Services depend on concrete ModelMapper

✅ Depend on abstraction:
```java
interface EntityMapper<E, D> {
    D toDto(E entity);
    E toEntity(D dto);
}
```

## Recommended Refactoring Strategy

### Phase 1: Quick Wins (1-2 weeks)
1. Update frontend dependencies
2. Extract common JavaScript utilities
3. Create Thymeleaf fragments

### Phase 2: Performance (3-4 weeks)
1. Migrate to MapStruct
2. Fix N+1 queries with batch loading
3. Add caching for frequent queries

### Phase 3: Architecture (2-3 months)
1. Introduce facade layer
2. Split large services
3. Standardize DTOs
4. Add API versioning

### Phase 4: Long-term (6+ months)
1. Consider bounded contexts
2. Evaluate microservices
3. Implement CQRS for read-heavy operations
4. Add event-driven architecture

## Metrics Tracking

| Metric | Current | Target | Progress |
|--------|---------|--------|----------|
| Service Method Length | 100+ lines | < 30 lines | 🔴 0% |
| Service Dependencies | 17 max | < 5 max | 🔴 0% |
| DTO Classes | 268 | 150 | 🔴 0% |
| Code Duplication | 4,349 JS lines | < 2,000 lines | 🔴 0% |
| Test Coverage | Unknown | > 80% | 🔴 0% |
| API Response Time | Unknown | < 200ms | 🔴 0% |

## Conclusion

The architecture is fundamentally sound (layered, modular) but needs refinement:
- **Immediate:** Fix security and performance issues
- **Short-term:** Reduce complexity and duplication
- **Long-term:** Introduce patterns for better scalability

Focus on incremental improvements rather than big rewrites.
