import cosc343.assig2.World;
import cosc343.assig2.Creature;
import java.util.*;

/**
 * The MyWorld extends the cosc343 assignment 2 World.  Here you can set 
 * some variables that control the simulations and override functions that
 * generate populations of creatures that the World requires for its
 * simulations.
 *
 * @author  
 * @version 1.0
 * @since   2017-04-05 
 */
public class MyWorld extends World {

    Random r = new Random();
    private static ArrayList<Float> results = new ArrayList<Float>();
    private static ArrayList<Integer> surviving = new ArrayList<Integer>();

    private static ArrayList<Float> gene = new ArrayList<Float>();
 
    /* Here you can specify the number of turns in each simulation
     * and the number of generations that the genetic algorithm will 
     * execute.
     */
    private final int _numTurns = 100;
    private final int _numGenerations = 100;
  

  
    /* Constructor.  
   
       Input: worldType - specifies which simulation will be running
       griSize - the size of the world
       windowWidth - the width (in pixels) of the visualisation window
       windowHeight - the height (in pixels) of the visualisation window
       repeatableMode - if set to true, every simulation in each
       generation will start from the same state
    */
    public MyWorld(int worldType, int gridSize, int windowWidth, int windowHeight, boolean repeatableMode) {   
        // Initialise the parent class - don't remove this
        super(worldType, gridSize, windowWidth,  windowHeight, repeatableMode);

        // Set the number of turns and generations
        this.setNumTurns(_numTurns);
        this.setNumGenerations(_numGenerations);
      
      
    }
 
    /* The main function for the MyWorld application

     */
    public static void main(String[] args) {
        int gridSize = 24;
        int windowWidth =  1600;
        int windowHeight = 900;
        boolean repeatableMode = false;
     
        int worldType = 1;     
        MyWorld sim = new MyWorld(worldType, gridSize, windowWidth, windowHeight, repeatableMode);;
    }
  

    /* The MyWorld class must override this function, which is
       used to fetch a population of creatures at the beginning of the
       first simulation.  This is the place where you need to  generate
       a set of creatures with random behaviours.
  
       Input: numCreatures - this variable will tell you how many creatures
       the world is expecting
                            
       Returns: An array of MyCreature objects - the World will expect numCreatures
       elements in that array     
    */  
    @Override
    public MyCreature[] firstGeneration(int numCreatures) {

        int numPercepts = this.expectedNumberofPercepts();
        int numActions = this.expectedNumberofActions();
      
        // This is just an example code.  You may replace this code with
        // your own that initialises an array of size numCreatures and creates
        // a population of your creatures
        MyCreature[] population = new MyCreature[numCreatures];
        for (int i = 0; i < numCreatures; i++) {
            population[i] = new MyCreature(numPercepts, numActions);     
        }
        return population;
    }
  
    /* The MyWorld class must override this function, which is
       used to fetch the next generation of creatures.  This World will
       proivde you with the old_generation of creatures, from which you can
       extract information relating to how they did in the previous simulation...
       and use them as parents for the new generation.
  
       Input: old_population_btc - the generation of old creatures before type casting. 
       The World doesn't know about MyCreature type, only
       its parent type Creature, so you will have to
       typecast to MyCreatures.  These creatures 
       have been participating in a simulation and their state
       can be queried to evaluate their fitness
       numCreatures - the number of elements in the old_population_btc
       array
                        
                            
       Returns: An array of MyCreature objects - the World will expect numCreatures
       elements in that array.  This is the new population that will be
       used for the next simulation.  
    */  
    @Override
    public MyCreature[] nextGeneration(Creature[] old_population_btc, int numCreatures) {
        MyCreature[] old_population = (MyCreature[]) old_population_btc;
        MyCreature[] new_population = new MyCreature[numCreatures];

        float total_fitness = 0;

        float average_fitness = 0;
        int survivors = 0;

        float gene_average = 0;

        for (MyCreature creature : old_population) {
            total_fitness += fitness(creature);
            if (!creature.isDead()) {
                survivors++;
            }
        }
        surviving.add(survivors);
        
        System.out.println("Survivors " + survivors + "/34");
        System.out.println("Average: " + total_fitness / 34);

        results.add(total_fitness / 34);


        // Parent Selection for next generation

        float[] cummulative_fitness = new float[numCreatures];
        float[] individual_normalised_fitness = new float[numCreatures];

        float previous_creature_fitness = 0;
        
        for (int i = 0; i < numCreatures; i++) {
            gene_average += old_population[i].getGene(0);
            individual_normalised_fitness[i] = fitness(old_population[i]) / total_fitness;
            
            cummulative_fitness[i] = individual_normalised_fitness[i]
                + previous_creature_fitness;
            previous_creature_fitness = cummulative_fitness[i];
            
        }

        gene_average /= 34;
        gene.add(gene_average);

        if (fitness_unit_test(individual_normalised_fitness) == false) {
            System.out.println("Error: Normalised fitness does not sum to 1");
        }


        

        for (int i = 0; i < numCreatures; i++) {
            if (r.nextFloat() < 0.1) {
                new_population[i] = old_population[getBestCreature(individual_normalised_fitness)];
            } else {
                int mother = rouletteSpin(cummulative_fitness);
                int farther = rouletteSpin(cummulative_fitness);
                new_population[i] = new MyCreature(old_population[mother], old_population[farther]);
            }
        }

        for (int i = 0; i < results.size(); i++) {
            System.out.println(results.get(i));
        }

        for (int i = 0; i < surviving.size(); i++) {
            System.out.println(surviving.get(i));
        }

        for (int i = 0; i < gene.size(); i++) {
            System.out.println(gene.get(i));
        }

        return new_population;
    }


    /* fitness provides a score of the success of each creature from the previous
       round. Only the relative value of the fitness is relevent because the entire
       distribution will be compressed to the range of 0 - 1.
    */
    public float fitness(Creature c) {
        int fitness = 0;

 
        if (!c.isDead()) {
            fitness += 200;
            fitness += c.getEnergy() * 2;
        } else {
            fitness += c.timeOfDeath();
            fitness += c.getEnergy();
        }
        
        return fitness;
    }


    /* Unit test ensures fitness sums to 1 */
    public boolean fitness_unit_test(float[] fitness) {
        float total = 0;
        for (int i = 0; i < fitness.length; i++) {
            total += fitness[i];
        }

        if (total < 0.98 || total > 1.02) {
            return false;
        }

        return true;
    }


    public int rouletteSpin(float[] fitness) {

        float spin = r.nextFloat() * fitness[fitness.length - 1];
        int winner = 0;

        if (spin < 0 || spin > 1) {
            System.out.println("Error: Spin out of valid range");
        }

        float previous_creature = 0;
        
        for (int i = 0; i < fitness.length; i++) {
            if (spin >= previous_creature && spin <= fitness[i]) {
                winner = i;
            }
            previous_creature = fitness[i];
        }

        return winner;
    }

    public int getBestCreature(float[] fitness) {

        int best = 0;
        float best_score = 0;

        for (int i = 0; i < fitness.length; i++) {
            if (best_score < fitness[i]) {
                best_score = fitness[i];
                best = i;
            }
        }
        return best;
    }
}
