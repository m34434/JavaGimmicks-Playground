package net.sf.javagimmicks.alexa.process.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;

public class ProcessSnapshot
{
    private String processId;
    
    private String businessKey;
    
    private Map<String, Object> variables = new HashMap<>();

    private List<ExecutionSnapshot> executions;

    public String getProcessId()
    {
        return processId;
    }

    public void setProcessId(String processId)
    {
        this.processId = processId;
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
        this.variables = variables;
    }

    public List<ExecutionSnapshot> getExecutions()
    {
        return executions;
    }

    public void setExecutions(List<ExecutionSnapshot> executions)
    {
        this.executions = executions;
    }
    
    public ProcessInstance startProcessInstance(RuntimeService r)
    {
        final ProcessInstantiationBuilder pib = r.createProcessInstanceById(processId);
        pib.businessKey(businessKey);
        pib.setVariables(variables);
        
        for(ExecutionSnapshot es : executions)
        {
            es.submit(pib);
        }
        
        return pib.execute(true, true);
    }
    
    public static ProcessSnapshot fromProcessInstance(RuntimeService r, ProcessInstance pi)
    {
        final String pid = pi.getProcessInstanceId();        
        
        final ProcessSnapshot ps = new ProcessSnapshot();
        ps.setProcessId(pi.getProcessDefinitionId());
        ps.setBusinessKey(pi.getBusinessKey());
        ps.setVariables(r.getVariables(pid));
        ps.setExecutions(new ArrayList<>());
        
        for(Execution e : r.createExecutionQuery().processInstanceId(pid).list())
        {
            final ExecutionSnapshot es = new ExecutionSnapshot();
            
            final ExecutionEntity ee = (ExecutionEntity)e;
            es.setActivityId(ee.getActivityId());
            
            if(!ee.isProcessInstanceExecution())
            {
                es.setLocalVariables(r.getVariablesLocal(e.getId()));
            }
            
            ps.getExecutions().add(es);
        }
        
        return ps;
    }
}