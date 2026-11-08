# BECETTI CI build errors

Commit: 5e5de1d59bb74e691db55d5539540e6774589d3d

## compileDebugKotlin
```
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/developer/ApiGithubScreens.kt:311:49 Type mismatch: inferred type is Pair<String, Result<String?>> but Pair<String, String> was expected
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/developer/EncodingTools.kt:242:27 Type mismatch: inferred type is List<String> but List<Pair<String, String>> was expected
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:47:15 Unresolved reference: LocalContext
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:229:15 Unresolved reference: LocalContext
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:238:22 Not enough information to infer type variable T
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:238:22 Property delegate must have a 'getValue(Nothing?, KProperty<*>)' method. None of the following functions is suitable: 
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:238:33 Not enough information to infer type variable T
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:247:32 Overload resolution ambiguity: 
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:250:40 Overload resolution ambiguity: 
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:250:50 Cannot infer a type for this parameter. Please specify it explicitly.
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:254:62 Cannot infer a type for this parameter. Please specify it explicitly.
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:262:71 Overload resolution ambiguity: 
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:290:13 Overload resolution ambiguity: 
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:333:15 Unresolved reference: LocalContext
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:345:19 Not enough information to infer type variable T
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:345:19 Property delegate must have a 'getValue(Nothing?, KProperty<*>)' method. None of the following functions is suitable: 
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:345:30 Not enough information to infer type variable T
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:355:32 Overload resolution ambiguity: 
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:357:35 Unresolved reference: it
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:357:48 Unresolved reference: it
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:360:55 Unresolved reference: it
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:360:84 Unresolved reference: it
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:363:35 Overload resolution ambiguity: 
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:363:45 Cannot infer a type for this parameter. Please specify it explicitly.
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:366:64 Unresolved reference: it
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:366:85 Unresolved reference: it
e: file:///home/runner/work/BECETTITOOLS/BECETTITOOLS/app/src/main/java/com/becetti/tools/features/files/FileMasterTools.kt:392:13 Overload resolution ambiguity: 
	at org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.execute(DefaultPlanExecutor.java:463)
	at org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.run(DefaultPlanExecutor.java:380)
	at org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)
	at org.gradle.internal.concurrent.AbstractManagedExecutor$1.run(AbstractManagedExecutor.java:47)
Caused by: org.jetbrains.kotlin.gradle.tasks.CompilationErrorException: Compilation error. See log for more details
	at org.jetbrains.kotlin.gradle.tasks.TasksUtilsKt.throwExceptionIfCompilationFailed(tasksUtils.kt:20)
	at org.jetbrains.kotlin.compilerRunner.GradleKotlinCompilerWork.run(GradleKotlinCompilerWork.kt:141)
	at org.jetbrains.kotlin.compilerRunner.GradleCompilerRunnerWithWorkers$GradleKotlinCompilerWorkAction.execute(GradleCompilerRunnerWithWorkers.kt:73)
	at org.gradle.workers.internal.DefaultWorkerServer.execute(DefaultWorkerServer.java:63)
	at org.gradle.workers.internal.NoIsolationWorkerFactory$1$1.create(NoIsolationWorkerFactory.java:66)
	at org.gradle.workers.internal.NoIsolationWorkerFactory$1$1.create(NoIsolationWorkerFactory.java:62)
	at org.gradle.internal.classloader.ClassLoaderUtils.executeInClassloader(ClassLoaderUtils.java:100)
	at org.gradle.workers.internal.NoIsolationWorkerFactory$1.lambda$execute$0(NoIsolationWorkerFactory.java:62)
	at org.gradle.workers.internal.AbstractWorker$1.call(AbstractWorker.java:44)
	at org.gradle.workers.internal.AbstractWorker$1.call(AbstractWorker.java:41)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:204)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:199)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:66)
	at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:157)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:59)
	at org.gradle.internal.operations.DefaultBuildOperationRunner.call(DefaultBuildOperationRunner.java:53)
	at org.gradle.internal.operations.DefaultBuildOperationExecutor.call(DefaultBuildOperationExecutor.java:73)
	at org.gradle.workers.internal.AbstractWorker.executeWrappedInBuildOperation(AbstractWorker.java:41)
	at org.gradle.workers.internal.NoIsolationWorkerFactory$1.execute(NoIsolationWorkerFactory.java:59)
	at org.gradle.workers.internal.DefaultWorkerExecutor.lambda$submitWork$0(DefaultWorkerExecutor.java:170)
	at org.gradle.internal.work.DefaultConditionalExecutionQueue$ExecutionRunner.runExecution(DefaultConditionalExecutionQueue.java:187)
	at org.gradle.internal.work.DefaultConditionalExecutionQueue$ExecutionRunner.access$700(DefaultConditionalExecutionQueue.java:120)
	at org.gradle.internal.work.DefaultConditionalExecutionQueue$ExecutionRunner$1.run(DefaultConditionalExecutionQueue.java:162)
	at org.gradle.internal.Factories$1.create(Factories.java:31)
	at org.gradle.internal.work.DefaultWorkerLeaseService.withLocks(DefaultWorkerLeaseService.java:264)
	at org.gradle.internal.work.DefaultWorkerLeaseService.runAsWorkerThread(DefaultWorkerLeaseService.java:128)
	at org.gradle.internal.work.DefaultWorkerLeaseService.runAsWorkerThread(DefaultWorkerLeaseService.java:133)
	at org.gradle.internal.work.DefaultConditionalExecutionQueue$ExecutionRunner.runBatch(DefaultConditionalExecutionQueue.java:157)
	at org.gradle.internal.work.DefaultConditionalExecutionQueue$ExecutionRunner.run(DefaultConditionalExecutionQueue.java:126)
	... 2 more


BUILD FAILED in 1m 57s
15 actionable tasks: 15 executed
```

## assembleRelease / bundleRelease
```
```
