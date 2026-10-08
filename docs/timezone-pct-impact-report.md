# BC Permanent Pacific Time (PCT, UTC-7): Impact Report for nr-brmb-common

| | |
|---|---|
| **Prepared** | 2026-10-07 |
| **Repo state** | public GitHub `dhlevi/nr-brmb-common`, branch `main` @ `7416d1b` (2025-10-16, "Merge pull request #9 from bcgov/hwang-vividsolutions-patch-1"). This commit is identical to `main` in the upstream `bcgov/nr-brmb-common` repository, so the findings apply to both. |
| **Components** | Shared Java 21 library, version `2.0.0-SNAPSHOT`, 205 main Java files in 12 modules: `brmb-common-utils`, `-model`, `-persistence`, `-rest-common`, `-rest-endpoints`, `-rest-client`, `-service-api`, `-http-utils`, `-checkhealth` and the code table modules (`-code-persistence`, `-code-service-api`, `-code-endpoints`). Oracle JDBC (`ojdbc7` 12.1.3). No Dockerfile or runtime settings; consuming applications supply the JVM. |
| **Deadline** | **Sunday 2026-11-01, 02:00 local (09:00 UTC)**, about 3.5 weeks away |
| **Bottom line** | **Medium risk, carried by the consuming applications.** The library contains no fixed PDT or PST offsets, and its REST serializers write `Instant` values as UTC and `LocalDate` values as plain dates, which is correct. However, `InstantTypeHandler` and the code table DAO store and read instants as **wall time in the JVM default zone** (B1), and several `DateUtils` helpers convert between instants and calendar days in the JVM default zone (B2). Every application that uses this library therefore needs a **JDK with tzdata 2026b (21.0.12 or later)** and `-Duser.timezone=America/Vancouver`, and all of its nodes must be patched together. The library is a near copy of `wfone-common-lib`, and the same finding applies. |

---

## 1. What changed (same basis as the earlier reports)

- **Government rule.** BC stopped changing clocks after 2026-03-08. On **2026-11-01 clocks do not fall back.** BC stays at **UTC-7** all year, named *Pacific time (PCT)*.
- **IANA tzdata 2026b** models `America/Vancouver` as permanent UTC-7 from 2026-11-01 02:00.
- **JDK builds with the rule:** 8u501, 11.0.32, 17.0.20, 21.0.12 and 25.0.4 or later. Earlier builds treat BC winter as PST (UTC-8).
- **Oracle** applies its own time zone file to `CURRENT_DATE` and `CURRENT_TIMESTAMP` in the session zone, which the JDBC driver sets from the JVM zone.

---

## 2. Summary of findings

| # | Area | Severity | Fails on Nov 1? | Fix |
|---|---|---|---|---|
| B1 | `InstantTypeHandler` and `BaseDao.getInstant` store and read `Instant` values as JVM wall time in `TIMESTAMP` / `DATE` columns | **Medium** | **Yes, in consuming applications** on an unpatched JVM or a mixed fleet: instants written after Nov 1 are off by one hour when read by a node with different rules; future-dated values written before the patch read back one hour off | R1, R2, R3 |
| B2 | `DateUtils` convenience overloads default to `ZoneOffset.systemDefault()` (`toLocalDate(long)`, `toLocalDate(Date)`, `toDate(LocalDate)`, `toZonedDateTime(Date)`, `toInstantString(...)`, `toLocalDateString(Date)`) | **Medium** | **Yes, in consuming applications** that exchange local-midnight epochs with a peer on different rules: dates shift by one day | R1, R2, R5 |
| B3 | Code tables: `CURRENT_DATE` / `CURRENT_TIMESTAMP` in Oracle compared with `LocalDate.now()` in the JVM | Low | Only if the Oracle time zone file and the JVM disagree, and only for changes made between 23:00 and midnight | R1, R4 |
| B4 | `BaseEndpointsImpl.toDate(String)` parses offset-free strings in the JVM zone | Low | Only on an unpatched JVM; epoch values are unaffected | R1 |
| B5 | `TimestampJAXBAdapter` formats and parses without an offset in the JVM zone | Low | Only on an unpatched JVM; not used inside the library | R1, R6 |
| B6 | `ZonedDateTime` serializers and deserializers | None | No; they write and read ISO instants and only attach the JVM zone for display | n/a |
| B7 | `LocalDate`, `LocalDateTime` and `LocalTime` type handlers and serializers; `Instant` and `Date` REST serializers | None | No | n/a |
| D1 | `DateUtils.toInstantArray` always throws `ClassCastException` (pre-existing, reproduced) | Medium | Not related to the change | R7 |
| D2 | Code tables set `ESTABLISHED_DATE` / `EXPIRY_DATE` to Oracle `CURRENT_DATE`, which carries a time of day (pre-existing, needs verification) | Medium | Not related to the change | R7 |
| D3 | `ojdbc7` 12.1.3 driver used with Java 21 (housekeeping) | Low | Not related to the change | R8 |

