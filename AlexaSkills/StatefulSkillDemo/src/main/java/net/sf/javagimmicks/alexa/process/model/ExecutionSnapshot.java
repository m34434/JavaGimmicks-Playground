package net.sf.javagimmicks.alexa.process.model;

import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;

public class ExecutionSnapshot
{
    private String activityId;
    private Map<String, Object> localVariables;

    public String getActivityId()
    {
        return activityId;
    }

    public void setActivityId(String activityId)
    {
        this.activityId = activityId;
    }

    public Map<String, Object> getLocalVariables()
    {
        return localVariables;
    }

    public void setLocalVariables(Map<String, Object> localVariables)
    {
        this.localVariables = localVariables;
    }
    
    public void submit(ProcessInstantiationBuilder pib)
    {
        pib.startBeforeActivity(activityId).setVariablesLocal(localVariables);
    }
}