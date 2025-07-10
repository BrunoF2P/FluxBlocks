package com.uneb.fluxblocks.architecture.events;

public class DatabaseEventTypes {
    
    // ===== EVENTOS DE CONEXÃO =====
    
    public static final EventType<DatabaseEvents.DatabaseConnectedEvent> DATABASE_CONNECTED = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.DatabaseDisconnectedEvent> DATABASE_DISCONNECTED = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.DatabaseConnectionFailedEvent> DATABASE_CONNECTION_FAILED = new EventType<>() {};
    
    // ===== EVENTOS DE OPERAÇÕES =====
    
    public static final EventType<DatabaseEvents.DatabaseOperationStartedEvent> DATABASE_OPERATION_STARTED = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.DatabaseOperationCompletedEvent> DATABASE_OPERATION_COMPLETED = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.DatabaseOperationFailedEvent> DATABASE_OPERATION_FAILED = new EventType<>() {};
    
    // ===== EVENTOS DE OPERAÇÕES =====
    
    public static final EventType<DatabaseEvents.DatabaseQuerySuccessEvent> DATABASE_QUERY_SUCCESS = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.DatabaseQueryFailedEvent> DATABASE_QUERY_FAILED = new EventType<>() {};
    
    // ===== EVENTOS DE PERFORMANCE =====
    
    public static final EventType<DatabaseEvents.SlowQueryDetectedEvent> SLOW_QUERY_DETECTED = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.IndexCreatedEvent> INDEX_CREATED = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.DatabaseOptimizedEvent> DATABASE_OPTIMIZED = new EventType<>() {};
    
    // ===== EVENTOS DE BACKUP =====
    
    public static final EventType<DatabaseEvents.BackupRequestEvent> BACKUP_REQUEST = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.BackupCreatedEvent> BACKUP_CREATED = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.BackupFailedEvent> BACKUP_FAILED = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.RestoreRequestEvent> RESTORE_REQUEST = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.RestoreCompletedEvent> RESTORE_COMPLETED = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.RestoreFailedEvent> RESTORE_FAILED = new EventType<>() {};
    
    // ===== EVENTOS DE MANUTENÇÃO =====
    
    public static final EventType<DatabaseEvents.DatabaseMaintenanceStartedEvent> DATABASE_MAINTENANCE_STARTED = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.DatabaseMaintenanceCompletedEvent> DATABASE_MAINTENANCE_COMPLETED = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.DataCleanupEvent> DATA_CLEANUP = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.IntegrityCheckEvent> INTEGRITY_CHECK = new EventType<>() {};
    
    // ===== EVENTOS DE MIGRAÇÃO =====
    
    public static final EventType<DatabaseEvents.DatabaseMigrationStartedEvent> DATABASE_MIGRATION_STARTED = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.DatabaseMigrationCompletedEvent> DATABASE_MIGRATION_COMPLETED = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.DatabaseMigrationFailedEvent> DATABASE_MIGRATION_FAILED = new EventType<>() {};
    
    // ===== EVENTOS DE SINCRONIZAÇÃO =====
    
    public static final EventType<DatabaseEvents.SyncStartedEvent> SYNC_STARTED = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.SyncCompletedEvent> SYNC_COMPLETED = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.SyncFailedEvent> SYNC_FAILED = new EventType<>() {};
    
    public static final EventType<DatabaseEvents.SyncConflictEvent> SYNC_CONFLICT = new EventType<>() {};
    
    private DatabaseEventTypes() {
    }
} 