---

## 3. Overview

This repository is the BRMB counterpart of `wfone-common-lib`: the packages are renamed to `ca.bc.gov.brmb.common`, but the date handling code is largely the same. It ships no application and no runtime configuration. The time zone behaviour that matters is therefore the behaviour each consuming application inherits:

- **Correct by design:** the REST layer serializes `Instant` values as ISO-8601 UTC (`InstantJacksonSerializer`, `InstantJAXBAdapter`), `java.util.Date` values as epoch milliseconds (`DateJAXBAdapter`), and `LocalDate`, `LocalDateTime` and `LocalTime` values as plain ISO strings. None of these depend on tzdata.
- **Depends on the JVM default zone:** the persistence layer converts `Instant` values to JDBC `Timestamp` wall time, and the utility class converts between instants and calendar days in the JVM default zone unless the caller passes a zone.

A consuming application that runs on a JDK with tzdata 2026b, sets `America/Vancouver` explicitly, and patches all nodes at once sees no behaviour change.

---

## 4. Areas of Concern

### B1. Instants stored as JVM wall time: MEDIUM

`brmb-common-persistence/.../dao/mybatis/InstantTypeHandler.java`:

- Line 45: `statement.setTimestamp(position, Timestamp.from(value));`
- Lines 20, 26 and 33: `resultSet.getTimestamp(...)` followed by `value.toInstant()`.

`brmb-common-code-persistence/.../dao/jdbc/BaseDao.java` lines 25-35 read `WHEN_CREATED` and `WHEN_UPDATED` the same way (`CodeTableDaoImpl.java` lines 145 and 147).

`Timestamp.from(Instant)` and `Timestamp.toInstant()` convert through the JVM default zone. Oracle `DATE` and `TIMESTAMP` columns hold no offset, so the database stores the local wall time of whichever JVM wrote the row. Consequences:

