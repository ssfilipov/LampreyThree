package eel.seprphase2.Simulator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import eel.seprphase2.GameOverException;
import eel.seprphase2.Persistence.FileSystem;
import eel.seprphase2.Persistence.SaveGame;
import eel.seprphase2.Utilities.Energy;
import eel.seprphase2.Utilities.Percentage;
import eel.seprphase2.Utilities.Pressure;
import eel.seprphase2.Utilities.Temperature;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import lamprey.seprphase3.DynSimulator.FluidFlowController;
import lamprey.seprphase3.DynSimulator.PlantModel;

/**
 *
 * @author David
 */
public class Simulator implements PlantController, PlantStatus, GameManager {

    private PlantModel plantModel;
    private PhysicalModel physicalModel;
    private FailureModel failureModel;
    private FluidFlowController fluidFlowController;
    private String userName;

    public Simulator() {
        plantModel = new PlantModel();
        physicalModel = new PhysicalModel(plantModel);
        fluidFlowController = new FluidFlowController(plantModel);
        failureModel = new FailureModel(fluidFlowController, physicalModel);
        userName = "";
    }

    @Override
    public void setUsername(String userName) {
        this.userName = userName;
    }

    @Override
    public void saveGame() throws JsonProcessingException {
        SaveGame saveGame = new SaveGame(physicalModel, failureModel, userName);
        try {
            saveGame.save();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }

    @Override
    public void loadGame(int gameNumber) {
        try {
            SaveGame saveGame = SaveGame.load(listGames()[gameNumber]);
            this.physicalModel = saveGame.getPhysicalModel();
            //this.fluidFlowController = saveGame.getFluidFlowController();
            //this.failureModel = new FailureModel(physicalModel, physicalModel);
            this.userName = saveGame.getUserName();
        } catch (JsonParseException ex) {
        } catch (IOException ex) {
        }
    }

    @Override
    public String[] listGames() {
        return FileSystem.listSaveGames(userName);
    }

    @Override
    public String[] listFailedComponents() {
        return failureModel.listFailedComponents();
    }
    
    @Override 
    public void step(double seconds) throws GameOverException {
        try {
            failureModel.step(seconds);
        } catch (GameOverException e) {
            throw new GameOverException("Dear " + userName + ",\n\n" +
                                        "YOU HAVE FAILED\n\n" +
                                        "The reactor vessel has failed catastrophically,\n"+ 
                                        "and everyone within a 100km radius is now either\n " +
                                        "dead or dying of radiation\n" +"poisioning.\n\n" +
                                        "However, you did successfully generate \n" + failureModel.energyGenerated() +
                                        "\nof energy before this occurred.");
        }
    }

    public void failStateCheck() {
        failureModel.failStateCheck();
    }

    @Override
    public void moveControlRods(Percentage extracted) {
        failureModel.moveControlRods(extracted);
    }

    @Override
    public void changeValveState(int valveNumber, boolean isOpen) throws KeyNotFoundException {
        failureModel.changeValveState(valveNumber, isOpen);
    }

    @Override
    public void changePumpState(int pumpNumber, boolean isPumping) throws CannotControlException, KeyNotFoundException {
        failureModel.changePumpState(pumpNumber, isPumping);
    }

    @Override
    public void repairPump(int pumpNumber) throws KeyNotFoundException, CannotRepairException {
        failureModel.repairPump(pumpNumber);
    }

    @Override
    public void repairCondenser() throws CannotRepairException {
        failureModel.repairCondenser();
    }
    
    @Override
    public String wornComponent() {
         return failureModel.wornComponent();
    }

    @Override
    public void repairTurbine() throws CannotRepairException {
        failureModel.repairTurbine();
    }

    @Override
    public Percentage controlRodPosition() {
        return failureModel.controlRodPosition();
    }

    @Override
    public Pressure reactorPressure() {
        return failureModel.reactorPressure();
    }
    
    @Override
    public void setWornComponent(FailableComponent currentWornComponent) {
        failureModel.setWornComponent(currentWornComponent);
    }

    @Override
    public Temperature reactorTemperature() {
        return failureModel.reactorTemperature();
    }

    @Override
    public Percentage reactorWaterLevel() {
        return failureModel.reactorWaterLevel();
    }
    
    @Override
    public Percentage reactorWear() { 
        return failureModel.reactorWear();
    }

    @Override
    public Energy energyGenerated() {
        return failureModel.energyGenerated();
    }
    
    public Percentage condenserToReactorWear() throws KeyNotFoundException {
        return failureModel.pumpWear(1);
    }
    
    public Percentage heatsinkToCondenserWear() throws KeyNotFoundException {
        return failureModel.pumpWear(2);
    }

    public boolean getReactorToTurbine() throws KeyNotFoundException {
        return failureModel.isValveOpen(1);
    }
    
    @Override
    public Percentage turbineWear() {
        return failureModel.turbineWear();
    }

    @Override
    public Temperature condenserTemperature() {
        return failureModel.condenserTemperature();
    }

    @Override
    public Pressure condenserPressure() {
        return failureModel.condenserPressure();
    }

    @Override
    public Percentage condenserWaterLevel() {
        return failureModel.condenserWaterLevel();
    }
    
    @Override
    public Percentage condenserWear() {
        return failureModel.condenserWear();
    }

    @Override
    public Percentage reactorMinimumWaterLevel() {
        return failureModel.reactorMinimumWaterLevel();
    }
   /** 
    @Override
    public void setWornComponent(FailableComponent currentWornComponent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
   **/

    @Override
    public void failCondenser() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void wearReactor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean turbineHasFailed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ArrayList<FailableComponent> failableComponents() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Percentage pumpWear(int pumpID) throws KeyNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isValveOpen(int valveID) throws KeyNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getPumpStatus(int pumpID) throws KeyNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
