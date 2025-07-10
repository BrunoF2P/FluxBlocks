package com.uneb.fluxblocks.architecture.events;


public abstract class DatabaseEvents {
    
    // ===== EVENTOS DE CONEXÃO =====
    
    /**
     * Conexão com banco estabelecida
     */
    public record DatabaseConnectedEvent(String databaseType, String connectionInfo) {}
    
    /**
     * Conexão com banco perdida
     */
    public record DatabaseDisconnectedEvent(String reason) {}
    
    /**
     * Tentativa de conexão com banco
     */
    public record DatabaseConnectionRequestEvent() {}
    
    /**
     * Falha na conexão com banco
     */
    public record DatabaseConnectionFailedEvent(String reason) {}
    
    // ===== EVENTOS DE OPERAÇÕES =====
    
    /**
     * Operação de banco iniciada
     */
    public record DatabaseOperationStartedEvent(String operation, String table) {}
    
    /**
     * Operação de banco concluída
     */
    public record DatabaseOperationCompletedEvent(String operation, String table, int affectedRows) {}
    
    /**
     * Erro em operação de banco
     */
    public record DatabaseOperationFailedEvent(String operation, String table, String error) {}
    
    /**
     * Query executada com sucesso
     */
    public record DatabaseQuerySuccessEvent(String query, int resultCount) {}
    
    /**
     * Query falhou
     */
    public record DatabaseQueryFailedEvent(String query, String error) {}
    
    // ===== EVENTOS DE PERFORMANCE =====
    
    /**
     * Query lenta detectada
     */
    public record SlowQueryDetectedEvent(String query, long executionTime) {}
    
    /**
     * Índice criado
     */
    public record IndexCreatedEvent(String table, String indexName) {}
    
    /**
     * Otimização de banco realizada
     */
    public record DatabaseOptimizedEvent(String optimizationType) {}
    
    // ===== EVENTOS DE BACKUP =====
    
    /**
     * Backup solicitado
     */
    public record BackupRequestEvent(String backupPath) {}
    
    /**
     * Backup criado com sucesso
     */
    public record BackupCreatedEvent(String backupPath, long size) {}
    
    /**
     * Falha ao criar backup
     */
    public record BackupFailedEvent(String backupPath, String reason) {}
    
    /**
     * Restauração solicitada
     */
    public record RestoreRequestEvent(String backupPath) {}
    
    /**
     * Restauração concluída
     */
    public record RestoreCompletedEvent(String backupPath) {}
    
    /**
     * Falha na restauração
     */
    public record RestoreFailedEvent(String backupPath, String reason) {}
    
    // ===== EVENTOS DE MANUTENÇÃO =====
    
    /**
     * Manutenção de banco iniciada
     */
    public record DatabaseMaintenanceStartedEvent(String maintenanceType) {}
    
    /**
     * Manutenção de banco concluída
     */
    public record DatabaseMaintenanceCompletedEvent(String maintenanceType, String results) {}
    
    /**
     * Limpeza de dados antigos
     */
    public record DataCleanupEvent(String table, int deletedRows) {}
    
    /**
     * Verificação de integridade
     */
    public record IntegrityCheckEvent(String table, boolean passed) {}
    
    // ===== EVENTOS DE MIGRAÇÃO =====
    
    /**
     * Migração de banco iniciada
     */
    public record DatabaseMigrationStartedEvent(String fromVersion, String toVersion) {}
    
    /**
     * Migração de banco concluída
     */
    public record DatabaseMigrationCompletedEvent(String fromVersion, String toVersion) {}
    
    /**
     * Falha na migração
     */
    public record DatabaseMigrationFailedEvent(String fromVersion, String toVersion, String error) {}
    
    // ===== EVENTOS DE SINCRONIZAÇÃO =====
    
    /**
     * Sincronização iniciada
     */
    public record SyncStartedEvent(String syncType) {}
    
    /**
     * Sincronização concluída
     */
    public record SyncCompletedEvent(String syncType, int syncedItems) {}
    
    /**
     * Falha na sincronização
     */
    public record SyncFailedEvent(String syncType, String error) {}
    
    /**
     * Conflito de sincronização detectado
     */
    public record SyncConflictEvent(String entityType, String entityId) {}
    
    private DatabaseEvents() {
    }
} 