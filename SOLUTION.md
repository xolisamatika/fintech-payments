# SOLUTION.md (high-level notes to include)

**Atomicity**: Both balance updates + two immutable ledger entries in a single DB transaction, guarded by pessimistic row locks (or switch to optimistic via @Version if you prefer retries).

**No distributed transaction**: Transfer Service makes a single call to Ledger; Ledger is the source of truth for balances.

Idempotency:

- **Transfer**: transfers(clientKey UNIQUE) ensures the same response for repeat client calls (24h TTL can be a scheduled cleanup).

**Concurrency**: Orders row locks by account id to avoid deadlocks; prevents negative balances.

**Resilience**: Resilience4j CircuitBreaker around Ledger call; logs failure & breaker state changes; request correlation via X-Request-Id (propagated downstream).

**Batch**: Parallelizes up to 20 with a bounded pool; Java 21 could switch to virtual threads (Executors.newVirtualThreadPerTaskExecutor()), note if used.

**Security ready**: Add API keys/JWT later via a Gateway or Spring Security filters at each service; correlation header remains.

**Observability**: Structured logs with request id; could add Micrometer and Prometheus later.