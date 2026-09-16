# Intelligent Scheduler

A university course timetabling system built around a **hybrid genetic
algorithm**. Given a set of buildings, rooms, teachers, student groups,
and courses, it generates a weekly timetable that satisfies hard
scheduling constraints (no double-booked rooms/teachers/groups, correct
room type and capacity, teacher availability) while optimizing soft,
"nice to have" constraints (teacher preferences, daily workload balance,
compact student schedules).

This project was built as part of a bachelor's thesis on hybridizing
genetic algorithms with local-search repair strategies for the University
Course Timetabling Problem (UCTP).

## Features

- A from-scratch genetic algorithm (no external GA/optimization library)
  — tournament selection, single-point crossover, per-gene mutation.
- **Three interchangeable hybridization strategies**, each implementing a
  common `Repairer` interface (a textbook Strategy pattern):
  - **Greedy first-fit** — single-pass, takes the first valid placement found.
  - **Min-conflicts** — iterative local search, always takes the best available move.
  - **Simulated annealing** — random moves with temperature-based acceptance.
- A statistically rigorous evaluation harness: runs every strategy across
  many random seeds and reports feasibility rate, mean/stddev fitness.
- Human-readable output: a real Monday-to-Friday weekly grid per student
  group and per teacher, both as console text and as a styled, standalone
  HTML page.

## Project Structure

```
scheduler/
├── results/                   generated at runtime (HTML/CSV output)
└── src/main/java/
    ├── model/     domain entities: Room, Teacher, Course, Session, TimeSlot, ...
    ├── ga/        the algorithm: GeneticAlgorithm, ConstraintChecker,
    │              Repairer strategies, statistical evaluation harness
    ├── output/    TimetableGridFormatter, TimetableHtmlExporter
    └── demo/      runnable entry points (Main, ExperimentMain) + sample data
```

See the thesis document's System Design chapter for a full description of
every class and how they communicate at runtime.

## Data Model

Every dataset — defined in `SampleDataFactory.build()` — is built from the
same entity types, all defined in the `model` package:

| Class | Key properties | Notes |
|---|---|---|
| **Building** | `id`, `name` | A physical location that owns one or more rooms. |
| **Room** | `id`, `name`, `building`, `capacity`, `type` | `type` is one of `LECTURE_HALL`, `LAB`, or `SEMINAR_ROOM`. A room can only host a session if its type matches the course's required type *and* its capacity is at least the student group's size. |
| **Teacher** | `id`, `name`, `maxSessionsPerDay`, `unavailable`, `preferred` | See "Teacher availability" below. |
| **StudentGroup** | `id`, `name`, `size` | A cohort of students attending courses together. |
| **Course** | `id`, `subjectName`, `teacher`, `group`, `requiredRoomType`, `sessionsPerWeek` | Links one teacher to one student group, and states how many times a week they meet and what kind of room the meeting needs. |

Two further classes matter but are never authored by hand — they're
derived automatically from the classes above:

- **Session** — one weekly occurrence of a `Course`. A course with
  `sessionsPerWeek = 2` automatically expands into two `Session`
  objects; this is the actual unit the algorithm assigns a room and time
  slot to.
- **TimeSlot** — a `(day, period)` pair, where `day` is `0`-`4` for
  Monday-Friday and `period` is `0`-`7` for the eight periods in a day.
  Used both for a teacher's availability (below) and internally by the
  algorithm when assigning sessions to slots.

### Teacher availability

A `Teacher` carries two separate sets of `TimeSlot`s, which mean very
different things:

- **`unavailable`** — slots this teacher can *never* be scheduled in.
  This is a **hard constraint**: any timetable that schedules them here
  anyway is treated as infeasible.
- **`preferred`** — slots this teacher would *like* to be scheduled in.
  This is a **soft constraint**: not scheduling them here is allowed, but
  costs a small penalty the algorithm tries to minimize.

`maxSessionsPerDay` is a third, separate property — a soft cap on how
many sessions that teacher should teach on any single day; exceeding it
is allowed but penalized, similar to `preferred`.

## Requirements

- JDK 17 or later.
- No build tool, no external dependencies — everything here is plain Java
  using only the standard library.

## Building

```bash
cd scheduler/src/main/java
javac model/*.java ga/*.java output/*.java demo/*.java -d ../../../out
```

This compiles everything into an `out/` directory at the project root.

## Usage

All commands below are run from the `scheduler/` project root, after
building as above.

### Quick single-seed run

Runs all four algorithm variants (plain baseline + three hybrids) once
each, prints per-generation convergence stats, prints a weekly timetable
grid per student group to the console, and writes a styled HTML version
to `results/timetable.html`:

```bash
java -cp out demo.Main
```

This always uses the built-in sample dataset, defined in
`demo.SampleDataFactory`. To use a different dataset, edit that file's
`build()` method directly and recompile.

### Full statistical evaluation

Runs all four variants across 20 random seeds each (80 runs total),
prints aggregated summary statistics (feasibility rate, mean ± standard
deviation of fitness, average generations to reach feasibility), and
writes every individual run to a timestamped CSV file under `results/`:

```bash
java -cp out demo.ExperimentMain
```

This is slower than `demo.Main` since it runs the full generation budget
80 times over — expect it to take noticeably longer.

## Adjusting Algorithm Parameters

- **Population size, generation count, mutation rate, tournament size,
  elitism count** — set via `ga.GAConfig`, either by editing its default
  field values or by constructing and customizing a `GAConfig` instance
  where it's used in `Main` / `ExperimentMain`.
- **Repair strategy parameters** — passed to each strategy's constructor
  where it's instantiated (e.g. `MinConflictsRepairer`'s iteration count,
  or `SimulatedAnnealingRepairer`'s initial temperature and cooling rate).

## Output

- **Console**: per-generation convergence stats, a weekly timetable grid
  per student group.
- **`results/timetable.html`**: the same timetable as a styled, portable
  HTML page — one grid per student group and one per teacher.
- **`results/experiment_results_<timestamp>.csv`**: raw per-run results
  from the statistical evaluation harness (one row per seed per
  strategy), for further analysis in a spreadsheet or plotting script.
