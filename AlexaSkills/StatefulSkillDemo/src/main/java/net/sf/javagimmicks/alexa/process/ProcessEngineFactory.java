package net.sf.javagimmicks.alexa.process;

import java.util.function.Supplier;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.history.HistoryLevel;

public class ProcessEngineFactory implements Supplier<ProcessEngine>
{
    public static final ProcessEngineFactory INSTANCE = new ProcessEngineFactory();
    
    public static final ProcessEngine getProcessEngine()
    {
        return INSTANCE.get();
    }
    
    private final ProcessEngine pe;
    
    private ProcessEngineFactory()
    {
        final StandaloneInMemProcessEngineConfiguration cfg = new StandaloneInMemProcessEngineConfiguration();
        cfg.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP);
        cfg.setJdbcUrl("jdbc:h2:mem:camunda;DB_CLOSE_DELAY=-1");
        cfg.setJobExecutorActivate(false);
        cfg.setHistoryLevel(HistoryLevel.HISTORY_LEVEL_NONE);
        
        pe = cfg.buildProcessEngine();
    }

    @Override
    public ProcessEngine get()
    {
        return pe;
    }
    
    public static void deployFromClassPath(ProcessEngine pe, ClassLoader c, String processName)
    {
        pe.getRepositoryService().createDeployment().addInputStream(processName, c.getResourceAsStream("processes/" + processName)).deploy();
    }
}
