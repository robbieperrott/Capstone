/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aim5.sim.setup;

/**
 *
 * @author Philip
 */
public class SimSetup {
     /** The number of columns */
  protected int numOfColumns;
  /** The number of rows */
  protected int numOfRows;
  /** The width of lanes */
  protected double laneWidth;
  /** The speed limit of the roads */
  protected double speedLimit;
  /** The number of lanes per road */
  protected int lanesPerRoad;
  /** The width of the area between the opposite directions of a road */
  protected double medianSize;
  /** The distance between intersection */
  protected double distanceBetween;
  /** The traffic level */
  protected double trafficLevel;
  /** The stopping distance before intersection */
  protected double stopDistBeforeIntersection;
  /** The pedestrian traffic level */
  protected double pedestrianLevel;
  /** The max wait time for pedestrians */
  protected double maxWait;
  
  /**
   * Create a basic simulator setup.
   *
   * @param columns                     the number of columns
   * @param rows                        the number of rows
   * @param laneWidth                   the width of lanes
   * @param speedLimit                  the speed limit of the roads
   * @param lanesPerRoad                the number of lanes per road
   * @param medianSize                  the width of the area between the
   *                                    opposite directions of a road
   * @param distanceBetween             the distance between intersections
   * @param trafficLevel                the traffic level
   * @param stopDistBeforeIntersection  the stopping distance before
   *                                    intersection
   * @param pedestrianLevel             the pedestrian traffic level
   * @param maxWait                     the max waiting time for pedestrians
   */
  public SimSetup(int columns, int rows,
                       double laneWidth, double speedLimit,
                       int lanesPerRoad,
                       double medianSize, double distanceBetween,
                       double trafficLevel,
                       double stopDistBeforeIntersection,
                       double pedestrianLevel, double maxWait) {
    this.numOfColumns = columns;
    this.numOfRows = rows;
    this.laneWidth = laneWidth;
    this.speedLimit = speedLimit;
    this.lanesPerRoad = lanesPerRoad;
    this.medianSize = medianSize;
    this.distanceBetween = distanceBetween;
    this.trafficLevel = trafficLevel;
    this.stopDistBeforeIntersection = stopDistBeforeIntersection;
    this.pedestrianLevel = pedestrianLevel;
    this.maxWait = maxWait;
  }
  
  public Simulator getSimulator() {
    // TODO: creates and returns a simulation using the values stored.
    // This is done in AutoDriverOnlyClass of aim4.
    
  }
  
  /**
   * Get the number of columns.
   *
   * @return the number of columns
   */
  public int getColumns() {
    return numOfColumns;
  }

  /**
   * Get the number of rows.
   *
   * @return the number of rows
   */
  public int getRows() {
    return numOfRows;
  }

  /**
   * Get the width of lanes.
   *
   * @return the width of lanes
   */
  public double getLaneWidth() {
    return laneWidth;
  }

  /**
   * Get the speed limit of the roads.
   *
   * @return the speed limit of the roads.
   */
  public double getSpeedLimit() {
    return speedLimit;
  }

  /**
   * Get the number of lanes per road.
   *
   * @return the number of lanes per road
   */
  public int getLanesPerRoad() {
    return lanesPerRoad;
  }

  /**
   * Get the width of the area between the opposite directions of a road.
   *
   * @return the width of the area between the opposite directions of a road
   */
  public double getMedianSize() {
    return medianSize;
  }

  /**
   * Get the distance between intersections.
   *
   * @return the distance between intersections
   */
  public double getDistanceBetween() {
    return distanceBetween;
  }

  /**
   * Get the traffic level.
   *
   * @return the traffic level
   */
  public double getTrafficLevel() {
    return trafficLevel;
  }

  /**
   * Get the stopping distance before intersection.
   *
   * @return the stopping distance before intersection
   */
  public double getStopDistBeforeIntersection() {
    return stopDistBeforeIntersection;
  }
  
  /**
   * Get the pedestrian traffic level
   *
   * @return the pedestrian traffic level
   */
  public double getPedestrianLevel(){
      return pedestrianLevel;
  }
  
  /**
   * Get the max wait time for pedestrians
   *
   * @return the max wait time for pedestrians
   */
  public double getMaxWait(){
      return maxWait;
  }
  
}
