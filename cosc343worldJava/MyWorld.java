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
 
    /* Here you can specify the number of turns in each simulation
     * and the number of generations that the genetic algorithm will 
     * execute.
     */
    private final int _numTurns = 100;
    private final int _numGenerations = 500;
  

  
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
        // Here you can specify the grid size, window size and whether to run
        // in repeatable mode or not
        int gridSize = 24;
        int windowWidth =  1600;
        int windowHeight = 900;
        boolean repeatableMode = false;
     
        /* Here you can specify world type - there are two to
           choose from: 1 and 2.  Refer to the Assignment2 instructions for
           explanation of the world type formats.
        */
        int worldType = 1;     
     
        // Instantiate MyWorld object.  The rest of the application is driven
        // from the window that will be displayed.
        MyWorld sim = new MyWorld(worldType, gridSize, windowWidth, windowHeight, repeatableMode);
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
     

        float avgLifeTime=0f;
        int nSurvivors = 0;
        for(MyCreature creature : old_population) {

            int energy = creature.getEnergy();
            boolean dead = creature.isDead();
        
            if(dead) {
                int timeOfDeath = creature.timeOfDeath();
                avgLifeTime += (float) timeOfDeath;
            } else {
                nSurvivors += 1;
                avgLifeTime += (float) _numTurns;
            }
        }

        avgLifeTime /= (float) numCreatures;
        System.out.println("Simulation stats:");
        System.out.println("  Survivors    : " + nSurvivors + " out of " + numCreatures);
        System.out.println("  Avg life time: " + avgLifeTime + " turns");

        float total_fitness = 0;
        for (MyCreature creature : old_population) {
            total_fitness += fitness(creature);
        }

        System.out.println("Average fitness: " + total_fitness / numCreatures);
        
        float[] creature_probabilities = new float[numCreatures];

        // Gives each creature a probability out of 1 of being selected based on
        // the fitness of the creature, this is the roulette wheel idea.
        for (int i = 0; i < numCreatures; i++) {
            creature_probabilities[i] = fitness(old_population[i]) / total_fitness;
        }

        insertionSort(creature_probabilities);

        float[] cummulative_fitness = new float[numCreatures];
        float sum_of_fitness = 0;

        MyCreature best = null;
        float best_score = 0;
        
        for (int i = 0; i < numCreatures; i++) {
            sum_of_fitness += creature_probabilities[i];
            cummulative_fitness[i] = sum_of_fitness;
            if (creature_probabilities[i] > best_score) {
                best = old_population[i];
                best_score = creature_probabilities[i];
            }
        }

        

        for (int i = 0; i < numCreatures; i++) {

            float elitism_probabilty = r.nextInt(10);
            if (elitism_probabilty == 1) {
                new_population[i] = best;
            }

    
            //MyCreature mother = chooseParent(old_population, cummulative_fitness);
            //MyCreature farther = chooseParent(old_population, cummulative_fitness);
            

            MyCreature mother = tournamentSelection(old_population, numCreatures);
            MyCreature farther = tournamentSelection(old_population, numCreatures);

            //MyCreature mother = chooseMum(old_population, numCreatures);
            //MyCreature farther = chooseDad(old_population, numCreatures);
            
            new_population[i] = new MyCreature(mother, farther);
        }

        //printAverageChromosome(old_population);
        
        return new_population;
    }


    /* fitness provides a score of the success of each creature from the previous
       round. Only the relative value of the fitness is relevent because the entire
       distribution will be compressed to the range of 0 - 1.
    */
    public float fitness(Creature c) {
        int fitness = 0;

        if (c.isDead()) {
            fitness = c.getEnergy()/ 10;
        } else {
            fitness = c.getEnergy();
            fitness += 100;
        }
        return fitness;
    }


    /* chooseParent performs a spin of the roulette wheel to return a radomly
       selected parent with the probabilities of parents being selected beings
       weighted by the fitness function as given in creature_probabilities array.

       This method essentially spins the roulette wheel and then checks one by one
       the slice of the wheel of each creature to see if the wheel 'choose' that
       creature.
    */
    public MyCreature chooseParent(MyCreature[] old_population, float[] cummulative_fitness) {

        float roulette_spin = r.nextFloat();
        MyCreature winner = null;

        for (int i = old_population.length - 1; i >= 0; i--) {
            if (cummulative_fitness[i] >= roulette_spin ) {
                winner = old_population[i];
            }
        }
        return winner;
    }

    public void insertionSort(float[] keys) {

        for (int i = 1; i < keys.length; i++) {
            float key = keys[i];
            int j = i - 1;
            while (j >= 0 && keys[j] > key) {
                keys[j + 1] = keys[j];
                j = j - 1;
            }
            keys[j + 1] = key;
        }
    }

    public MyCreature tournamentSelection(MyCreature[] old_population, int numCreatures) {
        int rand_point = rand.nextInt(numCreatures - 6) + 3;
        MyCreature winner = old_population[rand_point];
        float best = 0;
        
        for (int i = rand_point - 3; i < rand_point + 3; i++) {
            //System.out.println("Creature [" + i + "] has fitness : " + fitness(old_population[i]));
            if (fitness(old_population[i]) > best) {
                best = fitness(old_population[i]);
                winner = old_population[i];
            }
        }

        //System.out.println("Winner fitness: " + fitness(winner));
        return winner;
    }

    public void printAverageChromosome(MyCreature[] old_population) {
        float []chromosome = new float[7];

        for (int i = 0; i < 7; i++) {
            chromosome[i] = 0;
            for (int j = 0; j < old_population.length; j++) {
                chromosome[i] += old_population[j].getGene(i);
            }
            chromosome[i] = chromosome[i] / old_population.length;
        }

        for (int i = 0; i < 7; i++) {
            System.out.println("Average for gene: " + i + " == " + chromosome[i]);
        }
    }


    public MyCreature chooseMum(MyCreature[] old_population, int numCreatures) {
        float best = 0;
        MyCreature winner = null;

        for (int i = 0; i < numCreatures / 2; i++) {
            if (fitness(old_population[i]) > best) {
                best = fitness(old_population[i]);
                winner = old_population[i];
            }
        }
        return winner;
    }

    
    public MyCreature chooseDad(MyCreature[] old_population, int numCreatures) {
        float best = 0;
        MyCreature winner = null;

        for (int i = numCreatures / 2; i < numCreatures; i++) {
            if (fitness(old_population[i]) > best) {
                best = fitness(old_population[i]);
                winner = old_population[i];
            }
        }
        return winner;
    }

}


