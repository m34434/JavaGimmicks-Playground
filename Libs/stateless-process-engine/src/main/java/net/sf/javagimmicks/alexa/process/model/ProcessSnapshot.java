package net.sf.javagimmicks.alexa.process.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceWithVariables;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;
import org.camunda.bpm.engine.task.Task;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProcessSnapshot
{
    private String processDefinitionKey;
    
    private String businessKey;
    
    private Map<String, Object> variables = new HashMap<>();

    private List<ExecutionSnapshot> executions;
    
    private List<TaskSnapshot> tasks;

    public String getProcessDefinitionKey()
    {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey)
    {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getBusinessKey()
    {
        return businessKey;
    }

    public void setBusinessKey(String businessKey)
    {
        this.businessKey = businessKey;
    }

    public Map<String, Object> getVariables()
    {
        return variables;
    }

    public void setVariables(Map<String, Object> variables)
    {
        if(variables == null || variables.isEmpty())
        {
            this.variables = null;
        }
        else
        {
            this.variables = variables;
        }
    }

    public List<ExecutionSnapshot> getExecutions()
    {
        return executions;
    }

    public void setExecutions(List<ExecutionSnapshot> executions)
    {
        if(executions == null || executions.isEmpty())
        {
            this.executions = null;
        }
        else
        {
            this.executions = executions;
        }
    }
    
    public List<TaskSnapshot> getTasks()
    {
        return tasks;
    }

    public void setTasks(List<TaskSnapshot> tasks)
    {
        if(tasks == null || tasks.isEmpty())
        {
            this.tasks = null;
        }
        else
        {
            this.tasks = tasks;
        }
    }

    public ProcessInstanceWithVariables startProcessInstance(ProcessEngine pe)
    {
        final RuntimeService runtimeService = pe.getRuntimeService();
        
        final ProcessInstantiationBuilder builder = runtimeService.createProcessInstanceByKey(processDefinitionKey);
        builder.businessKey(businessKey);
        builder.setVariables(variables);
        
        for(ExecutionSnapshot executionSnapshot : executions)
        {
            executionSnapshot.submit(builder);
        }
        
        final ProcessInstanceWithVariables pi = builder.executeWithVariablesInReturn(true, true);

        for(TaskSnapshot taskSnapshot : tasks)
        {
            taskSnapshot.submit(pe, pi);
        }
        
        return pi;
    }
    
    public static ProcessSnapshot fromProcessInstance(ProcessEngine pe, ProcessInstance pi)
    {
        final RuntimeService r = pe.getRuntimeService();
        final TaskService t = pe.getTaskService();
        
        final String pid = pi.getProcessInstanceId();        
        
        final ProcessSnapshot ps = new ProcessSnapshot();
        ps.setProcessDefinitionKey(pe.getRepositoryService().getProcessDefinition(pi.getProcessDefinitionId()).getKey());
        ps.setBusinessKey(pi.getBusinessKey());
        ps.setVariables(r.getVariables(pid));
        
        final List<ExecutionSnapshot> executions = new ArrayList<>();
        final List<TaskSnapshot> tasks = new ArrayList<>();
        
        for(Execution e : r.createExecutionQuery().processInstanceId(pid).list())
        {
            final ExecutionEntity executionEntity = (ExecutionEntity)e;
            final String activityId = executionEntity.getActivityId();
            
            if(activityId == null)
            {
                continue;
            }
            
            final ExecutionSnapshot executionSnapshot = new ExecutionSnapshot();
            executionSnapshot.setActivityId(activityId);
            
            final String executionId = e.getId();
            if(!executionEntity.isProcessInstanceExecution())
            {
                executionSnapshot.setLocalVariables(r.getVariablesLocal(executionId));
            }
            
            executions.add(executionSnapshot);
            
            final Task task = t.createTaskQuery().executionId(executionId).singleResult();
            if(task != null)
            {
                final TaskSnapshot taskSnapshot = new TaskSnapshot();
                taskSnapshot.setTaskDefinitionKey(task.getTaskDefinitionKey());
                
                final Map<String, Object> taskLocalVariables = t.getVariablesLocal(task.getId());
                
                if(!taskLocalVariables.isEmpty())
                {
                    taskSnapshot.setLocalVariables(taskLocalVariables);
                    tasks.add(taskSnapshot);
                }
            }
        }
        
        ps.setExecutions(executions);
        ps.setTasks(tasks);

        return ps;
    }
    
    public static String processToJson(ObjectMapper m, ProcessEngine pe, ProcessInstance pi) throws JsonProcessingException
    {
        return m.writeValueAsString(fromProcessInstance(pe, pi));
    }
    
    public static ProcessInstanceWithVariables jsonToProcess(ObjectMapper m, ProcessEngine pe, String json) throws JsonParseException, JsonMappingException, IOException
    {
        return m.readValue(json, ProcessSnapshot.class).startProcessInstance(pe);
    }
}