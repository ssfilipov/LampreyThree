/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eel.seprphase2.Simulator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import eel.seprphase2.FailureModel.FailableComponent;
import eel.seprphase2.FailureModel.FailureState;
import eel.seprphase2.Utilities.Energy;
import eel.seprphase2.Utilities.Percentage;
import eel.seprphase2.Utilities.Pressure;
import eel.seprphase2.Utilities.Temperature;
import static eel.seprphase2.Utilities.Units.*;
import java.util.ArrayList;
import eel.seprphase2.Persistence.Persistence;
import eel.seprphase2.Utilities.Mass;
import java.util.HashMap;

/**
 *
 * @author Yazidi
 */
public class PhysicalModel implements PlantController, PlantStatus {

    @JsonProperty
    private Reactor reactor = new Reactor();
    @JsonProperty
    private Turbine turbine = new Turbine();
    @JsonProperty
    private Condenser condenser = new Condenser();
    @JsonProperty
    private Energy energyGenerated = joules(0);
    @JsonProperty
    private Connection reactorToTurbine;
    @JsonProperty
    private Connection turbineToCondenser;
    @JsonProperty
    private Pump condenserToReactor;
    @JsonProperty
    private Pump reactorToCondenser;
    @JsonProperty
    private Pump heatsinkToCondenser;
    @JsonProperty
    private String username;
    @JsonProperty
    private HashMap<Integer,Pump> allPumps;
    @JsonProperty
    private HashMap<Integer,Connection> allConnections;
    @JsonProperty
    private HeatSink heatSink;
    
    /**
     *
     */
    @JsonIgnore
    public ArrayList<FailableComponent> components;
    /**
     *
     */
    public PhysicalModel() {
        
        heatSink = new HeatSink();
        
        allPumps =  new HashMap<Integer, Pump>();
        allConnections = new HashMap<Integer, Connection>();
        
        components = new ArrayList<FailableComponent>();
        components.add(0, turbine);
        components.add(1, reactor);
        components.add(2, condenser);
        
        reactorToTurbine = new Connection(reactor.outputPort(), turbine.inputPort(), 0.05);
        turbineToCondenser = new Connection(turbine.outputPort(), condenser.inputPort(), 0.05);
        
        
        condenserToReactor = new Pump(condenser.outputPort(), reactor.inputPort());
        reactorToCondenser = new Pump(reactor.outputPort(), condenser.inputPort());
        heatsinkToCondenser = new Pump(heatSink.outputPort(), condenser.coolantInputPort());
        
        reactorToCondenser.setStatus(false);
        
        
        
        
        allConnections.put(1,reactorToTurbine);
        allConnections.put(2,turbineToCondenser);
        
        allPumps.put(1, reactorToCondenser);
        allPumps.put(2, condenserToReactor);
        allPumps.put(3, heatsinkToCondenser);
        
    }
    
    /**
     *
     * @param steps
     */
    public void step(int steps) {
        for (int i = 0; i < steps; i++) {
            reactor.step();
            turbine.step();
            condenser.step();
            energyGenerated = joules(energyGenerated.inJoules() + turbine.outputPower());
            reactorToTurbine.step();
            turbineToCondenser.step();
            condenserToReactor.step();
            reactorToCondenser.step();
            heatsinkToCondenser.step();
            
        //System.out.println("Turbine Fail State: " + turbine.getFailureState());
        //System.out.println("Condenser Fail State: " + condenser.getFailureState());
        }
    }
    
    /**
     *
     * @param username
     */
    @Override
    public void setUsername(String username){
        this.username = username;
    }

    /**
     *
     * @param percent
     */
    @Override
    public void moveControlRods(Percentage percent) {
        reactor.moveControlRods(percent);
    }

    /**
     *
     * @return
     */
    @Override
    public Temperature reactorTemperature() {
        return reactor.temperature();
    }

    
    public Mass reactorMinimumWaterMass() {
        return reactor.minimumWaterMass();
    }
    
    
    public Mass reactorMaximumWaterMass() {
        return reactor.maximumWaterMass();
    }
    
