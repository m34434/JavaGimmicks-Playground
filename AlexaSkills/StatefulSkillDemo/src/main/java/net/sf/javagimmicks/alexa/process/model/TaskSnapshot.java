package net.sf.javagimmicks.alexa.process.model;

import java.util.Map;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

public class TaskSnapshot
{
    private String taskDefinitionKey;
    private Map<String, Object> localVariables;

    public String getTaskDefinitionKey()
    {
        return taskDefinitionKey;
    }

    public void setTaskDefinitionKey(String taskDefinitionKey)
    {
        this.taskDefinitionKey = taskDefinitionKey;
    }

    public Map<String, Object> getLocalVariables()
    {
        return localVariables;
    }

    public void setLocalVariables(Map<String, Object> localVariables)
    {
        if(localVariables.isEmpty())
        {
            this.localVariables = null;
        }
        else
        {
            this.localVariables = localVariables;
        }
    }
    
    public void submit(ProcessEngine pe, ProcessInstance pi)
    {
        final TaskService taskService = pe.getTaskService();
        
        final Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).taskDefinitionKey(taskDefinitionKey).singleResult();
        taskService.setVariablesLocal(task.getId(), localVariables);
    }
}