1. **Unpatched JVM after Nov 1.** Writing `2026-12-01T19:00:00Z` stores `11:00` (PST) instead of `12:00` (PCT). The same JVM reads it back consistently, but every other consumer of the column (reports, SQL, a patched node, the database's own `CURRENT_TIMESTAMP`) sees an instant one hour off.
2. **Mixed fleet.** Patched and unpatched nodes write different wall times for the same instant and read each other's rows one hour off.
3. **Future-dated values written before the patch.** A value for a date after Nov 1 written today by an unpatched JVM is stored as PST wall time. After the JVM is patched, it reads back one hour later than intended. Scheduled dates, deadlines and expiry timestamps are the typical examples.

This is the same finding as the `InstantTypeHandler` finding in the `wfone-common-lib` report.

### B2. `DateUtils` defaults to the JVM zone: MEDIUM

`brmb-common-utils/.../utils/DateUtils.java`:

- Line 26: `toLocalDate(long epochMilli)` calls `toLocalDate(epochMilli, ZoneOffset.systemDefault())`.
- Line 151: `toLocalDate(Date value)` and line 167: `toDate(LocalDate value)` use `systemDefault()`; `toDate(LocalDate)` returns local midnight (`atStartOfDay`).
- Line 64 and the `Date` overloads of `toInstantString`, `toZonedDateTime` and `toLocalDateString` also default to `systemDefault()`.

Calendar dates exchanged as local-midnight epoch values change meaning when the sender and receiver disagree on the rules. A patched browser or service sends 2026-12-01 as `07:00Z`; an unpatched JVM converts that to 2026-11-30. Callers that pass an explicit `ZoneId` are not affected by the default, but still need a JDK that knows the new rule for `America/Vancouver`.

### B3. Code tables mix database and JVM clocks: LOW

`brmb-common-code-persistence/.../dao/jdbc/CodeTableDaoImpl.java` and `CodeHierarchyDaoImpl.java`:

- Inserts default `ESTABLISHED_DATE` to `CURRENT_DATE` (line 184) and set `WHEN_CREATED` / `WHEN_UPDATED` to `CURRENT_TIMESTAMP` (lines 188, 190 and 236). Expiry sets `EXPIRY_DATE = CURRENT_DATE` (line 273; `CodeHierarchyDaoImpl.java` line 310).
- The decision to expire a row uses `LocalDate.now()` in the JVM (line 98; `CodeHierarchyDaoImpl.java` line 103), and the validators fetch with `LocalDate.now()` (`CodeValueValidator.java` line 47, `CodeHierarchyValidator.java` line 52).

Oracle evaluates `CURRENT_DATE` and `CURRENT_TIMESTAMP` in the session zone using its own time zone file. If that file and the JVM disagree after Nov 1, the two clocks differ by one hour. The effect is limited to code table maintenance between 23:00 and midnight, and to `WHEN_CREATED` / `WHEN_UPDATED` audit values read through B1.

### B4. Offset-free request parameters: LOW

`brmb-common-rest-endpoints/.../BaseEndpointsImpl.java` lines 417-449: `toDate(String)` accepts epoch milliseconds (safe) or `yyyy-MM-dd'T'HH:mm:ss` and `yyyy-MM-dd` strings, which `SimpleDateFormat` parses in the JVM zone. Offset-free strings mean local time to the JVM, so an unpatched JVM reads them one hour off after Nov 1.

### B5. `TimestampJAXBAdapter`: LOW

`brmb-common-rest-common/.../transformers/TimestampJAXBAdapter.java` lines 19 and 28 use `SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S")` with no offset, in the JVM zone. The library does not reference it, but a consuming application could. Values are ambiguous to any client and shift by one hour on an unpatched JVM.

### B6 and B7. No impact

- `ZonedDateTimeJAXBAdapter` and `ZonedDateTimeJacksonDeserializer` (lines 24-43) parse an ISO instant or epoch value and attach `ZoneId.systemDefault()`; the instant is preserved. The serializers write `ISO_INSTANT`.
- `LocalDateTypeHandler`, `LocalDateTimeTypeHandler` and `LocalTimeTypeHandler` round-trip through `java.sql.Date`, `Timestamp` and strings without changing the local value.
- `InstantJacksonSerializer`, `InstantJAXBAdapter`, `DateJAXBAdapter` and the `LocalDate`, `LocalDateTime` and `LocalTime` serializers are zone-independent.

---

## 5. Areas of Failure

The library itself has no failure on Nov 1 because it does not run on its own. In a consuming application:

- **F1 (from B1):** On an unpatched JVM or a mixed fleet, instants written after Nov 1 are stored one hour off; future-dated instants written before the patch read back one hour off after it.
- **F2 (from B2):** Calendar dates passed as local-midnight epochs between a patched peer and an unpatched JVM shift by one day.

---

## 6. Pre-existing Defects Found (independent of this change)

- **D1.** `DateUtils.java` line 127, `toInstantArray(String[])`, ends with `return (Instant[]) instantList.toArray();`. `List.toArray()` returns `Object[]`, so the cast always throws `ClassCastException`, even for an empty array. This was reproduced on Java 21. The method is not called inside the library. The fix is `return instantList.toArray(new Instant[0]);`.
- **D2.** In Oracle, `CURRENT_DATE` returns a `DATE` with the current time of day. `CodeTableDaoImpl` sets `ESTABLISHED_DATE` and `EXPIRY_DATE` to `CURRENT_DATE` (lines 184 and 273), but fetches with `WHERE ? BETWEEN ESTABLISHED_DATE AND EXPIRY_DATE` (line 120), binding the `effectiveAsOfDate` as midnight. A code added today at 14:00 is therefore not returned for an "as of today" request until tomorrow, and a code expired today is still returned for today. This needs verification against the actual column types; `TRUNC(CURRENT_DATE)` would remove the time of day. `CodeHierarchyDaoImpl` uses the same pattern (lines 228 and 310).
- **D3.** The Oracle driver is `ojdbc7` 12.1.3 (`pom.xml` line 95), built for Java 7 and 8. It works with Java 21 in most cases but is unsupported; `ojdbc11` is the matching driver.

---

## 7. Potential Resolutions

| # | Action | Owner | Priority |
|---|--------|-------|----------|
| R1 | In every consuming application, run on a JDK with tzdata 2026b (21.0.12 or later, since the library targets Java 21) and confirm `ZoneId.of("America/Vancouver").getRules().getOffset(Instant.parse("2026-12-01T12:00:00Z"))` returns `-07:00`. | Consuming teams and Platform | Before 2026-11-01 |
| R2 | Set `-Duser.timezone=America/Vancouver` explicitly in each consuming application, rather than relying on the host or `TZ`. | Consuming teams | Before 2026-11-01 |
| R3 | Patch all nodes of a consuming application at the same time, so that no mixed period exists (B1). Review any future-dated `Instant` columns written before the patch. | Consuming teams | Before 2026-11-01 |
| R4 | Confirm that the Oracle database time zone file carries 2026b (`SELECT * FROM V$TIMEZONE_FILE`). | DBA | Before 2026-11-01 |
| R5 | Longer term, change `InstantTypeHandler` to `statement.setObject(position, value.atOffset(ZoneOffset.UTC))` with `TIMESTAMP WITH TIME ZONE` columns, or store UTC explicitly, and deprecate the `DateUtils` overloads that default to `systemDefault()` in favour of the explicit `ZoneId` versions. | Development | Optional |
| R6 | Deprecate `TimestampJAXBAdapter`, or add an explicit UTC zone and offset. | Development | Optional |
| R7 | Fix D1 and verify D2. | Development | Independent |
| R8 | Replace `ojdbc7` with `ojdbc11` (D3). | Development | Housekeeping |

---

## 8. Verification Checklist

1. In a consuming application on the target JDK, write an `Instant` of `2026-12-01T19:00:00Z` through `InstantTypeHandler` and confirm that the column holds `2026-12-01 12:00:00`.
2. Call `DateUtils.toLocalDate(1796108400000L)` (`2026-12-01T07:00:00Z`) and confirm it returns `2026-12-01`.
3. On the Oracle database after Nov 1, confirm that `SELECT SESSIONTIMEZONE, CURRENT_TIMESTAMP FROM DUAL` shows `America/Vancouver` and an offset of `-07:00`.

---

## 9. Cross-References

- **wfone-common-lib** report: same `InstantTypeHandler` and `DateUtils` design; the BRMB library appears to be derived from it.
- **nr-brmb-pit-claim** report: currently uses the `wfone-common` modules rather than this library.