    @Override
    public Percentage reactorMinimumWaterLevel() {
        return reactor.minimumWaterLevel();
    }
    
    public void failReactor() {
        reactor.setFailureState(FailureState.Failed);
    }
    
    public void failCondenser() {
        condenser.setFailureState(FailureState.Failed);
    }
    /**
     *
     * @return
     */
    @Override
    public Energy energyGenerated() {
        return energyGenerated;
    }

    /**
     *
     * @return
     */
    @Override
    public Percentage controlRodPosition() {
        return reactor.controlRodPosition();
    }

    /**
     *
     * @return
     */
    @Override
    public Pressure reactorPressure() {
        return reactor.pressure();
    }

    /**
     *
     * @return
     */
    @Override
    public Percentage reactorWaterLevel() {
        return reactor.waterLevel();
    }
   
    /**
     * 
     * @param open
     */
    @Override
    public void setReactorToTurbine(boolean open){
        reactorToTurbine.setOpen(open);
    }
    
    /**
     *
     * @return
     */
    @Override
    public boolean getReactorToTurbine(){
        return reactorToTurbine.getOpen();
    }

    @Override
    public void changeValveState(int valveNumber, boolean isOpen) {
        if(allConnections.containsKey(valveNumber))
        {
            allConnections.get(valveNumber).setOpen(isOpen);
        }
        else
        {
            //Throw KeyNotFoundException
        }
    }

    @Override
    public void changePumpState(int pumpNumber, boolean isPumping) {
        if(allPumps.containsKey(pumpNumber))
        {
            allPumps.get(pumpNumber).setStatus(isPumping);
            
        }
        else
        {
            //Throw KeyNotFoundException
        }
    }

    @Override
    public void saveGame() throws JsonProcessingException {
        eel.seprphase2.Simulator.Persistence p = new eel.seprphase2.Simulator.Persistence();
        
        String r = p.serialize(this);
        Persistence.SaveGameState(username, r); 
        
                
    }
    

    @Override
    public void loadGame(int gameNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
          
    @Override
    public String[] listGames() {
        return Persistence.GetSaveGames(username);
    }

    @Override
    public void repairPump(int pumpNumber) {
        if(allPumps.containsKey(pumpNumber))
        {
            allPumps.get(pumpNumber).setCapacity(kilograms(3));
            allPumps.get(pumpNumber).setFailureState(FailureState.Normal);
            allPumps.get(pumpNumber).setStatus(true);
            allPumps.get(pumpNumber).setWear(new Percentage(0));
        }
        else
        {
            //Throw KeyNotFoundException
        }
    }

    @Override
    public void repairCondenser() {
        condenser.setFailureState(FailureState.Normal);
        condenser.setWear(new Percentage(0));
        condenser = new Condenser();
    }

    @Override
    public void repairTurbine() {
        turbine.setFailureState(FailureState.Normal);
        turbine.setWear(new Percentage(0));
        turbine = new Turbine();
    }
    
    @Override
    public void help() {
        System.out.println("\n" + "Possible Commands: " + "\n" + "movecontrolrods <Percentage>" + "\n" +
                                              "openvalve <ValveNumber>" + "\n" + 
                                              "closevalve <ValveNumber>" + "\n" + 
                                              "pumpon <PumpNumber>" + "\n" + 
                                              "pumpoff <PumpNumber>" + "\n" + 
                                              "repair pump <PumpNumber>" + "\n" + 
                                              "repair turbine" + "\n" + 
                                              "repair condenser" + "\n" + 
                                              "save" + 
                                              "load <GameNumber>" +"\n" + "\n");
    }

    @Override
    public Temperature condenserTemperature() {
        return condenser.getTemperature();
    }

    @Override
    public Pressure condenserPressure() {
        return condenser.getPressure();
    }

    @Override
    public Percentage condenserWaterLevel() {
        return condenser.getWaterLevel();
    }

    

   
    
}
