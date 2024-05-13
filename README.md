| Strategy                | Works? | Time, ms |
|-------------------------|--------|----------|
| ReadUncommitedIsolation | -      | 1200     |
| ReadCommitedIsolation   | -      | 1200     |
| RepeatableReadIsolation | +      | 1600     |
| SerializableIsolation   | +      | 1500     |
| PessimisticLock         | +      | 1500     |
| OptimisticLock          |        |          |
