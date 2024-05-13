| Strategy                | Works? | Time, ms |
|-------------------------|--------|----------|
| ReadUncommitedIsolation | -      | 200      |
| ReadCommitedIsolation   | -      | 200      |
| RepeatableReadIsolation | +      | 700      |
| SerializableIsolation   | +      | 700      |
| PessimisticLock         | +      | 600      |
| OptimisticLock          | +      | 600      |
